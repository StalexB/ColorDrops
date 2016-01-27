package me.stalexgaming.colordrops.utils;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Copyright by Bankras, created on 23-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Minecart {

    private org.bukkit.entity.Minecart minecart;
    private Location beginLoc;
    private String xOrZ;

    public Minecart(org.bukkit.entity.Minecart minecart, String xOrZ){
        this.minecart = minecart;
        this.beginLoc = minecart.getLocation();
        this.xOrZ = xOrZ;
    }

    public Location getBeginLoc(){
        return beginLoc;
    }

    public void reset(){
        minecart.teleport(beginLoc);
    }

    public void remove(){
        minecart.remove();
    }

    public org.bukkit.entity.Minecart getMinecart(){
        return minecart;
    }

    public void moveMineCart(Team team){
        Location loc = minecart.getLocation();
        if(xOrZ.equalsIgnoreCase("x")){
            if(team == Team.RED) {
                minecart.setVelocity(new Vector(0.5, 0, 0));
                stop(loc, "plus");
            } else if(team == Team.BLUE){
                minecart.setVelocity(new Vector(-0.5, 0, 0));
                stop(loc, "minus");
            }
        } else if(xOrZ.equalsIgnoreCase("z")){
            if(team == Team.RED) {
                minecart.setVelocity(new Vector(0, 0, 0.5));
                stop(loc, "plus");
            } else if(team == Team.BLUE){
                minecart.setVelocity(new Vector(0, 0, -0.5));
                stop(loc, "minus");
            }
        }
    }

    public void stop(Location loc, String plusOrMinus) {
        if (xOrZ.equalsIgnoreCase("x")) {
            if (plusOrMinus.equalsIgnoreCase("plus")) {
                Location wanted = loc.clone().add(3, 0, 0);
                new BukkitRunnable() {
                    public void run() {
                        if ((int) minecart.getLocation().getX() == (int) wanted.getX()) {
                            minecart.setVelocity(new Vector(0, 0, 0));
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 2);
            } else {
                Location wanted = loc.clone().subtract(3, 0, 0);
                new BukkitRunnable() {
                    public void run() {
                        if ((int) minecart.getLocation().getX() == (int) wanted.getX()) {
                            minecart.setVelocity(new Vector(0, 0, 0));
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 2);
            }
        } else if (xOrZ.equalsIgnoreCase("z")) {
            if (plusOrMinus.equalsIgnoreCase("plus")) {
                Location wanted = loc.clone().add(0, 0, 3);
                new BukkitRunnable() {
                    public void run() {
                        if ((int) minecart.getLocation().getZ() == (int) wanted.getZ()) {
                            minecart.setVelocity(new Vector(0, 0, 0));
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 2);
            } else {
                Location wanted = loc.clone().subtract(0, 0, 3);
                new BukkitRunnable() {
                    public void run() {
                        if ((int) minecart.getLocation().getZ() == (int) wanted.getZ()) {
                            minecart.setVelocity(new Vector(0, 0, 0));
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 2);
            }
        }
    }

}
