package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.powers.Piglin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class BlockGoldenEatListener implements Listener {

    @EventHandler
    public static void onPlayerEat(PlayerItemConsumeEvent e){
        Player player = e.getPlayer();

        if (Piglin.inBlockingRadius(player) && (e.getItem().getType() == Material.GOLDEN_APPLE || e.getItem().getType() == Material.GOLDEN_CARROT || e.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE)){
            e.setCancelled(true);
        }
    }
}
