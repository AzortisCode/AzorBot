package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ChatListener implements AzorbotListener {

    @Override
    public void incoming(GuildMessageReceivedEvent e){
        if (!e.getAuthor().isBot()){
            Main.info(e.getAuthor().getName() + ": " + e.getMessage().getContentDisplay());
        }
    }
}
