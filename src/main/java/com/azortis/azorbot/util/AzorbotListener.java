package com.azortis.azorbot.util;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface AzorbotListener {
    public default void incoming(GuildMessageReceivedEvent e){}
}
