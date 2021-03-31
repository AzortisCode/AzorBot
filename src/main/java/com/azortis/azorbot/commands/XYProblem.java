package com.azortis.azorbot.commands;

import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class XYProblem extends AzorbotCommand {
    public XYProblem(){
        super(
                "XYProblem",
                new String[]{"xy", "xyp"},
                "Prints a help menu for the xy problem (asking about your solution, rather than your actual problem)"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("XY-Problem", e.getMessage());
        embed.setDescription("Please ask about your problem, rather than about your solution.\n" +
                "[XY-Problem](https://xyproblem.info/)");
        embed.send();
    }
}