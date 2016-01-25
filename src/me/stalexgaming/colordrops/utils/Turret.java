package me.stalexgaming.colordrops.utils;

import me.stalexgaming.colordrops.Main;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * Copyright by Bankras, created on 25-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Turret {

    private Location playerLoc;
    private Player p;
    private boolean canShoot = true;

    public Turret(Location loc, Player p){
        this.playerLoc = loc;
        this.p = p;
    }

    public Player getUser(){
        return p;
    }

    public void setUser(Player p){
        this.p = p;
    }

    public Location getLocation(){
        return playerLoc;
    }

    public void shoot(){
        if(containsUser()) {
            if(canShoot) {
                Vector v = p.getLocation().getDirection().normalize().multiply(5);

                Snowball s = p.launchProjectile(Snowball.class, v);
                s.setCustomName("stun");

                p.playSound(p.getLocation(), Sound.FIREWORK_BLAST2, 3F, 1F);

                canShoot = false;
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        canShoot = true;
                    }
                }.runTaskLater(Main.getInstance(), 40);
            } else {
                p.sendMessage(Color.np("&cThis turret is reloading!"));
            }
        }
    }

    public boolean containsUser(){
        for(Entity e : playerLoc.getWorld().getNearbyEntities(playerLoc, 1, 1, 1)){
            if(e instanceof Player){
                if(e == p) {
                    return true;
                }
            }
        }
        return false;
    }
}
