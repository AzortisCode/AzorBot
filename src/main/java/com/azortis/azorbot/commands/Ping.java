package com.azortis.azorbot.commands;

import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class Ping extends AzorbotCommand {
    // Constructor
    public Ping(){
        super(
                "Ping",
                "Pong"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        new AzorbotEmbed("Pong!", true).send(e.getChannel());
    }
}
