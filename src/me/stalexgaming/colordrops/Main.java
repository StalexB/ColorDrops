package me.stalexgaming.colordrops;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.stalexgaming.colordrops.commands.Commands;
import me.stalexgaming.colordrops.listeners.Listeners;
import me.stalexgaming.colordrops.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Main extends JavaPlugin {

    private static Main instance;

    public File locations = null;

    public int minimumPlayers;

    GameManager gameManager = GameManager.getInstance();

    public void onEnable(){
        instance = this;

        locations = new File(this.getDataFolder(), "locations.yml");

        registerCommands();
        registerListeners();

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        minimumPlayers = getConfig().getInt("minimum-players");

        gameManager.countdown();
    }

    public WorldEditPlugin getWorldEditInstance(){
        Plugin p = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if(p instanceof WorldEditPlugin){
            return (WorldEditPlugin) p;
        }
        return null;
    }

    public void onDisable(){
        instance = null;
    }

    public static Main getInstance(){
        return instance;
    }

    private void registerCommands(){
        this.getCommand("colordrops").setExecutor(new Commands(this));
    }

    private void registerListeners(){
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new Listeners(this), this);
    }

}