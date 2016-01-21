package me.stalexgaming.noname.enums;

import me.stalexgaming.noname.managers.TeamManager;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public enum Team {

    RED("&c"), BLUE("&b");

    public TeamManager teamManager = TeamManager.getInstance();

    String color;

    Team(String color){
        this.color = color;
    }

    public String getColor(){
        return color;
    }

    public List<String> getPlayers(){
        return teamManager.getTeamPlayers(this);
    }

    public boolean containsPlayer(Player p){
        for(String s : getPlayers()){
            if(s.equalsIgnoreCase(p.getName())){
                return true;
            }
        }
        return false;
    }

    public void addPlayer(Player p){
        teamManager.setTeam(p, this);
    }
}
