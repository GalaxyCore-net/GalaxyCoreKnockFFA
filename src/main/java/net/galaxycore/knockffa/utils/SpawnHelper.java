package net.galaxycore.knockffa.utils;

import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.bindings.CoinsBinding;
import net.galaxycore.knockffa.bindings.StatsBinding;
import net.galaxycore.knockffa.ingame.IngameEventListener;
import net.galaxycore.knockffa.ingame.StreakManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class SpawnHelper {

    public static Location getRandomSpawn() {
        int spawns = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("spawn.count"));
        return LocationHelper.getLocation("spawn." + new Random().nextInt(spawns));
    }

    public static boolean isPlayerInASpawn(Player player) {
        return isLocationInASpawn(player.getLocation());
    }

    public static boolean isLocationInASpawn(Location location) {
        boolean inSpawn = false;

        String[] spawns = KnockFFA.getInstance().getConfigNamespace().get("spawn.shapes").split("\\|");

        for (String spawn : spawns) {
            String[] spawnSplit = spawn.split(" ");

            double x0 = Double.parseDouble(spawnSplit[0]);
            double y0 = Double.parseDouble(spawnSplit[1]);
            double z0 = Double.parseDouble(spawnSplit[2]);
            double x1 = Double.parseDouble(spawnSplit[3]);
            double y1 = Double.parseDouble(spawnSplit[4]);
            double z1 = Double.parseDouble(spawnSplit[5]);
            double xMin = Math.min(x0, x1);
            double yMin = Math.min(y0, y1);
            double zMin = Math.min(z0, z1);
            double xMax = Math.max(x0, x1);
            double yMax = Math.max(y0, y1);
            double zMax = Math.max(z0, z1);

            double x = location.getBlockX();
            double y = location.getBlockY();
            double z = location.getBlockZ();

            inSpawn = inSpawn || (
                    (y >= yMin) &&
                            (y <= yMax) &&
                            (x >= xMin) &&
                            (x <= xMax) &&
                            (z >= zMin) &&
                            (z <= zMax)
            );
        }
        return inSpawn;
    }

    public static void reset(Player player, boolean dead) {
        player.teleport(SpawnHelper.getRandomSpawn());

        if (IngameEventListener.getLastDamage().containsKey(player)) {
            Player killer = IngameEventListener.getLastDamage().get(player);

            player.sendTitle(
                    I18NUtils.getRF(player, "killed", killer),
                    I18NUtils.getRF(player, "killed.sub", killer),
                    20,
                    40,
                    20
            );

            killer.sendTitle(
                    I18NUtils.getRF(killer, "wonfight", player),
                    I18NUtils.getRF(killer, "wonfight.sub", player),
                    20,
                    40,
                    20
            );

            player.playSound(Sound.sound(Key.key("minecraft", "entity.player.death"), Sound.Source.MASTER, 1f, 1f));
            killer.playSound(Sound.sound(Key.key("minecraft", "block.note_block_pling"), Sound.Source.MASTER, 1f, 2f));

            registerPlayerDead(killer, player);
            IngameEventListener.getLastDamage().remove(player);
        } else {
            if (dead) {
                registerPlayerDead(player);
            }
        }

        KnockFFA.getInstance().getLobbyPhase().setItems(player);
    }

    private static void registerPlayerDead(Player damager, Player damaged) {
        new StatsBinding(damager).addKill();
        new StatsBinding(damaged).addDeath();

        StreakManager.registerKill(damager);
        StreakManager.registerDeath(damaged);

        new CoinsBinding(damager).increase(Long.parseLong(KnockFFA.getInstance().getConfigNamespace().get("kill_coins_plus")));
        new CoinsBinding(damaged).decrease(Long.parseLong(KnockFFA.getInstance().getConfigNamespace().get("death_coins_minus")), "PlayerDeath");
    }

    private static void registerPlayerDead(Player damaged) {
        new StatsBinding(damaged).addDeath();

        StreakManager.registerDeath(damaged);

        new CoinsBinding(damaged).decrease(Long.parseLong(KnockFFA.getInstance().getConfigNamespace().get("death_coins_minus")), "PlayerDeath");
    }

}
