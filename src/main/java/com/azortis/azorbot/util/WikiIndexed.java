package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
import lombok.Getter;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WikiIndexed {
    // Global wiki variables
    private static final String absolutePath = "wikis/";
    private static final List<WikiIndexed> wikis = new ArrayList<>();

    // This wiki's variables
    private final String name;
    private JSONObject wiki;
    private boolean updated = false;
    private LocalDateTime updatedDate = null;
    private final String creationInfo;

    // Load wiki
    private WikiIndexed(File wiki, boolean update){
        this.name = wiki.getName().replace(".json", "");
        this.wiki = load(wiki);
        if (this.wiki == null) {
            this.creationInfo = "FAILED";
        } else {
            this.creationInfo = "Successfully loaded";
        }
        if (update) Main.info(update());
    }

    public WikiIndexed(String name, String rawTextURL){
        String creationInfo;
        this.name = name;
        this.wiki = null;
        creationInfo = create(rawTextURL);
        this.creationInfo = creationInfo;
    }

    private String create(String rawTextURL) {
        for (WikiIndexed wiki : wikis){
            if (wiki.getName().equalsIgnoreCase(name)) {
                return "This wiki already exists!";
            }
        }
        File path = new File(absolutePath + name + ".json");
        try {
            path.createNewFile();
        } catch (IOException ex) {
            Main.error("Failed to create wiki file at: " + path.getAbsolutePath());
            return "FAILED";
        }
        wiki = new JSONObject();
        WikiImporter importer = new WikiImporter(name, rawTextURL);
        wiki.put("name", name)
                .put("path", path.getAbsolutePath())
                .put("URL", rawTextURL.replace("SUMMARY.md", ""))
                .put("index", importer.getWiki());
        Main.debug("Generated wiki json object: \n" + wiki.toString(4));
        if (!save()) return "FAILED";
        JSONObject newWiki = load();
        if (newWiki == null) {
            Main.error("Failed to load wiki from: " + path.getAbsolutePath());
            return "FAILED";
        } else {
            wiki = newWiki;
            return "Successfully created";
        }
    }

    // Saves this wiki
    private boolean save(){
        Main.info("Saving");
        Main.info("Current wiki:\n" + wiki.toString(4));
        File toFile = new File(absolutePath + name + ".json");
        FileWriter fw;
        try {
            fw = new FileWriter(toFile);
        } catch (IOException ex){
            Main.error("Failed to open fileWriter at: " + toFile.getAbsolutePath());
            return false;
        }
        try {
            fw.write(wiki.toString(4));
            fw.flush();
        } catch (IOException ex) {
            Main.error("Failed to write file for " + wiki + ": " + toFile.getAbsolutePath());
            return false;
        }
        return true;
    }


    // Loads this wiki from saved path & name
    private JSONObject load() {
        return load(new File(absolutePath + name + ".json"));
    }

    // Loads this wiki from `fromFile`
    private static JSONObject load(File fromFile){
        //TODO: Load from fromFile
        return null;
    }

    // Updates the wiki
    private String update() {
        if (wiki == null) {
            return "Empty wiki";
        }
        String old = wiki.toString();
        if (!wiki.has("URL")) {
            new File(absolutePath + name + ".json").delete();
            return "Corrupt save file. Deleting";
        }
        String status = create(wiki.getString("URL"));
        if (!status.equalsIgnoreCase("Successfully created")) return status;
        wiki = new JSONObject();
        WikiImporter importer = new WikiImporter(name, wiki.getString("URL"));
        wiki.put("name", name)
                .put("path", absolutePath + name + ".json")
                .put("URL", wiki.getString("URL"))
                .put("index", importer.getWiki());
        Main.info(wiki.toString(4));
        updated = true;
        updatedDate = LocalDateTime.now();
        if (!save()) return "Failed to save";
        if (wiki.toString().equalsIgnoreCase(old)) return "Nothing changed";
        return "Successfully updated wiki";
    }

    // Load all wikis in wikis folder and builds a nice info message
    public static String loadAll() {
        File f = new File(absolutePath);
        f.mkdirs();
        StringBuilder status = new StringBuilder("LoadAll: ");
        File[] files = f.listFiles();
        if (files == null || files.length == 0) return "No wikis loaded";
        for (File file : files){
            if (file.isFile() && file.getName().endsWith(".json")){
                WikiIndexed newWiki = new WikiIndexed(file, true);
                if (newWiki.wiki != null) {
                    status.append("+")
                            .append(file.getName())
                            .append(" ");
                } else {
                    status.append("-error:")
                            .append(file.getName())
                            .append(" ");
                }
            }
        }
        return status.toString().equalsIgnoreCase("LoadAll: ") ? "No wikis loaded" : status.toString();
    }
}
