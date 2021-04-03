package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Prefix extends ListenerAdapter {
    /**
     * Handles incoming
     * @param e message event
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if (e.getMessage().getContentRaw().startsWith(Main.prefix)) return;
        if (e.getMessage().getMentionedUsers().contains(Main.botUser)){
            String Sender = e.getMessage().getAuthor().getName();
            AzorbotEmbed embed = new AzorbotEmbed("ಥ_ಥ", e.getMessage());
            embed
                    .setAuthor("Hello "+ Sender)
                    .setDescription("Everytime you @ me, it hurts... Use my prefix please.")
                    .addField("Here is my prefix", "`" + Main.prefix + "`" , false);

            embed.send(e.getMessage(), true, 1000);
        }
    }
}

