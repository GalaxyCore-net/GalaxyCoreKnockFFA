package net.galaxycore.knockffa;

import lombok.Getter;
import net.galaxycore.galaxycorecore.GalaxyCoreCore;
import net.galaxycore.galaxycorecore.configuration.ConfigNamespace;
import net.galaxycore.knockffa.debug.KnockFFADebug;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
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
        assert core != null;
        configNamespace = core.getDatabaseConfiguration().getNamespace("knockffa");

        // CONFIG //

        // I18N //

        // LISTENERS //

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
