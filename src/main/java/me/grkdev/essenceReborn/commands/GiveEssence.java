package me.grkdev.essenceReborn.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class GiveEssence implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player player = plugin.getServer().getPlayer(args[0]);

        if (args[1].equals("all")){
            for (EssenceTypes essenceTypes : EssenceTypes.values()){
                EssenceManager.addEssence(player, essenceTypes, 10000);
            }
            return;
        }

        EssenceManager.addEssence(player, EssenceTypes.getEssenceType(args[1]), Integer.parseInt(args[2]));


    }

    @Override
    public boolean canUse(CommandSender sender) {
        // only let a player send the command
//        return sender instanceof Player player && ((Player) sender).getUniqueId().equals(plugin.getServer().getPlayerUniqueId("GrkDev"));
        return sender instanceof Player player && player.isOp();
    }
}
