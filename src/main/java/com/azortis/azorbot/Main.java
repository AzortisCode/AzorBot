package com.azortis.azorbot;

import com.azortis.azorbot.commands.*;
import com.azortis.azorbot.listeners.*;
import com.azortis.azorbot.util.WikiIndexed;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;

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
    public static       String GitBookUser;
    public static       String GitBookPass;

    private static final boolean DEBUG = true;
    public static final String configPath = "config/";

    @Getter
    private static JDA      jda;


    public static void main(String[] args){

        // Try logging in
        if (!login()) return;

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

        // Listeners
        jda.addEventListener(new A2AWatchdog());
        jda.addEventListener(new Prefix());
        jda.addEventListener(new PingWatchdogListener(jda));

        // Commands
        jda.addEventListener(new Ping());
        jda.addEventListener(new Shutdown());
        jda.addEventListener(new Links());
        jda.addEventListener(new Tester());
        jda.addEventListener(new XYProblem());
        jda.addEventListener(new GitBookLogin());

        // Categories
        jda.addEventListener(new A2A());
        jda.addEventListener(new Wiki());
        jda.addEventListener(new PingWatchdog());

        // Add command index help page listener
        // Any commands registered after are NOT displayed in the index
        jda.addEventListener(new Commands(jda));

        // Load wikis
        WikiIndexed.loadAll();
    }

    private static boolean login(){
        // Load token from env file
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // Get token from env
        String token = dotenv.get("token");
        GitBookUser = dotenv.get("GitBookUser");
        GitBookPass = dotenv.get("GitBookPass");

        // Log into Discord & build JDA
        try {
            jda = JDABuilder.createDefault(token).build();
        } catch (LoginException e){
            warn("Failed to load bot. Did you forget to create an environment file?");
            warn("Please create a new `.env` file with as content `token=<token>`");
            warn("Otherwise, please double-check the token in the .env file");
            return false;
        }
        return true;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if (!e.getAuthor().isBot()){
            // Updates configurations
            Main.LOGGER.info(e.getAuthor().getName() + ": " + e.getMessage().getContentDisplay());
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent e){
        LOGGER.info("{} IS FUCKING READY", botName);
    }

    public static void shutdown(){
        LOGGER.warn("{} IS SHUTTING DOWN", botName);
        jda.getPresence().setStatus(OnlineStatus.OFFLINE);
        jda.shutdown();
        System.exit(0);
    }

    public static void warn(String message){
        LOGGER.warn(" > {}", message);
    }

    public static void info(String message){
        LOGGER.info(" > {}", message);
    }

    public static void error(String message){
        LOGGER.error(" > {}", message);
    }

    public static void debug(String message){
        if (DEBUG) LOGGER.debug(" > {}", message);
    }
}
