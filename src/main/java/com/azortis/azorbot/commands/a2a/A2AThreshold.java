package com.azortis.azorbot.commands.a2a;

import com.azortis.azorbot.listeners.A2AWatchdog;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class A2AThreshold extends AzorbotCommand {
    public A2AThreshold(){
        super(
                "Threshold",
                new String[]{"limit", "th"},
                null,
                "Sets the threshold for detection",
                true,
                "A2A Threshold <number>"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Set A2A Threshold", e.getMessage());
        embed.setDescription("New Threshold is: `" + args.get(0) + "`%");
        embed.send(true, 1000);
        A2AWatchdog.setThreshold(Integer.parseInt(args.get(0)));
    }
}
