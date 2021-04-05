package com.azortis.azorbot.commands.pingwatchdog;

import com.azortis.azorbot.listeners.PingWatchdogListener;
import com.azortis.azorbot.util.CocoCommand;
import com.azortis.azorbot.util.CocoEmbed;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class PingWatchdogAdd extends CocoCommand {
    // Constructor
    public PingWatchdogAdd(){
        super(
                "Add",
                new String[]{"a", "+"},
                null, //Inherited
                "Adds a role or user to PingWatchdog's staff lists",
                true,
                "PingWatchdog Add role <@role>\nPingWatchdog Add user <@user>"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        CocoEmbed embed = new CocoEmbed("Adding definition to PingWatchdog", e.getMessage());
        if (args.size() < 2){
            embed.setDescription("Please specify either `role <@role>` or `user <@user>`.");
            embed.send(true, 30000);
        } else if (args.get(0).equalsIgnoreCase("role")){
            List<Role> roles = e.getMessage().getMentionedRoles();
            if (roles.size() == 0){
                embed.setDescription("No mentioned role or it does not exist. Did you mention a role?");
                embed.send(true, 30000);
                return;
            }
            Role role = roles.get(0);
            if (PingWatchdogListener.addRole(role)){
                embed.setDescription("Added new role to staff list: " + role.getName());
            } else {
                embed.setDescription("Tried adding " + role.getName() + " to staff list but it is already in.");
            }
            embed.send(true);
        } else if (args.get(0).equalsIgnoreCase("user")){
            List<Member> members = e.getMessage().getMentionedMembers();
            if (members.size() == 0){
                embed.setDescription("No mentioned member or they do not exist (in this server). Did you mention a member?");
                embed.send(true, 30000);
                return;
            }
            Member member = members.get(0);
            if (PingWatchdogListener.addMember(member)){
                embed.setDescription("Added new member to staff list: " + member.getUser().getName());
            } else {
                embed.setDescription("Tried adding " + member.getUser().getName() + " to staff list but they are already in");
            }
            embed.send(true);

        } else {
            embed.setDescription("You specified something other than `role` or `user` as first parameter.\n" +
                    "Please double-check your command");
            embed.send(true, 30000);
        }
    }
}
