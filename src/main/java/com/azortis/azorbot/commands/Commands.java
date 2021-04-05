package com.azortis.azorbot.commands;

import com.azortis.azorbot.cocoUtil.CocoBot;
import com.azortis.azorbot.cocoUtil.CocoCommand;
import com.azortis.azorbot.cocoUtil.CocoCommandCenter;
import com.azortis.azorbot.cocoUtil.CocoEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class Commands extends CocoCommand {

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
        CocoEmbed embed = new CocoEmbed("༼ つ ◕_◕ ༽つ **" + CocoBot.getBotName() + " Info Page!**", e.getMessage());

        // Add explanation
        embed.addField(
                "All commands you can use",
                "!<command> followed by a list of aliases",
                false
        );

        // Loop over and add all commands with their respective information
        for (CocoCommand command : CocoCommandCenter.getCommands()){
            if (command.noPermission(Objects.requireNonNull(e.getMember()).getRoles(), e.getAuthor().getId())) continue;
            String cmd = CocoBot.getPrefix() + command.getName().substring(0, 1).toUpperCase() + command.getName().substring(1);
            if (command.getCommands().size() == 0){
                embed.addField(cmd, "`*no aliases*`\n" + command.getDescription(), true);
            } else {
                StringBuilder body = new StringBuilder();
                body
                        .append("\n`")
                        .append(CocoBot.getPrefix())
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
