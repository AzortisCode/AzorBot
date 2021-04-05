package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.cocoUtil.CocoCommand;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class WikiThreshold extends CocoCommand {
    public WikiThreshold(){
        super(
                "Threshold",
                new String[]{"th", "bounds", "Alias3"},
                new String[]{"Admin", "Developer"},
                "Sets the search threshold for a wiki",
                true,
                "wiki threshold Orbis 90/get"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        CocoEmbed embed = new CocoEmbed("Set Wiki Threshold", e.getMessage());
        if (args.size() == 0){
            embed.setDescription("You did not specify a wiki name nor a threshold");
            embed.send(true, 10000);
            sendHelp(e.getMessage());
        } else if (args.size() == 1){
            embed.setDescription("You did not specify a threshold or you only specified the threshold");
            WikiIndexed wiki = WikiIndexed.findWiki(args.get(0));
            if (wiki == null){
                embed.addField("Specified wiki does not exist", args.get(0), false);
                embed.addField("Loaded wikis", WikiIndexed.getLoadedWikis(), false);
            } else {
                embed.setTitle("Get Wiki Threshold");
                embed.addField(wiki.getName() + " threshold", "Threshold is `" + wiki.getThreshold() + "%`", false);
            }
            embed.send(true, 10000);
        } else {
            WikiIndexed wiki = WikiIndexed.findWiki(args.get(0));
            if (wiki == null){
                embed.addField("Specified wiki does not exist", args.get(0), false);
                embed.addField("Loaded wikis", WikiIndexed.getLoadedWikis(), false);
                embed.send(true, 10000);
            } else if (args.get(1).equals("get")){
                embed.setTitle("Get Wiki Threshold");
                embed.addField(
                        wiki.getName() + " threshold",
                        "Threshold is `" + wiki.getThreshold() + "%`", false);
                embed.send(true);
            } else if (Integer.parseInt(args.get(1)) > 100 || Integer.parseInt(args.get(1)) < 0){
                embed.addField("Faulty threshold: " + args.get(1), "Threshold is more than 100 or less than 0", false);
                embed.send(true, 10000);
            } else {
                Objects.requireNonNull(WikiIndexed.findWiki(args.get(0))).setThreshold(Integer.parseInt(args.get(1)));
                embed.setDescription("New Threshold is: `" + wiki.getThreshold() + "%`");
                embed.send(true);
            }
        }
    }
}
