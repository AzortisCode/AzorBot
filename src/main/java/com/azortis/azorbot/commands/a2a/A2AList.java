package com.azortis.azorbot.commands.a2a;

import com.azortis.azorbot.listeners.A2AWatchdog;
import com.azortis.azorbot.util.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.CocoCommand;

import java.util.List;

public class A2AList extends CocoCommand {
    public A2AList(){
        super(
                "List",
                new String[]{"l", "get"},
                null,
                "Gets a list of definitions",
                false
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        CocoEmbed embed = new CocoEmbed("List of A2A definitions: ", e.getMessage());
        embed.setDescription(A2AWatchdog.getNiceDefinitions());
        embed.send(true);
    }
}