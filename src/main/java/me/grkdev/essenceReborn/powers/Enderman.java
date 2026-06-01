package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Enderman extends Power {
    private EssenceTypes essenceType = EssenceTypes.ENDERMAN;

    public EssenceTypes getEssenceType() {
        return essenceType;
    }

    public void setEssenceType(EssenceTypes essenceType) {
        this.essenceType = essenceType;
    }


    @Override
    public void onPassivePower(Player player) {
        // increase player reach by 1.5 blocks
        player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(6);
        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(4.5);
    }

    // !! Possibly change to being able to swap places with the selected player or make 2 players (not you) swap places
    // !! Possibly support all entities not just players
    // !! add effects and sounds
    @Override
    public void onWeakPower(Player player) {
        // title animation tasks
        int[] i = {0};

        Runnable lookingTitle = () -> {
            Component titleComponent;

            if (i[0] == 0){
                titleComponent = Component.text("Looking for Player", NamedTextColor.GRAY);
                i[0] = 1;
            }
            else if (i[0] == 1){
                titleComponent = Component.text("Looking for Player.", NamedTextColor.GRAY);
                i[0] = 2;
            }
            else if (i[0] == 2){
                titleComponent = Component.text("Looking for Player..", NamedTextColor.GRAY);
                i[0] = 3;
            }
            else{
                titleComponent = Component.text("Looking for Player...", NamedTextColor.GRAY);
                i[0] = 0;
            }

            Title.Times times = Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofMillis(100));
            Title title =  Title.title(titleComponent, Component.text("Look at a player to swap positions!", NamedTextColor.AQUA), times);

            player.showTitle(title);
        };
        Runnable leapingTitle = () -> {
            Component titleComponent;

            if (i[0] == 0){
                titleComponent = Component.text("Initiating Quantum Leap", NamedTextColor.DARK_PURPLE);
                i[0] = 1;
            }
            else if (i[0] == 1){
                titleComponent = Component.text("Initiating Quantum Leap.", NamedTextColor.DARK_PURPLE);
                i[0] = 2;
            }
            else if (i[0] == 2){
                titleComponent = Component.text("Initiating Quantum Leap..", NamedTextColor.DARK_PURPLE);
                i[0] = 3;
            }
            else{
                titleComponent = Component.text("Initiating Quantum Leap...", NamedTextColor.DARK_PURPLE);
                i[0] = 0;
            }

            Title.Times times = Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofMillis(100));
            Title title =  Title.title(titleComponent, Component.text("Continue looking at the player to complete the Quantum Leap!", NamedTextColor.YELLOW), times);

            player.showTitle(title);
        };

        // used for glow color
        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team purpleTeam = mainScoreboard.getTeam("purple");


        Player[] prevPlayer = {player};
        int[] count = {0};
        boolean[] leapingText = {false, false};

        // start with looking for player text
        BukkitTask[] titleTask = {Bukkit.getScheduler().runTaskTimer(plugin, lookingTitle, 0, 12)};


        float[] pitch = {1f};

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            // display the Quantum Leaping... text if it is supposed to be displayed and display looking for player text otherwise
            if(leapingText[0] && !leapingText[1]){
                titleTask[0].cancel();
                titleTask[0] = Bukkit.getScheduler().runTaskTimer(plugin, leapingTitle, 0, 12);
                leapingText[1] = true;
            }
            else if (!leapingText[0] && leapingText[1]){
                titleTask[0].cancel();
                titleTask[0] = Bukkit.getScheduler().runTaskTimer(plugin, lookingTitle, 0, 12);
                leapingText[1] = false;
            }



            // remove everyone from purple team to make sure that people don't stay purple after not being looked at anymore
            for (String s : purpleTeam.getEntries()){
                purpleTeam.removeEntry(s);
            }

            // find player that player is looking at
            RayTraceResult result = player.rayTraceEntities(100);


            if (result == null || !(result.getHitEntity() instanceof Player p)){
                leapingText[0] = false;
                count[0] = 0;
                return;
            }


            // make selected player glow purple
            p.setScoreboard(mainScoreboard);
            purpleTeam.addEntry(p.getName());
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5, 0));


            // play teleporting sound with increasing pitch
            p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, pitch[0]);
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, pitch[0]);
            pitch[0] += 0.2f;


            // check that looking at same player
            if (prevPlayer[0].equals(p)){
                count[0]++;
                leapingText[0] = true;
            }
            else{
                leapingText[0] = false;
                prevPlayer[0] = p;
                count[0] = 0;
            }


            // switch positions if looking at for 3 seconds
            if (count[0] >= 12){
                Location loc1 = player.getLocation();
                Location loc2 = p.getLocation();
                player.teleport(loc2);
                p.teleport(loc1);

                // teleport sound effects and particles
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 250, 1, 1, 1, new Particle.DustOptions(Color.fromRGB(151, 35, 223), 2f));
                p.getWorld().spawnParticle(Particle.DUST, p.getLocation(), 250, 1, 1, 1, new Particle.DustOptions(Color.fromRGB(151, 35, 223), 2f));


                titleTask[0].cancel();

                for (String s : purpleTeam.getEntries()){
                    purpleTeam.removeEntry(s);
                }

                task.cancel();
            }
        }, 0, 5);



    }

    @Override
    public void onStrongPower(Player player){
        //time counter
        int[] i = {0};

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            // stop after 15 seconds
            if (i[0] >= 60){
                task.cancel();
                return;
            }


            Location loc1 = player.getLocation().add(3, 3, 3);
            Location loc2 = player.getLocation().subtract(3, 3, 3);

            // get every block in 6x6 volume
            for(int x = loc2.getBlockX(); x < loc1.getBlockX(); x++) {
                for(int z = loc2.getBlockZ(); z < loc1.getBlockZ(); z++) {
                    for(int y = loc2.getBlockY(); y < loc1.getBlockY(); y++) {


                        // replace all water blocks with air
                        Block block = player.getWorld().getBlockAt(x, y, z);
                        if (block.getType() == Material.WATER){
                            // particles
                            player.getWorld().playSound(block.getLocation(), Sound.BLOCK_SPONGE_ABSORB, .02f, 1f);
                            player.getWorld().spawnParticle(Particle.BLOCK, block.getLocation(), 1, BlockType.WHITE_CONCRETE_POWDER.createBlockData());
                            player.getWorld().spawnParticle(Particle.BLOCK, block.getLocation(), 1, BlockType.LIGHT_BLUE_CONCRETE_POWDER.createBlockData());

                            // remove water
                            block.setType(Material.AIR);
                        }
                    }
                }
            }

            i[0]++;
        }, 0,  5);
    }

//    @Override
//    public void onStrongPower(Player player) {
//
//        // spawns 6 endermen to be a part of the ritual
//        ArrayList<org.bukkit.entity.Enderman> endermen = new ArrayList<>();
//        for (int i = 0; i <= 5; i++){
//            org.bukkit.entity.Enderman enderman = (org.bukkit.entity.Enderman) player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDERMAN);
//            enderman.customName(Component.text("Ender Summoner"));
//            enderman.setAI(false);
//            EndPortalFrame frame = (EndPortalFrame) Bukkit.createBlockData(Material.END_PORTAL_FRAME);
//            frame.setEye(true);
//            frame.setFacing(enderman.getFacing());
//            enderman.setCarriedBlock(frame);
//            enderman.setInvulnerable(true);
//
//            endermen.add(enderman);
//        }
//
//        // ! ------------------------------------------------------------------------------------------------
//        // AI Made Particles (I could never make particles this good)
//        long startTime = System.nanoTime();
//        float timeout = 15;
//        double radius = 10;
//        double[] rotation = {0};
//        Location center = player.getLocation();
//
//        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
//            if (timeout <= (float)(System.nanoTime() - startTime) / 1_000_000_000L) {
//                task.cancel();
//                // ! ------------------------------------------------------------------------------------------------
//                // my code
//                for (Entity enderman : endermen){
//                    enderman.remove();
//                }
//
//                summonRideableDragon(player, center);
//
//                // ! ------------------------------------------------------------------------------------------------
//
//                return;
//            }
//
//            rotation[0] += 0.06;
//            Particle.DustOptions bright = new Particle.DustOptions(Color.fromRGB(220, 80, 255), 1f);
//            Particle.DustOptions mid = new Particle.DustOptions(Color.fromRGB(180, 0, 255), 1f);
//            Particle.DustOptions inner = new Particle.DustOptions(Color.fromRGB(255, 150, 255), .5f);
//
//            // outer ring 1
//            for (int i = 0; i < 120; i++) {
//                double angle = (2 * Math.PI / 120) * i;
//                Location pos = center.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
//                player.getWorld().spawnParticle(Particle.DUST, pos, 1, 0, 0, 0, 0, bright);
//            }
//
//            // outer ring 2 (slightly smaller, counter spins)
//            for (int i = 0; i < 120; i++) {
//                double angle = (2 * Math.PI / 120) * i;
//                Location pos = center.clone().add(Math.cos(angle) * (radius * 0.88), 0, Math.sin(angle) * (radius * 0.88));
//                player.getWorld().spawnParticle(Particle.DUST, pos, 1, 0, 0, 0, 0, mid);
//            }
//
//            // middle ring
//            for (int i = 0; i < 80; i++) {
//                double angle = (2 * Math.PI / 80) * i - rotation[0];
//                Location pos = center.clone().add(Math.cos(angle) * (radius * 0.55), 0, Math.sin(angle) * (radius * 0.55));
//                player.getWorld().spawnParticle(Particle.DUST, pos, 1, 0, 0, 0, 0, bright);
//            }
//
//            // inner ring
//            for (int i = 0; i < 60; i++) {
//                double angle = (2 * Math.PI / 60) * i + rotation[0];
//                Location pos = center.clone().add(Math.cos(angle) * (radius * 0.3), 0, Math.sin(angle) * (radius * 0.3));
//                player.getWorld().spawnParticle(Particle.DUST, pos, 1, 0, 0, 0, 0, mid);
//            }
//
//
//            int index = 0;
//            // star of david — two triangles
//            for (int t = 0; t < 2; t++) {
//                double triangleOffset = t * (Math.PI / 3) * 2 + rotation[0] * (t == 0 ? 1 : -1);
//                double starRadius = radius * 0.72;
//                double[] px = new double[3];
//                double[] pz = new double[3];
//                for (int i = 0; i < 3; i++) {
//                    double angle = (2 * Math.PI / 3) * i + triangleOffset;
//                    px[i] = Math.cos(angle) * starRadius;
//                    pz[i] = Math.sin(angle) * starRadius;
//                }
//                // draw each side of the triangle
//                for (int i = 0; i < 3; i++) {
//                    int next = (i + 1) % 3;
//                    for (double s = 0; s <= 1; s += 0.03) {
//                        double x = px[i] + (px[next] - px[i]) * s;
//                        double z = pz[i] + (pz[next] - pz[i]) * s;
//                        player.getWorld().spawnParticle(Particle.DUST, center.clone().add(x, 0, z), 1, 0, 0, 0, 0, bright);
//                    }
//                }
//                // small circles at each triangle point
//                for (int i = 0; i < 3; i++) {
//                    for (int j = 0; j < 20; j++) {
//                        double angle = (2 * Math.PI / 20) * j;
//                        double x = px[i] + Math.cos(angle) * (radius * 0.08);
//                        double z = pz[i] + Math.sin(angle) * (radius * 0.08);
//                        player.getWorld().spawnParticle(Particle.DUST, center.clone().add(x, 0, z), 1, 0, 0, 0, 0, inner);
//                    }
//                }
//
//                // teleports endermen at little circle points
//                for (int i = 0; i < 3; i++){
//                    org.bukkit.entity.Enderman enderman = endermen.get(index);
//                    Location endermanLoc = center.clone().add(px[i], 0, pz[i]);
//                    Location centerLoc = center.clone();
//                    double angle = Math.toDegrees(Math.atan2(centerLoc.getZ() - endermanLoc.getZ(), centerLoc.getX() - endermanLoc.getX())) - 90;
//                    endermanLoc.setYaw((float) angle);
//                    endermanLoc.setPitch(0);
//                    enderman.teleport(endermanLoc);
//                    index++;
//                }
//
//            }
//
//            // hexagon in center
//            double hexRadius = radius * 0.22;
//            for (int i = 0; i < 6; i++) {
//                double angle1 = (2 * Math.PI / 6) * i + rotation[0];
//                double angle2 = (2 * Math.PI / 6) * (i + 1) + rotation[0];
//                double x1 = Math.cos(angle1) * hexRadius, z1 = Math.sin(angle1) * hexRadius;
//                double x2 = Math.cos(angle2) * hexRadius, z2 = Math.sin(angle2) * hexRadius;
//                for (double s = 0; s <= 1; s += 0.1) {
//                    double x = x1 + (x2 - x1) * s;
//                    double z = z1 + (z2 - z1) * s;
//                    player.getWorld().spawnParticle(Particle.DUST, center.clone().add(x, 0, z), 1, 0, 0, 0, 0, bright);
//                }
//            }
//
//            // enchant sparkles scattered around
//            for (int i = 0; i < 6; i++) {
//                double angle = (2 * Math.PI / 6) * i + rotation[0];
//                Location pos = center.clone().add(Math.cos(angle) * (radius * 0.72), 0, Math.sin(angle) * (radius * 0.72));
//                player.getWorld().spawnParticle(Particle.ENCHANT, pos, 3, 0.2, 0.1, 0.2, 0);
//            }
//
//        }, 0, 1);
//
//        // ! ------------------------------------------------------------------------------------------------
//
//    }
//
//    private void summonRideableDragon(Player player, Location loc) {
//        player.getWorld().spawn(loc, EnderDragon.class, dragon -> {
//           dragon.addPassenger(player);
//           dragon.setAware(false);
//           dragon.setCollidable(false);
//           dragon.getPersistentDataContainer().set(new NamespacedKey(plugin, "rideable"), PersistentDataType.BOOLEAN, true);
//
//
//            long startTime = System.nanoTime();
//            float timeout = 60;
//           Bukkit.getScheduler().runTaskTimer(plugin, task -> {
//               if (timeout <= (float)(System.nanoTime() - startTime) / 1000000000L) {
//                   dragon.remove();
//                   task.cancel();
//                   return;
//               }
//
//               // player controls dragon if riding
//               if (Objects.equals(player.getVehicle(), dragon)){
//                   dragon.setPhase(EnderDragon.Phase.LEAVE_PORTAL);
//                   dragon.setRotation(player.getLocation().getYaw() + 180, player.getLocation().getPitch());
//                   dragon.setVelocity(player.getLocation().getDirection());
//                   return;
//               }
//
//               // if not riding freeze dragon
//               dragon.setPhase(EnderDragon.Phase.HOVER);
//               dragon.setVelocity(new Vector(0, 0, 0));
//
//
//
//
//
//           }, 0, 2);
//        });
//    }
}
