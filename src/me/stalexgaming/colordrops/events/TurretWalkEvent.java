package me.stalexgaming.colordrops.events;

import me.stalexgaming.colordrops.utils.Turret;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Copyright by Bankras, created on 25-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class TurretWalkEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player p;
    private Turret t;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public TurretWalkEvent(Player p, Turret t){
        this.p = p;
        this.t = t;
    }

    public Player getPlayer(){
        return p;
    }

    public Turret getTurret(){
        return t;
    }

    public boolean isOccupied(){
        for(Entity e : t.getLocation().getWorld().getNearbyEntities(t.getLocation(), 1, 1, 1)){
            if(e instanceof Player){
                if(e != p) {
                    return true;
                }
            }
        }
        return false;
    }

}
