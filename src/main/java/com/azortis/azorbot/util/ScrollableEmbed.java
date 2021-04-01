package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScrollableEmbed extends ListenerAdapter {
    private final Message message;
    private final MessageChannel channel;
    private final List<AzorbotEmbed> embeds;
    private final LocalDateTime end;
    private final long ID;
    private int prev;
    private int current;

    private static final String[] emojis = new String[]{"U+23EA", "U+25C0", "U+25B6", "U+23E9"};

    /**
     * Create a new scrollable embed
     * @param embeds List of embeds to scroll through
     * @param msg Message of which we use the channel
     */
    public ScrollableEmbed(List<AzorbotEmbed> embeds, Message msg){
        this.message = msg;
        this.ID = msg.getIdLong();
        this.channel = msg.getChannel();
        this.embeds = embeds;
        this.prev = 0;
        this.end = LocalDateTime.now().plusHours(2);
        update();
        Main.getJda().addEventListener(this);
    }

    /**
     * Updates the embed
     */
    private void update(){
        this.prev = current;
        setResetEmbed();
        addEmojis();
    }

    /**
     * Sets the current embed and resets it reactions
     */
    private void setResetEmbed() {
        this.channel.editMessageById(ID, embeds.get(current).build()).queue();
        this.channel.removeReactionById(ID, emojis[0]).queue();
        this.channel.removeReactionById(ID, emojis[1]).queue();
        this.channel.removeReactionById(ID, emojis[2]).queue();
        this.channel.removeReactionById(ID, emojis[3]).queue();
    }

    /**
     * Adds all emojis for scrolling
     */
    private void addEmojis() {
        if (embeds.size() > 2) {
            this.channel.addReactionById(ID, emojis[0]).queue();
        }
        this.channel.addReactionById(ID, emojis[1]).queue();
        this.channel.addReactionById(ID, emojis[2]).queue();
        if (embeds.size() > 2) {
            this.channel.addReactionById(ID, emojis[3]).queue();
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
        if (current <= 0){
            current = 0;
            Main.info("Already as far left as possible");
            return;
        }
        current -= 1;
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
        if (current >= embeds.size() - 1){
            current = embeds.size() - 1;
            Main.info("Already as far right as possible");
            return;
        }
        current += 1;
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
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent e){
        if (LocalDateTime.now().isAfter(end)) Main.getJda().removeEventListener(this);
        if (e.getMessageIdLong() == ID){
            checkReactions(e.getReactionEmote().getEmoji());
        }
    }
}