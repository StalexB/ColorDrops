package me.stalexgaming.colordrops.managers;

import me.stalexgaming.colordrops.enums.Area;
import me.stalexgaming.colordrops.utils.Color;
import me.stalexgaming.colordrops.utils.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright by Bankras, created on 28-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class BlockManager {

    private static BlockManager instance = new BlockManager();

    public HashMap<Location, Boolean> blocksStatus = new HashMap<>();
    public HashMap<Location, String> replaceBlocks = new HashMap<>();
    public HashMap<Location, Area> blocksInMap = new HashMap<>();
    public HashMap<String, Area> playerBlocks = new HashMap<>();
    public List<Location> blockLocs = new ArrayList<>();

    NexusManager nexusManager = NexusManager.getInstance();
    ScoreboardUtil scoreboardUtil = ScoreboardUtil.getInstance();

    public static BlockManager getInstance(){
        return instance;
    }

    public void setTaken(Area a){
        blocksStatus.put(a.getBlockSpawnBlock(), true);
    }

    public void setUnTaken(Area a){
        blocksStatus.put(a.getBlockSpawnBlock(), false);
    }

    public boolean isTaken(Area a){
        if(blocksStatus.containsKey(a.getBlockSpawnBlock())){
            return blocksStatus.get(a.getBlockSpawnBlock());
        } else {
            return false;
        }
    }

    public void resetAll(){
        for(Location loc : blockLocs){
            String[] data = replaceBlocks.get(loc).split(";");
            loc.getBlock().setTypeIdAndData(Integer.parseInt(data[0]), Byte.valueOf(data[1]), false);
        }

        for(Player p : Bukkit.getOnlinePlayers()){
            GameManager.setCarrying(p, 0);
        }

        blocksStatus.clear();
        replaceBlocks.clear();
        blocksInMap.clear();
        playerBlocks.clear();
        blockLocs.clear();
    }

    public void resetBlock(Location loc, Area a){
        if(replaceBlocks.get(loc) != null) {
            String[] data = replaceBlocks.get(loc).split(";");
            loc.getBlock().setTypeIdAndData(Integer.valueOf(data[0]), Byte.valueOf(data[1]), false);
            a.getBlockSpawnBlock().getBlock().setTypeIdAndData(159, (byte) NexusManager.getColor(a), false);
        } else {
            loc.getBlock().setType(Material.AIR);
            a.getBlockSpawnBlock().getBlock().setTypeIdAndData(159, (byte) NexusManager.getColor(a), false);
        }
    }

    public void dropBlock(Location loc, Area a){
        replaceBlocks.put(loc, String.valueOf(loc.getBlock().getTypeId() + ";" + loc.getBlock().getData()));
        loc.getBlock().setTypeIdAndData(159, (byte) NexusManager.getColor(a), false);
        blocksInMap.put(loc, a);
        blockLocs.add(loc);

        setUnTaken(a);
    }

    public boolean canPickUpBlock(Player p){
        for(Location loc : blockLocs){
            for(Entity e : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)){
                if(e == p){
                    return true;
                }
            }
        }
        return false;
    }

    public Location getNearbyBlockLoc(Player p){
        for(Location loc : blockLocs){
            for(Entity e : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)){
                if(e == p){
                    return loc;
                }
            }
        }
        return null;
    }

    public void pickUpBlock(Player p){
        if(getNearbyBlockLoc(p) != null){
            Location loc = getNearbyBlockLoc(p);
            Area a = blocksInMap.get(loc);

            byte color = loc.getBlock().getData();

            if(GameManager.getCarrying(p) != (int) color){
                GameManager.setCarrying(p, (int) color);
                String[] data = replaceBlocks.get(loc).split(";");
                loc.getBlock().setTypeIdAndData(Integer.parseInt(data[0]), Byte.parseByte(data[1]), false);
                replaceBlocks.remove(loc);
                blocksInMap.remove(loc);
                blockLocs.remove(loc);
                playerBlocks.put(p.getName(), a);
                setTaken(a);
                p.sendMessage(Color.np("&6You picked up a " + scoreboardUtil.getColorName((int) color) + "&6 block"));
            }
        }
    }

    public void forcePickUpBlock(Player p, Area a){
        if(playerBlocks.get(p.getName()) != null){
            resetBlock(playerBlocks.get(p.getName()).getBlockSpawnBlock(), playerBlocks.get(p.getName()));
        }

        GameManager.setCarrying(p, (int) a.getBlockSpawnBlock().getBlock().getData());
        int color = (int) a.getBlockSpawnBlock().getBlock().getData();
        setTaken(a);
        playerBlocks.put(p.getName(), a);
        p.sendMessage(Color.np("&6You picked up a " + scoreboardUtil.getColorName(color) + "&6 block"));

        a.getBlockSpawnBlock().getBlock().setTypeIdAndData(0, (byte) 0, false);
    }

}
