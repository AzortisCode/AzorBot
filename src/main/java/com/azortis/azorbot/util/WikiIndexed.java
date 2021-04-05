package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.cocoUtil.*;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
public class WikiIndexed {
    private static boolean done = false;
    // Global wiki variables
    private static final String absolutePath = Main.configPath + "wikis/{}.json";
    @Getter
    private static final List<WikiIndexed> wikis = new ArrayList<>();

    // This wiki's variables
    private final CocoFiles file;
    private final String name;
    private final String docs;
    private final String gitPath;
    private JSONObject wiki;
    private Map<String, List<String>> flatWiki;
    private LocalDateTime updatedDate;
    @Setter
    private int threshold;

    /**
     * Creates a new blank wiki with
     * @param name Name
     * @param gitPath and raw github page path
     * @param docs url to the docs page of this wiki
     * @param threshold integer 1-100 scale threshold for searches
     */
    public WikiIndexed(String name, String gitPath, String docs, int threshold){
        this.name = name;
        this.gitPath = gitPath;
        this.docs = docs;
        this.file = new CocoFiles(absolutePath.replace("{}", name));
        this.file.checkExists(true, false);
        this.threshold = threshold;
        this.load();
        wikis.add(this);
    }

    /**
     * Gets info for a wiki by name
     * @param name Name to query
     * @param embed Embed to write info to (is also sent)
     * @param rawJSON Toggle to also add raw json to embed
     */
    public static void getInfo(String name, CocoEmbed embed, boolean rawJSON){

        // Find the wiki
        WikiIndexed wiki = findWiki(name);

        // Make sure wiki is found
        if (wiki != null){

            // Check if raw JSON should be added
            if (rawJSON){

                // Print info
                CocoBot.info("Adding raw JSON to embed");

                // Get json string
                String sWiki = wiki.getWiki().toString(4).replace("`", "");

                // Set size per field (cap for in embed)
                int sizePerField = 1000;

                // Check if we should make a scrollable embed
                if (sWiki.length() > sizePerField){

                    List<CocoEmbed> embeds = new ArrayList<>();

                    // Loop over each part of the string
                    for (int i = 0; i < Math.ceil(sWiki.length()/(float) sizePerField); i++){

                        // Rebuild embed
                        CocoEmbed thisEmbed = new CocoEmbed(Objects.requireNonNull(embed.build().getTitle()), embed.getMessage());

                        // Add a field with each
                        thisEmbed.setDescription(
                                "```json\n" + // Json code block
                                CocoText.bnk + sWiki // With the right content
                                .substring(
                                        i * sizePerField,
                                        Math.min(
                                                (i + 1) * sizePerField,
                                                sWiki.length() - 1
                                        )
                                ) + "\n```"
                        );

                        // Add the datetime info
                        String dateTime = DateTimeFormatter
                                .ofPattern("dd-MM-yyyy kk:HH:ss")
                                .withLocale(Locale.getDefault())
                                .withZone(ZoneId.systemDefault())
                                .format(wiki.getUpdatedDate());
                        embed.addField("Last updated on", dateTime + "\n*(Server time)*", false);

                        // Add this to embeds
                        embeds.add(thisEmbed);
                    }

                    // Add the datetime info
                    String dateTime = DateTimeFormatter
                            .ofPattern("dd-MM-yyyy kk:HH:ss")
                            .withLocale(Locale.getDefault())
                            .withZone(ZoneId.systemDefault())
                            .format(wiki.getUpdatedDate());
                    // Do not mind deprecation. This is because ScrollableEmbed will set the message.
                    CocoEmbed thisEmbed = new CocoEmbed(Objects.requireNonNull(embed.build().getTitle()), embed.getMessage());
                    thisEmbed.setDescription("**Last updated on**." + dateTime + "\n*(Server time)*");

                    // Add
                    embeds.add(thisEmbed);

                    // Create and send a scrollable embed
                    new CocoScrollable(embeds, embed.getMessage());

                    // Delete command but don't send original embed (do send scrollable)
                    embed.getMessage().delete().queue();

                    return;
                } else {
                    embed.setDescription("```json\n" + CocoText.bnk + sWiki + "\n```");
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
        embed.send(true);
    }

    /**
     * Gets the indexed wiki inside an embed
     * @param name Name of the wiki to index
     * @return String containing indices
     */
    public static String getIndex(String name){
        WikiIndexed wiki = findWiki(name);
        if (wiki == null){
            return "No wiki found by that name. Please double-check\n" +
                    "Loaded wikis are: `" + getLoadedWikis() + "`";
        } else {
            return buildIndex(
                    wiki.getWiki().toMap(),
                    "",
                    wiki.getWiki().getString("docs"),
                    "",
                    wiki.getName()
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

        // Loop over all keys
        for (String key : wiki.keySet()){

            // Prevent key is wiki name, item's path or wiki docs link
            if (key.equalsIgnoreCase("name")
                    || key.equalsIgnoreCase("path")
                    || key.equalsIgnoreCase("docs")
                    || key.equalsIgnoreCase("page")
                    || key.equalsIgnoreCase("README")
            ){
                continue;
            }

            // Get item
            Object item = wiki.get(key);

            // Prevent item not map
            if (!(item instanceof Map)){
                continue;
            }

            /*
                Next bit is somewhat confusing
                1. Check if there is NO path: This is a MAIN category, which has no main page
                2. Add the current item
                3. Check if the path contains a README.md extension, indicating it is a SUB category
                    We must then enter this category and add all its stuff as well
             */
            CocoBot.info(((Map<?, ?>) item).keySet().toString());

            // 1. Check if there is NO path: This is a MAIN category, which has no main page
            if (!((Map<?, ?>) item).containsKey("path")){
                string.append(depth)
                        .append(key)
                        .append("\n");
                string.append(buildIndex((Map<String, Object>) item, depth + CocoText.tab + CocoText.tab + CocoText.bnk, docs, subPath + key + "/", name));
                continue;
            }
            // 2. Add the current item
            string.append(depth)
                    .append("[")
                    .append(key)
                    .append("](")
                    .append(docs)
                    .append(subPath.toLowerCase(Locale.ROOT))
                    .append(key.equalsIgnoreCase(name) ? "" : key.toLowerCase(Locale.ROOT).replace(" ", "-"))
                    .append(")")
                    .append("\n");

            // 3. Check if the path contains a README.md extension, indicating it is a SUB category
            //      We must then enter this category and add all its stuff as well
            if ((((Map<?, ?>) item)).containsKey("README")){
                string.append(buildIndex((Map<String, Object>) item, depth + CocoText.tab + CocoText.tab + CocoText.bnk, docs, subPath + key + "/", name));
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

        // Check folder exists
        File wikiFolder = new File(absolutePath.replace("{}.json", ""));
        if (!wikiFolder.exists()){
            CocoBot.info("Tried loading wikis but folder does not exist. Creating folder");
            try {
                if (!wikiFolder.createNewFile())
                    CocoBot.error("Failed creating folder");
            } catch (IOException e){
                CocoBot.error("Failed creating folder");
                e.printStackTrace();
            }
            return;
        }

        // Check all existing wikis
        for (File wiki : wikiFolder.listFiles()){
            // If not a json file, continue
            if (!wiki.getName().endsWith(".json")) continue;

            // Load into file manager, get/cleanup string, turn into json
            CocoFiles wManager = new CocoFiles(wiki);
            if (!wManager.checkExists(false, false)){
                return;
            }
            String wString = wManager.read().get(0);
            JSONObject wJson = new JSONObject(wString);

            // Check if is wiki
            if (!isWikiJSON(wJson)) continue;

            // Send info
            CocoBot.info("Loaded wiki: " + wJson.getString("name"));

            // Add wiki (automatically added to wikis)
            new WikiIndexed(wJson.getString("name"), wJson.getString("path"), wJson.getString("docs"), wJson.getInt("threshold"));
        }
    }

    /**
     * Checks if the specified json object is a wiki
     * @param wikiJson The Json object to check
     * @return True if it is a wiki, false if not
     */
    private static boolean isWikiJSON(JSONObject wikiJson){
        if (wikiJson.has("path")
                && wikiJson.has("name")
                && wikiJson.has("docs")
                && wikiJson.has("threshold")
        ) return true;
        CocoBot.warn("Checked wiki JSON but was no wiki:\n" +
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
        wikis.forEach(wiki -> wikiString.append(" - `").append(CocoText.capitalize(wiki.getName())).append("`\n"));
        return wikiString.toString().strip();
    }

    /**
     * (re)loads this wiki
     * @return True if new definitions, False if the same
     */
    private boolean load(){

        // Load importer
        WikiImporter importer = new WikiImporter(this.name, this.gitPath, this.docs, this.threshold);

        // Get wiki from importer
        this.wiki = importer.getWiki();
        this.flatWiki = importer.getFlatWiki();

        // Read from saved file
        List<String> fromFile = this.file.read();
        String fromImport = this.wiki.toString();

        // Check for equality
        if (fromFile.toString().equalsIgnoreCase(fromImport)){
            return false;
        } else {
            // Write to file if not equal
            this.file.write(fromImport);

            // Get date
            this.updatedDate = LocalDateTime.now();

            return true;
        }
    }

    /**
     * Searches for a query stored in
     * @param args these arguments (list of words)
     * @param msg the message object of which the channel is used to send messages to
     * @return list of embeds for each of the pages.
     */
    public List<CocoEmbed> search(List<String> args, Message msg){

        // Make empty pages list
        List<CocoEmbed> pages = new ArrayList<>();

        CocoTimer timer = new CocoTimer(69420);

        // Get matching pages (key is page name, element is page snippet)
        Map<String, List<String>> matches = findMatchingPages(args);

        timer.stop();

        if (matches.containsKey("Options")){
            // Return a single error-like embed if no matching pages were found
            CocoEmbed embed = new CocoEmbed("No matching pages found", msg);
            embed.setDescription("Closest keywords are: `" +
                    matches.get("Options").toString()
                            .replace("[", "")
                            .replace("]", "")
                    + "`\n" +
                    "Please try one of these instead.\n");
            embed.addField("Otherwise, search the wiki", "[" + getName() + "](" + getDocs() + ")", false);
            pages.add(embed);
            return pages;
        }

        // Loop over all matches and save the embeds
        for (String key : matches.keySet()){
            CocoEmbed embed = new CocoEmbed(key, msg);
            String pageName = key.split("/")[key.split("/").length - 1];
            embed.setTitle(pageName, key);
            embed.setDescription(("Page: `" +
                    Arrays.toString(key.replace(docs, "")
                            .split("/"))
                            .replace("[", "")
                            .replace("]","")
                            .replace(",", " ->")
            + "`").replace("``", "`Main page`"));
            embed.addField("Snippet", matches.get(key).get(1), false);
            pages.add(embed);
        }

        // Return the pages
        return pages;
    }

    /**
     * Finds matching pages in this wiki
     * @param args using these arguments
     * @return a map where the keys are the page names, and the elements a list of strings representing a page.
     * <p>If no good items were found, this map has only key "Options" with element a string array with the closest matches.</p>
     */
    private Map<String, List<String>> findMatchingPages(@NotNull List<String> args){

        // Return hash
        Map<String, List<String>> results = new HashMap<>();

        // Go over all pages in the wiki
        this.flatWiki.keySet().forEach(key -> {

            // Retrieve the item as a list
            List<String> item = cleanupPage(flatWiki.get(key));

            // Query the page
            List<List<String>> snippets = searchMessage(item, args);

            // Return if empty
            if (snippets.size() == 0) return;

            // Return all them snippets
            String url = docs + key.replace(".md", "").replace("README", "");
            snippets.forEach(snip -> results.put(url, snip));
        });

        // Return
        return results.size() != 0 ?
                results :
                Map.of("Options", new ArrayList<>(Collections.singleton(args.get(0))));
    }

    /**
     * Cleans up
     * @param page this page
     * @return the cleaned up page
     */
    private List<String> cleanupPage(List<String> page){
        List<String> newPage = new ArrayList<>();
        page.forEach(line -> {
            String newLine = line.replace("{% tab title=\\", "Tab:")
                    .replace("{% hint style=\\\"danger\\\" %}", "Hint (Danger):")
                    .replace("{% hint style=\\\"info\\\" %}", "Hint (Info):")
                    .replace("{% hint style=\\\"warning\\\" %}", "Hint (Warning):")
                    .replace("{% hint style=\\\"success\\\" %}", "Hint (Success):");
            if (!newLine.equals(line)){
                if (newLine.startsWith("####")) newLine = "__" + newLine.replace("####", "") + "__";
                else if (newLine.startsWith("###")) newLine = "**" + newLine.replace("####", "") + "**";
                else if (newLine.startsWith("##")) newLine = "__**" + newLine.replace("####", "") + "**__";
                else if (newLine.startsWith("$$")) newLine = "Equation";
            }
            if (!newLine.startsWith("%{")) {
                newPage.add(newLine);
            }
        });
        return newPage;
    }

    /**
     * Searches for a query in a message
     * @param message the message to search
     * @param query this query
     * @return List of List of strings representing snippets for this page.
     * <p>If empty, there are no found matches</p>
     */
    private List<List<String>> searchMessage(List<String> message, List<String> query){
        if (!done){
            CocoFiles file = new CocoFiles(Main.configPath + "example.txt");
            file.write(message);
            CocoFiles file2 = new CocoFiles(Main.configPath + "example2.txt");
            file2.write(query);
            done = true;
        }
        List<List<String>> results = new ArrayList<>();
        /*
         * Query item 1 is found
         * add a snippet to the results list
         *
         * A snippet is basically a collection of lines that together form some useful information
         *
         * Full query match = 100% match
         * 1e 2e query word match = 95% match
         * is
         */
        return new ArrayList<>(Collections.singleton(message));
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
