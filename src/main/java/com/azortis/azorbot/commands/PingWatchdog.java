package com.azortis.azorbot.commands;

import com.azortis.azorbot.commands.pingwatchdog.*;
import com.azortis.azorbot.util.CocoCommand;

public class PingWatchdog extends CocoCommand {
    // Constructor
    public PingWatchdog(){
        super(
                "PingWatchdog",
                new String[]{"pingW", "pw"},
                new String[]{"Admin", "Developer", "Moderator"}, // Add role name here. Empty: always / 1+: at least one.
                "PingWatchdog command category",
                new CocoCommand[]{
                        new PingWatchdogDelete(),
                        new PingWatchdogAdd(),
                        new PingWatchdogList(),
                        new PingWatchdogExcuse()
                }
        );
    }
}