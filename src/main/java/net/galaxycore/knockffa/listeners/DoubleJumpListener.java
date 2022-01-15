package net.galaxycore.knockffa.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.packetwrappers.WrapperPlayClientAbilities;
import net.galaxycore.knockffa.utils.SpawnHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DoubleJumpListener extends PacketAdapter {

    private static final List<Player> doubleJumpCooldown = new ArrayList<>();

    public DoubleJumpListener() {
        super(KnockFFA.getInstance(), ListenerPriority.HIGH, PacketType.Play.Client.ABILITIES);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        event.setCancelled(SpawnHelper.isPlayerInASpawn(event.getPlayer()) || doubleJumpCooldown.contains(event.getPlayer()));
        event.getPlayer().setAllowFlight(false);
        if(!event.isCancelled()) {
            event.getPlayer().setFlying(false);
            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(.5).setY(2));
            doubleJumpCooldown.add(event.getPlayer());
            Bukkit.getScheduler().runTaskLaterAsynchronously(KnockFFA.getInstance(), () -> {
                event.getPlayer().setFlying(false);
                doubleJumpCooldown.remove(event.getPlayer());
            }, 3*20L);
        }else {
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
            event.getPlayer().setAllowFlight(true);
        }
    }

}
