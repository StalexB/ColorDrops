package me.stalexgaming.noname.player;

import me.stalexgaming.noname.enums.Team;
import me.stalexgaming.noname.managers.TeamManager;
import org.bukkit.entity.Player;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class SPlayer {

    private Player p;

    public TeamManager teamManager = TeamManager.getInstance();

    public SPlayer(Player p){
        this.p = p;
    }

    public Team getTeam(){
        return teamManager.getTeam(p);
    }

    public boolean hasTeam(){
        return teamManager.hasTeam(p);
    }

    public void setTeam(Team team){
        teamManager.setTeam(p, team);
    }

    public void removePlayer(){
        teamManager.removePlayer(p);
    }

}
