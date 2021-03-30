package com.azortis.azorbot;

import com.azortis.azorbot.commands.Commands;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.http.WebSocket;

public class Main extends ListenerAdapter {

    public static final Logger LOGGER = LoggerFactory.getLogger(WebSocket.Listener.class);
    public static final String prefix = "!";
    public static final String botColor = "0x003b6f";
    public static final String botCompany = "Azortis";
    public static       User   botUser;
    public static       String botName;
    public static       Long   botID;

    private static final String token = "NzQwNTA1OTYyNjg2OTcxOTI1.Xyp_6w.syj-5iYuqswE4MwltVumZAqFeYY";
    private static final boolean DEBUG = true;

    @Getter
    private static JDA      jda;


    public static void main(String[] args) throws LoginException {

        // Log into Discord & build JDA
        jda     = JDABuilder.createDefault(token).build();

        // Save bot info
        botID   = jda.getSelfUser().getIdLong();
        botUser = jda.getUserById(botID);
        assert botUser != null;
        botName = botUser.getName();

        // Set presence
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setActivity(Activity.watching("over Azortis: `/help`"));

        /// Listener Registrar

        // Log incoming messages
        jda.addEventListener(new Main());

        // Add command index help page listener
        // Any commands registered after are NOT displayed in the index
        jda.addEventListener(new Commands(jda));
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            // Updates configurations
            Main.LOGGER.info(e.getAuthor().getName() + ": " + e.getMessage().getContentDisplay());
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent e) {
        LOGGER.info("{} IS FUCKING READY", botName);
    }

    public static void shutdown(){
        LOGGER.warn("{} IS SHUTTING DOWN", botName);
        jda.getPresence().setStatus(OnlineStatus.OFFLINE);
        jda.shutdown();
        System.exit(1);
    }

    public static void warn(String message){
        LOGGER.warn(" \uD83E\uDC17 {}", message);
    }

    public static void info(String message){
        LOGGER.info(" \uD83E\uDC17 {}", message);
    }

    public static void error(String message){
        LOGGER.error("\uD83E\uDC17 {}", message);
    }

    public static void debug(String message){
        if (DEBUG) LOGGER.debug("\uD83E\uDC17 {}", message);
    }
}
