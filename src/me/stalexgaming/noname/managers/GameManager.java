package me.stalexgaming.noname.managers;

import me.stalexgaming.noname.Main;
import me.stalexgaming.noname.enums.GameState;
import me.stalexgaming.noname.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class GameManager {

    private static GameManager instance = new GameManager();

    public static GameManager getInstance(){
        return instance;
    }

    public void countdown(){
        int minimumPlayers = Main.getInstance().minimumPlayers;
        new BukkitRunnable(){
            int counter = 45;
            public void run() {
                if(Bukkit.getOnlinePlayers().size() >= minimumPlayers){
                    if(GameState.getState() == GameState.LOBBY){
                        if(counter % 5 == 0 && counter != 0){ Bukkit.broadcastMessage(Color.np("&6Moving to arena in &e" + counter + "&6 seconds.")); }
                        if(counter == 0) startGame();
                        counter--;
                        if(Bukkit.getOnlinePlayers().size() == 10 && counter > 10) { counter = 10; }
                    } else {
                        this.cancel();
                    }
                } else {
                    counter = 45;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void startGame(){

    }

}
