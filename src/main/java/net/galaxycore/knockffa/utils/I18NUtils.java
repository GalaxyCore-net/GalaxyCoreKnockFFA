package net.galaxycore.knockffa.utils;

import net.galaxycore.galaxycorecore.configuration.internationalisation.I18N;
import net.galaxycore.galaxycorecore.permissions.LuckPermsApiWrapper;
import net.galaxycore.galaxycorecore.utils.StringUtils;
import net.galaxycore.knockffa.listeners.MessageSetLoader;
import org.bukkit.entity.Player;

public class I18NUtils {

    public static String get(Player player, String key) {
        String s = I18N.getByPlayer(player, "knockffa." + MessageSetLoader.get(player) + "." + key);
        if (s == null)
            s = I18N.getByLang("en_GB", "knockffa." + MessageSetLoader.get(player) + "." + key);

        return StringUtils.replaceRelevant(s, new LuckPermsApiWrapper(player));
    }

    public static String getRF(Player player, String key, Player target) {
        String s = I18N.getByPlayer(player, "knockffa." + MessageSetLoader.get(player) + "." + key);
        if (s == null)
            s = I18N.getByLang("en_GB", "knockffa." + MessageSetLoader.get(player) + "." + key);

        return StringUtils.replaceRelevant(s, new LuckPermsApiWrapper(target));
    }

}
