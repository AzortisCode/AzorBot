package com.azortis.azorbot;

import com.azortis.azorbot.commands.*;
import com.azortis.azorbot.listeners.A2AWatchdog;
import com.azortis.azorbot.listeners.ChatListener;
import com.azortis.azorbot.listeners.PingWatchdogListener;
import com.azortis.azorbot.listeners.Prefix;
import com.azortis.azorbot.util.AzorbotCommand;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.*;


public class CommandCenter extends ListenerAdapter {
    @Getter
    private static final List<AzorbotCommand> commands = Arrays.asList(
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
            new Commands()
    );
    private static final List<ListenerAdapter> listeners = Arrays.asList(
            new A2AWatchdog(),
            new PingWatchdogListener(),
            new Prefix(),
            new ChatListener()
    );
    private static final List<ListenerAdapter> emojiListeners = new ArrayList<>();

    /**
     * Creates a command center
     */
    public CommandCenter(){
        Main.info("Building Command Center");
    }

    /**
     * Adds an emoji listener
     * @param listener the listener to add
     */
    public static void addEmojiListener(ListenerAdapter listener) {
        Main.info("Adding emoji listener: " + listener.getClass().toString());
        emojiListeners.add(listener);
    }

    /**
     * Removes an emoji listener
     * @param listener the listener to remove
     */
    public static void removeEmojiListener(ListenerAdapter listener) {
        emojiListeners.remove(listener);
    }

    /**
     * Handles message/command listeners, then checks if prefix and not bot, then passes on to commands.
     * @param e The guild message event that needs to be processed
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){

        // Prevent bot user
        if (e.getAuthor().isBot()) return;

        // Pass to all listeners
        listeners.forEach(listener -> listener.onGuildMessageReceived(e));

        // Check prefix
        if (!e.getMessage().getContentRaw().startsWith(Main.prefix)) return;

        // Send to all commands
        commands.forEach(cmd -> cmd.onGuildMessageReceived(e));
    }

    /**
     * Handles emoji listeners
     * @param e Emoji event
     */
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent e) {
        if (Objects.requireNonNull(e.getUser()).isBot()) return;
        emojiListeners.forEach(listener -> listener.onGuildMessageReactionAdd(e));
    }

    /**
     * Handles emoji listeners
     * @param e Emoji event
     */
    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent e){
        if (Objects.requireNonNull(e.getUser()).isBot()) return;
        emojiListeners.forEach(listener -> listener.onGuildMessageReactionRemove(e));
    }
}