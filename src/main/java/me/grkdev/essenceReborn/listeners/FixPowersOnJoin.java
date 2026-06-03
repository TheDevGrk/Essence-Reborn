package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class FixPowersOnJoin implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        EssenceTypes type = EssenceManager.getActiveEssence(player);

        // reactivate passive
        if (type.power.isEnabled()){ // only activate if passive is enabled
            type.power.activatePassivePower(player);
        }

        // show active cooldowns
        int remainingWeak = PowerManager.getRemainingWeakCooldown(player);
        int remainingStrong = PowerManager.getRemainingStrongCooldown(player);
        if (remainingWeak > 0) {
            PowerManager.showCooldownBar(player, type.weakPowerName, remainingWeak, BossBar.Color.YELLOW);
        }
        if (remainingStrong > 0) {
            PowerManager.showCooldownBar(player, type.strongPowerName, remainingStrong, BossBar.Color.RED);
        }
    }
}
