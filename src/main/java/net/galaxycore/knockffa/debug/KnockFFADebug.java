package net.galaxycore.knockffa.debug;

import net.galaxycore.knockffa.KnockFFA;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
                player.damage(10000, player);
                player.sendMessage(Component.text("Done"));
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
                player.sendMessage(Component.text("Done"));
            } else {
                sender.sendMessage("§cYou can only use this command as a player!");
            }
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                debugPlayers.remove(player);
                player.sendMessage(Component.text("Done"));
            } else {
                sender.sendMessage("§cYou can only use this command as a player!");
            }
        }

        if (args[0].equalsIgnoreCase("statreset")) {
            //TODO: reset Stats
            sender.sendMessage("Not there yet");
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
                "resetstats » not there yet"
        );
    }

}
