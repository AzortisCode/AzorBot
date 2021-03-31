package com.azortis.azorbot.commands;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class GitBookLogin extends AzorbotCommand {
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
        AzorbotEmbed embed = new AzorbotEmbed("GitBook login details");
        embed.setDescription("Username: `" + Main.GitBookUser + "`\n" +
                "Password: `" + Main.GitBookPass + "`");
        Objects.requireNonNull(e.getMember()).getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage(embed.build())).queue();
    }
}
