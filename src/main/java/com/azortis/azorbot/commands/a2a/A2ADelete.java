package com.azortis.azorbot.commands.a2a;

import com.azortis.azorbot.listeners.A2AWatchdog;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class A2ADelete extends AzorbotCommand {
    public A2ADelete(){
        super(
                "Delete",
                new String[]{"del", "-"},
                null,
                "Deletes an A2A definition",
                true,
                "A2A Delete <sentence that closely matches>"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Remove A2A definition", e.getMessage());
        StringBuilder def = new StringBuilder();
        args.forEach(arg -> def.append(arg).append(" "));
        if (A2AWatchdog.removeDefinition(def.toString())){
            embed.setDescription("Removed A2A definition: \n" +
                    "`" + def.toString() + "`");
        } else {
            embed.setDescription("Failed to remove definition: \n" +
                    "`" + def.toString() + "`");
            embed.addField("Definitions are: ", A2AWatchdog.getNiceDefinitions(), false);
        }
        embed.send(true, 5000);
    }
}
