package com.azortis.azorbot.commands;

import com.azortis.azorbot.commands.wiki.*;
import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
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
                        new wikiGitPath(),
                        new wikiSearch(),
                        new wikiDelete()
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
        AzorbotCommand.info(getName() + " category command");
        AzorbotEmbed embed = new AzorbotEmbed("Wiki search", e.getMessage());
        if (args.size() == 0){
            embed.setDescription("Please specify the wiki and/or query");
            embed.send(true, 15000);
            sendHelp(e.getMessage());
            return;
        }
        AzorbotCommand.info(getName() + " Final. Running Search");
        wikiSearch.process(args, e, embed);
    }
}
