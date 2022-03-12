package net.galaxycore.knockffa.listeners;

import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.utils.SpawnHelper;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BoundingBox;

public class MoveListener implements Listener {

    public MoveListener() {
        Bukkit.getPluginManager().registerEvents(this, KnockFFA.getInstance());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (SpawnHelper.isLocationInASpawn(event.getPlayer().getLocation())) {
            KnockFFA.getInstance().getLobbyPhase().setItems(event.getPlayer());
        } else if (!SpawnHelper.isLocationInASpawn(event.getPlayer().getLocation().subtract(0, 5, 0))) {
            KnockFFA.getInstance().getIngamePhase().setItems(event.getPlayer());
        }
        if (event.getPlayer().getLocation().getBlockY() < Double.parseDouble(KnockFFA.getInstance().getConfigNamespace().get("death_height"))) {
            SpawnHelper.reset(event.getPlayer(), true);
        }
    }

}
