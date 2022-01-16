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
        if(isOnGround(event.getPlayer())) {
            event.getPlayer().setAllowFlight(true);
        }
        if (SpawnHelper.isPlayerInASpawn(event.getPlayer())) {
            KnockFFA.getInstance().getLobbyPhase().setItems(event.getPlayer());
        } else {
            KnockFFA.getInstance().getIngamePhase().setItems(event.getPlayer());
        }
        if (event.getPlayer().getLocation().getBlockY() < Double.parseDouble(KnockFFA.getInstance().getConfigNamespace().get("death_height"))) {
            SpawnHelper.reset(event.getPlayer(), true);
        }
    }

    public static boolean isOnGround(Player player) {
        Block block = player.getLocation().getBlock();
        Block blockBelow = block.getRelative(BlockFace.DOWN);

        BoundingBox playerBox = player.getBoundingBox().clone().expand(BlockFace.DOWN, 0.01);

        boolean overlapsPrev = false;

        overlapsPrev = isOverlap(block, playerBox, overlapsPrev);
        overlapsPrev = isOverlap(blockBelow, playerBox, overlapsPrev);

        return overlapsPrev;
    }

    private static boolean isOverlap(Block block, BoundingBox playerBox, boolean overlaps) {
        overlaps = overlaps | playerBox.overlaps(block.getBoundingBox());
        overlaps = overlaps | playerBox.overlaps(block.getRelative(BlockFace.NORTH).getBoundingBox());
        overlaps = overlaps | playerBox.overlaps(block.getRelative(BlockFace.NORTH_EAST).getBoundingBox());
        overlaps = overlaps | playerBox.overlaps(block.getRelative(BlockFace.EAST).getBoundingBox());
        overlaps = overlaps | playerBox.overlaps(block.getRelative(BlockFace.EAST_SOUTH_EAST).getBoundingBox());
        overlaps = overlaps | playerBox.overlaps(block.getRelative(BlockFace.SOUTH).getBoundingBox());
        overlaps = overlaps | playerBox.overlaps(block.getRelative(BlockFace.SOUTH_WEST).getBoundingBox());
        overlaps = overlaps | playerBox.overlaps(block.getRelative(BlockFace.WEST).getBoundingBox());
        overlaps = overlaps | playerBox.overlaps(block.getRelative(BlockFace.NORTH_WEST).getBoundingBox());
        return overlaps;
    }

}
