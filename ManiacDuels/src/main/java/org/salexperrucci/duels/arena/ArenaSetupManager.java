package org.salexperrucci.duels.arena;

import org.salexperrucci.duels.utiliy.Colorize;
import org.salexperrucci.duels.utiliy.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

@RequiredArgsConstructor
public class ArenaSetupManager implements Listener {

    private final ArenaManager arenaManager;
    private final Map<UUID, TemporaryArena> playerToTempArenaMap = new HashMap<>();

    private final String SET_LOCATION_ONE_ITEM_NAME = Colorize.format("&aSet Location One &7(Right click)");
    private final String SET_LOCATION_TWO_ITEM_NAME = Colorize.format("&aSet Location Two &7(Right click)");
    private final String SAVE_ITEM_NAME = Colorize.format("&aSave &7(Right click)");
    private final String CANCEL_ITEM_NAME = Colorize.format("&cCancel &7(Right click)");

    public void addToSetup(Player player, TemporaryArena temporaryArena) {
        if (playerToTempArenaMap.containsKey(player.getUniqueId())) return;

        arenaManager.getRollbackManager().save(player);
        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);

        playerToTempArenaMap.put(player.getUniqueId(), temporaryArena);
        Colorize.sendMessage(player, "&aMoved to setup mode for " + temporaryArena.getDisplayName());

        player.getInventory().addItem(
                new ItemBuilder(Material.STICK)
                    .setName(SET_LOCATION_ONE_ITEM_NAME)
                    .toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.BLAZE_ROD)
                        .setName(SET_LOCATION_TWO_ITEM_NAME)
                        .toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.DIAMOND)
                        .setName(SAVE_ITEM_NAME)
                        .toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.BARRIER)
                        .setName(CANCEL_ITEM_NAME)
                        .toItemStack()
        );
    }

    public void removeFromSetup(Player player) {
        if (!playerToTempArenaMap.containsKey(player.getUniqueId())) return;

        player.getInventory().clear();

        playerToTempArenaMap.remove(player.getUniqueId());
        arenaManager.getRollbackManager().restore(player, null);
    }

    public boolean inSetupMode(Player player) {
        return playerToTempArenaMap.containsKey(player.getUniqueId());
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!inSetupMode(event.getPlayer())) return;
        if (!event.hasItem()) return;
        if (!event.getItem().hasItemMeta()) return;

        Player player = event.getPlayer();

        TemporaryArena temporaryArena = playerToTempArenaMap.get(player.getUniqueId());
        String itemName = event.getItem().getItemMeta().getDisplayName();

        if (itemName.equalsIgnoreCase(SAVE_ITEM_NAME)) {
            arenaManager.saveArenaToConfig(temporaryArena.toArena());
            Colorize.sendMessage(player, "&aSaved arena!");
            removeFromSetup(player);
        } else if (itemName.equalsIgnoreCase(SET_LOCATION_ONE_ITEM_NAME)) {
            temporaryArena.setSpawnLocationOne(player.getLocation());
            Colorize.sendMessage(player, "&aSaved location one!");
        } else if (itemName.equalsIgnoreCase(SET_LOCATION_TWO_ITEM_NAME)) {
            temporaryArena.setSpawnLocationTwo(player.getLocation());
            Colorize.sendMessage(player, "&aSaved location two!");
        } else if (itemName.equalsIgnoreCase(CANCEL_ITEM_NAME)) {
            Colorize.sendMessage(player, "&cCancelled setup.");
            removeFromSetup(player);
        } else {
            return;
        }

        event.setCancelled(true);
    }

}
