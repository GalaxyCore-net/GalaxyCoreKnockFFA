package net.galaxycore.knockffa.listeners;

import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import lombok.SneakyThrows;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.galaxycorecore.configuration.internationalisation.I18N;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.ingame.IngamePhase;
import net.galaxycore.knockffa.lobby.InvSortMenu;
import net.galaxycore.knockffa.utils.I18NUtils;
import net.galaxycore.knockffa.utils.SpawnHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.galaxycore.knockffa.ingame.IngamePhase.makeItem;

public class BaseListeners implements Listener {

    public BaseListeners() {
        Bukkit.getPluginManager().registerEvents(this, KnockFFA.getInstance());
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntitySummon(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.ENDER_PEARL && event.getEntityType() != EntityType.FISHING_HOOK)
            event.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        boolean cancel;
        cancel = !SpawnHelper.isLocationInASpawn(event.getWhoClicked().getLocation());
        if (!(event.getView().getTitle().contains(I18NUtils.get((Player) event.getWhoClicked(), "invsort")) && event.getClickedInventory() == event.getView().getTopInventory()))
            cancel = true;
        else {
            if (event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_SOME)
                Bukkit.getScheduler().runTaskLater(KnockFFA.getInstance(), () -> InvSortMenu.updateInvSortEntry((Player) event.getWhoClicked(), event.getView().getTopInventory()), 1);
        }
        if (event.getView().getTitle().contains(I18NUtils.get((Player) event.getWhoClicked(), "settings")) && event.getClickedInventory() == event.getView().getTopInventory())
            cancel = false;
        event.setCancelled(cancel);
    }

    @EventHandler
    public void onHandChange(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickupAroow(PlayerPickupArrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDamaged(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onArmorStandEdit(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
                event.setCancelled(false);
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    @SneakyThrows
    public void onBlockPlace(BlockPlaceEvent event) {
        if (SpawnHelper.isLocationInASpawn(event.getBlockPlaced().getLocation()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }

        PreparedStatement invSort = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                "SELECT * FROM `knockffa_inventory_sort` WHERE pid=?"
        );
        invSort.setInt(1, PlayerLoader.load(event.getPlayer()).getId());
        ResultSet resultInvSort = invSort.executeQuery();

        if (!resultInvSort.next()) {
            PreparedStatement update = KnockFFA.getInstance().getCore().getDatabaseConfiguration().getConnection().prepareStatement(
                    "INSERT INTO `knockffa_inventory_sort` (pid) VALUES (?)"
            );
            update.setInt(1, PlayerLoader.load(event.getPlayer()).getId());
            update.executeUpdate();
            update.close();
            onBlockPlace(event);
        }

        if (event.getBlock().getType() == Material.COBWEB)
            new WebDestroyJob(event.getBlock()).runTaskLater(KnockFFA.getInstance(), 5 * 20L);
        else if (event.getBlock().getType() == Material.getMaterial(resultInvSort.getString("block_material").toUpperCase()))
            new BlockReturnJob(event).runTaskLater(KnockFFA.getInstance(), 5 * 20L);
        event.setCancelled(false);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onFlowerEdit(PlayerFlowerPotManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamageTaken(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(false);
            return;
        }
        if (event.getEntity() instanceof Player &&
                (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                && (event.getCause() != EntityDamageEvent.DamageCause.VOID))
            event.setCancelled(true);
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            SpawnHelper.reset((Player) event.getEntity(), true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        event.setCancelled(SpawnHelper.isPlayerInASpawn(event.getPlayer()));
        if (!event.isCancelled()) {
            if (event.getState().equals(PlayerFishEvent.State.IN_GROUND)) {

                Location playerLoc = event.getPlayer().getLocation();
                Location hookLoc = event.getHook().getLocation();
                Location change = hookLoc.subtract(playerLoc);
                change.setY(change.getY() + 2);
                event.getPlayer().setVelocity(change.toVector().multiply(.35));
                setRodCooldown(event.getPlayer());

            }
        }
    }

    @SneakyThrows
    private void setRodCooldown(Player player) {
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
            setRodCooldown(player);
        }
        ItemStack wait = new IngamePhase.ItemBuilder(Material.FIREWORK_STAR).setDisplayName(I18NUtils.get(player, "rod.cooldown")).build();
        player.getInventory().setItem(resultInvSort.getInt("rod_slot"), wait);
        player.updateInventory();
        Bukkit.getScheduler().runTaskLater(KnockFFA.getInstance(), () -> {
            try {
                ItemStack rod = makeItem(Material.FISHING_ROD, I18NUtils.get(player, "rod"), I18NUtils.get(player, "rod.lore")).build();
                int rodSlot = resultInvSort.getInt("rod_slot");
                player.getInventory().setItem(rodSlot, rod);
                player.updateInventory();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 3 * 20L);
    }

    static class WebDestroyJob extends BukkitRunnable {

        Block web;

        public WebDestroyJob(Block block) {
            this.web = block;
        }

        @Override
        public void run() {
            web.setType(Material.AIR);
        }

    }

    public static class BlockReturnJob extends BukkitRunnable {

        private final BlockPlaceEvent event;

        public BlockReturnJob(BlockPlaceEvent event) {
            this.event = event;
        }

        @Override
        @SneakyThrows
        public void run() {
            event.getBlock().setType(Material.RED_SANDSTONE);
            Bukkit.getScheduler().runTaskLater(KnockFFA.getInstance(), () -> event.getBlock().setType(Material.AIR), 3 * 20L);
        }

    }

}
