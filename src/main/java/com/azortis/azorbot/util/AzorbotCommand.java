package com.azortis.azorbot.util;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.azortis.azorbot.Main;

import java.util.*;

/* TODO: Example Command Class

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

import java.util.List;

// TODO: Replace "CommandName" with name of the command
public class CommandName extends AzorbotCommand {
    // Constructor
    public CommandName() {
        super(
                "CommandName",
                new String[]{"Alias1", "Alias2", "Alias3"},
                new String[]{}, // Add role name here. Empty: always / 1+: at least one.
                "CommandDescription",
                false, // Weather command needs arguments or not
                "CommandName run example"
                // For an example for a category, see command.Wiki
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e) {
        Main.info("Command ran!");
    }
}



*/


/* TODO: Example Category Class

import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotCommand;

// TODO: Replace "CategoryName" with name of the category
public class CategoryName extends AzorbotCommand {
    // Constructor
    public Wiki() {
        super(
                "CategoryName",
                new String[]{"Alias1", "Alias2"},
                new String[]{}, // Add role name here. Empty: always / 1+: at least one.
                "CategoryName command category",
                "CategoryName <subcommand>",
                new AzorbotCommand[]{
                        new SubCommand1(),
                        new SubCommand2(),
                        new SubCommand3()
                }
        );
    }
}



*/

public class AzorbotCommand extends ListenerAdapter {

    @Getter
    public String name;
    @Getter
    public List<String> commands;
    @Getter
    public List<String> roles;
    @Getter
    public String description;
    @Getter
    public boolean needsArguments;
    @Getter
    public String example;
    @Getter
    public String category;
    @Getter
    public List<AzorbotCommand> subcommands;


    // Creator sets name, command aliases, requires any one of entered roles, and adds a description and example
    public AzorbotCommand(String name, String[] commands, String[] roles, String description, boolean needsArguments, String example) {
        if (commands == null || commands.length == 0) commands = new String[]{name};
        if (roles == null) roles = new String[]{};
        this.name = name;
        this.commands = Arrays.asList(commands);
        this.roles = Arrays.asList(roles);
        this.description = !description.equals("") ? description : "This command has no description";
        this.needsArguments = needsArguments;
        this.example = example;
        this.category = null;
        this.subcommands = null;
    }

    // Creator sets name, command aliases, requires any of entered roles, adds a category description,
    public AzorbotCommand(String name, String[] commands, String[] roles, String description, String category, AzorbotCommand[] subcommands) {
        if (commands == null || commands.length == 0) commands = new String[]{name};
        if (roles == null) roles = new String[]{};
        this.name = name;
        this.commands = Arrays.asList(commands);
        this.roles = Arrays.asList(roles);
        this.description = !description.equals("") ? description : "This command has no description";
        this.needsArguments = true;
        this.example = null;
        this.category = category;
        this.subcommands = Arrays.asList(subcommands);
    }

    // Override me!
    public void handle(List<String> args, GuildMessageReceivedEvent e) {
        e.getMessage().reply("The command you ran is improperly written. The handle() must be overwritten!");
    }

    // Handles prefix, handles bot users.
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        // Prevent bot user
        if (e.getAuthor().isBot()) return;

        // Prevent non-permitted users
        if (noPermission(Objects.requireNonNull(e.getMember()).getRoles(), e.getAuthor().getId())) return;

        // Convert args
        List<String> args = new LinkedList<>(Arrays.asList(e.getMessage().getContentRaw().replace(Main.prefix, "").split(" ")));

        // Check match command
        if (!checkCommand(args.get(0))) return;

        // Print success and continue
        continueToHandle(args, e);
    }

    // Handle
    public void continueToHandle(List<String> args, GuildMessageReceivedEvent e){



        // Check for permissions (again, but required when passing to here directly)
        if (getRoles() != null && getRoles().size() != 0){
            if (noPermission(Objects.requireNonNull(e.getMember()).getRoles(), e.getAuthor().getId())) return;
        }

        // Print info message
        Main.info("Command passed checks: " + getName());

        // If it doesn't require arguments just pass it with null
        if (!needsArguments) {
            handle(null, e);
        } else if (getCategory() != null) {
            // If it's a category do:
            if (args.size() < 2) {
                sendCategoryHelp(e.getMessage());
            } else {
                // Print subcommands
                StringBuilder subs = new StringBuilder("Subs: ");
                for (AzorbotCommand cmd : getSubcommands()) subs.append(cmd.getName()).append((" "));
                Main.info(subs.toString());
                // Pass to subcommands
                for (AzorbotCommand sub : getSubcommands()) {
                    for (String commandAlias : sub.getCommands()){
                        if (commandAlias.equalsIgnoreCase(args.get(1))){
                            sub.continueToHandle(args.subList(1, args.size()), e);
                            return;
                        }
                    }
                }
            }
            // Check for arg size to see if help should be sent
        } else if (args.size() < 2) {
            sendHelp(e.getMessage());
            // Pass to (overwritten) handle
        } else {
            Main.info("Final command. Running: " + getName());
            handle(args, e);
        }
    }

    /* Checks if the author has any of the specified roles, or if the ID matches */
    private boolean noPermission(List<Role> roles, String ID){
        if (getRoles() != null && getRoles().size() != 0) {
            for (Role userRole : roles) {
                String userRoleName = userRole.getName();
                for (String needsRole : getRoles()) {
                    if (needsRole.equals(userRoleName)) {
                        return false;
                    }
                }
                if (ID.equals(userRole.getName())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /* Checks if the specified command is this command */
    private boolean checkCommand(String command){
        for (String cmd : getCommands()){
            if (command.equalsIgnoreCase(cmd)) {
                return true;
            }
        }
        return false;
    }

    /* Sends a help message for this command's usage in the specified message's channel */
    public void sendHelp(Message message) {
        AzorbotEmbed embed = new AzorbotEmbed(getName() + " Command Usage", message);

        String cmd = Main.prefix + getName().substring(0, 1).toUpperCase() + getName().substring(1);
        if (getCommands().size() < 2) {
            embed.addField(cmd, "`*no aliases*`\n" + getDescription(), true);
        } else {
            embed.addField(
                    cmd,
                    "\n`" + Main.prefix +
                            (getCommands().size() == 2 ?
                                    getCommands().get(1) :
                                    " " + getCommands().subList(1, getCommands().size()).toString()
                                            .replace("[", "").replace("]", "")) +
                            "`\n" + getDescription(),
                    true
            );
        }
        if (getExample() != null) {
            embed.addField("**Usage**", "`" + Main.prefix + getExample() + "`", false);
        }
        if (getRoles() != null && getRoles().size() != 0) {
            embed.addField("**Permitted for role(s)**", "`" + getRoles().toString() + "`", false);
        }
        embed.send(message);
    }

    /* Sends a category help message for this category in the channel of the specified message */
    protected void sendCategoryHelp(Message message) {
        AzorbotEmbed embed = new AzorbotEmbed(getName() + " Command Usage", message);

        getSubcommands().forEach(command -> {
            String cmd = Main.prefix + command.getName().substring(0, 1).toUpperCase() + command.getName().substring(1);
            if (command.getCommands().size() < 2) {
                embed.addField(cmd, "`*no aliases*`\n" + command.getDescription(), true);
            } else {
                String body =
                        "\n`" + Main.prefix +
                                (command.getCommands().size() == 2 ?
                                        command.getCommands().get(1) :
                                        " " + command.getCommands().subList(1, command.getCommands().size()))
                                        .replace("[", "").replace("]", "") +
                                "`\n" +
                                command.getDescription() +
                                (command.getExample() != null ? "\n**Usage**\n`" + Main.prefix + command.getExample() + "`": "");
                embed.addField(
                        cmd,
                        body,
                        true
                );
            }
        });
        embed.send(message);
    }
}