package com.azortis.azorbot.commands;

import com.azortis.azorbot.cocoUtil.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.cocoUtil.CocoCommand;

import java.util.List;

public class Ping extends CocoCommand {
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
        new CocoEmbed("Pong!", e.getMessage(), true).send();
    }
}
