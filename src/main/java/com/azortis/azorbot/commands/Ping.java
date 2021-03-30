package com.azortis.azorbot.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class Ping extends AzorbotCommand {
    // Constructor
    public Ping() {
        super(
                "Ping",
                new String[]{},
                new String[]{}, // Add role name here. Empty: always / 1+: at least one.
                "Pong",
                false, // Weather command needs arguments or not
                ""
                // For an example for a category, see command.Wiki
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e) {
        Main.info("Command ran!");
    }
}
