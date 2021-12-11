package net.galaxycore.knockffa.bindings;

import lombok.Getter;
import lombok.SneakyThrows;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.debug.KnockFFADebug;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class StatsBinding {

    private final Player player;
    private static final HashMap<UUID, Float> kdCache = new HashMap<>();
    private static final HashMap<UUID, Float> kills = new HashMap<>();
    private static final HashMap<UUID, Float> deaths = new HashMap<>();

    public StatsBinding(Player player) {
        this.player = player;
    }

    @SneakyThrows
    public static void init() {
        KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `knockffa_stats` (`pid` INTEGER NOT NULL, `kills` INTEGER NOT NULL DEFAULT 0, `deaths` INTEGER NOT NULL DEFAULT 0)"
        ).executeUpdate();
    }

    @SneakyThrows
    public static void load(PlayerLoader playerLoader) {
        PreparedStatement statementIsLoaded = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "SELECT '' FROM `knockffa_stats` WHERE `pid`=?"
        );
        statementIsLoaded.setInt(1, playerLoader.getId());

        ResultSet resultIsLoaded = statementIsLoaded.executeQuery();
        if (!resultIsLoaded.next()) {
            PreparedStatement statementInsert = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "INSERT INTO `knockffa_stats` (`pid`) VALUES (?)"
            );
            statementInsert.setInt(1, playerLoader.getId());
            statementInsert.executeUpdate();
            statementInsert.close();
        }
        resultIsLoaded.close();
        statementIsLoaded.close();

        PreparedStatement getDefaultKD = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "SELECT `kills`, `deaths` FROM `knockffa_stats` WHERE `pid`=?"
        );
        getDefaultKD.setInt(1, playerLoader.getId());
        ResultSet resultDefaultKD = getDefaultKD.executeQuery();
        resultDefaultKD.next();
        float k = resultDefaultKD.getInt("kills");
        float d = resultDefaultKD.getInt("deaths");

        kdCache.remove(playerLoader.getUuid());
        kills.remove(playerLoader.getUuid());
        deaths.remove(playerLoader.getUuid());

        if (d == 0)
            kdCache.put(playerLoader.getUuid(), 0F);
        else
            kdCache.put(playerLoader.getUuid(), k / d);

        kills.put(playerLoader.getUuid(), k);
        deaths.put(playerLoader.getUuid(), d);

        resultDefaultKD.close();
        getDefaultKD.close();
    }

    @SneakyThrows
    public void addKill() {
        KnockFFADebug.debug(player + " gets a Kill added");

        PreparedStatement statement = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "UPDATE `knockffa_stats` SET `kills` = `kills` + 1 WHERE `pid`=?"
        );
        statement.setInt(1, PlayerLoader.load(player).getId());
        statement.executeUpdate();
        statement.close();

        kdUpdate();
    }

    @SneakyThrows
    public void kdUpdate() {
        PreparedStatement getDefaultKD = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "SELECT `kills`, `deaths` FROM `knockffa_stats` WHERE `pid`=?"
        );
        getDefaultKD.setInt(1, PlayerLoader.load(player).getId());
        ResultSet resultDefaultKD = getDefaultKD.executeQuery();

        resultDefaultKD.next();
        float k = resultDefaultKD.getInt("kills");
        float d = resultDefaultKD.getInt("deaths");

        kdCache.remove(player.getUniqueId());
        kills.remove(player.getUniqueId());
        deaths.remove(player.getUniqueId());

        if (d == 0)
            kdCache.put(player.getUniqueId(), 0F);
        else
            kdCache.put(player.getUniqueId(), k / d);

        kills.put(player.getUniqueId(), k);
        deaths.put(player.getUniqueId(), d);

        resultDefaultKD.close();
    }

    @SneakyThrows
    public void addDeath() {
        KnockFFADebug.debug(player + " gets a Death added");

        PreparedStatement statement = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "UPDATE `knockffa_stats` SET `deaths` = `deaths` + 1 WHERE `pid`=?"
        );
        statement.setInt(1, PlayerLoader.load(player).getId());
        statement.executeUpdate();
        statement.close();

        kdUpdate();
    }

    public float getKD() {
        return kdCache.get(player.getUniqueId());
    }

    public float getKills() {
        return kills.get(player.getUniqueId());
    }

    public float getDeaths() {
        return deaths.get(player.getUniqueId());
    }

    @SneakyThrows
    public void reset() {
        KnockFFADebug.debug(player + " gets resetted");

        PreparedStatement statement = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "DELETE FROM `knockffa_stats` WHERE `pid`=?"
        );
        statement.setInt(1, PlayerLoader.load(player).getId());
        statement.executeUpdate();
        statement.close();

        load(PlayerLoader.loadNew(player));
    }

}
