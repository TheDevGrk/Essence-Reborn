package me.grkdev.essenceReborn.commands;

import com.google.common.util.concurrent.ServiceManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceDescriptions;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class MyEssence implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args){
        Player player = (Player) source.getSender();


        // add each essence type and the amount of that type that the player has to a list to be added to the dialog
        List<DialogBody> essenceDialogs = EssenceDescriptions.createEssenceListDialog(player);


        // build dialog to show player
        Dialog essenceDisplay = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Your Essences"))
                        .body(essenceDialogs)
                        .build()
                )
                .type(DialogType.notice())
        );

        player.showDialog(essenceDisplay);

    }

    @Override
    public boolean canUse(CommandSender sender) {
        // only let a player send the command
        return sender instanceof Player player;
    }


}
