package com.azortis.azorbot.listeners;

import com.azortis.azorbot.cocoUtil.CocoBot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if (!e.getAuthor().isBot()){
            CocoBot.info(e.getAuthor().getName() + ": " + (e.getMessage().getContentDisplay().length()>=40 ? (e.getMessage().getContentDisplay().substring(0, 40) + " (...)") : (e.getMessage().getContentDisplay())));
        }
    }
}
