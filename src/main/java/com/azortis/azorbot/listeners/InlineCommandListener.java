package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.cocoUtil.CocoBot;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import com.azortis.azorbot.cocoUtil.CocoFiles;
import com.azortis.azorbot.cocoUtil.CocoText;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;

import java.util.*;

public class InlineCommandListener extends ListenerAdapter {

    // This file contains entries such as description:wikiURL:cmd:alias:alias:...
    // Commands are only the MAIN part of the command, a unique identifier preferably
    // Every line in the file contains such an entry
    private static final CocoFiles file = new CocoFiles(Main.configPath + "inlineCommands.txt", true);

    // Search by command. Aliases have same output.
    // Output is string array with 2 elements: description and wiki link URL
    private static final Map<String, String[]> commands = new HashMap<>();
    private static final Logger LOGGER = CocoBot.getLOGGER();

    private static Set<String> regCmd;

    private static final String divider = "~";

    public InlineCommandListener(){
        load();
    }

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
    private void load(){

        // Get file
        List<String> content = file.read();

        // Make sure there is content
        if (content.size() == 0 || content.get(0).isBlank()){
            CocoBot.error("No command definitions in the file for Inline Commands");
            return;
        }

        // Process rest of the commands
        content.stream().filter(i -> !i.isBlank() && i.split(divider).length >= 3).forEach(cmd -> {

            LOGGER.info("Command for listening: " + cmd);

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
     * Updates the command registrar
     */
    private void updateReg(){
        regCmd = commands.keySet();
    }
}
