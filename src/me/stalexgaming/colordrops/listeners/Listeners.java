package me.stalexgaming.colordrops.listeners;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.Area;
import me.stalexgaming.colordrops.enums.GameState;
import me.stalexgaming.colordrops.enums.Team;
import me.stalexgaming.colordrops.events.AreaWalkEvent;
import me.stalexgaming.colordrops.events.TurretWalkEvent;
import me.stalexgaming.colordrops.managers.GameManager;
import me.stalexgaming.colordrops.managers.NexusManager;
import me.stalexgaming.colordrops.managers.TeamManager;
import me.stalexgaming.colordrops.player.SPlayer;
import me.stalexgaming.colordrops.utils.*;
import me.stalexgaming.colordrops.utils.Color;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCollisionEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Listeners implements Listener {

    Main plugin;

    private ArrayList<String> stunned = new ArrayList<>();

    private static Location neededBlock;

    public static boolean released = false;
    public static boolean isPickedUp = false;
    public static String neededBlockMaterial = "0;0";

    public Listeners(Main main){
        this.plugin = main;
    }

    LocationUtil locationUtil = LocationUtil.getInstance();
    TeamManager teamManager = TeamManager.getInstance();
    NexusManager nexusManager = NexusManager.getInstance();
    GameManager gameManager = GameManager.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);

        Player p = e.getPlayer();
        SPlayer player = new SPlayer(p);

        int blue = Team.BLUE.getPlayers().size();
        int red = Team.RED.getPlayers().size();

        p.teleport(locationUtil.deserializeLoc(locationsFile.getString("arena.lobby")));
        gameManager.setCarrying(p, 0);


        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        org.bukkit.scoreboard.Team r = sb.registerNewTeam("red");
        r.setPrefix(Color.np("&c"));
        org.bukkit.scoreboard.Team b = sb.registerNewTeam("blue");
        b.setPrefix(Color.np("&b"));
        for(Player online : Bukkit.getOnlinePlayers()){
            Team team = teamManager.getTeam(online);
            if(team == Team.BLUE){
                b.addEntry(online.getName());
            } else {
                r.addEntry(online.getName());
            }
        }

        p.setScoreboard(sb);

        e.setJoinMessage(null);

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

        e.setQuitMessage(null);

        player.removePlayer();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location loc = new Location(e.getTo().getWorld(), (int) e.getTo().getX(), (int) e.getTo().getY(), (int) e.getTo().getZ());
        if(Main.getInstance().redSpawnArea.contains(loc)){
            Bukkit.getPluginManager().callEvent(new AreaWalkEvent(Area.RED_SPAWN, p));
        }
        if(Main.getInstance().blueSpawnArea.contains(loc)){
            Bukkit.getPluginManager().callEvent(new AreaWalkEvent(Area.BLUE_SPAWN, p));
        }
        if(Main.getInstance().blockspawnAreas.contains(loc)){
            Bukkit.getPluginManager().callEvent(new AreaWalkEvent(getArea(loc), p));
        }
        if(canGetNeededBlock(p)){
            gameManager.setCarrying(p, nexusManager.getCurrentNexusColor());
            String[] data = neededBlockMaterial.split(";");
            neededBlock.getBlock().setTypeIdAndData(Integer.valueOf(data[0]), Byte.valueOf(data[1]), false);
            neededBlock = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);

            Bukkit.broadcastMessage(Color.np("&6The needed block was picked up by the " + teamManager.getTeam(p).getTeamName() + "&6 team!"));
        }
        if ((int) e.getFrom().getX() != (int) e.getTo().getX() || (int) e.getFrom().getZ() != (int) e.getTo().getZ() || (int) e.getFrom().getY() != (int) e.getTo().getY()) {
            if(getTurret(loc) != null) {
                Bukkit.getPluginManager().callEvent(new TurretWalkEvent(p, getTurret(loc)));
            }
            if(isInTurret(p)){
                Turret t = getTurret(p);
                if(t.containsUser()){
                    e.getPlayer().teleport(e.getFrom());
                }
            }
        }
        if ((int) e.getFrom().getX() != (int) e.getTo().getX() || (int) e.getFrom().getZ() != (int) e.getTo().getZ()) {
            if (!released || stunned.contains(p.getName())) {
                if (GameState.getState() == GameState.INGAME) {
                    e.getPlayer().teleport(e.getFrom());
                }
            }
        }
    }

    public Turret getTurret(Location loc){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        for(Turret t : Main.getInstance().turretsList){
            if(t.getLocation().equals(loc)){
                return t;
            }
        }
        return null;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e){
        Player p = e.getPlayer();
        if(isInTurret(p)){
            Turret t = getTurret(p);
            if(t.containsUser()){
                t.setUser(null);
                p.teleport(p.getLocation().add(1, 1, 0));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        Player p = e.getEntity();
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SPECTATOR);
        if(GameManager.getCarrying(p) == nexusManager.getCurrentNexusColor()){
            Bukkit.broadcastMessage(Color.np("&6The " + teamManager.getTeam(p).getTeamName() + " &6team has dropped the needed block!"));
            neededBlockMaterial = String.valueOf(p.getLocation().getBlock().getTypeId() + ";" + p.getLocation().getBlock().getData());
            p.getLocation().getBlock().setTypeIdAndData(159, (byte) nexusManager.getCurrentNexusColor(), false);
            neededBlock = p.getLocation();
        }
        gameManager.setCarrying(p, 0);
        new BukkitRunnable(){
            int i = 10;
            public void run() {
                i--;
                if(i > 0) {
                    Title title = new Title(Color.np("&6Respawning in &e" + String.valueOf(i)), "", 5, 10, 5);
                    title.sendToPlayer(p);
                } else {
                    this.cancel();
                    p.setGameMode(GameMode.SURVIVAL);

                    if(teamManager.getTeam(p) == Team.BLUE){
                        p.teleport(locationUtil.deserializeLoc(locationsFile.getString("arena.spawns.blue.1")));
                    } else if(teamManager.getTeam(p) == Team.RED){
                        p.teleport(locationUtil.deserializeLoc(locationsFile.getString("arena.spawns.red.1")));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            if(GameState.getState() == GameState.ENDING || GameState.getState() == GameState.LOBBY){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAreaWalk(AreaWalkEvent e){
        if(GameState.getState() != GameState.ENDING) {
            Player p = e.getPlayer();
            Area a = e.getArea();

            if (a != null) {
                if(e.isBlockSpawn()){
                    int block = nexusManager.getColor(a);
                    if(GameManager.getCarrying(p) == nexusManager.getCurrentNexusColor()) {
                        if (block != nexusManager.getCurrentNexusColor()) {
                            Bukkit.broadcastMessage(Color.np("&6The " + teamManager.getTeam(p).getTeamName() + " &6team has dropped the needed block. It has been brought back to it's original location."));
                            nexusManager.getNeededBlockArea().getBlockSpawnBlock().getBlock().setTypeIdAndData(159, (byte) nexusManager.getCurrentNexusColor(), false);
                            isPickedUp = false;
                        }
                    }
                    gameManager.setCarrying(p, block);

                    if(nexusManager.getColor(e.getArea()) == nexusManager.getCurrentNexusColor()){
                        if(!isPickedUp) {
                            Bukkit.broadcastMessage(Color.np("&6The " + teamManager.getTeam(p).getTeamName() + " &6team has picked up the needed block!"));
                            a.getBlockSpawnBlock().getBlock().setType(Material.AIR);
                            isPickedUp = true;
                        }
                    }
                } else {
                    Team spawn = getTeam(a);
                    int carrying = gameManager.getCarrying(p);
                    if(spawn == teamManager.getTeam(p)){
                        if(carrying == nexusManager.getCurrentNexusColor()){
                            Bukkit.broadcastMessage(Color.np(spawn.getTeamName() + " &6team has brought the block to their base first!"));
                            gameManager.addPoint(spawn);
                            gameManager.setCarrying(p, 0);
                            nexusManager.generateNewNexus();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTurretFire(PlayerInteractEvent e){
        if(isInTurret(e.getPlayer())){
            Player shooter = e.getPlayer();
            Turret t = getTurret(shooter);

            t.shoot();
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e.getDamager() instanceof Snowball) {
                Snowball s = (Snowball) e.getDamager();
                Player shooter = (Player) s.getShooter();
                if (teamManager.getTeam(p) != teamManager.getTeam((Player)s.getShooter())) {
                    if (s.getCustomName() != null) {
                        if (s.getCustomName().equalsIgnoreCase("stun")) {
                            if (!stunned.contains(p.getName())) {
                                stunned.add(p.getName());
                                p.playSound(p.getLocation(), Sound.FIREWORK_TWINKLE2, 3F, 1F);
                                shooter.playSound(shooter.getLocation(), Sound.SUCCESSFUL_HIT, 3F, 1F);
                                p.sendMessage(Color.np("&cYou got hit by a stun! You cannot move for 2 seconds!"));
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        stunned.remove(p.getName());
                                    }
                                }.runTaskLater(Main.getInstance(), 40);
                            } else {
                                shooter.sendMessage(Color.np("&cThat player is already stunned!"));
                            }
                        }
                    }
                } else {
                    shooter.sendMessage(Color.np("&cYou cannot stun team members!"));
                }
            }
        }
    }

    @EventHandler
    public void onTurretWalk(TurretWalkEvent e){
        if(GameState.getState() == GameState.INGAME) {
            if (!e.isOccupied()) {
                e.getTurret().setUser(e.getPlayer());
                e.getPlayer().sendMessage(Color.np("&aYou entered a turret. To leave, press shift."));
            } else {
                e.getPlayer().sendMessage(Color.np("&7That turret is already occupied!"));
            }
        }
    }

    public Area getArea(Location loc){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        List<Area> areas = Area.getBlockSpawns();
        int i = 0;
        for(int t = 1; t < 9; t++){
            String[] data = locationsFile.getString("arena.blockspawnareas." + t).split(" ");

            Location minimum = locationUtil.deserializeLoc(data[0]);
            Location maximum = locationUtil.deserializeLoc(data[1]);
            for (double x = minimum.getX(); x <= maximum.getX(); x++) {
                for (double y = minimum.getY(); y <= maximum.getY(); y++) {
                    for (double z = minimum.getZ(); z <= maximum.getZ(); z++) {
                        Location location = new Location(minimum.getWorld(), x, y, z);
                        if(location.equals(loc)){
                            return areas.get(i);
                        }
                    }
                }
            }
            i++;
        }
        return null;
    }

    private Team getTeam(Area a){
        if(a == Area.RED_SPAWN){
            return Team.RED;
        } else {
            return Team.BLUE;
        }
    }

    private boolean isInTurret(Player p){
        for(Turret t : Main.getInstance().turretsList){
            if(t.getUser() == p){
                return true;
            }
        }
        return false;
    }

    private Turret getTurret(Player p){
        for(Turret t : Main.getInstance().turretsList){
            if(t.getUser() == p){
                return t;
            }
        }
        return null;
    }

    public static Location getNeededBlockLocation(){
        if(neededBlock != null){
            return neededBlock;
        } else {
            return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }
    }

    public void setNeededBlockLocation(Location loc){
        neededBlock = loc;
    }

    public boolean canGetNeededBlock(Player p){
        if(neededBlock != null) {
            for (Entity e : neededBlock.getWorld().getNearbyEntities(neededBlock, 2, 2, 2)) {
                if (e == p) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

}
