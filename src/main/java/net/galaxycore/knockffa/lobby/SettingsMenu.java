package net.galaxycore.knockffa.lobby;

import me.kodysimpson.menumanagersystem.menusystem.Menu;
import me.kodysimpson.menumanagersystem.menusystem.PlayerMenuUtility;
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

    }

    @Override
    public void setMenuItems() {

        int price1 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.0"));
        int price2 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.1"));
        int price3 = Integer.parseInt(KnockFFA.getInstance().getConfigNamespace().get("price.messageset.2"));

        ItemStack messageSet0 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 0 ? Material.ENCHANTED_BOOK : Material.BOOK, I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.0.name"));
        ItemStack messageSet1 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 1 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("onehit.messageset.1") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.1.name"), (playerMenuUtility.getOwner().hasPermission("onehit.messageset.1") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price1 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
        ItemStack messageSet2 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 2 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("onehit.messageset.2") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.2.name"), (playerMenuUtility.getOwner().hasPermission("onehit.messageset.2") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price2 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));

        inventory.setItem(5, messageSet0);
        inventory.setItem(6, messageSet1);
        inventory.setItem(7, messageSet2);

        if (playerMenuUtility.getOwner().hasPermission("knockffa.teammsg")) {
            ItemStack messageSet3 = makeItem(MessageSetLoader.get(playerMenuUtility.getOwner()) == 3 ? Material.ENCHANTED_BOOK : Material.BOOK, (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.3") ? "" : I18NUtils.get(playerMenuUtility.getOwner(), "settings.buy")) + I18NUtils.get(playerMenuUtility.getOwner(), "settings.messageset.3.name"), (playerMenuUtility.getOwner().hasPermission("knockffa.messageset.3") ? I18NUtils.get(playerMenuUtility.getOwner(), "settings.alreadybought") : "§7" + price3 + " " + I18NUtils.get(playerMenuUtility.getOwner(), "settings.coins")));
            inventory.setItem(8, messageSet3);
        }

        setFillerGlass();

    }

}
