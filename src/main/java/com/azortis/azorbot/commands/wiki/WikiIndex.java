package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.CocoCommand;
import com.azortis.azorbot.util.CocoEmbed;
import com.azortis.azorbot.util.CocoScrollable;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class WikiIndex extends CocoCommand {
    public WikiIndex(){
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
        CocoEmbed embed = new CocoEmbed("{} wiki index".replace("{}", args.get(0)), e.getMessage());
        CocoScrollable scrollable = new CocoScrollable(
                WikiIndexed.getIndex(args.get(0)),
                embed,
                e.getMessage(),
                true
        );
    }
}
