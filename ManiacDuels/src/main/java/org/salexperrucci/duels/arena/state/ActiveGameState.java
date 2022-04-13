package org.salexperrucci.duels.arena.state;

import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.utiliy.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActiveGameState extends ArenaState {

    @Getter
    private List<UUID> alivePlayers;
    private DuelsPlugin plugin;
    private boolean isOver = false;

    @Override
    public void onEnable(DuelsPlugin plugin) {
        super.onEnable(plugin);

        this.plugin = plugin;

        alivePlayers = new ArrayList<>(getArena().getPlayers());

        int lastSpawnId = 0;

        for (UUID playerUUID : alivePlayers) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null || !player.isOnline()) continue;

            // this is where we could randomly assign teams lol
            if (lastSpawnId == 0) {
                player.teleport(getArena().getSpawnLocationOne());
                lastSpawnId = 1;
            } else {
                player.teleport(getArena().getSpawnLocationTwo());
                lastSpawnId = 0;
            }

            player.getInventory().setHelmet(
                    new ItemStack(Material.IRON_HELMET)
            );
            player.getInventory().setChestplate(
                    new ItemStack(Material.IRON_CHESTPLATE)
            );
            player.getInventory().setLeggings(
                    new ItemStack(Material.IRON_LEGGINGS)
            );
            player.getInventory().setBoots(
                    new ItemStack(Material.IRON_BOOTS)
            );
            player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
            player.getInventory().setItem(1, new ItemStack(Material.FISHING_ROD));
            player.getInventory().setItem(2, new ItemStack(Material.BOW));
            player.getInventory().setItem(3, new ItemBuilder(Material.FLINT_AND_STEEL).setDurability((short) 61).toItemStack());
            player.getInventory().setItem(8, new ItemStack(Material.ARROW, 5));
        }

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (alivePlayers.size() > 1 || isOver) return;

            this.isOver = true;

            if (alivePlayers.size() == 1) {
                UUID winnerUUID = alivePlayers.get(0);
                Player winner = Bukkit.getPlayer(winnerUUID);
                if (winner == null || !winner.isOnline()) {
                    getArena().sendMessage("&aGame over, but winner could not be found.");
                } else {
                    getArena().sendMessage("&a" + winner.getDisplayName() + " has won!");
                }
            } else {
                getArena().sendMessage("&cNo alive players? Game over anyway.");
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                getArena().setState(new WaitingArenaState(), plugin);
            }, 20 * 5);
        }, 0, 4);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!getArena().isPlayer(player)) return;

        if (isOver) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        if (event.getFinalDamage() >= player.getHealth()) {
            alivePlayers.remove(player.getUniqueId());

            player.setHealth(player.getMaxHealth());
            player.setGameMode(GameMode.SPECTATOR);
            getArena().sendMessage("&a" + player.getDisplayName() + " died!");
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (!getArena().isPlayer(event.getPlayer())) return;

        alivePlayers.remove(event.getPlayer().getUniqueId());
    }

}
