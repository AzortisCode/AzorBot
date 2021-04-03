package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if (!e.getAuthor().isBot()){
            Main.info(e.getAuthor().getName() + ": " + e.getMessage().getContentDisplay());
        }
    }
}
