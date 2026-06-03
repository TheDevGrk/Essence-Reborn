package me.grkdev.essenceReborn;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.grkdev.essenceReborn.commands.*;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import me.grkdev.essenceReborn.listeners.*;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import static net.kyori.adventure.text.Component.text;

public final class EssenceReborn extends JavaPlugin {
    public static EssenceReborn plugin;
    public static World overWorld = Bukkit.getWorld("world");

    @Override
    public void onEnable() {
        plugin = this;
        // set up all listeners
        Bukkit.getPluginManager().registerEvents(new MobDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EssenceSwitchDialogListener(), this);
        Bukkit.getPluginManager().registerEvents(new PowerCrystalListener(), this);
        Bukkit.getPluginManager().registerEvents(new FallDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new EggHitListener(), this);
        Bukkit.getPluginManager().registerEvents(new FixPowersOnJoin(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlayerAttackListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockGoldenEatListener(), this);
        Bukkit.getPluginManager().registerEvents(new RideEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new DragonDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new DragonBreakBlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new NecromancyMobDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new NecromancerAttackListener(), this);
        Bukkit.getPluginManager().registerEvents(new NecromancyMobTargetListener(), this);
        Bukkit.getPluginManager().registerEvents(new AttackEffectListener(), this);
        Bukkit.getPluginManager().registerEvents(new EssenceSwitcherListener(), this);
        Bukkit.getPluginManager().registerEvents(new SculkImmunityListener(), this);
        Bukkit.getPluginManager().registerEvents(new WardenImmunityListener(), this);
        Bukkit.getPluginManager().registerEvents(new ResourcePackOnJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new SurpriseChickenHatchListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockCraftingListener(), this);

        //-----------------------------------------------------------------------------------

        // register all commands
        registerCommand("myessence", new MyEssence());
        registerCommand("selectessence", new SelectEssence());
        registerCommand("crystal", new Crystal());
        registerCommand("trust", new Trust());
        registerCommand("untrust", new Untrust());
//        registerCommand("giveessence", new GiveEssence());

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Essence.build().build());
        });
        //-----------------------------------------------------------------------------------

        //register all recipes
        // essence switcher
        NamespacedKey essenceSwitcherKey = new NamespacedKey(plugin, "essence_switcher");
        ShapedRecipe switcherRecipe = new ShapedRecipe(essenceSwitcherKey, EssenceManager.createEssenceSwithcer());
        switcherRecipe.shape("ABA", "CDC", "ABA");
        switcherRecipe.setIngredient('A', Material.DIAMOND);
        switcherRecipe.setIngredient('B', Material.GOLDEN_APPLE);
        switcherRecipe.setIngredient('C', Material.NETHERITE_INGOT);
        switcherRecipe.setIngredient('D', Material.NETHER_STAR);

        if (getServer().getRecipe(essenceSwitcherKey) == null){
            getServer().addRecipe(switcherRecipe);
        }

        //-----------------------------------------------------------------------------------


        for (Player p : this.getServer().getOnlinePlayers()){
            p.discoverRecipe(essenceSwitcherKey);
            PowerManager.hideAllCooldownBars(p);
            PowerManager.resetWeakPowerCooldown(p);
            PowerManager.resetStrongPowerCooldown(p);

            EssenceTypes type = EssenceManager.getActiveEssence(p);
            if (type.power.isEnabled()){
                type.power.activatePassivePower(p);
            }

            p.getPersistentDataContainer().set(new NamespacedKey(plugin, "has_surprise_chicken"), PersistentDataType.BOOLEAN, false);
        }

        // create gold team for piglin essence
        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        if (mainScoreboard.getTeam("gilded") == null){
            mainScoreboard.registerNewTeam("gilded").color(NamedTextColor.GOLD);
        }

        //create gray team for necromanced mobs (wither essence)
        if (mainScoreboard.getTeam("necromanced") == null){
            mainScoreboard.registerNewTeam("necromanced").color(NamedTextColor.DARK_GRAY);
        }

        // create purple team for quantum leap (enderman power)
        if (mainScoreboard.getTeam("purple") == null){
            mainScoreboard.registerNewTeam("purple").color(NamedTextColor.LIGHT_PURPLE);
        }

        overWorld = Bukkit.getWorld("world");
        overWorld.loadChunk(0, 0);
        overWorld.setChunkForceLoaded(0, 0, true);




        Bukkit.getLogger().info("ESSENCE PLUGIN ENABLED");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player p : this.getServer().getOnlinePlayers()){
            EssenceManager.getActiveEssence(p).power.deactivatePassivePower(p);
        }

    }








    //! TODO
    //// Fix being able to wind charge in feather time
    //// Fix necromanced mobs not spawning
    //// Make sure all powers have effects and sounds
    //// ! Build trust into all powers
    //// switch necromancyMode pdc key to necromancy_mode (and look for any others that aren't snake case)
    //// be able to disable certain essences with command
    //// make it so /selectessence can only be used once
    //// make activatePassivePower and deactivatePassivePower functions instead of just onPassivePower and make this work with enable/disable
    //// update messaging for when you gain an essence
    //// make Surprise Chicken eggs not spawn chickens when cracking
    //// make cooldown bars reappear when joining
    //// ! essence crystal name/lore
    //// ! add essence switcher recipe

    //! Future Features
    // Essence as an item
    // Configurable amount of uses for /selectessence
    // Reset amount of uses for /selectessence
    // Generally expand /essence manage
        // be able to disabled only certain powers
    // Have all Essence #s configurable in config.yml
    // make @a, @p, etc. work for /essence commands
    // Dragon egg features
    // make essence crystal lore say name of currently equipped essence and instead of "Weak Power" and "Strong Power" have the power names and descriptions
}
