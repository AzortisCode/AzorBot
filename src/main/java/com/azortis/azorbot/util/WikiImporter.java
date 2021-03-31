package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
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
    private final Map<String, List<String>> pages = new HashMap<>();
    private final JSONObject wiki;

    /**
     * Imports a wiki
     * @param name with name as name
     * @param path and path as path to raw github repo
     */
    public WikiImporter (String name, String path) {
        this.name = name;
        this.path = path.replace("SUMMARY.md","");
        wiki = create();
        if (wiki == null) {
            Main.error("Wiki" + name + " has an issue during creation");
            return;
        }
        Main.info("Created wiki page for " + name);
    }

    /**
     * Get and scrape wiki from path
     * @return JSONObject with processed lines
     */
    private JSONObject create(){
        URL url;
        try {
            url = new URL(path + "SUMMARY.md");
        } catch (IOException e) {
            Main.error("Failed retrieving page for (" + name + "): " + path + "SUMMARY.md");
            Main.error("Failed to import wiki. Exiting wiki importer");
            return null;
        }
        List<String> siteContent;
        siteContent = scrape(url);
        assert siteContent != null;
        Main.debug(siteContent.toString());
        JSONObject wiki = new JSONObject(TableOfContents(siteContent));
        wiki.put("path", path);
        wiki.put("name", name);
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
                    map.put(category, processCategory(subCategory));

                    // Reset the subArray
                    subCategory = new ArrayList<>();
                }

                // Reset forget
                forget = false;

                // Set the new category
                category = line.replace("## ", "");

                // If the new category is "links", forget until new category
                if (category.equalsIgnoreCase("links")) {
                    forget = true;
                }
            }

            // Continue if should be forgotten
            if (forget) continue;

            // If the line is not a page
            if (!line.startsWith("* [")){

                // Print the line
                Main.debug(line);
                continue;
            }

            // If the line is a page

            // Get the info
            List<String> info = getKeyAndPath(line);

            // If the line belongs in a category
            if (category != null) {

                // Add it to the category
                subCategory.add(info);

                // If the line does not belong in a category
            } else {

                // Store the line
                map.put(info.get(0), info.get(2));
            }

        }

        // Add the last category
        if (category != null && !category.equalsIgnoreCase("links")){

            // Remove the category name from each item
            subCategory.forEach(item -> item.remove(1));

            // Process the category
            map.put(category, processCategory(subCategory));
        }

        // Return the map
        return map;
    }

    /**
     * Process
     * @param content a selection of lines
     * @return into a (recursively indexed) category
     */
    private Map<String, Object> processCategory(List<List<String>> content) {

        // Create an empty map
        Map<String, Object> map = new HashMap<>();

        // Set the category list
        List<List<String>> subCategory = new ArrayList<>();

        // Set the category name
        String category = null;

        // Loop over all lines
        for (List<String> line : content){

            // Print the line
            Main.debug(line.toString());

            // If the line is not a part of a subcategory
            if(line.get(1).contains("README.md")){

                // Set the main page
                map.put("README", line.get(2));

            } else if(line.get(1).contains(".md")){

                // Add the line to the map
                map.put(line.get(0), line.get(2));

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
                if (category != null) map.put(capitalize(category), processCategory(subCategory));

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
            map.put(capitalize(category), processCategory(subCategory));
        }

        // Return the map
        return map;
    }

    /**
     * Scrape the webpage of
     * @param url this url and
     * @return a list of strings (all the lines)
     */
    private List<String> scrape(URL url) {
        InputStream stream;
        try {
            stream = url.openStream();
        } catch (IOException e){
            Main.error("Failed to open stream for URL: " + url.toString());
            return null;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(stream));
        String inputLine;
        List<String> out = new ArrayList<>();
        try {
            while ((inputLine = in.readLine()) != null) {
                out.add(inputLine.strip());
            }
            in.close();
        } catch (IOException e){
            Main.error("Failed to read next line or close stream for URL: " + url.toString());
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

        // Try retrieving the page from github
        try {
            pages.put(str[1], scrape(new URL(path + str[1])));
        } catch (IOException e) {
            Main.error("Exception while retrieving page information for page: " + path + str[1]);
            pages.put(str[1], new ArrayList<>());
        }

        // Return the list
        return result;
    }

    /**
     * Capitalize the first letter of
     * @param str this string and
     * @return the capitalized string
     */
    private String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
