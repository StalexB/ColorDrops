package me.stalexgaming.colordrops.utils;

import me.stalexgaming.colordrops.Main;
import me.stalexgaming.colordrops.enums.GameState;
import me.stalexgaming.colordrops.enums.Team;
import me.stalexgaming.colordrops.managers.ArenaManager;
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
                    if (GameState.getState() == GameState.INGAME) {
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
                }
            }.runTaskTimer(Main.getInstance(), 0, 5);
        } else {
            return;
        }
    }

    private Scoreboard getScoreBoard(Player p){
        if(p.getScoreboard() != null){
            Scoreboard sb = p.getScoreboard();
            org.bukkit.scoreboard.Team red = sb.registerNewTeam("red");
            red.setPrefix(Color.np("&c"));
            org.bukkit.scoreboard.Team blue = sb.registerNewTeam("blue");
            blue.setPrefix(Color.np("&b"));
            for(Player online : Bukkit.getOnlinePlayers()){
                Team team = teamManager.getTeam(online);
                if(team == Team.BLUE){
                    blue.addEntry(online.getName());
                } else {
                    red.addEntry(online.getName());
                }
            }
            return p.getScoreboard();
        }
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        org.bukkit.scoreboard.Team red = sb.registerNewTeam("red");
        red.setPrefix(Color.np("&c"));
        org.bukkit.scoreboard.Team blue = sb.registerNewTeam("blue");
        blue.setPrefix(Color.np("&b"));
        for(Player online : Bukkit.getOnlinePlayers()){
            Team team = teamManager.getTeam(online);
            if(team == Team.BLUE){
                blue.addEntry(online.getName());
            } else {
                red.addEntry(online.getName());
            }
        }
        return sb;
    }

    private Objective getObjective(Player p, Scoreboard sb){
        if(sb.getObjective("lines") != null){
            return sb.getObjective("lines");
        }
        Objective o = sb.registerNewObjective("lines", "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(Color.np("&6&lColorDrops"));
        return o;
    }

    private List<String> getLines(Player p){
        List<String> lines = new ArrayList<>();

        lines.add(Color.np(" "));
        lines.add(Color.np("&6Carrying: "));
        lines.add("   ");
        lines.add(Color.np("&6Minecart:"));

        return lines;
    }
}
