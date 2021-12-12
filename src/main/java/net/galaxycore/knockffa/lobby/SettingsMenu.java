package net.galaxycore.knockffa.lobby;

import lombok.SneakyThrows;
import me.kodysimpson.menumanagersystem.menusystem.Menu;
import me.kodysimpson.menumanagersystem.menusystem.PlayerMenuUtility;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.bindings.CoinsBinding;
import net.galaxycore.knockffa.listeners.MessageSetLoader;
import net.galaxycore.knockffa.utils.I18NUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

public class SettingsMenu extends Menu {

    public SettingsMenu(Player player) {
        super(PlayerMenuUtility.getPlayerMenuUtility(player));
    }

    @Override
    public String getMenuName() {
        return I18NUtils.get(playerMenuUtility.getOwner(), "settings");
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    @SneakyThrows
    public void handleMenu(InventoryClickEvent inventoryClickEvent) {

        if (inventoryClickEvent.getCurrentItem() == null)
            return;

        //Messagesets
        if (inventoryClickEvent.getCurrentItem().getType() == Material.BOOK) {
            playerMenuUtility.getOwner().closeInventory();
            CoinsBinding coinsBinding = new CoinsBinding(playerMenuUtility.getOwner());
            if ((inventoryClickEvent.getSlot() - 5 != 0) && !playerMenuUtility.getOwner().hasPermission("knockffa.messageset." + (inventoryClickEvent.getSlot() - 5))) {
                int price = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset." + (inventoryClickEvent.getSlot() - 6)));
                if ((inventoryClickEvent.getSlot() - 5 == 0) || (coinsBinding.getCoins() < price)) {
                    playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "nomoney")));
                    return;
                }
                coinsBinding.decrease(price, "KNOCKFFA_BUY_MSG_" + price + 1);
                String permission = String.valueOf(price + 1);
                LuckPerms luckPerms = LuckPermsProvider.get();
                Node node = Node.builder(permission).build();
                luckPerms.getUserManager().modifyUser(playerMenuUtility.getOwner().getUniqueId(), (User user) -> user.data().add(node));
                playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "settings.setchosen")));
            }
            MessageSetLoader.set(playerMenuUtility.getOwner(), inventoryClickEvent.getSlot() - 5);
            MessageSetLoader.reloadPlayer(playerMenuUtility.getOwner());
        }

        //Materials
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
                playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "settings.setchosen")));
            }
            PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "UPDATE `knockffa_inventory_sort` SET `block_material`=? WHERE `pid`=?"
            );
            update.setString(1, inventoryClickEvent.getCurrentItem().getType().name().toUpperCase());
            update.setInt(2, PlayerLoader.load(playerMenuUtility.getOwner()).getId());
            update.executeUpdate();
        }

        //Sticks
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
                playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "settings.setchosen")));
            }
            PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "UPDATE `knockffa_inventory_sort` SET `stick_material`=? WHERE `pid`=?"
            );
            update.setString(1, inventoryClickEvent.getCurrentItem().getType().name().toUpperCase());
            update.setInt(2, PlayerLoader.load(playerMenuUtility.getOwner()).getId());
            update.executeUpdate();
        }

    }

    @Override
    @SneakyThrows
    public void setMenuItems() {

        int price1 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.0"));
        int price2 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.1"));
        int price3 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.2"));

        ItemStack messageSet0 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 0 ? Material.ENCHANTED_BOOK : Material.BOOK, I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.0.name"));
        ItemStack messageSet1 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 1 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.1") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.1.name"), (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.1") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price1 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
        ItemStack messageSet2 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 2 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.2") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.2.name"), (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.2") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price2 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));

        inventory.setItem(6, messageSet0);
        inventory.setItem(7, messageSet1);
        inventory.setItem(8, messageSet2);

        if (playerMenuUtility.getOwner().hasPermission("knockffa.teammsg")) {
            ItemStack messageSet3 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 3 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.3") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.3.name"), (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.3") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price3 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
            inventory.setItem(8, messageSet3);
        }

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

        inventory.setItem(0, mat0);
        inventory.setItem(1, mat1);
        inventory.setItem(2, mat2);

        int stickPrice1 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.stick.0"));
        int stickPrice2 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.stick.1"));

        ItemStack stick0 = makeItem(Material.STICK, I18NUtils.get(playerMenuUtility.getOwner(), "stick"));
        ItemStack stick1 = makeItem(Material.BLAZE_ROD, (playerMenuUtility.getOwner().hasPermission("knockffa.stick.1") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "stick"), (playerMenuUtility.getOwner().hasPermission("knockffa.stick.1") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + stickPrice1 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
        ItemStack stick2 = makeItem(Material.BONE, (playerMenuUtility.getOwner().hasPermission("knockffa.stick.2") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "stick"), (playerMenuUtility.getOwner().hasPermission("knockffa.stick.2") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + stickPrice2 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));

        inventory.setItem(3, stick0);
        inventory.setItem(4, stick1);
        inventory.setItem(5, stick2);

        setFillerGlass();

    }

}
