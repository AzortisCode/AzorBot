package com.azortis.azorbot.commands.a2a;

import com.azortis.azorbot.listeners.A2AWatchdog;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class A2AAdd extends AzorbotCommand {
    public A2AAdd(){
        super(
                "Add",
                new String[]{"+", "c"},
                null,
                "Adds a new definition",
                true,
                "A2A Add <sentence to add>"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        StringBuilder def = new StringBuilder();
        args.forEach(arg -> def.append(arg).append(" "));
        A2AWatchdog.addDefinition(def.toString());
        AzorbotEmbed embed = new AzorbotEmbed("Add A2A definition", e.getMessage());
        embed.setDescription("New A2A definition: \n" +
                "`" + def.toString() + "`");
        embed.send(true, 5000);
    }
}
