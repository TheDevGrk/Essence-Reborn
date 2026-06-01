package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class PowerCrystalListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent e){
        Player player = e.getPlayer();

        if (e.getItem() == null || !e.getItem().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "is_power_crystal"), PersistentDataType.BOOLEAN, false)){
            return;
        }

        if (player.isSneaking()){
            // toggle necromancy mode if using wither strong power
            if (e.getAction().isLeftClick() && PowerManager.hasStrongPowerUnlocked(player, EssenceTypes.WITHER) && EssenceManager.getActiveEssence(player) == EssenceTypes.WITHER){
                NamespacedKey key = new NamespacedKey(plugin, "necromancy_mode");
                boolean necromancyMode = player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BOOLEAN, false);
                player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, !necromancyMode);


                //indicate state to player
                if (!necromancyMode){
                    player.sendMessage(Component.text("Necromancy Mode Enabled!", NamedTextColor.GREEN));
                    player.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 3f, 1f);
                }
                else{
                    player.sendMessage(Component.text("Necromancy Mode Disabled!", NamedTextColor.RED));
                    player.playSound(player, Sound.BLOCK_BEACON_DEACTIVATE, 3f, 1f);
                }

                return;
            }



            //activate strong on sneak right click
            if (e.getAction().isRightClick()){
                PowerManager.activateStrongPower(player);
            }

            return;
        }


        // only activate weak power on right click
        if (e.getAction().isRightClick()){
            PowerManager.activateWeakPower(player);
        }
    }
}
