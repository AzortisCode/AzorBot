package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.cocoUtil.CocoBot;
import com.azortis.azorbot.cocoUtil.CocoCommand;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import com.azortis.azorbot.cocoUtil.CocoScrollable;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class WikiIndex extends CocoCommand {
    public WikiIndex(){
        super(
                "Index",
                new String[]{"ind", "i"},
                null,
                "Prints the index of the entered wiki",
                true,
                "wiki index Orbis"
        );
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        CocoEmbed embed = new CocoEmbed("{} wiki index".replace("{}", args.get(0)), e.getMessage());
        int randNum = new Random().nextInt();
        embed.addField("Faulty link?", "Ask an Admin to read the console log at `" + randNum + "`", false);
        CocoBot.info(randNum + ": Faulty link? Fix the 'slug' of a page by clicking the tree dots on gitbook.");
        CocoBot.info("Spaces are -'s. Caps don't matter.");
        new CocoScrollable(
                WikiIndexed.getIndex(args.get(0)),
                embed,
                e.getMessage(),
                true
        );
    }
}
