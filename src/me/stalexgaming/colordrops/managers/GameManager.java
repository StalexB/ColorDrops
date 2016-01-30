package me.stalexgaming.colordrops.managers;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.GameState;
import me.stalexgaming.colordrops.enums.Team;
import me.stalexgaming.colordrops.listeners.Listeners;
import me.stalexgaming.colordrops.utils.Color;
import me.stalexgaming.colordrops.utils.LocationUtil;
import me.stalexgaming.colordrops.utils.Minecart;
import me.stalexgaming.colordrops.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class GameManager {

    private static GameManager instance = new GameManager();

    public static GameManager getInstance(){
        return instance;
    }

    private static HashMap<String, Integer> carrying = new HashMap<>();

    private static int minecart = 0;

    private static Minecart mc;

    ArenaManager arenaManager = ArenaManager.getInstance();
    TeamManager teamManager = TeamManager.getInstance();
    LocationUtil locationUtil = LocationUtil.getInstance();
    NexusManager nexusManager = NexusManager.getInstance();
    BlockManager blockManager = BlockManager.getInstance();

    public void countdown(){
        if(arenaManager.isArenaReady()) {
            int minimumPlayers = Main.getInstance().minimumPlayers;
            new BukkitRunnable() {
                int counter = 10;

                public void run() {
                    if (Bukkit.getOnlinePlayers().size() >= minimumPlayers) {
                        if (GameState.getState() == GameState.LOBBY) {
                            if (counter % 5 == 0 && counter != 0) {
                                Bukkit.broadcastMessage(Color.np("&6Moving to arena in &e" + counter + "&6 seconds."));
                            }
                            if (counter == 0) startGame();
                            counter--;
                            if (Bukkit.getOnlinePlayers().size() == 10 && counter > 10) {
                                counter = 10;
                            }
                        } else {
                            this.cancel();
                        }
                    } else {
                        counter = 10;
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
    }

    public void startGame(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        GameState.setState(GameState.INGAME);
        for(Location loc : Main.getInstance().nexus){
            loc.getBlock().setTypeIdAndData(159, (byte) 8, false);
        }

        for(int i = 1; i < 9; i++){
            Location loc = locationUtil.deserializeLoc(locationsFile.getString("arena.blockspawns." + i));
            loc.getBlock().setTypeIdAndData(159, (byte) 8, false);
        }
        Location loc = locationUtil.deserializeLoc(locationsFile.getString("arena.minecart"));
        org.bukkit.entity.Minecart mc = (org.bukkit.entity.Minecart) loc.getWorld().spawnEntity(loc, EntityType.MINECART);

        Minecart minecart = new Minecart(mc, locationsFile.getString("arena.minecartxorz"));

        this.mc = minecart;

        for(Team team : teamManager.getTeams()){
            int i = 1;
            for(String s : teamManager.getTeamPlayers(team)){
                if(Bukkit.getPlayer(s) != null){
                    Player t = Bukkit.getPlayer(s);

                    t.teleport(locationUtil.deserializeLoc(locationsFile.getString("arena.spawns." + team.toString().toLowerCase() + "." + i)));
                    i++;
                }
                continue;
            }
            Main.getInstance().initializeBlocks();
        }

        new BukkitRunnable(){
            int counter = 10;
            public void run() {
                if(counter != 0) {
                    Bukkit.broadcastMessage(Color.np("&6ColorDrops starting in &e" + counter + "&6..."));
                    counter--;
                } else {
                    nexusManager.startNexus();
                    Listeners.released = true;
                    Bukkit.broadcastMessage(Color.np("&6The game has started!"));

                    Title title = new Title(Color.np("&6The game has started!"), "", 10, 30, 10);
                    title.sendToAllPlayers();

                    this.cancel();

                    startCounter();
                    setTimeLimit();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void setTimeLimit(){
        new BukkitRunnable(){
            int timer = 600;

            @Override
            public void run() {
                timer--;
                if(timer == 30 || timer == 10 || timer == 5){
                    Bukkit.broadcastMessage(Color.np("&3Time limit reached in &b" + timer + " &3seconds!"));
                }
                if(timer <= 0){
                    this.cancel();
                    if(minecart < 0){
                        GameState.setState(GameState.ENDING);
                        blockManager.resetAll();
                        Title win = new Title(Color.np("&aYou won!"), "", 15, 40, 15);
                        win.sendToTeam(Team.BLUE);
                        Title lose = new Title(Color.np("&cYou lost!"), "", 15, 40, 15);
                        lose.sendToTeam(Team.RED);

                        Bukkit.broadcastMessage(Color.np("&6ColorDrops was won by team &b&lBLUE&6!"));

                        Main.isGameWon = true;
                        GameState.setState(GameState.ENDING);

                        new BukkitRunnable(){
                            public void run() {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);
                                    try {
                                        out.writeUTF("Connect");
                                        out.writeUTF("lobby");
                                    } catch (IOException ex) {}
                                    p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
                                }
                                mc.remove();
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.getServer().shutdown();
                                    }
                                }.runTaskLater(Main.getInstance(), 40);
                            }
                        }.runTaskLater(Main.getInstance(), 300);
                    } else if(minecart > 0){
                        GameState.setState(GameState.ENDING);
                        Title win = new Title(Color.np("&aYou won!"), "", 15, 40, 15);
                        win.sendToTeam(Team.RED);
                        Title lose = new Title(Color.np("&cYou lost!"), "", 15, 40, 15);
                        lose.sendToTeam(Team.BLUE);

                        Bukkit.broadcastMessage(Color.np("&6ColorDrops was won by team &c&lRED&6!"));

                        Main.isGameWon = true;
                        GameState.setState(GameState.ENDING);

                        new BukkitRunnable(){
                            public void run() {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);
                                    try {
                                        out.writeUTF("Connect");
                                        out.writeUTF("lobby");
                                    } catch (IOException ex) {}
                                    p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
                                }
                                mc.remove();
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.getServer().shutdown();
                                    }
                                }.runTaskLater(Main.getInstance(), 40);
                            }
                        }.runTaskLater(Main.getInstance(), 300);
                    } else {
                        GameState.setState(GameState.ENDING);
                        Title tie = new Title(Color.np("&3It's a tie!"), "", 15, 40, 15);
                        tie.sendToAllPlayers();

                        Bukkit.broadcastMessage(Color.np("&6ColorDrops has ended in a tie!"));

                        Main.isGameWon = true;
                        GameState.setState(GameState.ENDING);

                        new BukkitRunnable(){
                            public void run() {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);
                                    try {
                                        out.writeUTF("Connect");
                                        out.writeUTF("lobby");
                                    } catch (IOException ex) {}
                                    p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
                                }
                                mc.remove();
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.getServer().shutdown();
                                    }
                                }.runTaskLater(Main.getInstance(), 40);
                            }
                        }.runTaskLater(Main.getInstance(), 300);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void startCounter(){
        new BukkitRunnable(){
            int i = 600;
            public void run() {
                i--;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public static void addPoint(Team team){
        if(team == Team.BLUE){
            minecart--;
            mc.moveMineCart(Team.BLUE);
            checkWin();
        } else if(team == Team.RED){
            minecart++;
            mc.moveMineCart(Team.RED);
            checkWin();
        }
    }

    public static void checkWin(){
        if(minecart <= -4){
            GameState.setState(GameState.ENDING);
            Title win = new Title(Color.np("&aYou won!"), "", 15, 40, 15);
            win.sendToTeam(Team.BLUE);
            Title lose = new Title(Color.np("&cYou lost!"), "", 15, 40, 15);
            lose.sendToTeam(Team.RED);

            Bukkit.broadcastMessage(Color.np("&6ColorDrops was won by team &b&lBLUE&6!"));

            Main.isGameWon = true;
            GameState.setState(GameState.ENDING);

            new BukkitRunnable(){
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        try {
                            out.writeUTF("Connect");
                            out.writeUTF("lobby");
                        } catch (IOException ex) {}
                        p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
                    }
                    mc.remove();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().shutdown();
                        }
                    }.runTaskLater(Main.getInstance(), 40);
                }
            }.runTaskLater(Main.getInstance(), 300);
        } else if(minecart >= 4){
            GameState.setState(GameState.ENDING);
            Title win = new Title(Color.np("&aYou won!"), "", 15, 40, 15);
            win.sendToTeam(Team.RED);
            Title lose = new Title(Color.np("&cYou lost!"), "", 15, 40, 15);
            lose.sendToTeam(Team.BLUE);

            Bukkit.broadcastMessage(Color.np("&6ColorDrops was won by team &c&lRED&6!"));

            Main.isGameWon = true;
            GameState.setState(GameState.ENDING);

            new BukkitRunnable(){
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        try {
                            out.writeUTF("Connect");
                            out.writeUTF("lobby");
                        } catch (IOException ex) {}
                        p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
                    }
                    mc.remove();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().shutdown();
                        }
                    }.runTaskLater(Main.getInstance(), 40);
                }
            }.runTaskLater(Main.getInstance(), 300);
        }
    }

    public static int getMinecart(){
        return minecart;
    }

    public static void setCarrying(Player p, int i){
        carrying.put(p.getName(), i);
    }

    public static Integer getCarrying(Player p){
        try {
            if (carrying.get(p.getName()) != null) {
                int color = carrying.get(p.getName());
                return color;
            } else {
                return 0;
            }
        } catch (Exception ex){
            return 0;
        }
    }

}
