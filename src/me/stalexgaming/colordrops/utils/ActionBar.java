package me.stalexgaming.colordrops.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class ActionBar {

    private PacketPlayOutChat packet;

    public ActionBar(String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
        this.packet = packet;
    }

    public void sendToPlayer(Player p) {
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendToAllPlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
        }
    }

}