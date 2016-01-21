package me.stalexgaming.noname.managers;

import me.stalexgaming.noname.enums.Team;
import me.stalexgaming.noname.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class TeamManager {

    private static TeamManager instance = new TeamManager();

    public HashMap<String, Team> playerTeams = new HashMap<>();

    public static TeamManager getInstance(){
        return instance;
    }

    public boolean hasTeam(Player p){
        return playerTeams.containsKey(p.getName());
    }

    public Team getTeam(Player p){
        if(hasTeam(p)){
            return playerTeams.get(p.getName());
        }
        return null;
    }

    public void setTeam(Player p, Team team){
        playerTeams.put(p.getName(), team);

        if(team == Team.RED) {
            p.sendMessage(" ");
            p.sendMessage(Color.np("&6&l               No Name"));
            p.sendMessage(Color.np("&6&l     You joined the &c&lred &6&lteam!"));
            p.sendMessage(" ");
        } else {
            p.sendMessage(" ");
            p.sendMessage(Color.np("&6&l               No Name"));
            p.sendMessage(Color.np("&6&l     You joined the &b&lblue &6&lteam!"));
            p.sendMessage(" ");
        }
    }

    public List<String> getTeamPlayers(Team team){
        List<String> players = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()){
            if(getTeam(p) == team){
                players.add(p.getName());
            }
        }
        return players;
    }

    public void removePlayer(Player p){
        Team team = getTeam(p);
        playerTeams.remove(p.getName());

        Bukkit.broadcastMessage(Color.np(getTeamColor(team) + p.getName() + " has left the game."));
    }

    public String getTeamColor(Team team){
        if(team == Team.RED){
            return "&c";
        } else if(team == Team.BLUE){
            return "&b";
        }
        return "&f";
    }

}
