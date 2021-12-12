package net.galaxycore.knockffa.ingame;

import lombok.Getter;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.utils.SpawnHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public class IngameEventListener implements Listener {

    public IngameEventListener() {
        Bukkit.getPluginManager().registerEvents(this, KnockFFA.getInstance());
    }

    @Getter
    private static final HashMap<Player, Player> lastDamage = new HashMap<>();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            event.setCancelled(false);
            return;
        }
        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (SpawnHelper.isPlayerInASpawn(damaged)) {
            event.setCancelled(true);
            return;
        }
        lastDamage.put(damaged, damager);

    }

}
