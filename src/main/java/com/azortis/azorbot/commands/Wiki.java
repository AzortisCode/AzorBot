package com.azortis.azorbot.commands;

import com.azortis.azorbot.commands.wiki.*;
import com.azortis.azorbot.cocoUtil.CocoCommand;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class Wiki extends CocoCommand {
    public Wiki(){
        super(
                "Wiki",
                new String[]{"Wikis", "w"},
                "All wiki-related commands",
                new CocoCommand[]{
                        new WikiIndex(),
                        new WikiCreate(),
                        new WikiUpdate(),
                        new WikiList(),
                        new WikiInfo(),
                        new WikiGitPath(),
                        new WikiSearch(),
                        new WikiDelete(),
                        new WikiThreshold()
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
        CocoCommand.info(getName() + " category command");
        CocoEmbed embed = new CocoEmbed("Wiki search", e.getMessage());
        if (args.size() == 0){
            embed.setDescription("Please specify the wiki and/or query");
            embed.send(true, 15000);
            sendHelp(e.getMessage());
            return;
        }
        CocoCommand.info(getName() + " Final. Running Search");
        WikiSearch.process(args, e, embed);
    }
}
