package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Getter
public class CocoScrollable extends ListenerAdapter {
    private final MessageChannel channel;
    private final List<CocoEmbed> embeds;
    private final LocalDateTime end;
    private Message message;
    private long ID;
    private int current;

    private static final String[] emojis = new String[]{"U+23EA", "U+25C0", "U+25B6", "U+23E9"};

    /**
     * Create a new scrollable embed
     * @param pages List of embeds to scroll through
     * @param message Message of which we use the channel
     */
    public CocoScrollable(List<CocoEmbed> pages, Message message){
        this.channel = message.getChannel();
        this.embeds = pages;
        this.end = LocalDateTime.now().plusHours(2);
        updateTitles();
        CocoCommandCenter.addEmojiListener(this);
        send();
        Main.info("Built new scrollable");
    }

    /**
     * Create a new scrollable embed
     * @param pages List of embeds to scroll through
     * @param message Message of which we use the channel
     * @param deleteOriginal If true, deletes original message
     */
    public CocoScrollable(List<CocoEmbed> pages, Message message, boolean deleteOriginal){
        this.channel = message.getChannel();
        this.embeds = pages;
        this.end = LocalDateTime.now().plusHours(2);
        updateTitles();
        CocoCommandCenter.addEmojiListener(this);
        send();
        if (deleteOriginal) message.delete().queue();
        Main.info("Built new scrollable");
    }

    /**
     * Create a new scrollable embed from a string
     * @param string String to build scrollable out of
     * @param embed The embed template to use (title etc)
     * @param message Message of which we use the channel
     */
    public CocoScrollable(String string, CocoEmbed embed, Message message) {
        this.channel = message.getChannel();
        this.embeds = makeEmbedsFromString(string, embed);
        this.end = LocalDateTime.now().plusHours(2);
        updateTitles();
        CocoCommandCenter.addEmojiListener(this);
        send();
        Main.info("Built new scrollable");
    }

    /**
     * Create a new scrollable embed from a string
     * @param string String to build scrollable out of
     * @param embed The embed template to use (title etc)
     * @param message Message of which we use the channel
     * @param deleteOriginal If true, deletes original message
     */
    public CocoScrollable(String string, CocoEmbed embed, Message message, boolean deleteOriginal) {
        this.channel = message.getChannel();
        this.embeds = makeEmbedsFromString(string, embed);
        this.end = LocalDateTime.now().plusHours(2);
        if (embeds.size() == 1){
            embeds.get(0).send(deleteOriginal);
            Main.info("New scrollable had small size, creating embed instead");
        } else {
            updateTitles();
            CocoCommandCenter.addEmojiListener(this);
            send();
            if (deleteOriginal) message.delete().queue();
            Main.info("Built new scrollable");
        }
    }

    /**
     * Creates a list of embeds from a string, which you can later port into a new scrollable
     * @param string A string
     * @param template the embed template to use
     * @return A list of CocoEmbeds
     */
    public static List<CocoEmbed> makeEmbedsFromString(String string, CocoEmbed template) {
        // TODO:
        return Collections.singletonList(template);
    }

    /**
     * Sends this scrollable embed in to the channel of
     */
    private void send(){
        channel.sendMessage(embeds.get(current).build()).queue(scrollableMessage ->{
            this.message = scrollableMessage;
            this.ID = scrollableMessage.getIdLong();
            this.update();
        });
    }

    /**
     * Updates the titles of all embeds to include their page number
     */
    private void updateTitles(){
        for (int i = 0; i < embeds.size(); i++){
            embeds.get(i).setTitle(
                    "`" + (i+1) + "/" + embeds.size() + "` " +
                            TextUtil.capitalize(
                                    Objects.requireNonNull(
                                            embeds.get(i).build().getTitle()
                                    )
                            ),
                    embeds.get(i).build().getUrl());
            embeds.get(i).setMessage(message);
        }
    }

    /**
     * Updates the embed
     */
    private void update(){
        setResetEmbed();
        resetEmojis();
    }

    /**
     * Sets the current embed and resets it reactions
     */
    private void setResetEmbed(){
        this.channel.retrieveMessageById(ID).queue(d -> this.channel.editMessageById(ID, embeds.get(current).build()).queue());
    }

    /**
     * Adds all emojis for scrolling
     */
    private void resetEmojis(){
        if (embeds.size() > 2){
            this.message.addReaction(emojis[0]).queue();
        }
        if (embeds.size() != 0){
            this.message.addReaction(emojis[1]).queue();
            this.message.addReaction(emojis[2]).queue();
        }
        if (embeds.size() > 2){
            this.message.addReaction(emojis[3]).queue();
        }
    }

    /**
     * Checks the reactions on this message
     * @param emoji Unicode emoji to check
     */
    private void checkReactions(String emoji){
        for (int i = 0; i < emojis.length; i++){
            if (emojis[i].equalsIgnoreCase(emoji)){
                switch (i){
                    case 0: wayBack(); break;
                    case 1: back(); break;
                    case 2: forward(); break;
                    case 3: wayForward(); break;
                }
                return;
            }
        }
    }

    /**
     * Scrolls back one page
     */
    public void back(){
        Main.info("Scrolling back 1 page");
        current -= 1;
        if (current < 0){
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
        Main.info("Scrolling back to first page");
        current = 0;
        update();
    }

    /**
     * Scrolls forward one page
     */
    public void forward(){
        Main.info("Scrolling forward 1 page");
        current += 1;
        if (current > embeds.size() - 1){
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
        Main.info("Scrolling forward to last page");
        current = embeds.size() - 1;
        update();
    }

    /**
     * Gets an emoji from Guild emoji event
     * @param e The event to check
     * @return The emoji unicode string
     */
    private String eventToEmoji(GuildMessageReactionAddEvent e){
        return e.getReactionEmote().toString().toUpperCase(Locale.ROOT).replace("RE:","");
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent e){
        if (LocalDateTime.now().isAfter(end)) CocoCommandCenter.removeEmojiListener(this);
        if (e.getMessageIdLong() == ID){
            Main.info("Scrollable was reacted on! Emoji: " + eventToEmoji(e));
            checkReactions(eventToEmoji(e));
            e.getReaction().removeReaction(e.getUser()).queue();
        }
    }
}
