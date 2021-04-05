package com.azortis.azorbot.commands;

import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class TryAndSee extends AzorbotCommand {
    public TryAndSee(){
        super(
                "TryAndSee",
                new String[]{"tas", "tns", "ts"},
                null,
                "Prints the try it and see video"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Try it and see!", e.getMessage());
        embed.send();
        e.getChannel().sendMessage("https://tryitands.ee/tias.mp4").queue();
        e.getMessage().delete().queue();
    }
}
