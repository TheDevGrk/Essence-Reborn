package me.grkdev.essenceReborn.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.grkdev.essenceReborn.data.EssenceDescriptions;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class SelectEssence implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player player = (Player) source.getSender();

        if (player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "select_essence_used"), PersistentDataType.BOOLEAN, false)){
            player.sendMessage(Component.text("You have already selected an Essence!", NamedTextColor.RED));
            return;
        }

        EssenceManager.showEssenceSwitchDialog(player, false);
    }


    @Override
    public boolean canUse(CommandSender sender) {
        // only let a player send the command
        return sender instanceof Player player;
    }
}
