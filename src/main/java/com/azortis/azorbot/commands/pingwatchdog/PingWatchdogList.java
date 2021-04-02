package com.azortis.azorbot.commands.pingwatchdog;

import com.azortis.azorbot.listeners.PingWatchdogListener;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

public class PingWatchdogList extends AzorbotCommand {
    public PingWatchdogList(){
        super(
                "List",
                new String[]{"l", "get"},
                null,
                "Lists ping watchdog configuration",
                false
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("PingWatchdog members and roles list:", e.getMessage());
        PingWatchdogListener.getList(embed);

        embed.send(true);
    }
}
