package com.azortis.azorbot.commands.pingwatchdog;

import com.azortis.azorbot.listeners.PingWatchdogListener;
import com.azortis.azorbot.util.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.CocoCommand;

import java.util.List;

public class PingWatchdogList extends CocoCommand {
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
        CocoEmbed embed = new CocoEmbed("PingWatchdog members and roles list:", e.getMessage());
        PingWatchdogListener.getList(embed);

        embed.send(true);
    }
}
