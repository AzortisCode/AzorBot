package com.azortis.azorbot.util;

import com.azortis.azorbot.CommandCenter;
import com.azortis.azorbot.Main;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
public class ScrollableEmbed implements AzorbotListener {
    private final MessageChannel channel;
    private final List<AzorbotEmbed> embeds;
    private final LocalDateTime end;
    private Message message;
    private long ID;
    private int prev;
    private int current;

    private static final String[] emojis = new String[]{"U+23EA", "U+25C0", "U+25B6", "U+23E9"};

    /**
     * Create a new scrollable embed
     * @param embeds List of embeds to scroll through
     * @param msg Message of which we use the channel
     */
    public ScrollableEmbed(List<AzorbotEmbed> embeds, Message msg){
        this.channel = msg.getChannel();
        this.embeds = embeds;
        this.prev = 0;
        this.end = LocalDateTime.now().plusHours(2);
        updateTitles();
        CommandCenter.addEmojiListener(this);
        send(msg);
    }

    /**
     * Sends this scrollable embed in to the channel of
     * @param msg this message
     */
    private void send(Message msg) {
        msg.getChannel().sendMessage(embeds.get(current).build()).queue(scrollableMessage ->{
            this.message = scrollableMessage;
            this.ID = scrollableMessage.getIdLong();
            this.update();
        });
    }

    /**
     * Updates the titles of all embeds to include their page number
     */
    private void updateTitles() {
        for (int i = 0; i < embeds.size(); i++){
            embeds.get(i).setTitle(embeds.get(i).getTitle() + " `" + i + "/" + embeds.size() + "`");
            embeds.get(i).setMessage(message);
        }
    }

    /**
     * Updates the embed
     */
    private void update(){
        this.prev = current;
        setResetEmbed();
        resetEmojis();
    }

    /**
     * Sets the current embed and resets it reactions
     */
    private void setResetEmbed() {
        this.channel.retrieveMessageById(ID).queue(d -> this.channel.editMessageById(ID, embeds.get(current).build()).queue());
    }

    /**
     * Adds all emojis for scrolling
     */
    private void resetEmojis() {
        if (embeds.size() > 2) {
            this.message.addReaction(emojis[0]).queue();
        }
        this.message.addReaction(emojis[1]).queue();
        this.message.addReaction(emojis[2]).queue();
        if (embeds.size() > 2) {
            this.message.addReaction(emojis[3]).queue();
        }
    }

    /**
     * Checks the reactions on this message
     * @param emoji Unicode emoji to check
     */
    private void checkReactions(String emoji){
        for (int i = 0; i < emojis.length; i++){
            try {
                if (emojis[i].equalsIgnoreCase(emoji)){
                    switch (i){
                        case 0: wayBack(); break;
                        case 1: back(); break;
                        case 2: forward(); break;
                        case 3: wayForward(); break;
                    }
                    break;
                }
            } catch (Exception ignored) {

            }
        }
    }

    /**
     * Scrolls back one page
     */
    public void back(){
        current -= 1;
        if (current <= 0){
            current = 0;
            Main.info("Already as far left as possible");
            return;
        }
        update();
    }

    /**
     * Scrolls all the way back
     */
    public void wayBack(){
        current = 0;
        update();
    }

    /**
     * Scrolls forward one page
     */
    public void forward(){
        current += 1;
        if (current >= embeds.size() - 1){
            current = embeds.size() - 1;
            Main.info("Already as far right as possible");
            return;
        }
        update();
    }

    /**
     * Scrolls all the way forward
     */
    public void wayForward(){
        current = embeds.size() - 1;
        update();
    }

    @Override
    public void incomingEmoji(@Nonnull GuildMessageReactionAddEvent e) {
        Main.info("Added reactions!");
        if (Objects.requireNonNull(e.getMember()).getIdLong() == Main.botID) return;
        if (LocalDateTime.now().isAfter(end)) CommandCenter.removeEmojiListener(this);
        if (e.getMessageIdLong() == ID){
            checkReactions(e.getReactionEmote().getEmoji());
        }
    }
}
