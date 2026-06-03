package me.grkdev.essenceReborn.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.grkdev.essenceReborn.data.EssenceManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Crystal implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player player = (Player) source.getSender();

        player.give(EssenceManager.createEssenceCrystal());
    }

    @Override
    public boolean canUse(CommandSender sender) {
        // only let a player send the command
        return sender instanceof Player player;
    }
}
