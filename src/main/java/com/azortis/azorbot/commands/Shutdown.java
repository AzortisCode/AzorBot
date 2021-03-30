package com.azortis.azorbot.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

// TODO: Replace "CommandName" with name of the command
public class Shutdown extends AzorbotCommand {
    // Constructor
    public Shutdown() {
        super(
                "Shutdown",
                new String[]{"x", "sd", "kill", "stop", "exit", "die"},
                new String[]{"Admin", "Developer"}, // Add role name here. Empty: always / 1+: at least one.
                "Shuts down the bot"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e) {
        Main.info("Command ran!");
    }
}