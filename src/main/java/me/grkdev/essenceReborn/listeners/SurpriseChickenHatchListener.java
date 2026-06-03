package me.grkdev.essenceReborn.listeners;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class SurpriseChickenHatchListener implements Listener {

    @EventHandler
    public static void onEggHatch(ThrownEggHatchEvent e){
        if (e.getEgg().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "is_surpise_egg"), PersistentDataType.BOOLEAN, false)){
            e.setHatching(false);
        }
    }
}
