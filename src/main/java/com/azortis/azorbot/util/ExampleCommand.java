package com.azortis.azorbot.util;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class ExampleCommand extends AzorbotCommand {
    public ExampleCommand(){
        super(
                "CommandName",
                new String[]{"CommandAlias1", "Alias2", "Alias3"},
                new String[]{"NeedsRole1", "OrRole2"},
                "CommandDescription",
                true,
                "parent CommandName param1"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        System.out.println(this.getClass().getName().split(":")[this.getClass().getName().length()] + "ran command!");
    }
}
