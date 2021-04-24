package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.cocoUtil.CocoBot;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import com.azortis.azorbot.cocoUtil.CocoFiles;
import com.azortis.azorbot.cocoUtil.CocoText;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONArray;

import java.util.*;

public class InlineCommandListener extends ListenerAdapter {

    // This file contains entries such as description:wikiURL:cmd:alias:alias:...
    // Commands are only the MAIN part of the command, a unique identifier preferably
    // Every line in the file contains such an entry
    private static final CocoFiles file = new CocoFiles(Main.configPath + "inlineCommands.txt", true);

    // Search by command. Aliases have same output.
    // Output is string array with 2 elements: description and wiki link URL
    private static final Map<String, String[]> commands = new HashMap<>();

    private static Set<String> regCmd;

    private static final String divider = "~";

    public InlineCommandListener(){}

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if (e.getAuthor().isBot()) return;
        List<String> found = new ArrayList<>();
        Arrays.asList(e.getMessage().getContentDisplay().split(" ")).forEach(word -> {
            if (regCmd.contains(word)) {
                found.add(word);
            }
        });
        if (found.size() > 0){
            CocoEmbed embed = new CocoEmbed("Commands in your message:", e.getMessage());
            embed.setDescription("Found " + found.size() + " commands.");
            found.forEach(cmd -> embed.addField(CocoText.capitalize(cmd), "[link](" + commands.get(cmd)[1] + ")\n```" +
                    commands.get(cmd)[0] + "```", false));
            embed.send(false);
        }
    }

    /**
     * Loads the configuration
     */
    public static void load(){

        // Get file
        List<String> content = file.read();

        // Make sure there is content
        if (content.size() == 0 || content.get(0).isBlank()){
            CocoBot.error("No command definitions in the file for Inline Commands");
            return;
        }

        // Process rest of the commands
        content.stream().filter(i -> !i.isBlank() && i.split(divider).length >= 3).forEach(cmd -> {

            CocoBot.info("Command for listening: " + cmd);

            // Retrieve entries
            String[] entry = cmd.split(divider);
            String description = entry[0];
            String link        = entry[1];
            for (int i = 2; i < entry.length; i++) {
                System.out.println(entry[i] + " " + description + " " + link);
            }

        });

        // Update registry
        updateReg();
    }

    /**
     * Saves the current set of commands to file.
     * <p>Note: This does not store commands with the same description & link like you can do with the input</p>
     */
    private static void save(){
        List<String> out = new ArrayList<>();
        regCmd.forEach(cmd -> out.add(commands.get(cmd)[0] + divider + commands.get(cmd)[1] + divider + cmd));
        file.write(out);
    }

    /**
     * Updates the command registrar
     */
    private static void updateReg(){
        regCmd = commands.keySet();
    }

    /**
     * Loads the commands from a wiki page,
     * <p>cross-references it with the currently loaded set in the file, and</p>
     * <p>adds missing entries</p>
     * @param page The page to look into for commands
     * @param docsURL The URL to this docs
     */
    public static void loadFromWiki(JSONArray page, String docsURL){
        CocoBot.info(page.toString());
        String current = "none";
        StringBuilder description = new StringBuilder();
        String link = null;
        String[] commands;
        for (Object o : page) {
            if (!(o instanceof String)){
                continue;
            }
            String s = (String) o;
            if (current.equals("example")){
                description.append("\n`").append(s).append("`");
            }
            if (s.startsWith("#") && s.replace("#", "").stripLeading().startsWith("/")){
                if (!description.toString().equals("")){
                    CocoBot.info("Actual command processed: " + link + " / " + description + " / " + s);
                }
                current = "command";
                link = docsURL + "commands#" + s.replace("\\(", "-")
                        .replace(", ", "-").replace(",", "-")
                        .replace(" +", "").replace(" -", "")
                        .replace(" ", "-".replace("\\)", "-"));
                CocoBot.info("Command found: " + s);
                CocoBot.info("Guessed link: " + link);
            } else if (s.startsWith("```")){
                current = "example";
            }
        }
    }
}
