package net.galaxycore.knockffa.listeners;

import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.utils.SpawnHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    public MoveListener() {
        Bukkit.getPluginManager().registerEvents(this, KnockFFA.getInstance());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (SpawnHelper.isPlayerInASpawn(event.getPlayer())) {
            KnockFFA.getInstance().getLobbyPhase().setItems(event.getPlayer());
        } else {
            KnockFFA.getInstance().getIngamePhase().setItems(event.getPlayer());
        }
        if (event.getPlayer().getLocation().getBlockY() < Double.parseDouble(KnockFFA.getInstance().getConfigNamespace().get("death_height"))) {
            SpawnHelper.reset(event.getPlayer(), true);
        }
    }

}
