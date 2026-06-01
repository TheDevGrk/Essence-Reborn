package me.grkdev.essenceReborn.listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class DragonBreakBlockListener implements Listener {

    @EventHandler
    public static void onDragonBreakBlock(EntityExplodeEvent e){
        // rideable dragons don't break blocks
        if (e.getEntity() instanceof EnderDragon dragon && dragon.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "rideable"), PersistentDataType.BOOLEAN, false)){
            e.setCancelled(true);
        }
    }
}
