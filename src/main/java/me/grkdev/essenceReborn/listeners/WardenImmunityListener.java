package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.entity.WardenAngerChangeEvent;

public class WardenImmunityListener implements Listener {

    @EventHandler
    public static void onWardenAggravate(WardenAngerChangeEvent e){
        if (!(e.getTarget() instanceof Player player)){
            return;
        }


        // makes players with warden passive immune to warden anger
        if (EssenceManager.getActiveEssence(player) == EssenceTypes.WARDEN && PowerManager.hasPassiveUnlocked(player)){
            e.setCancelled(true);
        }
    }
}
