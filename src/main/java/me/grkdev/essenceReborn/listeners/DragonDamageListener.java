package me.grkdev.essenceReborn.listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class DragonDamageListener implements Listener {

    @EventHandler
    public static void onDragonDamage(EntityDamageByEntityEvent e){
        // rideable dragons don't damage entities
        if (e.getDamager() instanceof EnderDragon dragon && dragon.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "rideable"), PersistentDataType.BOOLEAN, false)){
            e.setCancelled(true);
        }
    }
}
