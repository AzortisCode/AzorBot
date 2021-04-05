package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class AzorbotEmbed extends EmbedBuilder {
    @Setter
    private Message message;
    private final String title;

    /**
     * Creates a default AzorbotEmbed object.
     * @param title The title of the embed
     * @param message Message used to greet used and make further command easier
     */
    public AzorbotEmbed(String title, Message message) {
        this.message = message;
        this.title = title;
        this.setAuthor("Requested by: " + message.getAuthor().getName(), null, message.getAuthor().getAvatarUrl());
        this.setTitle(!title.equals("") ? title : "\u200E");
        this.setColor(Color.decode(Main.botColor));
        this.setFooter(Main.botCompany);
    }

    /**
     * Creates a default AzorbotEmbed object.
     * @param title The title of the embed
     * @param useShort Toggles if there should be a mention of the bot company
     */
    public AzorbotEmbed(String title, Message message, boolean useShort){
        this.message = message;
        this.title = title;
        this.setTitle(title).setColor(Color.decode(Main.botColor));
        if (!useShort){
            this.setAuthor("Requested by: " + message.getAuthor().getName(), null, message.getAuthor().getAvatarUrl())
                    .setFooter(Main.botCompany);
        }
    }


    /**
     * Send the embed
     * @return Sent message
     */
    public Message send(){
        return this.send(this.message, null, false, 0, null);
    }

    /**
     * Send the embed in the channel of the original message, if passed with the initial command, and
     * @param deleteMSG If true, delete original message
     * @return Sent message
     */
    public Message send(boolean deleteMSG){
        return this.send(this.message, deleteMSG);
    }

    /**
     * Send the embed
     * @param deleteMSG and, if true, delete original message
     * @param deleteAfterMS after X ms 
     * @return Sent message
     */
    public Message send(boolean deleteMSG, int deleteAfterMS){
        return this.send(this.message, deleteMSG, deleteAfterMS);
    }

    /**
     * Send the embed
     * @param reactions and add these reactions to the message
     * @return Sent message
     */
    public Message send(List<String> reactions){
        return this.send(this.message, null, false, 0, reactions);
    }

    /**
     * Send the embed
     * @param channel in this channel
     * @return Sent message
     */
    public Message send(TextChannel channel){
        return this.send(null, channel, false, 0, null);
    }

    /**
     * Send the embed 
     * @param channel in this channel
     * @param reactions and add these reactions to the message
     * @return Sent message
     */
    public Message send(TextChannel channel, List<String> reactions){
        return this.send(null, channel, false, 0, reactions);
    }

    /**
     * Send the embed
     * @param message in this channel
     * @return Sent message
     */
    public Message send(Message message){
        return this.send(message, false);
    }

    /**
     * Send the embed 
     * @param message in this channel
     * @param reactions and add these reactions to the message
     * @return Sent message
     */
    public Message send(Message message, List<String> reactions){
        return this.send(message, false, reactions);
    }


    /**
     * Send the embed
     * @param message in this channel
     * @param deleteMSG And delete the specified message
     * @return Sent message
     */
    public Message send(Message message, boolean deleteMSG){
        return this.send(message, deleteMSG, 0);
    }

    /**
     * Send the embed 
     * @param message in this channel
     * @param deleteMSG And delete the specified message
     * @param reactions and add these reactions to the message
     * @return Sent message
     */
    public Message send(Message message, boolean deleteMSG, List<String> reactions){
        return this.send(message, deleteMSG, 0, reactions);
    }

    /**
     * Send the embed
     * @param message in this channel
     * @param deleteMSG And delete the specified message
     * @param deleteAfterMS After X ms
     * @return Sent message
     */
    public Message send(Message message, boolean deleteMSG, int deleteAfterMS){
        return this.send(message, null, deleteMSG, deleteAfterMS, null);
    }

    /**
     * Send the embed 
     * @param message in this channel
     * @param deleteMSG And delete the specified message
     * @param deleteAfterMS After X ms
     * @param reactions and add these reactions to the message
     * @return Sent message
     */
    public Message send(Message message, boolean deleteMSG, int deleteAfterMS, List<String> reactions){
        return this.send(message, null, deleteMSG, deleteAfterMS, reactions);
    }

    /**
     * Send the embed
     * @param message in this channel
     * @param channel Or, if message is null, in this channel
     * @param deleteMSG And delete the message
     * @param deleteAfterMS After X ms
     * @param reactions and add these reactions to the message
     * @return Sent message
     */
    public Message send(Message message, TextChannel channel, boolean deleteMSG, int deleteAfterMS, List<String> reactions){
        if (reactions == null) reactions = new ArrayList<>();
        if (message == null && channel == null){
            Main.error("No channel and message specified.");
            return null;
        } else if (message != null){
            List<String> finalReactions = reactions;
            AtomicReference<Message> returnMSG = new AtomicReference<>();
            message.getChannel().sendMessage(this.build()).queue(msg ->{
                for (String emoji : finalReactions){
                    msg.addReaction(emoji).queue();
                    returnMSG.set(msg);
                }
            });
            if (deleteMSG){
                message.delete().queueAfter(deleteAfterMS, TimeUnit.MILLISECONDS);
            }
            return returnMSG.get();
        } else {
            List<String> finalReactions = reactions;
            AtomicReference<Message> returnMSG = new AtomicReference<>();
            channel.sendMessage(this.build()).queue(msg ->{
                for (String emoji : finalReactions){
                    msg.addReaction(emoji).queue();
                    returnMSG.set(msg);
                }
            });
            return returnMSG.get();
        }
    }
}