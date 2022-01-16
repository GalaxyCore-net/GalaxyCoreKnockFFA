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
import java.sql.ResultSet;

public class BlocksSettingsMenu extends Menu {

    public BlocksSettingsMenu(Player player) {
        super(PlayerMenuUtility.getPlayerMenuUtility(player));
    }

    @Override
    public String getMenuName() {
        return I18NUtils.get(playerMenuUtility.getOwner(), "blocks");
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

        if (inventoryClickEvent.getCurrentItem().getType() == Material.SANDSTONE || inventoryClickEvent.getCurrentItem().getType() == Material.STONE || inventoryClickEvent.getCurrentItem().getType() == Material.DIAMOND_BLOCK) {
            playerMenuUtility.getOwner().closeInventory();
            CoinsBinding coinsBinding = new CoinsBinding(playerMenuUtility.getOwner());
            if ((inventoryClickEvent.getSlot() != 0) && !playerMenuUtility.getOwner().hasPermission("knockffa.material." + (inventoryClickEvent.getSlot()))) {
                int price = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.material." + (inventoryClickEvent.getSlot())));
                if ((inventoryClickEvent.getSlot() == 0) || (coinsBinding.getCoins() < price)) {
                    playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "nomoney")));
                    return;
                }
                coinsBinding.decrease(price, "KNOCKFFA_BUY_MAT_" + price + 1);
                String permission = String.valueOf(price + 1);
                LuckPerms luckPerms = LuckPermsProvider.get();
                Node node = Node.builder(permission).build();
                luckPerms.getUserManager().modifyUser(playerMenuUtility.getOwner().getUniqueId(), (User user) -> user.data().add(node));
            }
            PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "UPDATE `knockffa_inventory_sort` SET `block_material`=? WHERE `pid`=?"
            );
            update.setString(1, inventoryClickEvent.getCurrentItem().getType().name().toUpperCase());
            update.setInt(2, PlayerLoader.load(playerMenuUtility.getOwner()).getId());
            update.executeUpdate();
            playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "settings.blockchosen")));
        }
    }

    @Override
    @SneakyThrows
    public void setMenuItems() {

        PreparedStatement invSort = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "SELECT * FROM `knockffa_inventory_sort` WHERE pid=?"
        );
        invSort.setInt(1, PlayerLoader.load(playerMenuUtility.getOwner()).getId());
        ResultSet resultInvSort = invSort.executeQuery();

        if (!resultInvSort.next()) {
            PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "INSERT INTO `knockffa_inventory_sort` (pid) VALUES (?)"
            );
            update.setInt(1, PlayerLoader.load(playerMenuUtility.getOwner()).getId());
            update.executeUpdate();
            update.close();
            setMenuItems();
        }

        int matPrice1 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.material.0"));
        int matPrice2 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.material.1"));

        ItemStack mat0 = makeItem(Material.SANDSTONE, I18NUtils.get(playerMenuUtility.getOwner(), "blocks"));
        ItemStack mat1 = makeItem(Material.STONE, (playerMenuUtility.getOwner().hasPermission("knockffa.material.1") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "blocks"), (playerMenuUtility.getOwner().hasPermission("knockffa.material.1") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + matPrice1 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
        ItemStack mat2 = makeItem(Material.DIAMOND_BLOCK, (playerMenuUtility.getOwner().hasPermission("knockffa.material.2") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "blocks"), (playerMenuUtility.getOwner().hasPermission("knockffa.material.2") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + matPrice2 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));

        inventory.setItem(9+3, mat0);
        inventory.setItem(9+4, mat1);
        inventory.setItem(9+5, mat2);

        setFillerGlass();

    }

}
