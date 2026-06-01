package me.grkdev.essenceReborn.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Untrust implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player player = (Player) source.getSender();
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        //adds the passed player's name to the list of trusted players in the command sender's pdc
        NamespacedKey key = new NamespacedKey(plugin, "trusted_players");
        List<String> names = pdc.getOrDefault(key, PersistentDataType.LIST.strings(), new ArrayList<String>());
        names = new ArrayList<>(names);
        names.remove(args[0]);
        pdc.set(key, PersistentDataType.LIST.strings(), names);

        player.sendMessage(Component.text(args[0] + " has been removed from your trusted list!"));
    }

    @Override
    public boolean canUse(CommandSender sender) {
        // only let a player send the command
        return sender instanceof Player player;
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
