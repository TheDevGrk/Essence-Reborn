package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.data.EssenceManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EssenceSwitcherListener implements Listener {

    @EventHandler
    public static void onEssenceSwitcherUse(PlayerInteractEvent e){
        if (e.getAction().isLeftClick() || e.getItem() == null || e.getItem().getType() != Material.NETHER_STAR || !e.getItem().getItemMeta().isGlider()){
            return;
        }

        EssenceManager.showEssenceSwitchDialog(e.getPlayer(), true);

    }
}
