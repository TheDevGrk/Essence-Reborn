package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.powers.Wither;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Objects;

public class NecromancyMobTargetListener implements Listener {

    @EventHandler
    public static void onTarget(EntityTargetEvent e){
        //|| Wither.getTargetUUID(e.getEntity()) == null
        if (e.getTarget() instanceof Player player && Wither.isOwned(e.getEntity(), player)  || !Objects.equals(Wither.getTargetUUID(e.getEntity()), e.getTarget().getUniqueId())){
            e.setCancelled(true);
        }
    }
}
