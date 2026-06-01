package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.EssenceReborn;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import static net.kyori.adventure.text.Component.text;

public class MobDeathListener implements Listener {
    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if (!EssenceTypes.isEssence(e.getEntityType()) || e.getEntity().getKiller() == null)
            return; //ignore event if the mob doesn't have an essence associated with it
        //-----------------------------------------------------------------------------------------------------------------------------



        EssenceTypes type = EssenceTypes.getEssenceType(e.getEntityType());
        int dropAmount = 1; // easier to change in future if variable drops are added
        Player player = e.getEntity().getKiller();




        if (Math.random() <= type.dropChance) { // give the player an essence of the mob's type if they make the % chance check
            EssenceManager.addEssence(player, type, dropAmount);

            final Component msg = text()
                    .content("The " + e.getEntityType().toString().toLowerCase() + " dropped " + dropAmount + " essence!")
                    .color(NamedTextColor.GREEN)
                    .build();

            player.sendMessage(msg);
        }
    }
}
