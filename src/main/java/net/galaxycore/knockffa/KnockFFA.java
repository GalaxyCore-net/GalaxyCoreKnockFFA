package net.galaxycore.knockffa;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class KnockFFA extends JavaPlugin {

    @Getter
    private static Logger pluginLogger;

    @Override
    public void onEnable() {
        pluginLogger = Bukkit.getLogger();
    }

    @Override
    public void onDisable() {

    }

}
