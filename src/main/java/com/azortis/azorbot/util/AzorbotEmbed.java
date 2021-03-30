package com.azortis.azorbot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import com.azortis.azorbot.Main;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AzorbotEmbed extends EmbedBuilder {
    private final Message message;

    /**
     * Creates a default, empty, AzorbotEmbed object.
     */
    public AzorbotEmbed(){
        this.message = null;
        this.setColor(Color.decode(Main.botColor))
                .setFooter(Main.botCompany);
    }

    /**
     * Creates a default AzorbotEmbed object.
     * @param title The title of the embed
     */
    public AzorbotEmbed(String title){
        this.message = null;
        this.setTitle(title)
                .setColor(Color.decode(Main.botColor))
                .setFooter(Main.botCompany);
    }

    /**
     * Creates a default AzorbotEmbed object.
     * @param title The title of the embed
     * @param name The name greeted in the top of the embed
     */
    public AzorbotEmbed(String title, String name){
        this.message = null;
        this.setAuthor("Requested by: " + name, null)
                .setTitle(!title.equals("") ? title : "\u200E")
                .setColor(Color.decode(Main.botColor))
                .setFooter(Main.botCompany);
    }

    /**
     * Creates a default AzorbotEmbed object.
     * @param title The title of the embed
     * @param message Message used to greet used and make further command easier
     */
    public AzorbotEmbed(String title, Message message){
        this.message = message;
        this.setAuthor("Requested by: " + message.getAuthor().getName(), null, message.getAuthor().getAvatarUrl())
                .setTitle(!title.equals("") ? title : "\u200E")
                .setColor(Color.decode(Main.botColor))
                .setFooter(Main.botCompany);
    }

    /**
     * Creates a default AzorbotEmbed object.
     * @param title The title of the embed
     * @param useShort Toggles if there should be a mention of the bot company
     */
    public AzorbotEmbed(String title, boolean useShort){
        this.message = null;
        this.setTitle(title).setColor(Color.decode(Main.botColor));
        if (!useShort){
            this.setFooter(Main.botCompany);
        }
    }


    /**
     * Send the embed in the channel of the original message, if passed with the initial command.
     */
    public void send(){
        this.send(this.message, null, false, 0, null);
    }

    /**
     * Send the embed in the channel of the original message, if passed with the initial command, with added reactions
     * @param reactions And add these reactions to the message
     */
    public void send(List<String> reactions){
        this.send(this.message, null, false, 0, reactions);
    }

    /**
     * Send the embed
     * @param channel The channel where the embed is sent to
     */
    public void send(TextChannel channel){
        this.send(null, channel, false, 0, null);
    }

    /**
     * Send the embed 
     * @param channel The channel where the embed is sent to
     * @param reactions And add these reactions to the message
     */
    public void send(TextChannel channel, List<String> reactions){
        this.send(null, channel, false, 0, reactions);
    }

    /**
     * Send the embed
     * @param message In the channel of this message
     */
    public void send(Message message){
        this.send(message, false);
    }

    /**
     * Send the embed 
     * @param message In the channel of this message
     * @param reactions And add these reactions to the message
     */
    public void send(Message message, List<String> reactions){
        this.send(message, false, reactions);
    }


    /**
     * Send the embed
     * @param message In the channel of this message
     * @param deleteMSG And delete the specified message
     */
    public void send(Message message, boolean deleteMSG){
        this.send(message, deleteMSG, 0);
    }

    /**
     * Send the embed 
     * @param message In the channel of this message
     * @param deleteMSG And delete the specified message
     * @param reactions And add these reactions to the message
     */
    public void send(Message message, boolean deleteMSG, List<String> reactions){
        this.send(message, deleteMSG, 0, reactions);
    }


    /**
     * Send the embed
     * @param message In the channel of this message
     * @param deleteMSG And delete the specified message
     * @param deleteAfterMS After X ms
     */
    public void send(Message message, boolean deleteMSG, int deleteAfterMS){
        this.send(message, null, deleteMSG, deleteAfterMS, null);
    }

    /**
     * Send the embed 
     * @param message In the channel of this message
     * @param deleteMSG And delete the specified message
     * @param deleteAfterMS After X ms
     * @param reactions And add these reactions to the message
     */
    public void send(Message message, boolean deleteMSG, int deleteAfterMS, List<String> reactions){
        this.send(message, null, deleteMSG, deleteAfterMS, reactions);
    }

    /**
     * Send the embed
     * @param message In the channel of this message
     * @param channel Or, if message is null, in this channel
     * @param deleteMSG And delete the message
     * @param deleteAfterMS After X ms
     * @param reactions And add these reactions to the message
     */
    public void send(Message message, TextChannel channel, boolean deleteMSG, int deleteAfterMS, List<String> reactions){
        if (reactions == null) reactions = new ArrayList<>();
        if (message == null && channel == null){
            Main.error("No channel and message specified.");
        } else if (message != null){
            List<String> finalReactions = reactions;
            message.getChannel().sendMessage(this.build()).queue(msg ->{
                for (String emoji : finalReactions){
                    msg.addReaction(emoji).queue();
                }
            });
            if (deleteMSG){
                message.delete().queueAfter(deleteAfterMS, TimeUnit.MILLISECONDS);
            }
        } else {
            List<String> finalReactions = reactions;
            channel.sendMessage(this.build()).queue(msg ->{
                for (String emoji : finalReactions){
                    msg.addReaction(emoji).queue();
                }
            });
        }
    }
}