package org.salexperrucci.duels.arena.state;

import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.arena.Arena;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class CommonStateListener implements Listener {

    private final DuelsPlugin plugin;
    private final Arena arena;

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent event) {
        if (!arena.isPlayer(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!arena.isPlayer(event.getPlayer())) return;

        if (event.getBlockPlaced().getType() == Material.FIRE) {
            event.setCancelled(false);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!arena.isPlayer((Player) event.getEntity())) return;
        if (arena.getArenaState() instanceof ActiveGameState) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        if (!arena.isPlayer(event.getPlayer())) return;

        arena.removePlayer(event.getPlayer(), plugin);
    }

}
