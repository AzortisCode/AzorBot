package com.azortis.azorbot.commands.pingwatchdog;

import com.azortis.azorbot.listeners.PingWatchdogListener;
import com.azortis.azorbot.util.AzorbotCommand;
import com.azortis.azorbot.util.AzorbotEmbed;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class PingWatchdogDelete extends AzorbotCommand {
    // Constructor
    public PingWatchdogDelete(){
        super(
                "Delete",
                new String[]{"remove", "del", "-"},
                null, //Inherited
                "Deletes a role or user from PingWatchdog's staff lists",
                true,
                "PingWatchdog Delete role <@role>\nPingWatchdog Delete user <@user>"
        );
    }

    // Handle
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent e){
        AzorbotEmbed embed = new AzorbotEmbed("Deleting definition from PingWatchdog", e.getMessage());
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
            if (PingWatchdogListener.deleteRole(role)){
                embed.setDescription("Deleted new role from staff list: " + role.getName());
            } else {
                embed.setDescription("Could not find " + role.getName() + " in staff role definitions.");
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
            if (PingWatchdogListener.deleteMember(member)){
                embed.setDescription("Deleted new role from staff list: " + member.getNickname());
            } else {
                embed.setDescription("Could not find " + member.getNickname() + " in staff role definitions.");
            }
        } else {
            embed.setDescription("You specified something other than `role` or `user` as first parameter.\n" +
                    "Please double-check your command");
            embed.send(true, 30000);
        }
    }
}
