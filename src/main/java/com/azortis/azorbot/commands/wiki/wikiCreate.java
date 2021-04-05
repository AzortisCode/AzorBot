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
                "Creates a new wiki (threshold is 1-100 scale integer, indicating % match to search)",
                true,
                "wiki create Name <GitBook URL> <Raw GitHub URL> <Threshold>"
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
            int threshold = Main.defaultSearchThreshold;
            if (Integer.parseInt(args.get(3)) > 0 && Integer.parseInt(args.get(3)) < 100){
                embed.addField("Entered threshold out of bounds [1, 100]", "Using default: " + args.get(3), false);
            } else if (args.size() == 4){
                threshold = Integer.parseInt(args.get(3));
            } else {
                embed.addField("Default threshold for searches", String.valueOf(threshold), false);
            }
            new WikiIndexed(args.get(0), args.get(2), args.get(1), threshold);
            timer.stop();
            embed.setDescription("Created new wiki " + args.get(0) + "\n" +
                    timer.duration("Create Wiki"));
            embed.addField("Loaded wikis: ", WikiIndexed.getLoadedWikis(), false);
        }
        embed.send(true, 60000);
    }
}