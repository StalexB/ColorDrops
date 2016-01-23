package me.stalexgaming.colordrops.events;

import me.stalexgaming.colordrops.enums.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Copyright by Bankras, created on 22-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class AreaWalkEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player p;
    private Area a;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AreaWalkEvent(Area a, Player p){
        this.a = a;
        this.p = p;
    }

    public Area getArea(){
        return a;
    }

    public Player getPlayer(){
        return p;
    }

    public boolean isBlockSpawn(){
        return Area.getBlockSpawns().contains(a);
    }

}
