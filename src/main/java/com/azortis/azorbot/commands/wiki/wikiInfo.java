package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

// TODO: Replace "wikiInfo" with name of the command

public class wikiInfo extends AzorbotCommand {
    public wikiInfo(){
        super(
                "Info",
                new String[]{"inf", "updated"},
                new String[]{"Admin", "Developer", "Moderator"},
                "Shows information about the wiki",
                true,
                "wiki info Orbis"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed(args.get(0) + " info", e.getMessage());
        if (args.get(0).length() < 4){
            embed.setDescription("Entered wiki name is too short (at least 4 characters)");
            embed.send(true, 15000);
            sendHelp(e.getMessage());
        }
        WikiIndexed.getInfo(args.get(0), embed);
        embed.send(true);
    }
}