package com.azortis.azorbot.commands;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Links extends AzorbotCommand {

    public Links() {
        super(
                "Links",
                new String[]{"Link", "URL"},
                "Send a number of useful links"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e) {
        AzorbotEmbed embed = new AzorbotEmbed("Here you go!", e.getMessage());
        embed.addField(
                "Orbis",
                "[Github](https://github.com/AzortisCode/Orbis)\n" +
                        "[Wiki](https://docs.azortis.com/)\n" +
                        "[Spigot](https://www.spigotmc.org/threads/orbis-dimension-engine.493384/)",
                false
        );
        embed.send();
    }
}
