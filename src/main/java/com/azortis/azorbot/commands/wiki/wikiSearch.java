package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.ScrollableEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.Collections;
import java.util.List;

public class wikiSearch extends AzorbotCommand {
    public wikiSearch(){
        super(
                "Search",
                new String[]{"src", "s", "find"},
                null,
                "Search something on the wiki",
                true,
                "wiki search Orbis Install"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("{} search results".replace("{}", args.get(0)), e.getMessage());
        if (args.size() == 0){
            embed.setDescription("Please specify the wiki and/or query");
            embed.send(true, 15000);
            sendHelp(e.getMessage());
            return;
        }

        // Process
        process(args, e, embed);
    }


    /**
     * Processes this command
     * @param args Arguments used as query and/or wiki name
     * @param e Guild event
     * @param embed Embed to write into
     */
    public static void process(List<String> args, GuildMessageReceivedEvent e, AzorbotEmbed embed){

        // Check if any wikis are loaded
        if (WikiIndexed.getWikis().size() == 0){
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

            if (wiki == null){
                embed.setDescription("Failed to search for wiki, specified wiki: " + args.get(0) + " can not be found");
                embed.send(true, 15000);
                return;
            } else {
                args = args.subList(1, args.size());
            }
        }

        // Treat as wiki index request if no parameters are specified
        if (args.size() == 0){
            new wikiIndex().handle(Collections.singletonList(wiki.getName()), e);
            return;
        }

        // Store search results for arguments in embed
        List<AzorbotEmbed> pages = wiki.search(args, e.getMessage());

        // Check if only one page (either when one found or when none found so returns error)
        if (pages.size() == 1){
            pages.get(0).send(true, 10000);
        } else {

            // Make another embed which contains some generic info
            embed.setDescription("If you did not find what you were looking for,\n" +
                    "please search for it on the wiki yourself, before contacting Support.");
            embed.addField("Wiki:", "[" + wiki.getName() + "](" + wiki.getDocs() + ")", false);

            // Add it to the pages
            pages.add(embed);

            // Send scrollable
            new ScrollableEmbed(pages, e.getMessage(), true);
        }
    }
}