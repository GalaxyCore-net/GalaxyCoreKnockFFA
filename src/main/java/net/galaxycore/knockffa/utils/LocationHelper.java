package net.galaxycore.knockffa.utils;

import net.galaxycore.knockffa.KnockFFA;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class LocationHelper {
    public static Location getLocation(String configKey) {
        String[] locationSerialized = KnockFFA.getInstance().getConfigNamespace().get(configKey).split(" "); // Something like this: world -9 97 90 180
        String world = locationSerialized[0];
        int x = Integer.parseInt(locationSerialized[1]);
        int y = Integer.parseInt(locationSerialized[2]);
        int z = Integer.parseInt(locationSerialized[3]);
        float yaw = Float.parseFloat(locationSerialized[4]);

        Location location = Objects.requireNonNull(Bukkit.getWorld(world))
                .getBlockAt(x, y, z).getLocation();

        location.setYaw(yaw);
        location.setPitch(0);
        location.add(0.5, 0, 0.5);

        return location;
    }
}
