package net.galaxycore.knockffa.debug;

import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.bindings.StatsBinding;
import net.galaxycore.knockffa.utils.LocationHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KnockFFADebug implements CommandExecutor, TabCompleter {

    private static final ArrayList<Player> debugPlayers = new ArrayList<>();

    public KnockFFADebug() {
        KnockFFA.getInstance().setCommandExecutor("kffadb", this);
        KnockFFA.getInstance().setTabCompleter("kffadb", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length != 1)
            return false;

        if (args[0].equalsIgnoreCase("suicide")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                int spawns = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("spawn.count"));
                Location loc = LocationHelper.getLocation("spawn." + new Random().nextInt(spawns));
                loc.setY(-loc.getY());
                player.teleport(loc);
                player.sendMessage(Component.text("Done that Master! Nya!"));
            } else {
                sender.sendMessage("§cYou can only use this command as a player!");
            }
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!debugPlayers.contains(player)) {
                    debugPlayers.add(player);
                }
                player.sendMessage(Component.text("Done that Master! Nya!"));
            } else {
                sender.sendMessage("§cYou can only use this command as a player!");
            }
        }

        if (args[0].equalsIgnoreCase("off")) {
            Player player = (Player) sender;
            debugPlayers.remove(player);
            player.sendMessage(Component.text("Done that Master! Nya!"));
        }

        if (args[0].equalsIgnoreCase("statreset")) {
            Player player = (Player) sender;
            new StatsBinding(player).reset();
            sender.sendMessage("Done that Master! Nya!");
        }

        return true;
    }

    public static void debug(String msg) {
        for (Player debugPlayer : debugPlayers) {
            if (Bukkit.getOnlinePlayers().contains(debugPlayer))
                debugPlayer.sendMessage(Component.text("§8[§9DEBUG§8]§7 " + msg));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of(
                "suicide",
                "on",
                "off",
                "statsreset"
        );
    }

}
