package com.azortis.azorbot.commands.pingwatchdog;

import com.azortis.azorbot.listeners.PingWatchdogListener;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class PingWatchdogExcuse extends AzorbotCommand {
    public PingWatchdogExcuse(){
        super(
                "Excuse",
                new String[]{"undo", "fix", "unban"},
                null,
                "Excuses a user who pinged a support role",
                true,
                "PingWatchdog Excuse <@user>"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Excusing user", e.getMessage());
        if (e.getMessage().getMentionedMembers().size() == 0) {
            embed.setDescription("You did not mention a user. Please double-check");
            embed.send(true, 15000);
        } else if (PingWatchdogListener.excuseMember(e.getMessage().getMentionedMembers().get(0))) {
            embed.setDescription("Excused user: " + e.getMessage().getMentionedMembers().get(0).getUser().getName());
            embed.send(true);
        } else {
            embed.setDescription("Tried excusing user: " +
                    e.getMessage().getMentionedMembers().get(0).getUser().getName() +
                    ", but they are not in the list"
            );
            embed.send(true, 15000);
        }
    }
}
