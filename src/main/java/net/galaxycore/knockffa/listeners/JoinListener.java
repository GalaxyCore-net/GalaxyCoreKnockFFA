package net.galaxycore.knockffa.listeners;

import lombok.SneakyThrows;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.utils.SpawnHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class JoinListener implements Listener {

    public JoinListener() {
        Bukkit.getPluginManager().registerEvents(this, KnockFFA.getInstance());
    }

    @EventHandler
    @SneakyThrows
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setGameMode(GameMode.SURVIVAL);

        event.getPlayer().setExp(0);
        event.getPlayer().setLevel(0);

        // Check if Always day is set, if not, set it and set time to 6000
        if(event.getPlayer().getWorld().getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) != null && !event.getPlayer().getWorld().getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) {
            event.getPlayer().getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            event.getPlayer().getWorld().setTime(6000);
        }

        Objects.requireNonNull(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(2);

        SpawnHelper.reset(event.getPlayer(), false);

        PreparedStatement exists = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "SELECT * FROM knockffa_inventory_sort WHERE `pid`=?"
        );
        exists.setInt(1, PlayerLoader.load(event.getPlayer()).getId());
        ResultSet resultExists = exists.executeQuery();
        if (!resultExists.next()) {
            PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "INSERT INTO `knockffa_inventory_sort` (pid) VALUES (?)"
            );
            update.setInt(1, PlayerLoader.load(event.getPlayer()).getId());
            update.executeUpdate();
            update.close();
        }
        resultExists.close();
        exists.close();
    }

}
