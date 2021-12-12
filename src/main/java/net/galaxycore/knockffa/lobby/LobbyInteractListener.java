package net.galaxycore.knockffa.lobby;

import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.utils.SpawnHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LobbyInteractListener implements Listener {

    public LobbyInteractListener() {
        Bukkit.getPluginManager().registerEvents(this, KnockFFA.getInstance());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL)
            return;

        if (event.getItem() == null)
            return;

        if (!SpawnHelper.isPlayerInASpawn(event.getPlayer()))
            return;
        //Settings
        if (event.getItem().getType() == Material.NETHER_STAR) {
            new SettingsMenu(event.getPlayer()).open();
        }

        //Invsort
        if (event.getItem().getType() == Material.CHEST) {
            new InvSortMenu(event.getPlayer()).open();
        }

    }

}
