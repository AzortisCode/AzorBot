package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class wikiIndex extends AzorbotCommand {
    public wikiIndex(){
        super(
                "Index",
                new String[]{"ind", "i", "list"},
                null,
                "Prints the index of the entered wiki",
                true,
                "wiki index Orbis"
        );
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("{} wiki index".replace("{}", args.get(0)), e.getMessage());
        WikiIndexed.getIndex(args.get(0), embed);
        embed.send();
    }
}
