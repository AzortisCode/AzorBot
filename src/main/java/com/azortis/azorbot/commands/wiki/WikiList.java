package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.CocoEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.CocoCommand;

import java.util.List;

public class WikiList extends CocoCommand {
    public WikiList(){
        super(
                "List",
                new String[]{"all", "l", "get"},
                "Lists all wikis"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        CocoEmbed embed = new CocoEmbed("List of wikis", e.getMessage());
        embed.setDescription("Loaded wikis are: `" + WikiIndexed.getLoadedWikis() + "`");
        embed.send(true);
    }
}
