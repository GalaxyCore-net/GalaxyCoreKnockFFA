package net.galaxycore.knockffa.lobby;

import lombok.Getter;
import net.galaxycore.knockffa.ingame.IngamePhase;
import net.galaxycore.knockffa.utils.I18NUtils;
import net.galaxycore.knockffa.utils.ObjectHelpers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class LobbyPhase {

    public void setItems(Player player) {

        Inventory inventory = player.getInventory();

        boolean correct = (inventory.getItem(0) == null &&
                inventory.getItem(1) != null) && (ObjectHelpers.objectOrDefault(inventory.getItem(1), new ItemStack(Material.WRITTEN_BOOK)).getType() == Material.CHEST &&
                inventory.getItem(2) == null &&
                inventory.getItem(3) == null &&
                inventory.getItem(4) == null &&
                inventory.getItem(5) == null &&
                inventory.getItem(6) == null &&
                inventory.getItem(7) != null) && (ObjectHelpers.objectOrDefault(inventory.getItem(7), new ItemStack(Material.WRITTEN_BOOK)).getType() == Material.NETHER_STAR &&
                inventory.getItem(8) == null);

        if (correct)
            return;

        IngamePhase.ItemBuilder invSort = new IngamePhase.ItemBuilder(Material.CHEST);
        invSort.setDisplayName(I18NUtils.get(player, "invsort"));

        IngamePhase.ItemBuilder settings = new IngamePhase.ItemBuilder(Material.NETHER_STAR);
        settings.setDisplayName(I18NUtils.get(player, "settings"));

        inventory.clear();

        inventory.setItem(1, invSort.build());
        inventory.setItem(7, settings.build());

    }

}
