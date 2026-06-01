package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class PassiveEffectsOnJoin implements Listener {

    static BukkitScheduler scheduler = Bukkit.getScheduler();

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        EssenceManager.getActiveEssence(player).power.onPassivePower(player);
    }
}
