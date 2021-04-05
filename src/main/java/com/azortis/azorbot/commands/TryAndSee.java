package com.azortis.azorbot.commands;

import com.azortis.azorbot.cocoUtil.CocoCommand;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class TryAndSee extends CocoCommand {
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
        CocoEmbed embed = new CocoEmbed("Try it and see!", e.getMessage());
        embed.send();
        e.getChannel().sendMessage("https://tryitands.ee/tias.mp4").queue();
        e.getMessage().delete().queue();
    }
}
