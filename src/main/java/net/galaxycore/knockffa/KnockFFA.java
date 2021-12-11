package net.galaxycore.knockffa;

import lombok.Getter;
import net.galaxycore.galaxycorecore.GalaxyCoreCore;
import net.galaxycore.galaxycorecore.configuration.ConfigNamespace;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.galaxycorecore.configuration.internationalisation.I18N;
import net.galaxycore.galaxycorecore.scoreboards.ScoreBoardController;
import net.galaxycore.knockffa.bindings.ScoreboardCallback;
import net.galaxycore.knockffa.bindings.StatsBinding;
import net.galaxycore.knockffa.debug.KnockFFADebug;
import net.galaxycore.knockffa.listeners.MessageSetLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class KnockFFA extends JavaPlugin {

    @Getter
    private static KnockFFA instance;

    @Getter
    private GalaxyCoreCore core;

    @Getter
    private ConfigNamespace configNamespace;

    @Getter
    private KnockFFADebug knockFFADebug;

    @Override
    public void onEnable() {
        instance = this;

        // CORE //
        core = Bukkit.getServicesManager().load(GalaxyCoreCore.class);
        getLogger().info("Using Core: " + core);

        // CONFIG //
        configNamespace = core.getDatabaseConfiguration().getNamespace("knockffa");
        configNamespace.setDefault("spawn.count", "1");
        configNamespace.setDefault("price.booster", "150");
        configNamespace.setDefault("price.messageset.0", "1500");
        configNamespace.setDefault("price.messageset.1", "1500");
        configNamespace.setDefault("price.messageset.2", "1500");
        configNamespace.setDefault("death_coins_minus", "1");
        configNamespace.setDefault("kill_coins_plus", "5");

        // I18N MESSAGE SET LOAER //
        new MessageSetLoader();

        // I18N //
        for (int i = 0; i < 4; i++) {
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings", "§eEinstellungen");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.buy", "§eKaufen: ");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.alreadybought", "§eBereits gekauft");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.coins", "Coins");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.setchosen", "§eSet Ausgewählt");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.booster.lore", "Schleudert dich für 5 min nach vorne");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.booster.bought", "§eDu hast den Booster für 5 min erhalten!");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.booster.price", "§7150 Coins");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.messageset.0.name", "Standart-Nachrichten");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.messageset.1.name", "Spezial-Nachrichten 1");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.messageset.2.name", "Spezial-Nachrichten 2");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".settings.messageset.3.name", "Spezial-Team-Nachrichten");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".booster", "§cBooster");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".sword", "§cSchwert");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".bow", "§cBogen");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".arrow", "§cPfeil");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".killed", "§l§c✖ §r§4Gestorben");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".killed.sub", "%rank_prefix%%player% §8| §c-5 Coins");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".killedself", "%rank_prefix%%player% §9hat sich selber mit dem Bogen abgeschossen. Das ist belastend.");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".wonfight", "§l§a✓ §r§4+5 Coins");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".wonfight.sub", "%rank_prefix%%player%");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.kills", "Kills");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.kills.color", "§e");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.deaths", "Tode");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.deaths.color", "§e");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.kd", "KD");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.kd.color", "§e");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.coins", "Coins");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.coins.color", "§e");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.sub", "Teamspeak-Server");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".score.sub.value", "galaxycore.net");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".nomoney", "§cDu hast nicht genügend Geld");
            I18N.setDefaultByLang("de_DE", "knockffa." + i + ".streak", "§c%player% hat nun einen Streak von %d!");

            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings", "§eSettings");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.buy", "§eBuy: ");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.coins", "Coins");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.alreadybought", "§eAlready bought");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.setchosen", "§eSet Chosen");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.booster.lore", "Throws yourself forward for 5 min\n§7(150 Coins)");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.booster.bought", "§eYou bought the Booster for five minutes!");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.booster.price", "§7150 Coins");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.messageset.0.name", "Normal Messages");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.messageset.1.name", "Special Messages 1");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.messageset.2.name", "Special Messages 2");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".settings.messageset.3.name", "Special Team Messages");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".booster", "§cBooster");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".sword", "§cSword");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".bow", "§cBow");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".arrow", "§cArrow");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".killed", "§l§c✖ §r§4You Died");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".killed.sub", "%rank_prefix%%player% §8| §c-1 Coin");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".killedself", "%rank_prefix%%player% §9tried to kill himself with a bow. That's hilarious!");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".wonfight", "§l§a✓ §r§4+5 Coins");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".wonfight.sub", "%rank_prefix%%player%");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.kills", "Kills");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.kills.color", "§e");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.deaths", "Deaths");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.deaths.color", "§e");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.kd", "KD");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.kd.color", "§e");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.coins", "Coins");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.coins.color", "§e");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.sub", "Teamspeak-Server");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".score.sub.value", "galaxycore.net");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".nomoney", "§cYou don't have enough money");
            I18N.setDefaultByLang("en_GB", "knockffa." + i + ".streak", "§c%player% now has a streak of %d");
        }

        // LISTENERS //

        // STATS //
        StatsBinding.init();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                StatsBinding.load(PlayerLoader.load(event.getPlayer()));
            }
        }, this);

        // SCOREBOARD //
        ScoreBoardController.setScoreBoardCallback(new ScoreboardCallback());

        // DEBUG //
        knockFFADebug = new KnockFFADebug();

    }

    public void setCommandExecutor(String name, CommandExecutor executor) {
        Objects.requireNonNull(getCommand(name)).setExecutor(executor);
    }

    public void setTabCompleter(String name, TabCompleter tabCompleter) {
        Objects.requireNonNull(getCommand(name)).setTabCompleter(tabCompleter);
    }

}
