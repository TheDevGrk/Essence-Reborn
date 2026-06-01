package me.grkdev.essenceReborn.listeners;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class EssenceSwitchDialogListener implements Listener {

    @EventHandler
    void handleEssenceSwitchDialog(PlayerCustomClickEvent e){
        if (!e.getIdentifier().equals(Key.key("essence_reborn:essence_switch/confirm/essence_switcher")) && !e.getIdentifier().equals(Key.key("essence_reborn:essence_switch/confirm/select_essence_command"))){
            return;
        }

        DialogResponseView view = e.getDialogResponseView();
        if (view == null){
            return;
        }

        String essenceType = view.getText("essenceType").toUpperCase();

        if (e.getCommonConnection() instanceof PlayerGameConnection conn){
            Player player = conn.getPlayer();
            EssenceTypes type = EssenceTypes.getEssenceType(essenceType);
            EssenceManager.setActiveEssence(player, type);
            type.power.onPassivePower(player);



            //turn off necromancy mode if switched from wither
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "necromancy_mode"), PersistentDataType.BOOLEAN, false);

            // reset enderman reach
            player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5);
            player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3);

            //reset warden swift sneak
            player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(.3);


            player.sendMessage(Component.text("Your essence has been switched to " + essenceType, NamedTextColor.AQUA));




            if (e.getIdentifier().equals(Key.key("essence_reborn:essence_switch/confirm/essence_switcher"))){
                // remove 1 essence switcher from inv
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null || !item.hasItemMeta()) continue;
                    if (item.getType() == Material.NETHER_STAR && item.getItemMeta().isGlider()) {
                        item.setAmount(item.getAmount() - 1);
                        break;
                    }
                }
            }
            else if (e.getIdentifier().equals(Key.key("essence_reborn:essence_switch/confirm/select_essence_command"))){
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "select_essence_used"), PersistentDataType.BOOLEAN, true);
            }
        }


    }
}
