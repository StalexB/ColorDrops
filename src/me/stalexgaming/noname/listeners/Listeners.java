package me.stalexgaming.noname.listeners;

import me.stalexgaming.noname.Main;
import me.stalexgaming.noname.managers.TeamManager;
import me.stalexgaming.noname.player.SPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Listeners implements Listener {

    Main plugin;

    public TeamManager teamManager = TeamManager.getInstance();

    public Listeners(Main main){
        this.plugin = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        SPlayer player = new SPlayer(p);

        int blue = teamManager.getTeamPlayers(TeamManager.Team.BLUE).size();
        int red = teamManager.getTeamPlayers(TeamManager.Team.RED).size();

        if(blue <= red){
            player.setTeam(TeamManager.Team.BLUE);
        } else {
            player.setTeam(TeamManager.Team.RED);
        }
    }

}
