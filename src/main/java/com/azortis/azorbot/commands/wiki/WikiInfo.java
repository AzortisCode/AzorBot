package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.cocoUtil.CocoEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.cocoUtil.CocoCommand;

import java.util.List;

public class WikiInfo extends CocoCommand {
    public WikiInfo(){
        super(
                "Info",
                new String[]{"inf", "updated"},
                new String[]{"Admin", "Developer", "Moderator"},
                "Shows information about the wiki, param is for showing raw JSON",
                true,
                "wiki info Orbis <yes>"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        CocoEmbed embed = new CocoEmbed(args.get(0) + " info", e.getMessage());
        if (args.get(0).length() < 4){
            embed.setDescription("Entered wiki name is too short (at least 4 characters)");
            embed.send(true, 15000);
            sendHelp(e.getMessage());
        }
        WikiIndexed.getInfo(args.get(0), embed, args.size() > 1);
    }
}
