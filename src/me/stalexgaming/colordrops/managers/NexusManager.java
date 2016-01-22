package me.stalexgaming.colordrops.managers;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
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

    public void startNexus(){
        for(Location b : Main.getInstance().nexus){
            b.getBlock().setTypeIdAndData(159, (byte) 3, true);
        }

        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if(i % 30 == 0){
                    int color = getRandomColor(0);

                    for(Location b : Main.getInstance().nexus){
                        b.getBlock().setTypeIdAndData(159, (byte) color, true);
                    }

                    Bukkit.broadcastMessage(Color.np("&6The Nexus has changed to a different color block!"));
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public int getRandomColor(int current){
        Random r = new Random();
        List<Integer> colors = new ArrayList<>();
        colors.add(1);
        colors.add(14);
        colors.add(4);
        colors.add(8);

        int random = r.nextInt(3);
        while(colors.get(random) == current){
            random = r.nextInt(3);
        }
        return colors.get(random);
    }

}
