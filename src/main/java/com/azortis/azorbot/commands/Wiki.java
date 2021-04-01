package com.azortis.azorbot.commands;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.commands.wiki.*;
import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class Wiki extends AzorbotCommand {
    public Wiki(){
        super(
                "Wiki",
                new String[]{"Wikis", "w"},
                "All wiki-related commands",
                new AzorbotCommand[]{
                        new wikiIndex(),
                        new wikiCreate(),
                        new wikiUpdate(),
                        new wikiList(),
                        new wikiInfo(),
                        new wikiGitPath()
                }
        );
    }

    /**
     * Sees if there is only one loaded wiki and uses that for its wiki information.
     * If there are multiple or none loaded, sends the normal help command
     * @param args Uses these arguments to deduct the wiki
     * @param e Uses this to send the message in the right channel
     */
    @Override
    public void categoryCommand(@Nonnull List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Wiki search", e.getMessage());

        // Check if any wikis are loaded
        if (WikiIndexed.getWikis().size() == 0) {
            embed.setDescription("Failed to search for wiki, there are none loaded");
            embed.send(true, 15000);
            return;
        }

        // Store wiki into here
        WikiIndexed wiki;

        if (WikiIndexed.getWikis().size() == 1){

            wiki = WikiIndexed.getWikis().get(0);

            // Only one wiki loaded. Check if the first parameter is the name of the wiki.
            if (wiki.getName().equalsIgnoreCase(args.get(0))){

                // First parameter is wiki, rest is query. Remove first parameter
                args = args.subList(1, args.size());
            }
        } else {
            wiki = WikiIndexed.findWiki(args.get(0));

            if (wiki == null) {
                embed.setDescription("Failed to search for wiki, specified wiki: " + args.get(0) + " can not be found");
                embed.send(true, 15000);
                return;
            } else {
                args = args.subList(1, args.size());
            }
        }

        // Store search results for arguments in embed
        wiki.search(args, embed);

        // Send embed
        embed.send(true, 30000);
    }
}
