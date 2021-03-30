package com.azortis.azorbot;

import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Tester extends AzorbotCommand {
    public Tester(){
        super(
                "Tester",
                new String[]{"Test", "Testr", "tst", "t", "try"},
                new String[]{"Admin", "Developer"},
                "Testing docker"
        );
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        new AzorbotEmbed("Test", true).send(e.getChannel());
    }
}
