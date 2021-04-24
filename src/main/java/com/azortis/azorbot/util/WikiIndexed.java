package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.cocoUtil.*;
import com.azortis.azorbot.listeners.InlineCommandListener;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
     * @param addCommandsToListener If true, adds all commands on the command page to the registry used for listening.
     *                              <p>The commands page must not be in a category on GitBook & it must be named "Commands", else it cannot be found</p><p></p>
     *                              <p>The command page must be quite specifically formatted:</p>
     *                              <p>* All lines starting with a #, ## or ### followed by a / are used as a command</p>
     *                              <p>* All lines after are used as the command description, until:</p>
     *                              <p>* A line starts with a `, which is when the next line over is used as the command example</p>
     *                              <p>* Please put other information such as parameter tables & notes after the command example</p>
     */
    public static void loadAll(boolean addCommandsToListener){

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
            WikiIndexed w = new WikiIndexed(wJson.getString("name"), wJson.getString("path"), wJson.getString("docs"), wJson.getInt("threshold"));
            if (addCommandsToListener){
                CocoBot.info("Loading wiki Commands");
                if (!w.getWiki().has("Commands")){
                    CocoBot.info("Failed to find commands page for " + w.getName());
                    CocoBot.info("The 'Commands' page could not be found but it was requested");
                } else {
                    InlineCommandListener.loadFromWiki((JSONArray) ((JSONObject) w.getWiki().get("Commands")).get("page"), w.getDocs());
                }
            }
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

        // Get matching page names
        String pageNamesMatching = findMatchingPageNames(args);

        timer.stop();

        if (!pageNamesMatching.isBlank()){
            // Return a page with the titles of the pages that match
            CocoEmbed embed = new CocoEmbed("Matching pages by name", msg);
            embed.setDescription(pageNamesMatching);
            pages.add(embed);
        }

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
            StringBuilder sb = new StringBuilder();
            matches.get(key).forEach(line -> sb.append(line).append("\n"));
            embed.addField("Snippet", sb.toString(), false);
            pages.add(embed);
        }

        // Return the pages
        return pages;
    }

    /**
     * Finds a list of matching page names and nicely formats them into markup
     * @param args The arguments to search using
     * @return A list of lines consisting of the names of each of the pages
     */
    private String findMatchingPageNames(List<String> args) {

        StringBuilder sb = new StringBuilder();

        this.flatWiki.keySet().forEach(key -> {
            for (String arg : args) {
                if (key.contains(arg)){
                    //- (title)[docs.something.com/key]
                    sb.append("- [")
                            .append(CocoText.capitalize(key.replace(".md", "")))
                            .append("](")
                            .append(docs)
                            .append(key)
                            .append(")")
                            .append("\n");
                    break;
                }
            }
        });

        return sb.toString();
    }

    /**
     * Finds matching pages in this wiki
     * @param args using these arguments
     * @return a map where the keys are the page names, and the elements a list of strings representing a page.
     * <p>If no good items were found, this map has only key "Options" with element a string array with the closest matches.</p>
     */
    private Map<String, List<String>> findMatchingPages(@NotNull List<String> args){

        // Make hash
        Map<String, List<String>> results = new HashMap<>();

        // Go over all pages in the wiki
        this.flatWiki.keySet().forEach(key -> {

            // Query the page
            List<List<String>> snippets = searchMessage(flatWiki.get(key), args);

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
     * Searches for a query in a message
     * @param message the message to search
     * @param query this query
     * @return List of List of strings representing snippets for this page.
     * <p>If empty, there are no found matches</p>
     */
    private List<List<String>> searchMessage(List<String> message, List<String> query){

        // Make a hash which will be filled with results
        Map<Integer, List<String>> queried = new HashMap<>();

        // Loop over every line in the message
        for (int i = 0; i < message.size(); i++){

            // Retrieve the line
            String l = message.get(i).toLowerCase(Locale.ROOT);

            // Store the current line index (required since forEach)
            int finalI = i;

            // Loop over every term in the query
            query.forEach(q -> {

                // Check if the queried word is in the line
                if (l.contains(q.toLowerCase(Locale.ROOT))){
                    CocoBot.info("Query (" + q + ") in line: " + l);

                    // If the line already has a queried entry stored in the hash
                    if (queried.containsKey(finalI)) {

                        // Add the new query to the hashed list
                        queried.get(finalI).add(q);
                    } else {

                        // Or, if this is the first element, add a singletonList
                        queried.put(finalI, Collections.singletonList(q));
                    }
                }
            });
        }

        // Make an arraylist in which we will store the snippets
        List<List<String>> snips = new ArrayList<>();

        // Build a snip from the message and line number
        for (Integer key : queried.keySet()){
            List<String> snip = getSnippetFromLines(message, key, queried.get(key));
            if (snip != null) {
                snips.add(snip);
            }
        }

        // Return the snippets
        return snips;
    }

    /**
     * Retrieve a snippet from a list of lines and a line number
     * @param lines the lines to retrieve from
     * @param lineNumber the line number to query with
     * @param queried The list of strings that hit on this line
     */
    private List<String> getSnippetFromLines(List<String> lines, Integer lineNumber, List<String> queried){

        // TODO: Limit this to 1000 characters in a nice manner (stop at newline)

        // Amount of lines to display / search in before and after the indicated linenumber
        int takeBefore = 5;
        int takeAfter = 20;

        // Max size of snippet (characters)
        int maxSize = 1000;

        // Prevent out of bounds
        if (lineNumber > lines.size()){
            CocoBot.error("Getting snippet with index: " + lineNumber + " while size is " + lines.size());
            return null;
        }

        // Offset line
        int low = Math.max(lineNumber - takeBefore, 0);

        // Store lines
        List<String> result = null;

        // Try to find a header
        for (int i = low; i < low + takeBefore; i++){
            if (lines.get(i).startsWith("#")){
                result = lines.subList(i, Math.min(i + takeAfter, lines.size()));
            }
        }

        // Return default offset since no header found
        if (result == null) {
            result = lines.subList(low, Math.min(low + takeAfter, lines.size()));
        }

        // Mark elements in results that were part of the query
        for (int i = 0; i < result.size(); i++) {
            String line = result.get(i);
            for (String query : queried) {
                line = line.replace(query, "`>`**" + query + "**`<`");
            }
            result.set(i, line);
        }

        // Return
        return result;
    }

    /**
     * Deletes this wiki
     * @return true if successfully removed file. Wiki will be deleted successfully always. File may fail.
     */
    public boolean delete(){
        wikis.remove(this);
        return file.delete();
    }
}
