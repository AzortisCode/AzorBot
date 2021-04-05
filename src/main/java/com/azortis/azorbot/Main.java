package com.azortis.azorbot;

import com.azortis.azorbot.util.ExecutionTimer;
import com.azortis.azorbot.util.WikiIndexed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;

import javax.security.auth.login.LoginException;
import java.net.http.WebSocket;

public class Main extends ListenerAdapter {

    public static final Logger   LOGGER = LoggerFactory.getLogger(WebSocket.Listener.class);
    public static final String   configPath = "config/";
    public static final String   prefix = "!";
    public static final String   botColor = "0x003b6f";
    public static final String   botCompany = "Azortis";
    public static       int      defaultSearchThreshold = 80;
    public static       User     botUser;
    public static       SelfUser botSelfUser;
    public static       String   botName;
    public static       Long     botID;
    public static       String   GitBookUser;
    public static       String   GitBookPass;

    private static final boolean DEBUG = true;
    private static JDA jda;

    public static void main(String[] args){

        ExecutionTimer timer = new ExecutionTimer();

        info("Logging in");

        // Try logging in
        if (!login()) return;

        info("Logged in");

        // Load wikis
        WikiIndexed.loadAll();

        info("Loaded wikis");

        // Stop timer
        timer.stop();

        info("Running Setup took: " + timer.durationLong() + "ms");
    }

    private static boolean login(){

        info("Loading environment file");
        // Load token from env file
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // Get token from env
        info("Loading token");
        String token = dotenv.get("token");
        GitBookUser = dotenv.get("GitBookUser");
        GitBookPass = dotenv.get("GitBookPass");

        // Log into Discord & build JDA
        info("Building JDA");
        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS,GatewayIntent.GUILD_PRESENCES)
                    .disableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(new CommandCenter())
                    .build().awaitReady();
            info("Retrieving JDA info");
            botID = jda.getSelfUser().getIdLong();
            botUser = jda.getUserById(botID);
            botSelfUser = jda.getSelfUser();
            assert botUser != null;
            botName = botUser.getName();

            // Set presence
            info("Setting presence details");
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.watching("over Azortis: `/help`"));

        } catch (LoginException e){
            warn("Failed to load bot. Did you forget to create an environment file?");
            warn("Please create a new `.env` file with as content `token=<token>`");
            warn("Otherwise, please double-check the token in the .env file");
            return false;
        } catch (InterruptedException e){
            warn("Interrupted while logging into JDA!");
            return false;
        }
        return true;
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
