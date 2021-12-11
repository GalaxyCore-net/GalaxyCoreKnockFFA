package net.galaxycore.knockffa.utils;

import net.galaxycore.knockffa.KnockFFA;
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

    public static void reset(Player player) {
        player.teleport(SpawnHelper.getRandomSpawn());
        //TODO Set Player Items
    }

}
