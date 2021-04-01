package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
import lombok.Getter;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.dv8tion.jda.api.entities.Message;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
public class WikiIndexed {
    // Global wiki variables
    private static final String absolutePath = "wikis/{}.json";
    @Getter
    private static final List<WikiIndexed> wikis = new ArrayList<>();

    // This wiki's variables
    private final FileManager file;
    private final String name;
    private final String docs;
    private final String gitPath;
    private JSONObject wiki;
    private LocalDateTime updatedDate;

    /**
     * Creates a new blank wiki with
     * @param name Name
     * @param gitPath and raw github page path
     */
    public WikiIndexed(String name, String gitPath, String docs){
        this.name = name;
        this.gitPath = gitPath;
        this.docs = docs;
        this.file = new FileManager(absolutePath.replace("{}", name));
        this.file.checkExists(true, false);
        this.load();
        wikis.add(this);
    }

    /**
     * Gets info for a wiki by name
     * @param name Name to query
     * @param embed Embed to write info to
     * @param rawJSON Toggle to also add raw json to embed
     */
    public static void getInfo(String name, AzorbotEmbed embed, boolean rawJSON){

        // Find the wiki
        WikiIndexed wiki = findWiki(name);

        // Make sure wiki is found
        if (wiki != null){

            // Check if raw JSON should be added
            if (rawJSON){

                // Print info
                Main.info("Adding raw JSON to embed");

                // Header
                embed.setDescription("Raw JSON:");

                // Get json string
                String sWiki = wiki.getWiki().toString(4);

                // Set size per field (cap for in embed)
                int sizePerField = 1000;

                // Loop over each part of the string
                for (int i = 0; i < Math.ceil(sWiki.length()/(float) sizePerField); i++){

                    // Add a field with each
                    embed.addField(
                            String.valueOf(i + 1), // With a header
                            "```json\n" + // And a json block
                            "\u200b" + sWiki // With the right content
                                    .substring(
                                            i * sizePerField,
                                            Math.min(
                                                    (i + 1) * sizePerField,
                                                    sWiki.length() - 1
                                            )
                                    ).replace("`", "") +
                            "\n```",
                            false
                    );

                }
            }

            // Add the datetime info
            String dateTime = DateTimeFormatter
                    .ofPattern("dd-MM-yyyy kk:HH:ss")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault())
                    .format(wiki.getUpdatedDate());
            embed.addField("Last updated on", dateTime + "\n*(Server time)*", false);
        } else {
            // Could not find
            embed.addField("Could not find wiki", name, false);
        }
    }

    /**
     * Gets the indexed wiki inside an embed
     * @param name Name of the wiki to index
     * @param embed Embed to write the index to
     */
    public static void getIndex(String name, AzorbotEmbed embed){
        WikiIndexed wiki = findWiki(name);
        if (wiki == null){
            embed.setDescription("No wiki found by that name. Please double-check\n" +
                    "Loaded wikis are: `" + getLoadedWikis() + "`");
        } else {
            embed.setDescription(
                    buildIndex(
                            wiki.getWiki().toMap(),
                            "",
                            wiki.getWiki().getString("docs"),
                            "",
                            wiki.getName()
                    )
            );
        }
    }

    /**
     * Builds a nicely formatted string from a json wiki
     * @param wiki the wiki to index
     * @param depth the current indent
     * @param docs link to the docs webpage
     * @param subPath subPath inside the docs
     * @param name the name of this wiki
     * @return the built string
     */
    @SuppressWarnings("unchecked")
    private static String buildIndex(Map<String, Object> wiki, String depth, String docs, String subPath, String name){

        // Init a string builder
        StringBuilder string = new StringBuilder();

        Main.info("(D) " + depth + " (Sp) " + subPath + " (Ks) " + wiki.keySet().toString());

        // Loop over all keys
        for (String key : wiki.keySet()){

            // Prevent key is wiki name, item's path or wiki docs link
            if (key.equalsIgnoreCase("name")
                    || key.equalsIgnoreCase("path")
                    || key.equalsIgnoreCase("docs")
                    || key.equalsIgnoreCase("page")
                    || key.equalsIgnoreCase("README")
            ) {
                Main.info(depth + "Skip: " + key);
                continue;
            }

            // Get item
            Object item = wiki.get(key);

            // Prevent item not map
            if (!(item instanceof Map)) {
                Main.info(depth + "Skip: " + key);
                continue;
            }

            Main.info(depth + " (K) " + key + " (Ks) " + ((Map<?, ?>) item).keySet().toString());
            /*
                Next bit is somewhat confusing
                1. Check if there is NO path: This is a MAIN category, which has no main page
                2. Add the current item
                3. Check if the path contains a README.md extension, indicating it is a SUB category
                    We must then enter this category and add all its stuff as well
             */


            // 1. Check if there is NO path: This is a MAIN category, which has no main page
            if (!((Map<?, ?>) item).containsKey("path")){
                string.append(depth)
                        .append(key)
                        .append("\n");
                string.append(buildIndex((Map<String, Object>) item, "" + "  ", docs, subPath + key + "/", name));
                continue;
            }

            // 2. Add the current item
            int subs = ((String) ((Map<?, ?>) item).get("path")).split("/").length; // Get substring length
            String itemPath = ((String) ((Map<?, ?>) item).get("path")) // Build item path
                    .split("/")[subs-1]
                    .replace("README", "")
                    .replace(".md", "")
                    .toLowerCase(Locale.ROOT);
            string.append(depth)
                    .append("[")
                    .append(key)
                    .append("](")
                    .append(docs)
                    .append(subPath.toLowerCase(Locale.ROOT))
                    .append(key.equalsIgnoreCase(name) ? "" : key.toLowerCase(Locale.ROOT))
                    .append(")")
                    .append("\n");

            // 3. Check if the path contains a README.md extension, indicating it is a SUB category
            //      We must then enter this category and add all its stuff as well
            Main.info("Path: " + ((Map<?, ?>) item).get("path"));
            if ((((Map<?, ?>) item)).containsKey("README")){
                string.append(buildIndex((Map<String, Object>) item, "" + "  ", docs, subPath + key + "/", name));
            }
        }
        return string.toString();
    }

    /**
     * Updates this wiki instance
     * @return Status indicator (null if not found, yes if new changes, no if no new changes)
     */
    public static String update(String name){
        WikiIndexed wiki = findWiki(name);
        if (wiki == null){
            return "null";
        } else {
            return wiki.load() ? "yes" : "no";
        }
    }

    /**
     * Finds wiki by name
     * @param name The name to find
     * @return The found wiki (can be null if none)
     */
    public static WikiIndexed findWiki(String name){
        for (WikiIndexed wiki : wikis){
            if (wiki.getName().equalsIgnoreCase(name)){
                return wiki;
            }
        }
        return null;
    }

    /**
     * Loads all existing wikis
     */
    public static void loadAll(){
        // Check all existing wikis
        for (File wiki : new File(absolutePath.replace("{}.json", "")).listFiles()){
            // If not a json file, continue
            if (!wiki.getName().endsWith(".json")) continue;

            // Load into file manager, get/cleanup string, turn into json
            FileManager wManager = new FileManager(wiki);
            String wString = wManager.read().get(0);
            JSONObject wJson = new JSONObject(wString);
            Main.info("JSON stuff:\n" + wJson.toString(4));

            // Check if is wiki
            if (!isWikiJSON(wJson)) continue;

            // Add wiki (automatically added to wikis)
            new WikiIndexed(wJson.getString("name"), wJson.getString("path"), wJson.getString("docs"));
        }
    }

    /**
     * Checks if the specified json object is a wiki
     * @param wikiJson The Json object to check
     * @return True if it is a wiki, false if not
     */
    private static boolean isWikiJSON(JSONObject wikiJson){
        if (wikiJson.has("path") && wikiJson.has("name") && wikiJson.has("docs")) return true;
        Main.warn("Checked wiki JSON but was no wiki:\n" +
                wikiJson.toString(2) + "\n" +
                "Only keys are: " + wikiJson.keySet().toString() + "\n" +
                "Needs at least: Path, Name, Docs");
        return false;
    }

    /**
     * Gets the loaded wikis
     * @return List of loaded wikis
     */
    public static String getLoadedWikis(){
        if (wikis.size() == 0){
            return "*none*";
        }
        StringBuilder wikiString = new StringBuilder();
        wikis.forEach(wiki -> wikiString.append(wiki.getName()).append(" "));
        return wikiString.toString().strip();
    }

    /**
     * (re)loads this wiki
     * @return True if new definitions, False if the same
     */
    private boolean load(){

        // Get date
        this.updatedDate = LocalDateTime.now();

        // Load importer
        WikiImporter importer = new WikiImporter(this.name, this.gitPath, this.docs);

        // Get wiki from importer
        this.wiki = importer.getWiki();

        // Read from saved file
        List<String> fromFile = this.file.read();
        String fromImport = this.wiki.toString();

        // Check for equality
        if (fromFile.toString().equalsIgnoreCase(fromImport)){
            return false;
        } else {
            // Write to file if not equal
            this.file.write(Collections.singletonList(fromImport));
            return true;
        }
    }

    /**
     * Capitalize the first letter of
     * @param str this string and
     * @return the capitalized string
     */
    private static String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Searches for a query stored in
     * @param args these arguments (list of words)
     * @param msg the message object of which the channel is used to send messages to
     * @return list of embeds for each of the pages.
     */
    public List<AzorbotEmbed> search(List<String> args, Message msg){

        // Make empty pages list
        List<AzorbotEmbed> pages = new ArrayList<>();

        // Get matching pages (key is page name, element is page snippet)
        Map<String, String[]> matches = findMatchingPages(args);

        if (matches.containsKey("Options")){
            // Return a single error-like embed if no matching pages were found
            AzorbotEmbed embed = new AzorbotEmbed("No matching pages found", msg);
            embed.setDescription("Closest keywords are: `" + matches.get("Options")[0] + "`\n" +
                    "Please try one of these instead.");
            pages.add(embed);
            return pages;
        }

        // Save some variables
        int thisPage = 1;
        int foundPages = matches.size();

        // Loop over all matches and save the embeds
        for (String key : matches.keySet()){
            AzorbotEmbed embed = new AzorbotEmbed(key, msg);
            embed.setTitle(key, matches.get(key)[0]);
            embed.setDescription("Page `" + thisPage + "/" + foundPages + "` relevant pages");
            embed.addField("Snippet", matches.get(key)[1], false);
            pages.add(embed);
        }

        // Return the pages
        return pages;
    }

    /**
     * Finds matching pages in this wiki
     * @param args using these arguments
     * @return a map where the keys are the page names, and the elements are a 2-element string array with the page url and the snippet.
     * If no good items were found, this map has only key "Options" with element a string array with the closest matches.
     */
    private Map<String, String[]> findMatchingPages(List<String> args){
        return new HashMap<String, String[]>(){{
            put("Options", new String[]{args.get(0)});
        }};
    }

    /**
     * Deletes this wiki
     * @return true if successful
     */
    public boolean delete(){
        wikis.remove(this);
        return file.delete();
    }
}
