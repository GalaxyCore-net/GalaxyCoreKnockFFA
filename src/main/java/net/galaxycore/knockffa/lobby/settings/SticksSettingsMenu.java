package net.galaxycore.knockffa.lobby.settings;

import lombok.SneakyThrows;
import me.kodysimpson.menumanagersystem.menusystem.Menu;
import me.kodysimpson.menumanagersystem.menusystem.PlayerMenuUtility;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.bindings.CoinsBinding;
import net.galaxycore.knockffa.utils.I18NUtils;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;

public class SticksSettingsMenu extends Menu {

    public SticksSettingsMenu(Player player) {
        super(PlayerMenuUtility.getPlayerMenuUtility(player));
    }

    @Override
    public String getMenuName() {
        return I18NUtils.get(playerMenuUtility.getOwner(), "sticks");
    }

    @Override
    public int getSlots() {
        return 9*3;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    @SneakyThrows
    public void handleMenu(InventoryClickEvent inventoryClickEvent) {

        if (inventoryClickEvent.getCurrentItem() == null)
            return;

        if (inventoryClickEvent.getCurrentItem().getType() == Material.STICK || inventoryClickEvent.getCurrentItem().getType() == Material.BLAZE_ROD || inventoryClickEvent.getCurrentItem().getType() == Material.BONE) {
            playerMenuUtility.getOwner().closeInventory();
            CoinsBinding coinsBinding = new CoinsBinding(playerMenuUtility.getOwner());
            if ((inventoryClickEvent.getSlot() != 0) && !playerMenuUtility.getOwner().hasPermission("knockffa.stick." + (inventoryClickEvent.getSlot()))) {
                int price = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.stick." + (inventoryClickEvent.getSlot())));
                if ((inventoryClickEvent.getSlot() == 0) || (coinsBinding.getCoins() < price)) {
                    playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "nomoney")));
                    return;
                }
                coinsBinding.decrease(price, "KNOCKFFA_BUY_STICK_" + price + 1);
                String permission = String.valueOf(price + 1);
                LuckPerms luckPerms = LuckPermsProvider.get();
                Node node = Node.builder(permission).build();
                luckPerms.getUserManager().modifyUser(playerMenuUtility.getOwner().getUniqueId(), (User user) -> user.data().add(node));
            }
            PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "UPDATE `knockffa_inventory_sort` SET `stick_material`=? WHERE `pid`=?"
            );
            update.setString(1, inventoryClickEvent.getCurrentItem().getType().name().toUpperCase());
            update.setInt(2, PlayerLoader.load(playerMenuUtility.getOwner()).getId());
            update.executeUpdate();
            playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "settings.stickchosen")));
        }

    }

    @Override
    public void setMenuItems() {

        int stickPrice1 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.stick.0"));
        int stickPrice2 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.stick.1"));

        ItemStack stick0 = makeItem(Material.STICK, I18NUtils.get(playerMenuUtility.getOwner(), "stick"));
        ItemStack stick1 = makeItem(Material.BLAZE_ROD, (playerMenuUtility.getOwner().hasPermission("knockffa.stick.1") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "stick"), (playerMenuUtility.getOwner().hasPermission("knockffa.stick.1") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + stickPrice1 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
        ItemStack stick2 = makeItem(Material.BONE, (playerMenuUtility.getOwner().hasPermission("knockffa.stick.2") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "stick"), (playerMenuUtility.getOwner().hasPermission("knockffa.stick.2") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + stickPrice2 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));

        inventory.setItem(3+9, stick0);
        inventory.setItem(4+9, stick1);
        inventory.setItem(5+9, stick2);

    }

}
