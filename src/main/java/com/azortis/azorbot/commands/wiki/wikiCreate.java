package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.entities.Message;
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
                "wiki create Orbis https://raw.githubusercontent.com/AzortisCode/OrbisDocumentation/master/SUMMARY.md"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Wiki creation", e.getMessage());
        if (args.size() == 1){
            embed.setDescription("You forgot to specify the raw GitPath");
            sendHelp(e.getMessage());
        } else if (args.get(0).length() < 4) {
            embed.setDescription("The specified wiki name is too short (min 4 characters)");
        } else if (!args.get(1).contains("https://raw.githubusercontent.com/")) {
            embed.setDescription("Your GitPath does not contain the required main path:\n`" + args.get(1) + "`");

            help(e.getMessage());
        } else {
            embed.setDescription("Created new wiki " + args.get(0));
            embed.addField("Loaded wikis: ", WikiIndexed.getWikis().toString(), false);
            new WikiIndexed(args.get(0), args.get(1));
        }
        embed.send(true, 60000);
    }

    @Override
    public void sendHelp(Message m){
        super.sendHelp(m);
        help(m);
    }

    /**
     * Sends a GitPath tutorial
     * @param m This message's channel the embed is sent to
     */
    private void help(Message m){
        AzorbotEmbed embed = new AzorbotEmbed("GitPath help", m);
        embed.setDescription(
                "You can find the raw GitPath as follows:\n" +
                "0. Go to GitHub and create a new repository for your documentation\n" +
                "1. Go to GitBook\n" +
                "2. Turn on GitHub integration under `integrations`\n" +
                "3. Select a repository and follow the steps\n" +
                "4. Navigate to the github repository\n" +
                "5. Navigate into the `SUMMARY.md` file\n" +
                "6. Click the `raw` button on the top right (next to `blame`, below `history`)\n" +
                "7. Copy the URL at the top and paste it in the command here");
        embed.send();
    }
}