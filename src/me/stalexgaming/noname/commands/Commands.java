package me.stalexgaming.noname.commands;

import me.stalexgaming.noname.Main;
import me.stalexgaming.noname.utils.Color;
import me.stalexgaming.noname.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
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

    public LocationUtil locationUtil = LocationUtil.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args){
        if(!(sender instanceof Player)) return true;
        FileConfiguration locationsFile = YamlConfiguration.loadConfiguration(Main.getInstance().locations);

        Player p = (Player) sender;

        if(cmd.getName().equalsIgnoreCase("noname")){
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
                        plugin.getLogger().log(Level.SEVERE, "[NoName] Error while saving lobby location!");
                    }
                    p.sendMessage(Color.p("&7You set the &aLobby &7spawn."));
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
                                plugin.getLogger().log(Level.SEVERE, "[NoName] Error while saving blockspawn location!");
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
                }
            } else if(args.length == 3){

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
}
