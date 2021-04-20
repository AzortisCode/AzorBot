package com.azortis.azorbot;

import com.azortis.azorbot.cocoUtil.CocoBot;
import com.azortis.azorbot.cocoUtil.CocoTimer;
import com.azortis.azorbot.listeners.InlineCommandListener;
import com.azortis.azorbot.util.WikiIndexed;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Collections;
import java.util.Objects;

public class Main extends ListenerAdapter {

    public static int      defaultSearchThreshold = 80;
    public static String   GitBookUser = "DotEnv GitBookUser=";
    public static String   GitBookPass = "DotEnv GitBookPass=";
    public static String   configPath = "config/"; // DotEnv configPath=


    public static void main(String[] args){

        CocoBot.setIntents(Collections.singleton(GatewayIntent.GUILD_MEMBERS));

        CocoBot.login();

        CocoTimer timer = new CocoTimer();

        WikiIndexed.loadAll();

        InlineCommandListener.load();

        timer.stop();

        CocoBot.info(timer.duration("Create Wikis"));

        loadConfig();
    }

    /**
     * Load configuration
     */
    private static void loadConfig(){
        // Load token from env file
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // Get token from env
        defaultSearchThreshold = Integer.parseInt(Objects.requireNonNull(dotenv.get("defaultSearchThreshold")));
        GitBookUser = dotenv.get("GitBookUser");
        GitBookPass = dotenv.get("GitBookPass");
        configPath = dotenv.get("configPath");
    }
}
