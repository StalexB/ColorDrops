package me.stalexgaming.colordrops.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.managers.ArenaManager;
import me.stalexgaming.colordrops.utils.Color;
import me.stalexgaming.colordrops.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class Commands implements CommandExecutor {

    Main plugin;

    public Commands(Main main){
        this.plugin = main;
    }

    LocationUtil locationUtil = LocationUtil.getInstance();
    ArenaManager arenaManager = ArenaManager.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args){
        if(!(sender instanceof Player)) return true;
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        WorldEditPlugin wep = plugin.getWorldEditInstance();

        Player p = (Player) sender;

        if(cmd.getName().equalsIgnoreCase("colordrops")){
            if(args.length == 0){
                sendHelpMessage(p);
                return true;
            } else if(args.length == 1){
                if(args[0].equalsIgnoreCase("help")){
                    sendHelpMessage(p);
                    return true;
                } else if(args[0].equalsIgnoreCase("setlobby")){
                    locationsFile.set("arena.lobby", locationUtil.serializeLocation(p.getLocation()));

                    try {
                        locationsFile.save(Main.getInstance().locations);
                    } catch (IOException ex){
                        ex.printStackTrace();
                        plugin.getLogger().log(Level.SEVERE, "[ColorDrops] Error while saving lobby location!");
                    }
                    p.sendMessage(Color.p("&7You set the &aLobby &7spawn."));
                    return true;
                } else if(args[0].equalsIgnoreCase("addnexusarea")){
                    if(hasSelection(p)){
                        List<String> nexusLocs = getNexusLocs();

                        Selection s = wep.getSelection(p);

                        nexusLocs.add(locationUtil.serializeLocation(s.getMinimumPoint()) + " " + locationUtil.serializeLocation(s.getMaximumPoint()));

                        locationsFile.set("arena.nexus", nexusLocs);

                        try {
                            locationsFile.save(Main.getInstance().locations);
                        } catch (IOException ex){
                            ex.printStackTrace();
                            plugin.getLogger().log(Level.SEVERE, "[ColorDrops] Error while saving nexus location!");
                        }
                        p.sendMessage(Color.p("&7You added an area to the &aNexus&7."));
                        return true;
                    } else {
                        p.sendMessage(Color.p("&cYou need a selection in order to set an area!"));
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("check")){
                    if(arenaManager.isArenaReady()){
                        p.sendMessage(Color.p("&7This server is &aready &7for use."));
                        return true;
                    } else {
                        p.sendMessage(Color.p("&7This server is &cnot ready &7for use."));
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("addturret")){
                    List<String> turrets = getTurrets();

                    turrets.add(locationUtil.serializeLocation(p.getLocation()));

                    locationsFile.set("arena.turrets", turrets);

                    try {
                        locationsFile.save(Main.getInstance().locations);
                    } catch (IOException ex){
                        ex.printStackTrace();
                        plugin.getLogger().log(Level.SEVERE, "[ColorDrops] Error while saving turret location!");
                    }
                    p.sendMessage(Color.p("&7You added a &aTurret&7."));
                    return true;
                }
            } else if(args.length == 2){
                if(args[0].equalsIgnoreCase("setblockspawn")){
                    if(isInt(args[1])){
                        int spawn = Integer.parseInt(args[1]);
                        if(spawn > 0 && spawn < 9){
                            locationsFile.set("arena.blockspawns." + spawn, locationUtil.serializeLocation(p.getLocation()));

                            try {
                                locationsFile.save(Main.getInstance().locations);
                            } catch (IOException ex){
                                ex.printStackTrace();
                                plugin.getLogger().log(Level.SEVERE, "[ColorDrops] Error while saving blockspawn location!");
                            }
                            p.sendMessage(Color.p("&7You set &aBlockSpawn &7number &a" + spawn + "&7."));
                            return true;
                        } else {
                            p.sendMessage(Color.p("&cThe blockspawn ID must be a number between 1 and 8!"));
                            return true;
                        }
                    } else {
                        p.sendMessage(Color.p("&cThe blockspawn ID must be a number between 1 and 8!"));
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("setspawnarea")){
                    if(args[1].equalsIgnoreCase("red") || args[1].equalsIgnoreCase("blue")){
                        if(hasSelection(p)){
                            Selection s = wep.getSelection(p);
                            locationsFile.set("arena.spawnarea." + args[1].toLowerCase(), locationUtil.serializeLocation(s.getMinimumPoint()) + " " + locationUtil.serializeLocation(s.getMaximumPoint()));

                            try {
                                locationsFile.save(Main.getInstance().locations);
                            } catch (IOException ex){
                                ex.printStackTrace();
                                plugin.getLogger().log(Level.SEVERE, "[ColorDrops] Error while saving a team spawn area location!");
                            }
                            p.sendMessage(Color.p("&7You set the &aspawn area &7for team &a" + args[1].toLowerCase() + "&7."));
                            return true;
                        } else {
                            p.sendMessage(Color.p("&cYou need a selection in order to set an area!"));
                            return true;
                        }
                    } else {
                        p.sendMessage(Color.p("&cThe team " + args[1] + " is invalid!"));
                        return true;
                    }
                }  else if(args[0].equalsIgnoreCase("setblockspawnarea")) {
                    if (isInt(args[1])) {
                        int blockSpawn = Integer.parseInt(args[1]);
                        if (blockSpawn > 0 && blockSpawn < 9) {
                            if (hasSelection(p)) {
                                Selection s = wep.getSelection(p);
                                locationsFile.set("arena.blockspawnareas." + blockSpawn, locationUtil.serializeLocation(s.getMinimumPoint()) + " " + locationUtil.serializeLocation(s.getMaximumPoint()));

                                try {
                                    locationsFile.save(Main.getInstance().locations);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    plugin.getLogger().log(Level.SEVERE, "[ColorDrops] Error while saving a block spawn area location!");
                                }
                                p.sendMessage(Color.p("&7You set &ablock spawn area &7for blockspawn &a" + blockSpawn + "&7."));
                                return true;
                            } else {
                                p.sendMessage(Color.p("&cYou need a selection in order to set an area!"));
                                return true;
                            }
                        } else {
                            p.sendMessage(Color.p("&cThe blockspawn ID specified is invalid."));
                            return true;
                        }
                    } else {
                        p.sendMessage(Color.p("&cThe blockspawn ID specified is invalid."));
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("setminecart")){
                    if(args[1].equalsIgnoreCase("x") || args[1].equalsIgnoreCase("z")){
                        locationsFile.set("arena.minecart", locationUtil.serializeLocation(p.getLocation()));
                        locationsFile.set("arena.minecartxorz", args[1].toLowerCase());

                        try {
                            locationsFile.save(Main.getInstance().locations);
                        } catch (IOException ex){
                            ex.printStackTrace();
                            plugin.getLogger().log(Level.SEVERE, "[ColorDrops] Error while saving minecart spawn location!");
                        }
                        p.sendMessage(Color.p("&7You set the &aminecart &7starting point."));
                        return true;
                    } else {
                        p.sendMessage(Color.p("&cYou must specify either X or Z!"));
                        return true;
                    }
                }
            } else if(args.length == 3){
                if(args[0].equalsIgnoreCase("setspawn")){
                    if(args[1].equalsIgnoreCase("red") || args[1].equals("blue")){
                        if(isInt(args[2])){
                            int point = Integer.parseInt(args[2]);
                            if(point > 0 && point < 6){
                                locationsFile.set("arena.spawns." + args[1].toLowerCase() + "." + point, locationUtil.serializeLocation(p.getLocation()));

                                try {
                                    locationsFile.save(Main.getInstance().locations);
                                } catch (IOException ex){
                                    ex.printStackTrace();
                                    plugin.getLogger().log(Level.SEVERE, "[ColorDrops] Error while saving a team spawn location!");
                                }
                                p.sendMessage(Color.p("&7You set &spawn &7number &a" + point + " &7for team &a" + args[1].toLowerCase() + "&7."));
                                return true;
                            } else {
                                p.sendMessage(Color.p("&cThe spawn ID must be a number between 1 and 6!"));
                                return true;
                            }
                        } else {
                            p.sendMessage(Color.p("&cThe spawn ID must be a number between 1 and 6!"));
                            return true;
                        }
                    } else {
                        p.sendMessage(Color.p("&cThe specified team: " + args[1] + ", is invalid."));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void sendHelpMessage(Player p){
        p.sendMessage("&b&l/");
        p.sendMessage("&b&l/");
        p.sendMessage("&b&l/");
        p.sendMessage("&b&l/");
        p.sendMessage("&b&l/");
        p.sendMessage("&b&l/");
        p.sendMessage("&b&l/");
    }

    private boolean isInt(String s){
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException ex){
            return false;
        }
        return true;
    }

    private boolean hasProperSelection(Player p){
        WorldEditPlugin wep = plugin.getWorldEditInstance();
        if(wep.getSelection(p) != null){
            Selection selection = wep.getSelection(p);
            Location min = selection.getMinimumPoint();
            Location max = selection.getMaximumPoint();
            if(max.getY() == min.getY()){
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean hasSelection(Player p){
        WorldEditPlugin wep = plugin.getWorldEditInstance();
        if(wep.getSelection(p) != null){
            return true;
        }
        return false;
    }

    private List<String> getNexusLocs(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        if(locationsFile.getString("arena.nexus") != null){
            return locationsFile.getStringList("arena.nexus");
        }
        return new ArrayList<>();
    }

    private List<String> getTurrets(){
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);
        if(locationsFile.getString("arena.turrets") != null){
            return locationsFile.getStringList("arena.turrets");
        }
        return new ArrayList<>();
    }
}
