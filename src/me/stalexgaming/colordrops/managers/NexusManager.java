package me.stalexgaming.colordrops.managers;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.Area;
import me.stalexgaming.colordrops.utils.Color;
import me.stalexgaming.colordrops.utils.LocationUtil;
import me.stalexgaming.colordrops.utils.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Copyright by Bankras, created on 22-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class NexusManager {

    private static NexusManager instance = new NexusManager();

    public static NexusManager getInstance(){
        return instance;
    }

    LocationUtil locationUtil = LocationUtil.getInstance();
    ScoreboardUtil scoreboardUtil = ScoreboardUtil.getInstance();

    private HashMap<Area, Integer> blockSpawns = new HashMap<>();
    private int currentNexusColor = 14;

    private boolean generateNewNexus = false;

    public void startNexus(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if(!Main.isGameWon) {
                    if (generateNewNexus) {
                        i = 120;
                        generateNewNexus = false;
                    }
                    if (i % 120 == 0) {
                        int color = getRandomColor(0);

                        for (Location b : Main.getInstance().nexus) {
                            b.getWorld().getBlockAt(b).setTypeIdAndData(159, (byte) color, false);
                        }
                        currentNexusColor = color;

                        Area a = getRandomBlockSpawn();
                        int areaId = 0;

                        int t = 1;
                        for (Location loc : getBlockSpawnLocations()) {
                            Area area = getBlockSpawnArea(t);
                            if (area != a) {
                                int random = getRandomColor(currentNexusColor);
                                loc.getBlock().setTypeIdAndData(159, (byte) random, false);
                                blockSpawns.put(area, random);
                                t++;
                            } else {
                                areaId = t;
                                t++;
                            }
                        }

                        Location loc = getBlockSpawnLocations().get(areaId - 1);
                        loc.getBlock().setTypeIdAndData(159, (byte) currentNexusColor, false);
                        blockSpawns.put(a, currentNexusColor);

                        Bukkit.broadcastMessage(Color.np("&6The Nexus has changed its color to " + scoreboardUtil.getColorName(currentNexusColor) + "&6!"));
                        i++;
                    }
                    i++;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 5);
    }

    public void generateNewNexus(){
        generateNewNexus = true;
    }

    public int getRandomColor(int current){
        Random r = new Random();
        List<Integer> colors = new ArrayList<>();
        colors.add(1);
        colors.add(14);
        colors.add(4);
        colors.add(8);

        int random = r.nextInt(4);
        while(colors.get(random) == current){
            random = r.nextInt(4);
        }
        return colors.get(random);
    }

    public List<Location> getBlockSpawnLocations(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        List<Location> locs = new ArrayList<>();
        int i = 1;
        for(Area a : Area.getBlockSpawns()){
            locs.add(locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns." + i)));
            i++;
        }

        return locs;
    }

    public Area getBlockSpawnArea(int i){
        switch(i){
            case 1:
                return Area.BLOCKSPAWN_1;
            case 2:
                return Area.BLOCKSPAWN_2;
            case 3:
                return Area.BLOCKSPAWN_3;
            case 4:
                return Area.BLOCKSPAWN_4;
            case 5:
                return Area.BLOCKSPAWN_5;
            case 6:
                return Area.BLOCKSPAWN_6;
            case 7:
                return Area.BLOCKSPAWN_7;
            case 8:
                return Area.BLOCKSPAWN_8;
            default:
                return null;
        }
    }

    public Area getRandomBlockSpawn(){
        Random r = new Random();
        int random = r.nextInt(8)-1;
        if(random < 0){
            return Area.getBlockSpawns().get(0);
        } else {
            return Area.getBlockSpawns().get(random);
        }
    }

    public int getColor(Area a){
        return blockSpawns.get(a);
    }

    public int getCurrentNexusColor(){
        return currentNexusColor;
    }
}
