package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.data.EssenceManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockCraftingListener implements Listener {

    @EventHandler
    public static void onItemCraft(CraftItemEvent e){
        for (ItemStack i : e.getInventory().getMatrix()){
            if ((i.getItemMeta().hasItemModel() && i.getItemMeta().getItemModel().equals(NamespacedKey.fromString("minecraft:item/essencereborn/power_crystal")))
                    || (i.getType() == Material.NETHER_STAR) && i.getItemMeta().isGlider()){

                // disallow craft for essence switchers and essence crystals
                e.setResult(Event.Result.DENY);


                // replace result with a barrier
                ItemStack denyRecipe = ItemStack.of(Material.BARRIER);
                ItemMeta denyRecipeMeta = denyRecipe.getItemMeta();
                denyRecipeMeta.itemName(Component.text("You cannot use this item in this recipe!", NamedTextColor.RED));
                denyRecipe.setItemMeta(denyRecipeMeta);
                e.getInventory().setResult(denyRecipe);
            }
        }
    }
}
