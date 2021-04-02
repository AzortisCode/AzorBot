package com.azortis.azorbot.commands;

import com.azortis.azorbot.commands.pingwatchdog.*;
import com.azortis.azorbot.util.AzorbotCommand;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class PingWatchdog extends AzorbotCommand {
    // Constructor
    public PingWatchdog(){
        super(
                "PingWatchdog",
                new String[]{"pingW", "pw"},
                new String[]{"Admin", "Developer", "Moderator"}, // Add role name here. Empty: always / 1+: at least one.
                "PingWatchdog command category",
                new AzorbotCommand[]{
                        new PingWatchdogDelete(),
                        new PingWatchdogAdd(),
                        new PingWatchdogList()
                }
        );
    }
}