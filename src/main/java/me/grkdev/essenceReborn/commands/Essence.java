package me.grkdev.essenceReborn.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.grkdev.essenceReborn.data.EssenceDescriptions;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class Essence {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("essence")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.literal("give")
                        .then(Commands.literal("essence")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .requires(sender -> sender.getSender().isOp())
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (EssenceTypes type : EssenceTypes.values()){
                                                        if (type == EssenceTypes.DEFAULT) continue;
                                                        builder.suggest(type.toString());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(Essence::giveEssence)
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("switcher")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .requires(sender -> sender.getSender().isOp())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(Essence::giveSwitcher)
                                        )
                                )
                        )
                )
                .then(Commands.literal("manage")
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .requires(sender -> sender.getSender().isOp())
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (EssenceTypes type : EssenceTypes.values()){
                                                        if (type == EssenceTypes.DEFAULT) continue;
                                                        builder.suggest(type.toString());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(Essence::manageAdd)
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .requires(sender -> sender.getSender().isOp())
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (EssenceTypes type : EssenceTypes.values()){
                                                        if (type == EssenceTypes.DEFAULT) continue;
                                                        builder.suggest(type.toString());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(Essence::manageRemove)
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("select")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .requires(sender -> sender.getSender().isOp())
                                        .then(Commands.argument("type", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (EssenceTypes type : EssenceTypes.values()){
                                                        if (type == EssenceTypes.DEFAULT) continue;
                                                        builder.suggest(type.toString());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(Essence::manageSelect)
                                        )
                                )
                        )
                        .then(Commands.literal("view")
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .requires(sender -> sender.getSender().isOp())
                                        .executes(Essence::manageView)
                                )
                        )
                        .then(Commands.literal("disable")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (EssenceTypes type : EssenceTypes.values()){
                                                if (type == EssenceTypes.DEFAULT) continue;
                                                builder.suggest(type.toString());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .requires(sender -> sender.getSender().isOp())
                                        .executes(Essence::manageDisable)
                                )
                        )
                        .then(Commands.literal("enable")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .requires(sender -> sender.getSender().isOp())
                                        .suggests((ctx, builder) -> {
                                            for (EssenceTypes type : EssenceTypes.values()){
                                                if (type == EssenceTypes.DEFAULT) continue;
                                                builder.suggest(type.toString());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(Essence::manageEnable)
                                )
                        )
                );
    }

    // !! once essence can be converted to items, make this give essence items instead of actual essence
    private static int giveEssence(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        // gets arguments from command
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve((ctx.getSource())).getFirst();
        String type = ctx.getArgument("type", String.class);
        int amount = ctx.getArgument("amount", Integer.class);

        // gives the player amount of each essence
        if (type.equals("all")){
            for (EssenceTypes essenceTypes : EssenceTypes.values()){
                EssenceManager.addEssence(player, essenceTypes, amount);
            }

            // send confirmation messages
            player.sendMessage(Component.text("You have received " + amount + " of each essence!", NamedTextColor.DARK_GREEN));
            ctx.getSource().getExecutor().sendMessage(Component.text("You have given " + player.getName() + " " + amount + " of each essence!", NamedTextColor.DARK_GREEN));

            return Command.SINGLE_SUCCESS;
        }

        // give essence
        EssenceManager.addEssence(player, EssenceTypes.getEssenceType(type), amount);

        // send confirmation messages
        player.sendMessage(Component.text("You have received " + amount + " of " + type + " essence!", NamedTextColor.DARK_GREEN));
        ctx.getSource().getExecutor().sendMessage(Component.text("You have given " + player.getName() + " " + amount + " of " + type + " essence!", NamedTextColor.DARK_GREEN));

        return Command.SINGLE_SUCCESS;
    }

    private static int giveSwitcher(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        // gets arguments from command
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve((ctx.getSource())).getFirst();
        int amount = ctx.getArgument("amount", Integer.class);
        ItemStack switchers = EssenceManager.createEssenceSwithcer();
        switchers.setAmount(amount);

        // give essence switchers
        player.give(switchers);

        // send confirmation messages
        player.sendMessage(Component.text("You have received " + amount + " Essence Switchers!", NamedTextColor.DARK_GREEN));
        ctx.getSource().getExecutor().sendMessage(Component.text("You have given " + player.getName() + " " + amount + " Essence Switchers!", NamedTextColor.DARK_GREEN));


        return Command.SINGLE_SUCCESS;
    }


    private static int manageAdd(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        // gets arguments from command
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve((ctx.getSource())).getFirst();
        String type = ctx.getArgument("type", String.class);
        int amount = ctx.getArgument("amount", Integer.class);

        // gives the player amount of each essence
        if (type.equals("all")){
            for (EssenceTypes essenceTypes : EssenceTypes.values()){
                EssenceManager.addEssence(player, essenceTypes, amount);
            }

            // send confirmation messages
            player.sendMessage(Component.text("You have received " + amount + " of each essence!", NamedTextColor.DARK_GREEN));
            ctx.getSource().getExecutor().sendMessage(Component.text("You have given " + player.getName() + " " + amount + " of each essence!", NamedTextColor.DARK_GREEN));

            return Command.SINGLE_SUCCESS;
        }

        // give essence
        EssenceManager.addEssence(player, EssenceTypes.getEssenceType(type), amount);

        // send confirmation messages
        player.sendMessage(Component.text("You have received " + amount + " of " + type + " essence!", NamedTextColor.DARK_GREEN));
        ctx.getSource().getExecutor().sendMessage(Component.text("You have given " + player.getName() + " " + amount + " of " + type + " essence!", NamedTextColor.DARK_GREEN));

        return Command.SINGLE_SUCCESS;
    }

    private static int manageRemove(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        // gets arguments from command
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve((ctx.getSource())).getFirst();
        String type = ctx.getArgument("type", String.class);
        int amount = ctx.getArgument("amount", Integer.class);

        // gives the player amount of each essence
        if (type.equals("all")){
            for (EssenceTypes essenceTypes : EssenceTypes.values()){
                EssenceManager.removeEssence(player, essenceTypes, amount);
            }

            // send confirmation messages
            player.sendMessage(Component.text("You have lost " + amount + " of each essence!", NamedTextColor.DARK_RED));
            ctx.getSource().getExecutor().sendMessage(Component.text("You have removed " + amount + " of each essence from " + player.getName() + "!", NamedTextColor.DARK_GREEN));

            return Command.SINGLE_SUCCESS;
        }

        // give essence
        EssenceManager.removeEssence(player, EssenceTypes.getEssenceType(type), amount);

        // send confirmation messages
        player.sendMessage(Component.text("You have lost " + amount + " of " + type + " essence!", NamedTextColor.DARK_RED));
        ctx.getSource().getExecutor().sendMessage(Component.text("You have removed " + amount + " of " + type + " essence from " + player.getName() + "!", NamedTextColor.DARK_GREEN));

        return Command.SINGLE_SUCCESS;
    }

    //! make it so that if they don't input an essence type, it opens the essence switch dialog instead
    private static int manageSelect(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve((ctx.getSource())).getFirst();
        String type = ctx.getArgument("type", String.class);


        EssenceManager.setActiveEssence(player, EssenceTypes.getEssenceType(type));

        player.sendMessage(Component.text("Your Essence Type has been set to " + type + "!", NamedTextColor.DARK_AQUA));
        ctx.getSource().getExecutor().sendMessage(Component.text("You have set " + player.getName() + "'s Essence Type to " + type + "!", NamedTextColor.DARK_GREEN));


        return Command.SINGLE_SUCCESS;
    }

    private static int manageView(@NonNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        // add each essence type and the amount of that type that the player has to a list to be added to the dialog
        List<DialogBody> essenceDialogs = EssenceDescriptions.createEssenceListDialog(ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve((ctx.getSource())).getFirst());



        // build dialog to show player
        Dialog essenceDisplay = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Your Essences"))
                        .body(essenceDialogs)
                        .build()
                )
                .type(DialogType.notice())
        );

        ctx.getSource().getExecutor().showDialog(essenceDisplay);

        return Command.SINGLE_SUCCESS;
    }



    private static int manageDisable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException{
        EssenceTypes type = EssenceTypes.getEssenceType(ctx.getArgument("type", String.class));
        type.power.setEnabled(false);

        ctx.getSource().getExecutor().sendMessage(Component.text("Disabled " + type + " Essence!", NamedTextColor.DARK_RED));


        return Command.SINGLE_SUCCESS;
    }

    private static int manageEnable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException{
        EssenceTypes type = EssenceTypes.getEssenceType(ctx.getArgument("type", String.class));
        type.power.setEnabled(true);

        ctx.getSource().getExecutor().sendMessage(Component.text("Enabled " + type + " Essence!", NamedTextColor.DARK_GREEN));


        return Command.SINGLE_SUCCESS;
    }
}
