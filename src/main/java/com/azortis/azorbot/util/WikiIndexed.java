package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
import lombok.Getter;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WikiIndexed {
    // Global wiki variables
    private static final String absolutePath = "wikis/{}.json";
    @Getter
    private static final List<WikiIndexed> wikis = new ArrayList<>();

    // This wiki's variables
    private final FileManager file;
    @Getter
    private final String name;
    @Getter
    private final String gitPath;
    @Getter
    private JSONObject wiki;
    @Getter
    private LocalDateTime updatedDate;

    /**
     * Creates a new blank wiki with
     * @param name Name
     * @param gitPath and raw github page path
     */
    public WikiIndexed(String name, String gitPath){
        this.name = name;
        this.gitPath = gitPath;
        this.file = new FileManager(absolutePath.replace("{}", name));
        this.file.checkExists(true, false);
        this.load();
        wikis.add(this);
    }

    /**
     * Gets the indexed wiki inside an embed
     * @param name Name of the wiki to index
     * @param embed Embed to write the index to
     */
    public static void getIndex(String name, AzorbotEmbed embed) {
        WikiIndexed wiki = findWiki(name);
        if (wiki == null){
            embed.setDescription("No wiki found by that name. Please double-check\n" +
                    "Loaded wikis are: `" + wikis.toString() + "`");
        } else {
            embed.setDescription(wiki.getWiki().toString(4));
            embed.addField("Last updated on", wiki.getUpdatedDate().toString(), false);
        }
    }

    /**
     * Updates this wiki instance
     * @return Status indicator (true if successful, false if failed)
     */
    public static boolean update(String name){
        WikiIndexed wiki = findWiki(name);
        if (wiki == null){
            return false;
        } else {
            return wiki.load();
        }
    }

    /**
     * Finds wiki by name
     * @param name The name to find
     * @return The found wiki (can be null if none)
     */
    private static WikiIndexed findWiki(String name){
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
    public static void loadAll() {
        // Check all existing wikis
        for (File wiki : new File(absolutePath.replace("{}.json", "")).listFiles()){
            // If not a json file, continue
            if (!wiki.getName().endsWith(".json")) continue;

            // Load into file manager, get/cleanup string, turn into json
            FileManager wManager = new FileManager(wiki);
            String wString = wManager.read().toString()
                    .replace(",", "\n")
                    .replace("\n\n", ",\n")
                    .replace("[","")
                    .replace("]", "");
            JSONObject wJson = new JSONObject(wString);

            // Check if is wiki
            if (!isWikiJSON(wJson)) continue;

            // Add wiki (automatically added to wikis)
            new WikiIndexed(wJson.getString("name"), wJson.getString("path"));
        }
    }

    /**
     * Checks if the specified json object is a wiki
     * @param wikiJson The Json object to check
     * @return True if it is a wiki, false if not
     */
    private static boolean isWikiJSON(JSONObject wikiJson){
        if (wikiJson.has("path") && wikiJson.has("name")) return true;
        Main.warn("Checked wiki JSON but was no wiki:\n" +
                wikiJson.toString(2) + "\n" +
                "Only keys are: " + wikiJson.keySet().toString());
        return false;
    }

    /**
     * (re)loads this wiki
     * @return True if new definitions, False if the same
     */
    private boolean load(){
        this.updatedDate = LocalDateTime.now();
        this.wiki = new WikiImporter(this.name, this.gitPath).getWiki();
        Main.debug(this.file.read().toString());
        Main.debug(Arrays.toString(wiki.toString(4).split("\n")));
        if (this.file.read().toString().equalsIgnoreCase(Arrays.toString(wiki.toString(4).split("\n")))) {
            return false;
        } else {
            this.file.write(Arrays.asList(wiki.toString(4).split("\n")));
            return true;
        }
    }
}
