package com.azortis.azorbot.commands;

import com.azortis.azorbot.cocoUtil.CocoBot;
import com.azortis.azorbot.cocoUtil.CocoCommand;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Shutdown extends CocoCommand {
    // Constructor
    public Shutdown(){
        super(
                "Shutdown",
                new String[]{"x", "sd", "kill", "stop", "exit", "die"},
                new String[]{"Admin", "Developer"}, // Add role name here. Empty: always / 1+: at least one.
                "Shuts down the bot"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        new CocoEmbed("Shutting down", e.getMessage()).send(true);
        CocoBot.shutdown();
    }
}