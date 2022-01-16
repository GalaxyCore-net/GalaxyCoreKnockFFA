package net.galaxycore.knockffa.lobby.settings;

import me.kodysimpson.menumanagersystem.menusystem.Menu;
import me.kodysimpson.menumanagersystem.menusystem.PlayerMenuUtility;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.bindings.CoinsBinding;
import net.galaxycore.knockffa.listeners.MessageSetLoader;
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

public class MessageSetsSettingsMenu extends Menu {

    public MessageSetsSettingsMenu(Player player) {
        super(PlayerMenuUtility.getPlayerMenuUtility(player));
    }

    @Override
    public String getMenuName() {
        return I18NUtils.get(playerMenuUtility.getOwner(), "messages");
    }

    @Override
    public int getSlots() {
        return 9 * 3;
    }

    @Override
    public void handleMenu(InventoryClickEvent inventoryClickEvent) {

        if (inventoryClickEvent.getCurrentItem() == null)
            return;

        if (inventoryClickEvent.getCurrentItem().getType() == Material.BOOK) {
            playerMenuUtility.getOwner().closeInventory();

            switch (inventoryClickEvent.getRawSlot()) {
                case 12:
                case 13:
                case 14:
                    buy(inventoryClickEvent.getRawSlot() - 12);
                    break;
                case 22:
                    buy(3);
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void buy(int set) {
        CoinsBinding coinsBinding = new CoinsBinding(playerMenuUtility.getOwner());
        if(set != 0 && !playerMenuUtility.getOwner().hasPermission("knockffa.messageset." + set)) {
            int price = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset." + (set - 1)));
            if(coinsBinding.getCoins() < price) {
                playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "nomoney")));
                return;
            }
            coinsBinding.decrease(price, "KNOCKFFA_BUY_MSG_" + price + 1);
            String permission = String.valueOf(price + 1);
            LuckPerms luckPerms = LuckPermsProvider.get();
            Node node = Node.builder(permission).build();
            luckPerms.getUserManager().modifyUser(playerMenuUtility.getOwner().getUniqueId(), (User user) -> user.data().add(node));
        }
        MessageSetLoader.set(playerMenuUtility.getOwner(), set);
        MessageSetLoader.reloadPlayer(playerMenuUtility.getOwner());
        playerMenuUtility.getOwner().sendMessage(Component.text(I18NUtils.get(playerMenuUtility.getOwner(), "settings.setchosen")));
    }

    @Override
    public void setMenuItems() {

        int price1 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.0"));
        int price2 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.1"));
        int price3 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.2"));

        ItemStack messageSet0 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 0 ? Material.ENCHANTED_BOOK : Material.BOOK, I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.0.name"));
        ItemStack messageSet1 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 1 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.1") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.1.name"), (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.1") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price1 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
        ItemStack messageSet2 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 2 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.2") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.2.name"), (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.2") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price2 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));

        inventory.setItem(9 + 3, messageSet0);
        inventory.setItem(9 + 4, messageSet1);
        inventory.setItem(9 + 5, messageSet2);

        if (playerMenuUtility.getOwner().hasPermission("knockffa.teammsg")) {
            ItemStack messageSet3 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 3 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.3") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.3.name"), (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.3") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price3 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
            inventory.setItem(18 + 4, messageSet3);
        }

        setFillerGlass();

    }

}
