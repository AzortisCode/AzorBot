package com.azortis.azorbot.listeners;

import com.azortis.azorbot.cocoUtil.CocoBot;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Prefix extends ListenerAdapter {
    /**
     * Handles incoming
     * @param e message event
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if (e.getMessage().getContentRaw().startsWith(CocoBot.getPrefix())) return;
        if (e.getMessage().getMentionedUsers().contains(CocoBot.getBotUser())){
            String Sender = e.getMessage().getAuthor().getName();
            CocoEmbed embed = new CocoEmbed("ಥ_ಥ", e.getMessage());
            embed
                    .setAuthor("Hello "+ Sender)
                    .setDescription("Everytime you @ me, it hurts... Use my prefix please.")
                    .addField("Here is my prefix", "`" + CocoBot.getPrefix() + "`" , false);

            embed.send(e.getMessage(), true, 1000);
        }
    }
}

