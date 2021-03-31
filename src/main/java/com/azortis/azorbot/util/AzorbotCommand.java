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
    public CommandName(){
        super(
                "CommandName",
                new String[]{"Alias1", "Alias2", "Alias2"},
                new String[]{"Role1", "Role2"},
                "CommandDescription",
                true,
                "parent CommandName param1"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        Main.info("Command ran!");
    }
}



*/


/* TODO: Example Category Class


// TODO: Replace "CategoryName" with name of the category
public class CategoryName extends AzorbotCommand {
    // Constructor
    public Wiki(){
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
@Getter
public class AzorbotCommand extends ListenerAdapter {

    public String name;
    public List<String> commands;
    public List<String> roles;
    public String description;
    public boolean needsArguments;
    public String example;
    public boolean category;
    public List<AzorbotCommand> subcommands;

    /**
     * Command creator
     * @param name Name of the command
     * @param description Description of the command
     */
    public AzorbotCommand(String name, String description){
        // Name
        this.name = name;

        // Description
        this.description = !description.equals("") ? description : "This command has no description";

        // Other
        this.commands = new ArrayList<>(Collections.singleton(name));
        this.roles = new ArrayList<>();
        this.needsArguments = false;
        this.example = null;
        this.category = false;
        this.subcommands = null;
    }

    /**
     * Command creator
     * @param name Name of the command
     * @param commands Array of aliases for the command
     * @param description Description of the command
     */
    public AzorbotCommand(String name, String[] commands, String description){
        // Name
        this.name = name;

        // Commands
        if (commands == null || commands.length == 0){
            this.commands = new ArrayList<>(Collections.singleton(name));
        } else {
            this.commands = Arrays.asList(commands);
        }

        // Description
        this.description = !description.equals("") ? description : "This command has no description";

        // Other
        this.roles = new ArrayList<>();
        this.needsArguments = false;
        this.example = null;
        this.category = false;
        this.subcommands = null;
    }

    /**
     * Command creator
     * @param name Name of the command
     * @param commands Array of aliases for the command
     * @param roles Array of roles the command requires (only one is enough for permission)
     * @param description Description of the command
     */
    public AzorbotCommand(String name, String[] commands, String[] roles, String description){
        // Name
        this.name = name;

        // Commands
        if (commands == null || commands.length == 0){
            this.commands = new ArrayList<>(Collections.singleton(name));
        } else {
            this.commands = Arrays.asList(commands);
        }

        // Roles
        if (roles == null){
            this.roles = new ArrayList<>();
        } else {
            this.roles = Arrays.asList(roles);
        }

        // Description
        this.description = !description.equals("") ? description : "This command has no description";

        // Other
        this.needsArguments = false;
        this.example = null;
        this.category = false;
        this.subcommands = null;
    }

    /**
     * Command creator
     * @param name Name of the command
     * @param commands Array of aliases for the command
     * @param roles Array of roles the command requires (only one is enough for permission)
     * @param description Description of the command
     * @param needsArguments Toggle for requiring arguments (helps with preventing argument-less calls)
     */
    public AzorbotCommand(String name, String[] commands, String[] roles, String description, boolean needsArguments){
        // Name
        this.name = name;

        // Commands
        if (commands == null || commands.length == 0){
            this.commands = new ArrayList<>(Collections.singleton(name));
        } else {
            this.commands = Arrays.asList(commands);
        }

        // Roles
        if (roles == null){
            this.roles = new ArrayList<>();
        } else {
            this.roles = Arrays.asList(roles);
        }

        // Description
        this.description = !description.equals("") ? description : "This command has no description";

        // Needs arguments
        this.needsArguments = needsArguments;

        // Other
        this.example = null;
        this.category = false;
        this.subcommands = null;
    }

    /**
     * Command creator (with example)
     * @param name Name of the command
     * @param commands Array of aliases for the command
     * @param roles Array of roles the command requires (only one is enough for permission)
     * @param description Description of the command
     * @param needsArguments Toggle for requiring arguments (helps with preventing argument-less calls)
     * @param example Example of command usage.
     */
    public AzorbotCommand(String name, String[] commands, String[] roles, String description, boolean needsArguments, String example){
        // Name
        this.name = name;

        // Commands
        if (commands == null || commands.length == 0){
            this.commands = new ArrayList<>(Collections.singleton(name));
        } else {
            this.commands = Arrays.asList(commands);
        }

        // Roles
        if (roles == null){
            this.roles = new ArrayList<>();
        } else {
            this.roles = Arrays.asList(roles);
        }

        // Description
        this.description = !description.equals("") ? description : "This command has no description";

        // Needs arguments
        this.needsArguments = needsArguments;

        // Example
        this.example = example;

        // Other
        this.category = false;
        this.subcommands = null;
    }

    /**
     * Command category creator
     * @param name Name of the category
     * @param commands Array of command aliases for this category
     * @param description Description of the category
     * @param subcommands Array of commands that this category contains
     */
    public AzorbotCommand(String name, String[] commands, String description, AzorbotCommand[] subcommands){
        // Name
        this.name = name;

        // Commands
        if (commands == null || commands.length == 0){
            this.commands = new ArrayList<>(Collections.singleton(name));
        } else {
            this.commands = Arrays.asList(commands);
        }

        // Description
        this.description = !description.equals("") ? description : "This category has no description";

        // Subcommands
        this.subcommands = Arrays.asList(subcommands);

        // Category
        this.category = true;

        // Other
        this.roles = new ArrayList<>();
        this.example = null;
        this.needsArguments = true;
    }

    /**
     * Command category creator
     * @param name Name of the category
     * @param commands Array of command aliases for this category
     * @param roles Roles required for accessing this category
     * @param description Description of the category
     * @param subcommands Array of commands that this category contains
     */
    public AzorbotCommand(String name, String[] commands, String[] roles, String description, AzorbotCommand[] subcommands){
        // Name
        this.name = name;

        // Commands
        if (commands == null || commands.length == 0){
            this.commands = new ArrayList<>(Collections.singleton(name));
        } else {
            this.commands = Arrays.asList(commands);
        }

        // Roles
        if (roles == null){
            this.roles = new ArrayList<>();
        } else {
            this.roles = Arrays.asList(roles);
        }

        // Description
        this.description = !description.equals("") ? description : "This category has no description";

        // Subcommands
        this.subcommands = Arrays.asList(subcommands);

        // Category
        this.category = true;

        // Other
        this.example = null;
        this.needsArguments = true;
    }

    /**
     * Override me!
     * @param args Command arguments
     * @param e Guild message even that needs to be processed
     */
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        e.getMessage().reply("The command you ran is improperly written. The handle() must be overwritten!");
    }

    /**
     * Handles: Prefix, Bot authors, required permissions, argument preprocessing, command check
     * @param e The guild message event that needs to be processed
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){

        // Check prefix
        if (!e.getMessage().getContentRaw().startsWith(Main.prefix)) return;

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

    /**
     * Handle: command permissions (again, but required for some instances. Not particularly slow)
     * parameters, subcommands, category mentions, etc.
     * @param args Arguments as passed in the original command (preprocessed list)
     * @param e Guild message event that needs to be processed
     */
    public void continueToHandle(List<String> args, GuildMessageReceivedEvent e){

        // Check for permissions (again, but required when passing to here directly)
        if (getRoles() != null && getRoles().size() != 0){
            if (noPermission(Objects.requireNonNull(e.getMember()).getRoles(), e.getAuthor().getId())) return;
        }

        // Print info message
        Main.info("Command passed checks: " + getName());

        // If it doesn't require arguments just pass it with null
        if (!needsArguments){
            handle(null, e);
        } else if (category){
            // If it's a category do:
            if (args.size() < 2){
                sendCategoryHelp(e.getMessage());
            } else {
                // Print subcommands
                StringBuilder subs = new StringBuilder("Subs: ");
                for (AzorbotCommand cmd : getSubcommands()) subs.append(cmd.getName()).append((" "));
                Main.info(subs.toString());
                // Pass to subcommands
                for (AzorbotCommand sub : getSubcommands()){
                    if (sub.getName().equalsIgnoreCase(args.get(1))){
                        sub.continueToHandle(args.subList(1, args.size()), e);
                        return;
                    }
                    for (String commandAlias : sub.getCommands()){
                        if (commandAlias.equalsIgnoreCase(args.get(1))){
                            sub.continueToHandle(args.subList(1, args.size()), e);
                            return;
                        }
                    }
                }
            }
            // Check for arg size to see if help should be sent
        } else if (args.size() < 2){
            sendHelp(e.getMessage());
            // Pass to (overwritten) handle
        } else {
            Main.info("Final command. Running: " + getName());
            handle(args.subList(1, args.size()), e);
        }
    }

    /**
     * Checks if the author has any of the specified roles, or if the ID matches
     * @param roles The list of roles that the user has
     * @param ID The user ID that needs to be checked for specific perms*/
    public boolean noPermission(List<Role> roles, String ID){
        if (getRoles() != null && getRoles().size() != 0){
            for (Role userRole : roles){
                String userRoleName = userRole.getName();
                for (String needsRole : getRoles()){
                    if (needsRole.equals(userRoleName)){
                        return false;
                    }
                }
                if (ID.equals(userRole.getName())){
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Checks if the specified command is this command
     * @param command The command that needs to be checked
     */
    private boolean checkCommand(String command){
        if (command.equalsIgnoreCase(name)) return true;
        for (String cmd : getCommands()){
            if (command.equalsIgnoreCase(cmd)){
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a help message for this command's usage in the specified message's channel
     * @param message The message object as delivered with the original event
     */
    public void sendHelp(Message message){
        AzorbotEmbed embed = new AzorbotEmbed(getName() + " Command Usage", message);

        String cmd = Main.prefix + getName().substring(0, 1).toUpperCase() + getName().substring(1);
        if (getCommands().size() < 2){
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
        if (getExample() != null){
            embed.addField("**Usage**", "`" + Main.prefix + getExample() + "`", false);
        }
        if (getRoles() != null && getRoles().size() != 0){
            embed.addField("**Permitted for role(s)**", "`" + getRoles().toString() + "`", false);
        }
        embed.send(message);
    }

    /**
     * Sends a category help message for this category in the channel of the specified message
     * @param message The message object as delivered with the original event
     */
    protected void sendCategoryHelp(Message message){

        // Make a new embed
        AzorbotEmbed embed = new AzorbotEmbed(getName() + " Command Usage", message);

        // Loop over all commands and add fields for all of them
        getSubcommands().forEach(command -> {

            // Collect command
            String cmd = Main.prefix + command.getName().substring(0, 1).toUpperCase() + command.getName().substring(1);

            // Process command properties
            if (command.getCommands().size() < 2){
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