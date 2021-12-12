package net.galaxycore.knockffa.ingame;

import lombok.SneakyThrows;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.utils.I18NUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IngamePhase {

    public IngamePhase() {
        init();
    }

    @SneakyThrows
    public static void init() {
        Connection connection = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS `knockffa_inventory_sort` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`pid` INTEGER NOT NULL, " +
                "`stick_slot` INTEGER NOT NULL DEFAULT 0," +
                "`rod_slot` INTEGER NOT NULL DEFAULT 7," +
                "`pearl_slot` INTEGER NOT NULL DEFAULT 8," +
                "`blocks_slot` INTEGER NOT NULL DEFAULT 1," +
                "`web_slot` INTEGER NOT NULL DEFAULT 4," +
                "`stick_material` VARCHAR(255) NOT NULL DEFAULT 'Stick'," +
                "`block_material` VARCHAR(255) NOT NULL DEFAULT 'Sandstone')").executeUpdate();
    }

    @SneakyThrows
    public void setItems(Player player) {
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
            setItems(player);
        }
        if (player.getInventory().contains(Objects.requireNonNull(Material.getMaterial(resultInvSort.getString("stick_material").toUpperCase()))))
            return;
        setItemsWithoutHesitation(player);
    }

    @SneakyThrows
    public void setItemsWithoutHesitation(Player player) {
        Inventory inventory = player.getInventory();
        inventory.clear();

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
            setItemsWithoutHesitation(player);
        }

        int stickSlot = resultInvSort.getInt("stick_slot");
        int rodSlot = resultInvSort.getInt("rod_slot");
        int pearlSlot = resultInvSort.getInt("pearl_slot");
        int blockSlot = resultInvSort.getInt("blocks_slot");
        int webSlot = resultInvSort.getInt("web_slot");

        ItemStack stick = makeItem(Material.getMaterial(resultInvSort.getString("stick_material").toUpperCase()), I18NUtils.get(player, "stick"), I18NUtils.get(player, "stick.lore")).addEnchant(Enchantment.KNOCKBACK, 2).build();
        ItemStack rod = makeItem(Material.FISHING_ROD, I18NUtils.get(player, "rod"), I18NUtils.get(player, "rod.lore")).build();
        ItemStack pearl = makeItem(Material.ENDER_PEARL, I18NUtils.get(player, "pearl"), I18NUtils.get(player, "pearl.lore")).build();
        ItemStack blocks = makeItem(Material.getMaterial(resultInvSort.getString("block_material").toUpperCase()), I18NUtils.get(player, "blocks"), I18NUtils.get(player, "blocks.lore")).build();
        blocks.setAmount(64);
        ItemStack web = makeItem(Material.COBWEB, I18NUtils.get(player, "web"), I18NUtils.get(player, "web.lore")).build();

        inventory.setItem(stickSlot, stick);
        inventory.setItem(rodSlot, rod);
        inventory.setItem(pearlSlot, pearl);
        inventory.setItem(blockSlot, blocks);
        inventory.setItem(webSlot, web);

        player.updateInventory();
    }

    public static ItemBuilder makeItem(Material material, String displayName, String... lore) {
        return new ItemBuilder(material).setDisplayName(displayName).setLore(lore).addEnchant(Enchantment.LUCK, 42).setItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    public static class ItemBuilder {

        private final ItemStack item;
        private final ItemMeta itemMeta;

        public ItemBuilder(Material material) {
            item = new ItemStack(material);
            itemMeta = item.getItemMeta();
        }

        public ItemBuilder setDisplayName(String displayName) {
            itemMeta.displayName(Component.text(displayName));
            return this;
        }

        public ItemBuilder setLore(String... lore) {
            List<Component> loreList = new ArrayList<>();
            Arrays.asList(lore).forEach(loreString -> loreList.add(Component.text(loreString)));
            itemMeta.lore(loreList);
            return this;
        }

        public ItemBuilder addEnchant(Enchantment enchantment, int level) {
            itemMeta.addEnchant(enchantment, level, true);
            return this;
        }

        public ItemBuilder setItemFlag(ItemFlag flag) {
            itemMeta.addItemFlags(flag);
            return this;
        }

        public ItemStack build() {
            item.setItemMeta(itemMeta);
            return item;
        }

    }

}
