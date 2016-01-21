package me.stalexgaming.noname.managers;

import me.stalexgaming.noname.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class ArenaManager {

    private static ArenaManager instance = new ArenaManager();

    public static ArenaManager getInstance(){
        return instance;
    }

    public boolean isArenaReady(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);

        if(locationsFile.getString("arena.lobby") != null){
            for(int i = 1; i < 6; i++){
                if(locationsFile.getString("arena.spawns.red." + i) != null){
                    continue;
                } else {
                    return false;
                }
            }
            for(int i = 1; i < 9; i++){
                if(locationsFile.getString("arena.blockspawns." + i) != null){
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
