package me.stalexgaming.colordrops.enums;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright by Bankras, created on 22-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public enum Area {

    RED_SPAWN, BLUE_SPAWN, BLOCKSPAWN_1, BLOCKSPAWN_2, BLOCKSPAWN_3, BLOCKSPAWN_4, BLOCKSPAWN_5, BLOCKSPAWN_6, BLOCKSPAWN_7, BLOCKSPAWN_8;

    LocationUtil locationUtil = LocationUtil.getInstance();

    public static List<Area> getAreas(){
        List<Area> areas = new ArrayList<>();

        areas.add(RED_SPAWN);
        areas.add(BLUE_SPAWN);
        areas.add(BLOCKSPAWN_1);
        areas.add(BLOCKSPAWN_2);
        areas.add(BLOCKSPAWN_3);
        areas.add(BLOCKSPAWN_4);
        areas.add(BLOCKSPAWN_5);
        areas.add(BLOCKSPAWN_6);
        areas.add(BLOCKSPAWN_7);
        areas.add(BLOCKSPAWN_8);

        return areas;
    }

    public static List<Area> getBlockSpawns(){
        List<Area> areas = new ArrayList<>();

        areas.add(BLOCKSPAWN_1);
        areas.add(BLOCKSPAWN_2);
        areas.add(BLOCKSPAWN_3);
        areas.add(BLOCKSPAWN_4);
        areas.add(BLOCKSPAWN_5);
        areas.add(BLOCKSPAWN_6);
        areas.add(BLOCKSPAWN_7);
        areas.add(BLOCKSPAWN_8);

        return areas;
    }

    public Location getBlockSpawnBlock(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        if(this != RED_SPAWN && this != BLUE_SPAWN){
            switch (this){
                case BLOCKSPAWN_1:
                    return locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns.1"));
                case BLOCKSPAWN_2:
                    return locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns.2"));
                case BLOCKSPAWN_3:
                    return locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns.3"));
                case BLOCKSPAWN_4:
                    return locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns.4"));
                case BLOCKSPAWN_5:
                    return locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns.5"));
                case BLOCKSPAWN_6:
                    return locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns.6"));
                case BLOCKSPAWN_7:
                    return locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns.7"));
                case BLOCKSPAWN_8:
                    return locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns.8"));
                default:
                    return null;
            }
        }
        return null;
    }

}
