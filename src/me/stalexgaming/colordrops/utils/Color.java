package me.stalexgaming.colordrops.utils;

import me.stalexgaming.colordrops.Main;
import org.bukkit.ChatColor;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Color {

    private static String prefix = Main.getInstance().getConfig().getString("prefix");

    public static String np(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String p(String s){
        return ChatColor.translateAlternateColorCodes('&', prefix + s);
    }

}
