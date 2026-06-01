package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockReceiveGameEvent;

public class SculkImmunityListener implements Listener {

    @EventHandler
    public static void onSculkActivate(BlockReceiveGameEvent e){
        if ((e.getBlock().getType() != Material.SCULK_SENSOR && e.getBlock().getType() != Material.SCULK_SHRIEKER && e.getBlock().getType() != Material.CALIBRATED_SCULK_SENSOR) || !(e.getEntity() instanceof Player player)){
            return;
        }


        // if player has warden passive active, cancel event
        if (EssenceManager.getActiveEssence(player) == EssenceTypes.WARDEN && PowerManager.hasPassiveUnlocked(player)){
            e.setCancelled(true);
        }
    }
}
