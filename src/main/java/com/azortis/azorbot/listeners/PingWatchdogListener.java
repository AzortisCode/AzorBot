package com.azortis.azorbot.listeners;

import com.azortis.azorbot.Main;
import com.azortis.azorbot.util.AzorbotEmbed;
import com.azortis.azorbot.util.AzorbotListener;
import com.azortis.azorbot.util.FileManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PingWatchdogListener implements AzorbotListener {

    private static boolean initialized = false;

    private static List<Long> staffRoleIDs = new ArrayList<>();
    private static List<Long> hasPingedStaffIDs = new ArrayList<>();
    private static final String path = Main.configPath + "ping-watchdog.txt";
    private static final FileManager file = new FileManager(path);
    private static final List<Role> staffRoles = new ArrayList<>();
    private static final List<Member> staffMembers = new ArrayList<>();
    private static final List<Member> hasPingedStaff = new ArrayList<>();
    private static final Map<Long, LocalDateTime> pingedStaffWhen = new HashMap<>();

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
    @SuppressWarnings("all")
    public static void getList(AzorbotEmbed embed) {
        StringBuilder roles = new StringBuilder();
        StringBuilder members = new StringBuilder();
        StringBuilder pingedStaff = new StringBuilder();
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
                    .append(") ")
                    .append(staffMembers.get(i).getAsMention())
                    .append("\n");
        }
        for (int i = 0; i < hasPingedStaff.size(); i++){
            pingedStaff
                    .append("(")
                    .append(i + 1)
                    .append(") ")
                    .append(hasPingedStaff.get(i).getAsMention());

            LocalDateTime time = pingedStaffWhen.get(hasPingedStaff.get(i));
            if (time != null) {
                String dateTime = DateTimeFormatter
                        .ofPattern("dd-MM-yyyy kk:HH:ss")
                        .withLocale(Locale.getDefault())
                        .withZone(ZoneId.systemDefault())
                        .format(time);
                pingedStaff.append(" @ ")
                        .append(dateTime);
            }
            pingedStaff.append("\n");
        }
        embed.addField("Staff roles:", roles.toString(), false);
        embed.addField("Staff members:", members.toString(), false);
        embed.addField("Staff mentioned by:", pingedStaff.toString(), false);
    }

    /**
     * Excuses a member
     * @param member the member to excuse
     * @return true if existed
     */
    public static boolean excuseMember(Member member) {
        if (hasPingedStaff.contains(member)){
            hasPingedStaff.remove(member);
            return true;
        }
        return false;
    }

    /**
     * Handles incoming
     * @param e message event
     */
    @Override
    public void incoming(GuildMessageReceivedEvent e){

        if (e.getMessage().getContentRaw().startsWith(Main.prefix)) return;
        if (e.getMessage().getContentRaw().startsWith("#")) return;

        // Make atomic done
        AtomicBoolean done = new AtomicBoolean(false);

        // Build embed
        AzorbotEmbed embed = new AzorbotEmbed("Please do not ping staff directly", e.getMessage());

        // Check if initialized
        if (!initialized) initialize(e.getGuild());

        // Check pinged members and roles
        List<Member> pingedMembers = e.getMessage().getMentionedMembers();
        List<Role> pingedRoles = e.getMessage().getMentionedRoles();

        // Go over members
        pingedMembers.forEach(member -> {
            if (done.get()) return;
            if (staffMembers.contains(member)) {
                Main.info(e.getAuthor().getName() + " pinged staff member: " + member.getUser().getName());
                done.set(true);
            }
        });

        // Check if done
        if (done.get()) {
            pingedStaff(e.getMessage(), e.getMember(), embed, true);
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
            pingedStaff(e.getMessage(), e.getMember(), embed, false);
        }
    }

    /**
     * Runs logic when someone pinged staff
     * @param message the message whose contents will be copied
     * @param member the member that pinged staff
     * @param embed the embed to output to (will be sent here)
     * @param isDP true if direct ping to staff
     */
    private static void pingedStaff(Message message, Member member, AzorbotEmbed embed, boolean isDP) {
        if (isDP){
            embed.setDescription("It is not allowed to directly ping staff.\n" +
                    "Please ping a support role instead\n" +
                    "Never ping support twice within 12 hours.");
            embed.addField("Your message:", message.getContentRaw(), false);
            embed.send(true);
        } else if (hasPingedStaff.contains(member)){
            embed.setDescription("It is not allowed to ping staff multiple times within 12 hours");
            embed.addField("Your message:", message.getContentRaw(), false);
            if (pingedStaffWhen.containsKey(member.getIdLong())){
                String dateTime = DateTimeFormatter
                        .ofPattern("dd-MM-yyyy kk:HH:ss")
                        .withLocale(Locale.getDefault())
                        .withZone(ZoneId.systemDefault())
                        .format(pingedStaffWhen.get(member.getIdLong()));
                embed.addField("Pinged on", dateTime, false);
            }
            embed.send(true);
        } else {
            hasPingedStaff.add(member);
            pingedStaffWhen.put(member.getIdLong(), LocalDateTime.now());
            save();
        }
    }

    /**
     * Initializes the bot
     */
    private static void initialize(Guild e) {
        Main.info("Initializing ping watchdog (first message received)");
        initialized = true;
        if (!load()) return;
        loadStaffRoles(e);
        loadStaffUsers(e);
        loadHasPingedStaff(e);
    }

    /**
     * Saves all content to file
     */
    private static void save() {

        // Reset ID lists
        staffRoleIDs = new ArrayList<>();
        hasPingedStaffIDs = new ArrayList<>();

        // Load IDs
        staffRoles.forEach(role -> {
            if (!staffRoleIDs.contains(role.getIdLong())) staffRoleIDs.add(role.getIdLong());
        });
        hasPingedStaff.forEach(member -> {
            if (!hasPingedStaffIDs.contains(member.getIdLong())) hasPingedStaffIDs.add(member.getIdLong());
        });

        // Build string
        StringBuilder out = new StringBuilder();

        // Add staff roles
        out.append("staffRoleIDs").append("\n");
        if (staffRoleIDs.size() != 0 && staffRoleIDs.get(0) != null) staffRoleIDs.forEach(id -> out.append(id).append("\n"));

        // Add has pinged members
        out.append("hasPingedStaffIDs").append("\n");
        if (hasPingedStaffIDs.size() != 0 && hasPingedStaffIDs.get(0) != null) hasPingedStaffIDs.forEach(id -> out.append(id).append("OnDate").append(pingedStaffWhen.get(id).toString()).append("\n"));

        // Add staff members
        out.append("staffMembers").append("\n");
        if (staffMembers.size() != 0 && staffMembers.get(0) != null) staffMembers.forEach(member -> out.append(member.getNickname()).append("\n"));

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

            if (line.isBlank()) continue;
            if (line.startsWith("null")) continue;

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
                case 2: {
                    String[] split = line.split("OnDate");
                    hasPingedStaffIDs.add(Long.parseLong(split[0]));
                    pingedStaffWhen.put(Long.parseLong(split[0]), LocalDateTime.parse(split[1]));
                    break;
                }
                case 3: Main.info("PingWatchdog staff member: " + line); break;
                default: Main.error("PingWatchdog sections not starting with staffRoleIDs or out of bounds"); return false;
            }
        }
        return true;
    }

    /**
     * Loads staff roles
     * @param guild The guild to find roles in
     */
    private static void loadStaffRoles(Guild guild){
        staffRoleIDs.forEach(role -> staffRoles.add(guild.getRoleById(role)));
    }

    /**
     * Loads staff members
     * Uses staff role list for query
     * @param guild The guild to check for users
     */
    private static void loadStaffUsers(Guild guild){
        guild.findMembers(member -> staffRoles.stream().anyMatch(member.getRoles()::contains)).onSuccess(staffMembers::addAll);
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
     * @return true if didn't already exist
     */
    public static boolean addRole(Role role) {
        if (staffRoles.contains(role)) return false;
        staffRoles.add(role);
        save();
        return true;
    }

    /**
     * Adds a member
     * @param member This member
     * @return true if didn't already exist
     */
    public static boolean addMember(Member member) {
        if (staffMembers.contains(member)) return false;
        staffMembers.add(member);
        save();
        return true;
    }

}
