package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.commands.Wiki;
import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class wikiDelete extends AzorbotCommand {
    public wikiDelete(){
        super(
                "Delete",
                new String[]{"del", "x", "remove"},
                new String[]{"Admin", "Developer"},
                "Deletes a wiki (cannot be undone)",
                true,
                "wiki delete Orbis"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Delete wiki", e.getMessage());
        WikiIndexed wiki = WikiIndexed.findWiki(args.get(0));
        if (wiki == null){
            embed.setDescription("Failed to find wiki, loaded wikis are: `" + WikiIndexed.getLoadedWikis() + "`");
        } else {
            if (wiki.delete()){
                embed.setDescription("Successfully deleted wiki: " + args.get(0));
            } else {
                embed.setDescription("Failed to remove wiki file, successfully removed wiki instance\n" +
                        "Note that the wiki will load on startup again.");
            }
        }
        embed.send(true, 1000);
    }
}