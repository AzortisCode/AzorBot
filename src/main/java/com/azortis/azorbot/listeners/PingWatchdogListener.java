package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.FileManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PingWatchdogListener extends ListenerAdapter {

    private static JDA jda;
    private static boolean initialized = false;

    private static final String path = Main.configPath + "ping-watchdog.txt";
    private static final FileManager file = new FileManager(path);
    private static final List<Role> staffRoles = new ArrayList<>();
    private static final List<Long> staffRoleIDs = new ArrayList<>();
    private static final List<Member> staffMembers = new ArrayList<>();
    private static final List<Member> hasPingedStaff = new ArrayList<>();
    private static final List<Long> hasPingedStaffIDs = new ArrayList<>();

    /**
     * Creates a new ping watchdog
     * @param jda uses this jda to query users and roles
     */
    public PingWatchdogListener(JDA jda){
        PingWatchdogListener.jda = jda;
    }

    /**
     * Deletes from staff members
     * @param member this member
     * @return if it existed
     */
    public static boolean deleteMember(Member member) {
        if (staffMembers.contains(member)){
            staffMembers.remove(member);
            save();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Deletes from staff roles
     * @param role this role
     * @return if it existed
     */
    public static boolean deleteRole(Role role) {
        if (staffRoles.contains(role)){
            staffRoles.remove(role);
            save();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets a nice list of configs into
     * @param embed this embed
     */
    public static void getList(AzorbotEmbed embed) {
        StringBuilder roles = new StringBuilder();
        StringBuilder members = new StringBuilder();
        for (int i = 0; i < staffRoles.size(); i++){
            roles
                    .append("(")
                    .append(i + 1)
                    .append(") ")
                    .append(staffRoles.get(i).getAsMention())
                    .append(" `")
                    .append(staffRoles.get(i).getId())
                    .append("`\n");
        }
        for (int i = 0; i < staffMembers.size(); i++){
            members
                    .append("(")
                    .append(i + 1)
                    .append(")")
                    .append(staffMembers.get(i).getAsMention())
                    .append(" `")
                    .append(staffMembers.get(i).getId())
                    .append("`\n");
        }
        embed.addField("Staff roles:", roles.toString(), false);
        embed.addField("Staff members:", members.toString(), false);
    }

    /**
     * Runs checks for init, ping role and ping member
     * @param e Event to check
     */
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e){

        // Make atomic done
        AtomicBoolean done = new AtomicBoolean(false);

        // Build embed
        AzorbotEmbed embed = new AzorbotEmbed("Please do not ping staff twice", e.getMessage());

        // Check if initialized
        if (!initialized) initialize(e.getGuild());

        // Check pinged members and roles
        List<Member> pingedMembers = e.getMessage().getMentionedMembers();
        List<Role> pingedRoles = e.getMessage().getMentionedRoles();

        // Go over members
        pingedMembers.forEach(member -> {
            if (done.get()) return;
            if (staffMembers.contains(member)) {
                Main.info(e.getAuthor().getName() + " pinged staff member: " + member.getNickname());
                done.set(true);
            }
        });

        // Check if done
        if (done.get()) {
            pingedStaff(e.getMessage(), e.getMember(), embed);
            return;
        }

        // Go over roles
        pingedRoles.forEach(role -> {
            if (done.get()) return;
            Main.info(e.getAuthor().getName() + " pinged role: " + role.getName());
            if (staffRoles.contains(role)) {
                done.set(true);
            }
        });

        // Check if done
        if (done.get()) {
            PingWatchdogListener.pingedStaff(e.getMessage(), e.getMember(), embed);
        }
    }

    /**
     * Runs logic when someone pinged staff
     * @param message the message whose contents will be copied
     * @param member the member that pinged staff
     * @param embed the embed to output to (will be sent here)
     */
    private static void pingedStaff(Message message, Member member, AzorbotEmbed embed) {
        if (hasPingedStaff.contains(member)){
            embed.setDescription("It is not allowed to ping staff multiple times within 12 hours");
            embed.addField("Your message:", message.getContentRaw(), false);
            embed.send(true);
        } else {
            hasPingedStaff.add(member);
            save();
        }
    }

    /**
     * Initializes the bot
     */
    private static void initialize(Guild e) {
        Main.info("Initializing ping watchdog");
        initialized = true;
        if (!load()) return;
        loadStaffRoles();
        loadStaffUsers(e);
        loadHasPingedStaff(e);
    }

    /**
     * Saves all content to file
     */
    private static void save() {

        // Load IDs
        staffRoles.forEach(role -> staffRoleIDs.add(role.getIdLong()));
        hasPingedStaff.forEach(member -> hasPingedStaffIDs.add(member.getIdLong()));

        // Build string
        StringBuilder out = new StringBuilder();

        // Add staff roles
        out.append("staffRoleIDs").append("\n");
        staffRoleIDs.forEach(id -> out.append(id).append("\n"));

        // Add has pinged members
        out.append("hasPingedStaffIDs").append("\n");
        hasPingedStaffIDs.forEach(id -> out.append(id).append("\n"));

        // Add staff members
        out.append("staffMembers").append("\n");
        staffMembers.forEach(member -> out.append(member.getNickname()).append("\n"));

        // Write
        file.write(out.toString());
    }

    /**
     * Loads all content from file
     */
    private static boolean load() {
        List<String> in = file.read();
        if (in == null || in.size() < 3){
            Main.error("No content found in file for PingWatchdog");
            return false;
        }
        int section = 0;
        for (String line : in) {

            // Check if new section
            switch (line) {
                case "staffRoleIDs":
                    section = 1;
                    continue;
                case "hasPingedStaffIDs":
                    section = 2;
                    continue;
                case "staffMembers":
                    section = 3;
                    continue;
            }

            // Save to section
            switch (section){
                case 1: staffRoleIDs.add(Long.parseLong(line)); break;
                case 2: hasPingedStaffIDs.add(Long.parseLong(line)); break;
                case 3: Main.info("PingWatchdog staff member: " + line); break;
                default: Main.error("PingWatchdog sections not starting with staffRoleIDs or out of bounds"); return false;
            }
        }
        return true;
    }

    /**
     * Loads staff roles
     */
    private static void loadStaffRoles(){
        assert jda != null;
        staffRoleIDs.forEach(role -> staffRoles.add(jda.getRoleById(role)));
    }

    /**
     * Loads staff members
     * Uses staff role list for query
     * @param guild The guild to check for users
     */
    private static void loadStaffUsers(Guild guild){
        staffRoles.forEach(role -> guild.findMembersWithRoles(role).onSuccess(staffMembers::addAll));
    }

    /**
     * Loads pinged staff
     * @param guild The guild to check for users
     */
    private static void loadHasPingedStaff(Guild guild) {
        hasPingedStaffIDs.forEach(ID -> hasPingedStaff.add(guild.getMemberById(ID)));
    }

    /**
     * Adds a role
     * @param role This role
     */
    public static void addRole(Role role) {
        staffRoles.add(role);
        save();
    }

    /**
     * Adds a member
     * @param member This member
     */
    public static void addMember(Member member) {
        staffMembers.add(member);
        save();
    }

}
