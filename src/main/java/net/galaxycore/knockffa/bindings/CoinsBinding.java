package net.galaxycore.knockffa.bindings;

import net.galaxycore.galaxycorecore.coins.CoinDAO;
import net.galaxycore.galaxycorecore.coins.PlayerTransactionError;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.debug.KnockFFADebug;
import org.bukkit.entity.Player;

public class CoinsBinding {

    private final Player player;

    private final CoinDAO dao;

    public CoinsBinding(Player player) {
        this.player = player;
        this.dao = new CoinDAO(PlayerLoader.load(player), KnockFFA.getInstance());
    }

    public long getCoins() {
        return dao.get();
    }

    public void increase(long coins) {
        KnockFFADebug.debug(player + ": Add " + coins + " Coins");
        dao.transact(null, -coins, "KFFAGetCoinsForKill");
    }

    public void decrease(long coins, String reason) {
        KnockFFADebug.debug(player + ": Remove" + coins + " Coins");
        try {
            dao.transact(null, coins, "KFFARemoveCoins::" + reason);
        } catch (PlayerTransactionError ignored) {
            dao.transact(null, dao.get(), "OHRemoveCoins::" + reason);
        }
    }

    public boolean hasCoins(long coins) {
        return getCoins() >= coins;
    }

}
