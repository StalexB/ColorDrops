package me.stalexgaming.colordrops.utils;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.GameState;
import me.stalexgaming.colordrops.enums.Team;
import me.stalexgaming.colordrops.managers.ArenaManager;
import me.stalexgaming.colordrops.managers.GameManager;
import me.stalexgaming.colordrops.managers.NexusManager;
import me.stalexgaming.colordrops.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright by Bankras, created on 22-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public class ScoreboardUtil {

    private static ScoreboardUtil instance = new ScoreboardUtil();

    public static ScoreboardUtil getInstance(){
        return instance;
    }

    ArenaManager arenaManager = ArenaManager.getInstance();
    TeamManager teamManager = TeamManager.getInstance();

    public void setScoreboards(){
        if(arenaManager.isArenaReady()) {
            new BukkitRunnable() {
                public void run() {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            List<String> lines = getLines(p);

                            Scoreboard sb = getScoreBoard(p);
                            Objective o = getObjective(p, sb);

                            int i = lines.size();
                            for (String s : lines) {
                                o.getScore(s).setScore(i);
                                i--;
                            }

                            p.setScoreboard(sb);
                        }
                }
            }.runTaskTimer(Main.getInstance(), 0, 5);
        } else {
            return;
        }
    }

    private Scoreboard getScoreBoard(Player p) {
            if (GameState.getState() == GameState.INGAME) {
                if (p.getScoreboard() != null) {
                    Scoreboard sb = p.getScoreboard();
                    org.bukkit.scoreboard.Team red = sb.getTeam("red");
                    org.bukkit.scoreboard.Team blue = sb.getTeam("blue");
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        Team team = teamManager.getTeam(online);
                        if (team == Team.BLUE) {
                            if (!(red.hasEntry(online.getName()))) {
                                blue.addEntry(online.getName());
                            }
                        } else {
                            if (!(blue.hasEntry(online.getName()))) {
                                red.addEntry(online.getName());
                            }
                        }
                    }
                    return sb;
                }
            } else {
                Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
                org.bukkit.scoreboard.Team red = sb.registerNewTeam("red");
                red.setPrefix(Color.np("&c"));
                org.bukkit.scoreboard.Team blue = sb.registerNewTeam("blue");
                blue.setPrefix(Color.np("&b"));
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Team team = teamManager.getTeam(online);
                    if (team == Team.BLUE) {
                        blue.addEntry(online.getName());
                    } else {
                        red.addEntry(online.getName());
                    }
                }
                return sb;
            }
        return Bukkit.getScoreboardManager().getNewScoreboard();
    }

    private Objective getObjective(Player p, Scoreboard sb){
        if(sb.getObjective("lines") != null){
            Objective o = sb.getObjective("lines");
            o.unregister();
            Objective newO = sb.registerNewObjective("lines", "dummy");
            newO.setDisplaySlot(DisplaySlot.SIDEBAR);
            newO.setDisplayName(Color.np("&6&lColorDrops"));
            return newO;
        }
        Objective o = sb.registerNewObjective("lines", "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(Color.np("&6&lColorDrops"));
        return o;
    }

    private String getMineCartStatus(){
        int minecart = GameManager.getMinecart();

        StringBuilder sb = new StringBuilder();
        for(int i = -4; i <= 4; i++){
            if(minecart != i){
                if(i == -4){
                    sb.append("&b+");
                } else if(i == 4){
                    sb.append("&c+");
                } else {
                    sb.append("&7-");
                }
            } else {
                sb.append("&6&l#");
            }
        }

        return sb.toString();
    }

    private List<String> getLines(Player p){
        List<String> lines = new ArrayList<>();

        lines.add(Color.np(" "));
        lines.add(Color.np("&6Carrying: "));
        int color = GameManager.getCarrying(p);
        lines.add(Color.np(getColorName(color)));
        lines.add(Color.np("&6Minecart:"));
        lines.add(Color.np(getMineCartStatus()));
        lines.add("  ");
        lines.add(Color.np("&6Nexus time: &e" + NexusManager.timer));

        return lines;
    }

    public String getColorName(int color){
        switch(color){
            case 0:
                return "&f-";
            case 1:
                return "&6Orange";
            case 2:
                return "&5Magenta";
            case 3:
                return "&bLight blue";
            case 4:
                return "&eYellow";
            case 5:
                return "&aLime";
            case 6:
                return "&dPink";
            case 7:
                return "&8Gray";
            case 8:
                return "&7Light gray";
            case 9:
                return "&3Cyan";
            case 10:
                return "&5Purple";
            case 11:
                return "&9Blue";
            case 12:
                return "&0Brown";
            case 13:
                return "&2Green";
            case 14:
                return "&cRed";
            case 15:
                return "&0Black";
            default:
                return "&fWhite";
        }
    }
}
