package me.grkdev.essenceReborn.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Crystal implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player player = (Player) source.getSender();

        ItemStack crystal = new ItemStack(Material.PAPER);
        ItemMeta crystalMeta = crystal.getItemMeta();

        crystalMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_power_crystal"), PersistentDataType.BOOLEAN, true);
        crystalMeta.setItemModel(NamespacedKey.fromString("minecraft:item/essencereborn/power_crystal"));

        crystal.setItemMeta(crystalMeta);

        player.give(crystal);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        // only let a player send the command
        return sender instanceof Player player;
    }
}
