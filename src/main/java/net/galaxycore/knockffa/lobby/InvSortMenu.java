package net.galaxycore.knockffa.lobby;

import lombok.SneakyThrows;
import me.kodysimpson.menumanagersystem.menusystem.Menu;
import me.kodysimpson.menumanagersystem.menusystem.PlayerMenuUtility;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.utils.I18NUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class InvSortMenu extends Menu {

    public InvSortMenu(Player playerMenuUtility) {
        super(PlayerMenuUtility.getPlayerMenuUtility(playerMenuUtility));
    }

    @Override
    public String getMenuName() {
        return I18NUtils.get(playerMenuUtility.getOwner(), "invsort");
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent inventoryClickEvent) {

        if (inventoryClickEvent.getClickedInventory() == null && inventoryClickEvent.getCurrentItem() == null)
            return;
        inventoryClickEvent.setCancelled(false);
    }

    @SneakyThrows
    public static void updateInvSortEntry(Player player, Inventory inventory) {

        PreparedStatement invSort = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "SELECT * FROM `knockffa_inventory_sort` WHERE pid=?"
        );
        invSort.setInt(1, PlayerLoader.load(player).getId());
        ResultSet resultInvSort = invSort.executeQuery();

        if (!resultInvSort.next()) {
            PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "INSERT INTO `knockffa_inventory_sort` (pid) VALUES (?)"
            );
            update.setInt(1, PlayerLoader.load(player).getId());
            update.executeUpdate();
            update.close();
            updateInvSortEntry(player, inventory);
        }

        PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "UPDATE `knockffa_inventory_sort` SET stick_slot=?, blocks_slot=?, pearl_slot=?, rod_slot=?, web_slot=? WHERE pid=?"
        );
        update.setInt(1, resultInvSort.getInt("stick_slot"));
        update.setInt(2, resultInvSort.getInt("blocks_slot"));
        update.setInt(3, resultInvSort.getInt("pearl_slot"));
        update.setInt(4, resultInvSort.getInt("rod_slot"));
        update.setInt(5, resultInvSort.getInt("web_slot"));
        update.setInt(6, PlayerLoader.load(player).getId());
        Material blockMaterial = Material.getMaterial(resultInvSort.getString("block_material").toUpperCase());
        Material stickMaterial = Material.getMaterial(resultInvSort.getString("stick_material").toUpperCase());
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i) == null || Objects.requireNonNull(inventory.getItem(i)).getType() == Material.AIR)
                continue;
            Material type = Objects.requireNonNull(inventory.getItem(i)).getType();
            if (type == stickMaterial)
                update.setInt(1, i);
            else if (type == blockMaterial)
                update.setInt(2, i);
            else if (type == Material.ENDER_PEARL)
                update.setInt(3, i);
            else if (type == Material.FISHING_ROD)
                update.setInt(4, i);
            else if (type == Material.COBWEB)
                update.setInt(5, i);

        }
        update.executeUpdate();
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

        int stickSlot = resultInvSort.getInt("stick_slot");
        int rodSlot = resultInvSort.getInt("rod_slot");
        int pearlSlot = resultInvSort.getInt("pearl_slot");
        int blockSlot = resultInvSort.getInt("blocks_slot");
        int webSlot = resultInvSort.getInt("web_slot");

        ItemStack stick = makeItem(Material.getMaterial(resultInvSort.getString("stick_material").toUpperCase()), I18NUtils.get(playerMenuUtility.getOwner(), "stick"), I18NUtils.get(playerMenuUtility.getOwner(), "stick.lore"));
        ItemStack rod = makeItem(Material.FISHING_ROD, I18NUtils.get(playerMenuUtility.getOwner(), "rod"), I18NUtils.get(playerMenuUtility.getOwner(), "rod.lore"));
        ItemStack pearl = makeItem(Material.ENDER_PEARL, I18NUtils.get(playerMenuUtility.getOwner(), "pearl"), I18NUtils.get(playerMenuUtility.getOwner(), "pearl.lore"));
        ItemStack blocks = makeItem(Material.getMaterial(resultInvSort.getString("block_material").toUpperCase()), I18NUtils.get(playerMenuUtility.getOwner(), "blocks"), I18NUtils.get(playerMenuUtility.getOwner(), "blocks.lore"));
        blocks.setAmount(64);
        ItemStack web = makeItem(Material.COBWEB, I18NUtils.get(playerMenuUtility.getOwner(), "web"), I18NUtils.get(playerMenuUtility.getOwner(), "web.lore"));

        inventory.setItem(stickSlot, stick);
        inventory.setItem(rodSlot, rod);
        inventory.setItem(pearlSlot, pearl);
        inventory.setItem(blockSlot, blocks);
        inventory.setItem(webSlot, web);

    }

}
