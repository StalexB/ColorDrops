package me.stalexgaming.noname.utils;

import me.stalexgaming.noname.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.logging.Level;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class LocationUtil {

    private static LocationUtil instance = new LocationUtil();

    public static LocationUtil getInstance(){
        return instance;
    }

    public String serializeLocation(Location loc){
        return String.valueOf(loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName());
    }

    public Location deserializeLoc(String locString){
        try {
            String[] data = locString.split(";");
            double x = Double.parseDouble(data[0]), y = Double.parseDouble(data[1]), z = Double.parseDouble(data[2]);
            float yaw = Float.parseFloat(data[3]), pitch = Float.parseFloat(data[4]);
            String world = data[5];

            Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            return loc;
        } catch(Exception ex){
            Main.getInstance().getLogger().log(Level.SEVERE, "[NoName] Error with location:" + locString);
            return null;
        }
    }

}
