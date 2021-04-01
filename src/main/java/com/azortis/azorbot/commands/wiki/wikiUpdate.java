package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.ExecutionTimer;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class wikiUpdate extends AzorbotCommand {
    public wikiUpdate(){
        super(
                "Update",
                new String[]{"upd", "up", "u", "reload"},
                null,
                "Updates the specified wiki",
                true,
                "wiki update Orbis"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        ExecutionTimer timer = new ExecutionTimer();
        AzorbotEmbed embed = new AzorbotEmbed("Wiki update message", e.getMessage());
        String status = WikiIndexed.update(args.get(0));
        timer.stop();
        if (status.equalsIgnoreCase("yes")){
            embed.setDescription("Successfully updated {} wiki".replace("{}", args.get(0)) + "\n" +
                    timer.duration("update"));
            embed.send(true);
        } else if (status.equalsIgnoreCase("no")) {
            embed.setDescription("Attempted updating {} wiki".replace("{}", args.get(0)) + "\n" +
                    "But found no new changes." + "\n" +
                    timer.duration("update"));
            embed.send(true);
        } else if (status.equalsIgnoreCase("null")) {
            StringBuilder loadedWikis = new StringBuilder();
            WikiIndexed.getWikis().forEach(wiki -> loadedWikis.append(wiki.getName()).append(" "));
            embed.setDescription("Failed to update {} wiki.\n".replace("{}", args.get(0)) +
                    "Existing wikis are: `" + loadedWikis.toString().strip() + "`\n\n" +
                    "Did you perhaps want to create a new one?\n" +
                    "Here is the command help for creating a new wiki:");
            embed.send(true, 15000);
            new wikiCreate().sendHelp(e.getMessage());
        } else {
            embed.setDescription("Unknown return from wiki creation: " + status + "\n" +
                    timer.duration("update"));
            embed.send(true, 60000);
        }
    }
}