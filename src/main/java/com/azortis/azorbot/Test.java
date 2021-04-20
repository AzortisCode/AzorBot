package com.azortis.azorbot;

import com.azortis.azorbot.cocoUtil.CocoCommand;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import com.azortis.azorbot.cocoUtil.CocoFiles;
import com.azortis.azorbot.cocoUtil.CocoScrollable;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Test extends CocoCommand {
    private static final CocoFiles file = new CocoFiles("config/example.txt");
    private static final CocoFiles file2 = new CocoFiles("config/example2.txt");

    public Test (){
        super(
                "Test",
                new String[]{"tst"},
                null,
                "A test command",
                true,
                "Test stuff"
        );
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        StringBuilder sb = new StringBuilder();
        args.forEach(sb::append);
        CocoEmbed embed = new CocoEmbed("Test scrollable", e.getMessage());
        new CocoScrollable(sb.toString(), embed, e.getMessage(), false);
    }

    public static void main(String[] args){
    }
}
