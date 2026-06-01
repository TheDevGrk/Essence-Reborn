package me.grkdev.essenceReborn;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.grkdev.essenceReborn.commands.*;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.listeners.*;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import static net.kyori.adventure.text.Component.text;

public final class EssenceReborn extends JavaPlugin {
    public static EssenceReborn plugin;
    public static World overWorld;

    @Override
    public void onEnable() {
        plugin = this;
        // set up all listeners
        Bukkit.getPluginManager().registerEvents(new MobDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EssenceSwitchDialogListener(), this);
        Bukkit.getPluginManager().registerEvents(new PowerCrystalListener(), this);
        Bukkit.getPluginManager().registerEvents(new FallDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new EggHitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PassiveEffectsOnJoin(), this);
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

        //-----------------------------------------------------------------------------------

        // register all commands
        registerCommand("myessence", new MyEssence());
        registerCommand("selectessence", new SelectEssence());
        registerCommand("crystal", new Crystal());
        registerCommand("trust", new Trust());
        registerCommand("untrust", new Untrust());
        registerCommand("giveessence", new GiveEssence());

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Essence.build().build());
        });
        //-----------------------------------------------------------------------------------

        for (Player p : this.getServer().getOnlinePlayers()){
            PowerManager.setWeakPowerCooldown(p);
            PowerManager.setStrongPowerCooldown(p);
            EssenceManager.getActiveEssence(p).power.onPassivePower(p);
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
    }

    //! TODO
    //// Fix being able to wind charge in feather time
    //// Fix necromanced mobs not spawning
    //// Make sure all powers have effects and sounds
    //// ! Build trust into all powers
    //// switch necromancyMode pdc key to necromancy_mode (and look for any others that aren't snake case)
    //// be able to disable certain essences with command
    // make it so /selectessence can only be used once
    // make activatePassivePower and deactivatePassivePower functions instead of just onPassivePower and make this work with enable/disable
    // make passive mob attack be a Goal instead
    // make Surprise Chicken eggs not spawn chickens when cracking

    //! Future Features
    // Essence as an item
    // Configurable amount of uses for /selectessence
    // Reset amount of uses for /selectessence
    // Generally expand /essence manage
        // be able to disabled only certain powers
    // Have all Essence #s configurable in config.yml
    // make @a, @p, etc. work for /essence commands
    // Dragon egg featuresw
}
