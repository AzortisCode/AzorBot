package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotEmbed;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class WikiGitPath extends AzorbotCommand {
    public WikiGitPath(){
        super(
                "GitPath",
                new String[]{"GP"},
                "Sends help for finding the GitPath"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("GitPath help", e.getMessage());
        embed.setDescription(
                "You can find the raw GitPath as follows:\n" +
                        "0. Go to GitHub and create a new repository for your documentation\n" +
                        "1. Go to GitBook\n" +
                        "2. Turn on GitHub integration under `integrations`\n" +
                        "3. Select a repository and follow the steps\n" +
                        "4. Navigate to the github repository\n" +
                        "5. Navigate into the `SUMMARY.md` file\n" +
                        "6. Click the `raw` button on the top right (next to `blame`, below `history`)\n" +
                        "7. Copy the URL at the top and paste it in the wiki create command\n" +
                        "\n" +
                        "You will also need to paste the webpage for your docs after the command");
        embed.send(true);
    }
}