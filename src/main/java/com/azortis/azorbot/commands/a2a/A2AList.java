package com.azortis.azorbot.commands.a2a;

import com.azortis.azorbot.listeners.A2AWatchdog;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

// TODO: Replace "A2AList" with name of the command

public class A2AList extends AzorbotCommand {
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
        AzorbotEmbed embed = new AzorbotEmbed("List of A2A definitions: ", e.getMessage());
        embed.setDescription(A2AWatchdog.getNiceDefinitions());
        embed.send(true);
    }
}