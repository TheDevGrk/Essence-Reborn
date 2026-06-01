package me.grkdev.essenceReborn.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class RideEntityListener implements Listener {

    @EventHandler
    public static void onRideEntity(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();

        // if it's an enderdragon, need to convert to regular enderdragon entity
        if (entity instanceof EnderDragonPart){
            entity = ((EnderDragonPart) entity).getParent();
        }

        // make player ride entity if the entity is rideable
        if(entity.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "rideable"), PersistentDataType.BOOLEAN, false)){
            entity.addPassenger(player);
        }
    }
}
