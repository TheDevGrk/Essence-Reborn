package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Piglin extends Power {

    private EssenceTypes essenceType = EssenceTypes.PIGLIN;

    public EssenceTypes getEssenceType() {
        return essenceType;
    }

    public void setEssenceType(EssenceTypes essenceType) {
        this.essenceType = essenceType;
    }


    @Override
    public void onPassivePower(Player player) {
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (player.isOnline() && EssenceManager.getActiveEssence(player) == EssenceTypes.PIGLIN){
                if(PowerManager.hasPassiveUnlocked(player) && player.getWorld().getName().equals("world_nether")){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 40, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
                }
                return;
            }

            task.cancel();
        }, 0, 20);
    }

    @Override
    public void onWeakPower(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PIGLIN_ANGRY, 12f, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NETHER_GOLD_ORE_STEP, 10f, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_STRIDER_EAT, 3f, 1f);

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "weak_power_active");

        pdc.set(key, PersistentDataType.BOOLEAN, true);

        Bukkit.getScheduler().runTaskLater(plugin, task -> {
            pdc.set(key, PersistentDataType.BOOLEAN, false);
        }, 300);

        long startTime = System.nanoTime();
        float timeout = 15;
        float[] radius = {0};
        float maxRadius = 7;
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (timeout <= (float) (System.nanoTime() - startTime) / 1000000000L){
                task.cancel();
            }

            if (radius[0] < maxRadius){
                radius[0] += 0.35f;
            }

            int particles = 60;
            for (int i = 0; i < particles; i++){
                double angle = (2 * Math.PI / particles) * i;
                Location particlePos = player.getLocation().clone().add(Math.cos(angle) * radius[0], 0, Math.sin(angle) * radius[0]);

                player.getWorld().spawnParticle(Particle.DUST, particlePos, 2, 0, 0 ,0, new Particle.DustOptions(Color.fromRGB(255, 215, 0), 1.5f));
                player.getWorld().spawnParticle(Particle.DUST, particlePos, 2, 0, 0 ,0, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.5f));

            }


        }, 0, 2);
    }

    @Override
    public void onStrongPower(Player player) {
        // add player to "gilded" team for gold outline
        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(mainScoreboard);
        Team gildedTeam = mainScoreboard.getTeam("gilded");
        gildedTeam.addEntry(player.getName());

        player.setGlowing(true);

        //effects
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 250, 1, 1, 1);
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 250, 1, 1, 1);
        player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 250, 1, 1, 1, new Particle.DustOptions(Color.fromRGB(255, 215, 0), 2f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 3f, 1f);


        // make player invincible and unable to attack (visually, event handler actually cancels attack)
        player.setInvulnerable(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 254));

        // replace armor with gold armor
        // save old armor
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chest = player.getInventory().getChestplate();
        ItemStack legs = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();


        // make gold armor unbreakable and have curse of binding
        ItemStack[] goldenArmor = {
                new ItemStack(Material.GOLDEN_HELMET),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.GOLDEN_BOOTS)
        };

        for (ItemStack piece : goldenArmor) {
            ItemMeta meta = piece.getItemMeta();
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            piece.setItemMeta(meta);
        }


        // swap armor
        player.getInventory().setHelmet(goldenArmor[0]);
        player.getInventory().setChestplate(goldenArmor[1]);
        player.getInventory().setLeggings(goldenArmor[2]);
        player.getInventory().setBoots(goldenArmor[3]);



        Bukkit.getScheduler().runTaskLater(plugin, task -> {
            // reset player state
            player.getInventory().setHelmet(helmet);
            player.getInventory().setChestplate(chest);
            player.getInventory().setLeggings(legs);
            player.getInventory().setBoots(boots);
            player.setInvulnerable(false);
            player.setGlowing(false);
            gildedTeam.removeEntry(player.getName());
        }, 200);
    }


    // used for weak power
    public static boolean inBlockingRadius(Player player){
        double radius = 7;

        for (Player p : plugin.getServer().getOnlinePlayers()){
            if (EssenceManager.getActiveEssence(p) != EssenceTypes.PIGLIN || !PowerManager.hasWeakPowerUnlocked(p)
                    || !p.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "weak_power_active"), PersistentDataType.BOOLEAN, false)
                    || PowerManager.isTrusted(p, player)) continue;

            if (player.getLocation().distanceSquared(p.getLocation()) <= radius * radius){
                return true;
            }
        }

        return false;
    }
}


