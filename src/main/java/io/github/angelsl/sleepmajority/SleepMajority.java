package io.github.angelsl.sleepmajority;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SleepMajority extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getCommand("sleepmajority").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only in-game players can execute this command.");
        }

        World w = ((Player) sender).getWorld();
        int sleeping = 0, needsToBeSleeping = 0;
        for (Player q : w.getPlayers()) {
            if (q.getGameMode() == GameMode.SPECTATOR || q.isSleepingIgnored()) {
                continue;
            }

            needsToBeSleeping++;
            if (q.isSleeping()) {
                sleeping++;
            }
        }

        int more = needsToBeSleeping - sleeping*2;
        if (more <= 0 && sleeping > 0) {
            if (Boolean.TRUE.equals(w.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
                long i = w.getFullTime() + 24000L;
                w.setFullTime(i - i % 24000L);
            }

            if (Boolean.TRUE.equals(w.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
                w.setStorm(false);
                if (w.hasStorm()) {
                    w.setWeatherDuration(0);
                }
                w.setThundering(false);
                if (w.isThundering()) {
                    w.setThunderDuration(0);
                }
            }
        } else {
            more = Math.max(1, (more + more%2)/2);
            sender.sendMessage(ChatColor.DARK_RED + "Insufficient players are sleeping; need " + more + " more." + ChatColor.RESET);
        }
        return true;
    }
}
