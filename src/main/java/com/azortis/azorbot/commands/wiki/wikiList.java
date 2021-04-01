package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class wikiList extends AzorbotCommand {
    public wikiList(){
        super(
                "List",
                new String[]{"all", "l", "get"},
                "Lists all wikis"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("List of wikis", e.getMessage());
        embed.setDescription("Loaded wikis are: `" + WikiIndexed.getLoadedWikis() + "`");
        embed.send(true);
    }
}
