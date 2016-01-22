package me.stalexgaming.colordrops.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Title {

    private PacketPlayOutTitle packet;

    private String title;
    private String subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public Title(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public void sendToPlayer(Player p){
        if(title != null) {
            packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"));
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
        if(subtitle != null){
            packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"));
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void sendToAllPlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(title != null) {
                packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"));
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
            if(subtitle != null){
                packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"));
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }
}
