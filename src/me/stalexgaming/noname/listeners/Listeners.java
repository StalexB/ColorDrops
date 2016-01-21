package me.stalexgaming.noname.listeners;

import me.stalexgaming.noname.Main;
import me.stalexgaming.noname.enums.GameState;
import me.stalexgaming.noname.enums.Team;
import me.stalexgaming.noname.managers.TeamManager;
import me.stalexgaming.noname.player.SPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Listeners implements Listener {

    Main plugin;

    public boolean released = false;

    public Listeners(Main main){
        this.plugin = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        SPlayer player = new SPlayer(p);

        int blue = Team.BLUE.getPlayers().size();
        int red = Team.RED.getPlayers().size();

        if(blue <= red){
            player.setTeam(Team.BLUE);
        } else {
            player.setTeam(Team.RED);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        SPlayer player = new SPlayer(p);

        player.removePlayer();
    }

    public void onPlayerMove(PlayerMoveEvent e) {
        if ((int) e.getFrom().getX() != (int) e.getTo().getX() || (int) e.getFrom().getZ() != (int) e.getTo().getZ()) {
            if (!released) {
                if (GameState.getState() == GameState.INGAME) {
                    e.getPlayer().teleport(e.getFrom());
                }
            }
        }
    }

}
