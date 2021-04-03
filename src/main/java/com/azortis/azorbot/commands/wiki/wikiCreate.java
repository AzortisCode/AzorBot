package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.ExecutionTimer;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class wikiCreate extends AzorbotCommand {
    public wikiCreate(){
        super(
                "Create",
                new String[]{"c", "+"},
                null,
                "Creates a new wiki",
                true,
                "wiki create Name <GitBook URL> <Raw GitHub URL>"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Wiki creation", e.getMessage());
        if (args.size() == 1){
            embed.setDescription("You forgot to specify the raw GitPath and Docs page");
            sendHelp(e.getMessage());
        } else if (args.size() == 2){
            embed.setDescription("You forgot to specify the Docs page");
            sendHelp(e.getMessage());
        } else if (args.get(0).length() < 4){
            embed.setDescription("The specified wiki name is too short (min 4 characters)");
        } else if (!args.get(2).contains("https://raw.githubusercontent.com/")){
            embed.setDescription("Your GitPath does not contain the required main path:\n`" + args.get(1) + "`");
            embed.addField("GitPath Help", "You can request GitPath help with `" + Main.prefix + "wiki GitPath`", false);
        } else {
            ExecutionTimer timer = new ExecutionTimer();
            new WikiIndexed(args.get(0), args.get(2), args.get(1));
            timer.stop();
            embed.setDescription("Created new wiki " + args.get(0) + "\n" +
                    timer.duration("Create Wiki"));
            embed.addField("Loaded wikis: ", WikiIndexed.getLoadedWikis(), false);
        }
        embed.send(true, 60000);
    }
}