package com.azortis.azorbot;

import com.azortis.azorbot.commands.*;
import com.azortis.azorbot.listeners.A2AWatchdog;
import com.azortis.azorbot.listeners.ChatListener;
import com.azortis.azorbot.listeners.PingWatchdogListener;
import com.azortis.azorbot.listeners.Prefix;
import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;


public class CommandCenter extends ListenerAdapter {
    @Getter
    private final List<AzorbotCommand> commands = Arrays.asList(
            // Commands
            new Ping(),
            new Shutdown(),
            new Links(),
            new Tester(),
            new XYProblem(),
            new GitBookLogin(),

            // Categories
            new A2A(),
            new Wiki(),
            new PingWatchdog(),

            // Command command
            new Commands(this)
    );
    private final List<AzorbotListener> listeners = Arrays.asList(
            new A2AWatchdog(),
            new PingWatchdogListener(),
            new Prefix(),
            new ChatListener()
    );

    /**
     * Creates a command center
     */
    public CommandCenter(){
        Main.info("Building Command Center");
    }

    /**
     * Handles listeners, then checks if prefix and not bot, then passes on to commands.
     * @param e The guild message event that needs to be processed
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){

        // Pass to all listeners
        listeners.forEach(listener -> listener.incoming(e));

        // Check prefix
        if (!e.getMessage().getContentRaw().startsWith(Main.prefix)) return;

        // Prevent bot user
        if (e.getAuthor().isBot()) return;

        // Send to all commands
        commands.forEach(cmd -> cmd.incoming(e));
    }
}
