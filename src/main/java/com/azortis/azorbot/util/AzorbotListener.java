package com.azortis.azorbot.util;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public interface AzorbotListener {
    default void incoming(GuildMessageReceivedEvent e){}
    default void incomingEmoji(GuildMessageReactionAddEvent e){}
}
