package me.stalexgaming.colordrops.listeners;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.GameState;
import me.stalexgaming.colordrops.enums.Team;
import me.stalexgaming.colordrops.player.SPlayer;
import me.stalexgaming.colordrops.utils.Color;
import me.stalexgaming.colordrops.utils.Title;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Listeners implements Listener {

    Main plugin;

    public static boolean released = false;

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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if ((int) e.getFrom().getX() != (int) e.getTo().getX() || (int) e.getFrom().getZ() != (int) e.getTo().getZ()) {
            if (!released) {
                if (GameState.getState() == GameState.INGAME) {
                    e.getPlayer().teleport(e.getFrom());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SPECTATOR);

        new BukkitRunnable(){
            int i = 10;
            public void run() {
                i--;
                if(i > 0) {
                    Title title = new Title(Color.np("&6Respawning in &e" + String.valueOf(i)), "", 5, 10, 5);
                    title.sendToPlayer(p);
                } else {
                    //TODO: add respawn loc
                    p.setGameMode(GameMode.SURVIVAL);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

}
