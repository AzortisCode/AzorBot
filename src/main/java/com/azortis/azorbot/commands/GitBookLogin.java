package com.azortis.azorbot.commands;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.cocoUtil.CocoCommand;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class GitBookLogin extends CocoCommand {
    public GitBookLogin(){
        super(
                "GitBook Login",
                new String[]{"GBL", "GitBook", "WikiLogin"},
                new String[]{"Admin", "Developer", "Moderator"},
                "Sends GitBook Login information in DMs"
        );
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        CocoEmbed embed = new CocoEmbed("GitBook login details", e.getMessage());
        embed.setDescription("Username: `" + Main.GitBookUser + "`\n" +
                "Password: `" + Main.GitBookPass + "`");
        Objects.requireNonNull(e.getMember()).getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage(embed.build())).queue();
        new CocoEmbed("Sending login to DMS :)", e.getMessage()).send(true);
    }
}
