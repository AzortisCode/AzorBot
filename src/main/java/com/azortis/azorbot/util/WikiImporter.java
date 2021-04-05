package com.azortis.azorbot.util;

import com.azortis.azorbot.cocoUtil.CocoBot;
import com.azortis.azorbot.cocoUtil.CocoText;
import lombok.Getter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@Getter
public class WikiImporter {
    private final String name;
    private final String path;
    private final String docs;
    private final JSONObject wiki;
    private final Map<String, List<String>> flatWiki = new HashMap<>();
    private final int threshold;

    /**
     * Imports a wiki
     * @param name with name as name
     * @param path and path as path to raw github repo
     * @param docs url to docs of this wiki
     * @param threshold 1-100 scale for wiki queries
     */
    public WikiImporter (String name, String path, String docs, int threshold){
        this.name = name;
        this.path = path.replace("SUMMARY.md","");
        this.docs = docs;
        this.threshold = threshold;
        wiki = create();
        if (wiki == null){
            CocoBot.error("Wiki" + name + " has an issue during creation");
            return;
        }
        CocoBot.info("Created wiki page for " + name);
    }

    /**
     * Get and scrape wiki from path
     * @return JSONObject with processed lines
     */
    private JSONObject create(){
        URL url;
        try {
            url = new URL(path + "SUMMARY.md");
        } catch (IOException e){
            CocoBot.error("Failed retrieving page for (" + name + "): " + path + "SUMMARY.md");
            CocoBot.error("Failed to import wiki. Exiting wiki importer");
            return null;
        }
        List<String> siteContent;
        siteContent = scrape(url);
        assert siteContent != null;
        CocoBot.debug(siteContent.toString());
        JSONObject wiki = new JSONObject(TableOfContents(siteContent));
        wiki.put("path", path);
        wiki.put("name", name);
        wiki.put("docs", docs);
        wiki.put("threshold", threshold);
        return wiki;
    }

    /**
     * Processes
     * @param content a raw, scraped webpage
     * @return into a hash with all processed content
     */
    private Map<String, Object> TableOfContents(List<String> content){

        // Create an empty map
        Map<String, Object> map = new HashMap<>();

        // Set the category variable
        String category = null;

        // Set the category list
        List<List<String>> subCategory = new ArrayList<>();

        // Forget variable (if true it passes over all lines until a new category is found)
        boolean forget = false;

        // Remove the first line
        content.remove(0);

        // Loop over all lines
        for (String line : content){

            // If the line is blank
            // Continue
            if (line.isBlank()) continue;

            // If the line is a category header
            if (line.startsWith("##")){

                // If there already is a category ongoing
                if (category != null && !forget){

                    // Remove the category name from each item
                    subCategory.forEach(item -> item.remove(1));

                    // Process the category
                    map.put(category, processCategory(subCategory, null));

                    // Reset the subArray
                    subCategory = new ArrayList<>();
                }

                // Reset forget
                forget = false;

                // Set the new category
                category = line.replace("## ", "");

                // If the new category is "links", forget until new category
                if (category.equalsIgnoreCase("links")){
                    forget = true;
                }
            }

            // Continue if should be forgotten
            if (forget) continue;

            // If the line is not a page
            if (!line.startsWith("* [")){

                // Print the line
                CocoBot.debug(line);
                continue;
            }

            // If the line is a page

            // Get the info
            List<String> info = getKeyAndPath(line);

            // If the line belongs in a category
            if (category != null){

                // Add it to the category
                subCategory.add(info);

                // If the line does not belong in a category
            } else {

                // Store the line
                Map<String, Object> page = makePage(info.get(2));
                map.put(info.get(0), page);
            }

        }

        // Add the last category
        if (category != null && !category.equalsIgnoreCase("links")){

            // Remove the category name from each item
            subCategory.forEach(item -> item.remove(1));

            // Process the category
            map.put(category, processCategory(subCategory, null));
        }

        // Return the map
        return map;
    }

    /**
     * Turns a page into a hash with
     * @param s Path to page to index to
     * @return Map with: path (full path), page (array of strings)
     */
    private Map<String, Object> makePage(String s){
        Map<String, Object> page = new HashMap<>();
        // Try retrieving the page from github
        List<String> PGs = null;
        try {
            PGs = Objects.requireNonNull(scrape(new URL(path + s)));
            PGs.forEach(l -> l = l.replace(":", "#69420#"));
            page.put("page", PGs);
        } catch (IOException e){
            CocoBot.error("Exception while retrieving page information for page: " + path + s);
            page.put("page", new ArrayList<>());
        }
        page.put("path", path + s);

        flatWiki.put(s, PGs);
        return page;
    }

    /**
     * Process
     * @param content a selection of lines
     * @return into a (recursively indexed) category
     */
    private Map<String, Object> processCategory(List<List<String>> content, String name){

        // Create an empty map
        Map<String, Object> map = new HashMap<>();

        // Set the category path
        if (name != null){
            map.put("path", path + name);
        }

        // Set the category list
        List<List<String>> subCategory = new ArrayList<>();

        // Set the category name
        String category = null;

        // Loop over all lines
        for (List<String> line : content){

            // Print the line
            CocoBot.debug(line.toString());

            // If the line is not a part of a subcategory
            if(line.get(1).contains("README.md")){

                // Set the main page
                map.put("README", makePage(line.get(2)));

            } else if(line.get(1).contains(".md")){

                // Add the line to the map
                map.put(line.get(0), makePage(line.get(2)));

                // If the line is supposed to go into a subcategory
            } else if(line.get(1).equals(category)){

                // Remove the category name from the line
                line.remove(1);

                // Add it to the subcategory list
                subCategory.add(line);

                // If there is a new category
            } else {

                // End of the previous category
                // Add subcategory to mapping if previous exists
                if (category != null) map.put(CocoText.capitalize(category), processCategory(subCategory, category));

                // Set the category to the new category type
                subCategory = new ArrayList<>();

                // Set the category name to be the new name
                category = line.remove(1);

                // Add the current item to the new subcategory
                subCategory.add(line);
            }
        }

        // Add the last category to the map
        if (category != null){
            map.put(CocoText.capitalize(category), processCategory(subCategory, category));
        }

        // Return the map
        return map;
    }

    /**
     * Scrape the webpage of
     * @param url this url and
     * @return a list of strings (all the lines)
     */
    private List<String> scrape(URL url){
        InputStream stream;
        try {
            stream = url.openStream();
        } catch (IOException e){
            CocoBot.error("Failed to open stream for URL: " + url.toString());
            return null;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(stream));
        String inputLine;
        List<String> out = new ArrayList<>();
        try {
            while ((inputLine = in.readLine()) != null){
                out.add(inputLine.strip());
            }
            in.close();
        } catch (IOException e){
            CocoBot.error("Failed to read next line or close stream for URL: " + url.toString());
            return null;
        }
        return out;
    }

    /**
     * Processes
     * @param line a line and
     * @return a full webpage indexation
     */
    private List<String> getKeyAndPath(String line){

        // Clean up the string
        String[] str = line.replace("* [", "")
                .replace("]", "~")
                .replace("(", "")
                .replace(")", "").split("~");

        // Make an arraylist for the result
        List<String> result = new ArrayList<>();

        // First item is the key i.e. title of the page
        result.add(str[0]);

        // Second to second-to-last items are the path to the page
        result.addAll(Arrays.asList(str[1].split("/")));

        // Last item is the full path as a string
        result.add(str[1]);

        // Return the list
        return result;
    }


    /**
     * Get the flat wiki generated during creation.
     * @return A JSON with URLs as keys and an arraylist of strings per key for a page
     */
    public Map<String, List<String>> getFlatWiki(){
        return this.flatWiki;
    }
}
