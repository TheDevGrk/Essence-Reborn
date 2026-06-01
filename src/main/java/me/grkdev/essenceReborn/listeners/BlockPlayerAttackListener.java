package me.grkdev.essenceReborn.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BlockPlayerAttackListener implements Listener {

    @EventHandler
    public static void onPlayerAttack(EntityDamageByEntityEvent e){
        if (!(e.getDamager() instanceof Player player)){
            return;
        }

        // gilded players can't attack
        if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("gilded").hasPlayer(player)){
            e.setCancelled(true);
        }
    }
}
