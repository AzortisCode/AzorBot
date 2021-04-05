package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;

import java.util.*;

import com.azortis.azorbot.cocoUtil.CocoEmbed;
import com.azortis.azorbot.cocoUtil.CocoFiles;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class A2AWatchdog extends ListenerAdapter {
    private static final int defaultThreshold = 80;
    private static final List<String> defaultDefinitions = Arrays.asList(
            "Can someone please help?",
            "Hey I have a question",
            "I have a question, can anyone help?",
            "Help please",
            "Please help me",
            "Help, I need somebody. Help, not just anybody",
            "Can someone help me with",
            "Do you know",
            "Help me please",
            "Can anyone help me please",
            "Can anyone help me please I have bug",
            "Help",
            "Are any support people on?",
            "Pls help",
            "Any support",
            "Anyone online",
            "Anyone on",
            "Can you help me please",
            "Can you help me",
            "Help me",
            "Hi, could I ask you for information?",
            "Hi, could I ask you for help?",
            "Hello, may I ask you for help?",
            "Hello, may I ask you for some help?",
            "Hello, may I ask you for some information?");

    private static List<String> definitions = new ArrayList<>();
    private static int threshold = 85;
    private static final CocoFiles file = new CocoFiles(Main.configPath + "A2A.txt", true);

    /**
     * Creates a new A2A Watchdog
     */
    public A2AWatchdog(){
        getSetDefinitions();
    }

    /**
     * Adds a definition
     * @param definition the definition to add
     */
    public static void addDefinition(String definition){
        definitions.add(definition);
    }

    /**
     * Sets the threshold
     * @param threshold to threshold
     */
    public static void setThreshold(int threshold){
        A2AWatchdog.threshold = threshold;
        save();
    }

    /**
     * Removes a definition
     * @param definition the definition to remove
     * @return true if removed, false if not found
     */
    public static boolean removeDefinition(String definition){
        ExtractedResult r = FuzzySearch.extractOne(definition, definitions);
        if (r.getScore() < 70) return false;
        definitions.remove(r.getString());
        return true;
    }

    /**
     * Get a nice list of definitions
     * @return A list of definitions (in a single string)
     */
    public static String getNiceDefinitions(){
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < definitions.size(); i++){
            out.append("(").append(i + 1).append(") `").append(definitions.get(i)).append("`").append("\n");
        }
        return out.toString();
    }

    /**
     * Handles incoming
     * @param e message event
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if (e.getMessage().getContentRaw().startsWith(Main.prefix)) return;

        ExtractedResult r = FuzzySearch.extractOne(e.getMessage().getContentDisplay(), definitions);

        // Make sure the bypass character was not added and score was passed
        if (!e.getMessage().getContentRaw().startsWith("#") && r.getScore() >= threshold){
            Main.info("Message: (" + e.getMessage().getContentDisplay() + ")");
            Main.info("flagged as A2A with score: " + r.getScore());
            Main.info("for item: '" + r.getString() + "'");
            constructAndSendA2A(e.getMessage());
        }
    }

    /**
     * Constructs and sends an A2A information message
     * @param msg Message's channel which is sent to
     */
    private static void constructAndSendA2A(Message msg){
        CocoEmbed embed = new CocoEmbed("Please do not ask to ask!", msg);
        embed.setDescription("Please do not ask if you can ask your question, just ask!\n" +
                "We love to help, but if we don't know your question, we cannot help efficiently.");
        embed.addField("Ask To Ask", "[Ask To Ask website](https://dontasktoask.com/)", false);
        embed.addField("Error", "If you believe this should not happen, " +
                "please re-write your message with a `#` in front", false);
        embed.send();
    }

    /**
     * Set the A2A definitions from file if it exists.
     * If it does not, create a new default definitions file
     */
    private static void getSetDefinitions(){

        // Make sure file exists
        if (!file.checkExists(true, false)){

            // If not, create a new default definitions file
            Main.info("Created new definitions file with default definitions");
            List<String> out = new ArrayList<>(Collections.singleton(String.valueOf(defaultThreshold)));
            out.addAll(defaultDefinitions);
            file.write(out);
        }

        // Read the definitions file
        if (!file.read().isEmpty()){
            List<String> in = new ArrayList<>(file.read());
            threshold = Integer.parseInt(in.remove(0));
            definitions = in;
        } else {
            definitions = defaultDefinitions;
        }
    }

    /**
     * Save the definitions to file
     */
    private static void save(){
        List<String> out = new ArrayList<>(Collections.singleton(String.valueOf(threshold)));
        out.addAll(definitions);
        file.write(out);
    }
}
