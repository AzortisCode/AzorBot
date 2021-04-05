package com.azortis.azorbot.commands;

import com.azortis.azorbot.util.CocoCommand;
import com.azortis.azorbot.util.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Links extends CocoCommand {

    public Links(){
        super(
                "Links",
                new String[]{"Link", "URL"},
                "Send a number of useful links"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        CocoEmbed embed = new CocoEmbed("Here you go!", e.getMessage());
        embed.addField(
                "Orbis",
                "[Github](https://github.com/AzortisCode/Orbis)\n" +
                        "[Wiki](https://docs.azortis.com/)\n" +
                        "[Spigot](https://www.spigotmc.org/threads/orbis-dimension-engine.493384/)",
                false
        ).addField(
                "Server Optimization",
                "[YouHaveTrouble's Minecraft Server Optimization Guide](https://github.com/YouHaveTrouble/minecraft-optimization)",
                false
        );
        embed.send(true);
    }
}
