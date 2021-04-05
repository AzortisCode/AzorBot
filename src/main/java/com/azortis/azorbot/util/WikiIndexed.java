package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
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
    // Global wiki variables
    private static final String absolutePath = Main.configPath + "wikis/{}.json";
    @Getter
    private static final List<WikiIndexed> wikis = new ArrayList<>();

    // This wiki's variables
    private final FileManager file;
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
        this.file = new FileManager(absolutePath.replace("{}", name));
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
    public static void getInfo(String name, AzorbotEmbed embed, boolean rawJSON){

        // Find the wiki
        WikiIndexed wiki = findWiki(name);

        // Make sure wiki is found
        if (wiki != null){

            // Check if raw JSON should be added
            if (rawJSON){

                // Print info
                Main.info("Adding raw JSON to embed");

                // Get json string
                String sWiki = wiki.getWiki().toString(4).replace("`", "");

                // Set size per field (cap for in embed)
                int sizePerField = 1000;

                // Check if we should make a scrollable embed
                if (sWiki.length() > sizePerField){

                    List<AzorbotEmbed> embeds = new ArrayList<>();

                    // Loop over each part of the string
                    for (int i = 0; i < Math.ceil(sWiki.length()/(float) sizePerField); i++){

                        // Do not mind deprecation. This is because ScrollableEmbed will set the message.
                        AzorbotEmbed thisEmbed = new AzorbotEmbed(Objects.requireNonNull(embed.build().getTitle()), embed.getMessage());

                        // Add a field with each
                        thisEmbed.setDescription(
                                "```json\n" + // Json code block
                                TextUtil.bnk + sWiki // With the right content
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
                    AzorbotEmbed thisEmbed = new AzorbotEmbed(Objects.requireNonNull(embed.build().getTitle()), embed.getMessage());
                    thisEmbed.setDescription("**Last updated on**." + dateTime + "\n*(Server time)*");

                    // Add
                    embeds.add(thisEmbed);

                    // Create and send a scrollable embed
                    new ScrollableEmbed(embeds, embed.getMessage());

                    // Delete command but don't send original embed (do send scrollable)
                    embed.getMessage().delete().queue();

                    return;
                } else {
                    embed.setDescription("```json\n" + TextUtil.bnk + sWiki + "\n```");
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
                    ).substring(0, 2000)
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

            // 1. Check if there is NO path: This is a MAIN category, which has no main page
            if (!((Map<?, ?>) item).containsKey("path")){
                string.append(depth)
                        .append(key)
                        .append("\n");
                string.append(buildIndex((Map<String, Object>) item, "" + TextUtil.tab + TextUtil.tab, docs, subPath + key + "/", name));
                continue;
            }
            Main.info("key" + key);
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
            Main.info("Path: " + ((Map<?, ?>) item).get("path"));
            if ((((Map<?, ?>) item)).containsKey("README")){
                string.append(buildIndex((Map<String, Object>) item, "" + TextUtil.tab + TextUtil.tab, docs, subPath + key + "/", name));
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
            Main.info("Tried loading wikis but folder does not exist. Creating folder");
            try {
                if (!wikiFolder.createNewFile())
                    Main.error("Failed creating folder");
            } catch (IOException e){
                Main.error("Failed creating folder");
                e.printStackTrace();
            }
            return;
        }

        // Check all existing wikis
        for (File wiki : wikiFolder.listFiles()){
            // If not a json file, continue
            if (!wiki.getName().endsWith(".json")) continue;

            // Load into file manager, get/cleanup string, turn into json
            FileManager wManager = new FileManager(wiki);
            if (!wManager.checkExists(false, false)){
                return;
            }
            String wString = wManager.read().get(0);
            JSONObject wJson = new JSONObject(wString);

            // Check if is wiki
            if (!isWikiJSON(wJson)) continue;

            // Send info
            Main.info("Loaded wiki: " + wJson.getString("name"));

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
        wikis.forEach(wiki -> wikiString.append(" - `").append(TextUtil.capitalize(wiki.getName())).append("`\n"));
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
    public List<AzorbotEmbed> search(List<String> args, Message msg){

        // Make empty pages list
        List<AzorbotEmbed> pages = new ArrayList<>();

        // Get matching pages (key is page name, element is page snippet)
        Map<String, List<String>> matches = findMatchingPages(args);

        if (matches.containsKey("Options")){
            // Return a single error-like embed if no matching pages were found
            AzorbotEmbed embed = new AzorbotEmbed("No matching pages found", msg);
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
            AzorbotEmbed embed = new AzorbotEmbed(key, msg);
            String pageName = key.split("/")[key.split("/").length - 1];
            Main.info("pagename: " + pageName);
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
            List<List<String>> snippets = searchMessage(item, args, this.threshold);

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
                if (line.startsWith("%{")) page.remove(line);
                else if (line.startsWith("####")) line = "__" + line.replace("####", "") + "__";
                else if (line.startsWith("###")) line = "**" + line.replace("####", "") + "**";
                else if (line.startsWith("##")) line = "__**" + line.replace("####", "") + "**__";
                else if (line.startsWith("$$")) line = "Equation";
            }
            newPage.add(newLine);
        });
        return newPage;
    }

    /**
     * Searches for a query in a message
     * @param message the message to search
     * @param query this query
     * @param threshold 1-100 scale for certainty threshold
     * @return List of List of strings representing snippets for this page.
     * <p>If empty, there are no found matches</p>
     */
    private List<List<String>> searchMessage(List<String> message, List<String> query, int threshold){
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
