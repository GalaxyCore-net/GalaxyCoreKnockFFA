package net.galaxycore.knockffa.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import lombok.SneakyThrows;
import net.galaxycore.galaxycorecore.configuration.PlayerLoader;
import net.galaxycore.knockffa.KnockFFA;
import net.galaxycore.knockffa.utils.I18NUtils;
import net.galaxycore.knockffa.utils.SpawnHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        if (event.getEntity().getType() != EntityType.ENDER_PEARL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
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
    public void oninteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
                event.setCancelled(false);
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (SpawnHelper.isLocationInASpawn(event.getBlockPlaced().getLocation()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }

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
    public void onEnderPearlThrow(PlayerLaunchProjectileEvent event) {
        if (event.getProjectile().getType() == EntityType.ENDER_PEARL) {
            new EnderPearlBringBackJob(event.getPlayer()).runTaskLater(KnockFFA.getInstance(), 5 * 20L);
        }
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
    public void onSendArrow(EntityShootBowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        event.setCancelled(SpawnHelper.isPlayerInASpawn(event.getPlayer()));
    }

    static class EnderPearlBringBackJob extends BukkitRunnable {
        private final Player player;

        EnderPearlBringBackJob(Player source) {
            this.player = source;
        }

        @Override
        @SneakyThrows
        public void run() {
            Inventory inventory = player.getInventory();

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
                run();
            }

            int pearlSlot = resultInvSort.getInt("pearl_slot");
            ItemStack pearl = makeItem(Material.ENDER_PEARL, I18NUtils.get(player, "pearl"), I18NUtils.get(player, "pearl.lore")).build();

            inventory.setItem(pearlSlot, pearl);
            player.updateInventory();
        }
    }

}
