package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class wikiUpdate extends AzorbotCommand {
    public wikiUpdate(){
        super(
                "Update",
                new String[]{"upd", "up", "reload"},
                null,
                "Updates the specified wiki",
                true,
                "wiki update Orbis"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Wiki update message", e.getMessage());
        if (WikiIndexed.update(args.get(0))){
            embed.setDescription("Successfully updated {} wiki".replace("{}", args.get(0)));
        } else {
            embed.setDescription("Failed to update {} wiki.\n".replace("{}", args.get(0)) +
                    "Existing wikis are: `" + WikiIndexed.getWikis().toString() + "`\n\n" +
                    "Did you perhaps want to create a new one?\n" +
                    "Here is the command help for creating a new wiki:");
            new wikiCreate().sendHelp(e.getMessage());
        }
    }
}