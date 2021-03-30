package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;

import java.util.*;

import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.FileManager;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class A2AWatchdog extends ListenerAdapter {
    private static final int defaultThreshold = 30;
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

    private static List<String> definitions;
    private static int threshold;
    private static final FileManager file = new FileManager("A2A-Definitions.txt");

    /**
     * Creates a new A2A Watchdog
     */
    public A2AWatchdog() {
        getSetDefinitions();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        ExtractedResult r = FuzzySearch.extractOne(e.getMessage().getContentDisplay(), definitions);

        // Make sure the bypass character was not added and score was passed
        if (!e.getMessage().getContentRaw().startsWith("#") && r.getScore() >= threshold){
            constructAndSendA2A(e.getMessage());
        }
    }

    /**
     * Constructs and sends an A2A information message
     * @param msg
     */
    private void constructAndSendA2A(Message msg){
        AzorbotEmbed embed = new AzorbotEmbed("Please do not ask to ask!", msg);
        embed.setDescription("Please do not ask if you can ask your question, just ask!\n" +
                "We love to help, but if we don't know your question, we cannot help efficiently.");
        embed.addField("Ask To Ask", "[Ask To Ask website](https://dontasktoask.com/)", false);
        embed.send();
    }

    /**
     * Set the A2A definitions from file if it exists.
     * If it does not, create a new default definitions file
     */
    private void getSetDefinitions(){

        // Make sure file exists
        if (!file.checkExists(true, false)) {

            // If not, create a new default definitions file
            Main.info("Created new definitions file with default definitions");
            List<String> out = new ArrayList<>(Collections.singleton(String.valueOf(defaultThreshold)));
            out.addAll(defaultDefinitions);
            file.write(out);
        }

        // Read the definitions file
        List<String> in = new ArrayList<>(file.read());
        threshold = Integer.parseInt(in.remove(0));
        definitions = in;
    }
}
