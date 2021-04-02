package com.azortis.azorbot.commands;

import com.azortis.azorbot.CommandCenter;
import com.azortis.azorbot.Main;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;

import java.util.List;
import java.util.Objects;

public class Commands extends AzorbotCommand {

    /**
     * Creates a commands command
     */
    public Commands(){
        super(
                "commands",
                new String[]{"command", "cmd", "help", "?", ""},
                "Sends the command help page (this one)"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){

        // Init embed
        AzorbotEmbed embed = new AzorbotEmbed("༼ つ ◕_◕ ༽つ **" + Main.botName + " Info Page!**", e.getMessage());

        // Add explanation
        embed.addField(
                "All commands you can use",
                "!<command> followed by a list of aliases",
                false
        );

        // Loop over and add all commands with their respective information
        for (AzorbotCommand command : CommandCenter.getCommands()){
            if (command.noPermission(Objects.requireNonNull(e.getMember()).getRoles(), e.getAuthor().getId())) continue;
            String cmd = Main.prefix + command.getName().substring(0, 1).toUpperCase() + command.getName().substring(1);
            if (command.getCommands().size() == 0){
                embed.addField(cmd, "`*no aliases*`\n" + command.getDescription(), true);
            } else {
                StringBuilder body = new StringBuilder();
                body
                        .append("\n`")
                        .append(Main.prefix)
                        .append(
                                command.getCommands().size() == 1 ?
                                        command.getCommands().get(0) :
                                        " " + command.getCommands().toString()
                                                .replace("[", "").replace("]", "")
                        )
                        .append("`\n")
                        .append(command.getDescription())
                        .append(command.getExample() != null ? "\n**Usage**\n" + command.getExample() : "");
                if (command.getRoles() != null && command.getRoles().size() != 0){
                    if (command.getRoles().size() == 1){
                        body.append("\n__Required:__ ").append(command.getRoles().get(0));
                    } else {
                        body.append("\n__Required:__ ").append(command.getRoles().toString()
                                .replace("[", "").replace("]", ""));
                    }
                }
                embed.addField(
                        cmd,
                        body.toString(),
                        true
                );
            }
        }

        // Send the embed
        embed.send(true, 1000);
    }
}
