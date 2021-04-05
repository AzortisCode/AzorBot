package com.azortis.azorbot.cocoUtil;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.http.WebSocket;
import java.util.Collection;
import java.util.Objects;


public class CocoBot {
    private static String token = null; // DotEnv token=

    @Getter
    @Setter
    private static String prefix = "!"; // DotEnv prefix=
    @Getter
    @Setter
    private static boolean debug = false; // DotEnv debug=true/false
    @Getter
    @Setter
    private static Activity activity = null;
    @Getter
    @Setter
    private static OnlineStatus status = null;
    @Getter
    @Setter
    private static Collection<GatewayIntent> intents = null;
    @Getter
    @Setter
    private static Collection<CacheFlag> cFlags = null;

    @Getter
    private static JDA jda = null;
    @Getter
    private static SelfUser botSelfUser;
    @Getter
    private static String botName;
    @Getter
    private static User botUser;
    @Getter
    private static long botID;
    @Getter
    private static String botColor = "0x003b6f";
    @Getter
    private static String botCompany = "DotEnv: CompanyName=";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocket.Listener.class);
    private static final CocoCommandCenter COMMAND_CENTER = new CocoCommandCenter();

    /**
     * Login sequence
     * @return True if successful, false if not
     */
    public static void login(){

        // Start timer
        CocoTimer timer = new CocoTimer();

        log("Starting login sequence");

        // Load the token from env
        loadToken();

        // Build and log into JDA
        if (!buildJDA()) {
            log("Failed to login");
            return;
        }

        // Load own info
        if (!loadInfo()) {
            log("Failed to load info from bot. User not instantiated.");
            return;
        }

        // Set presence
        setPresence();

        log("Finished login sequence. Ready for commands :)");

        // Stop timer
        timer.stop();

        log(timer.duration("Login Sequence"));
        log("Further commands may be ran outside the framework.");
    }

    /**
     * Loads the token
     */
    private static void loadToken(){
        // Load token from env file
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // Get token from env
        token = dotenv.get("token");
        prefix = dotenv.get("prefix");
        botColor = dotenv.get("botColor");
        botCompany = dotenv.get("botCompany");
        String deb = dotenv.get("debug");
        if (deb != null && deb.equals("true")){
            debug = true;
        }
    }

    /**
     * Tries buildign a JDA from preconfigured settings
     * <p>Uses intents and cFlags</p>
     * @return true if successful, false if failed.
     */
    private static boolean buildJDA(){

        // Create builder from token
        JDABuilder builder = JDABuilder.createDefault(token);

        // Enable intents and flags if set
        if (intents != null) builder.enableIntents(intents);
        if (cFlags != null) builder.disableCache(cFlags);

        // Add command center to listeners
        builder.addEventListeners(COMMAND_CENTER);

        // Try logging in, and wait for ready.
        try {
            jda = builder.build().awaitReady();
        } catch (LoginException e) {
            log("Error while logging into bot. Please double-check your token");
            return false;
        } catch (InterruptedException e) {
            log("Interrupted while waiting on login. Please check server stability");
            return false;
        }

        // Return success
        return true;
    }

    /**
     * Load information for this bot
     * @return True if botUser exists, false if not.
     */
    private static boolean loadInfo() {
        botID = jda.getSelfUser().getIdLong();
        botUser = jda.getUserById(botID);
        botSelfUser = jda.getSelfUser();
        if (botUser == null) return false;
        botName = botUser.getName();
        return true;
    }

    /**
     * Sets bot presence
     */
    private static void setPresence() {
        jda.getPresence().setStatus(Objects.requireNonNullElse(status, OnlineStatus.ONLINE));
        jda.getPresence().setActivity(Objects.requireNonNullElseGet(activity, () -> Activity.listening(" to " + prefix)));
    }

    /**
     * Shuts down the bot
     */
    public static void shutdown(){
        log("Shutting down");
        jda.getPresence().setStatus(OnlineStatus.OFFLINE);
        jda.shutdown();
        System.exit(0);
    }

    /**
     * Sends a warning to console
     * @param message The warning to send
     */
    public static void warn(String message){
        LOGGER.warn(" > {}", message);
    }

    /**
     * Sends info to console
     * @param message The info to send
     */
    public static void info(String message){
        LOGGER.info(" > {}", message);
    }

    /**
     * Sends an error to console
     * @param message The error to send
     */
    public static void error(String message){
        LOGGER.error(" > {}", message);
    }

    /**
     * Sends a debug message to console
     * @param message The debug message to send
     */
    public static void debug(String message){
        if (debug) LOGGER.info(" > {}", message);
    }

    /**
     * Send raw message (overwrite prefixes)
     * @param message Message <p>({} replaced with below; object)</p>
     * @param object object to input into message
     */
    public static void raw(String message, Object object) {
        LOGGER.info(message, object);
    }

    /**
     * Logs used in bot startup sequence
     * @param message Log to send
     */
    private static void log(String message){
        LOGGER.info(" BOT: {}", message);
    }

}
