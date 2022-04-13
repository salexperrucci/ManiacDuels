package org.salexperrucci.duels.arena.state;

import org.salexperrucci.duels.DuelsPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
public class WaitingArenaState extends ArenaState {

    @Override
    public void onEnable(DuelsPlugin plugin) {
        super.onEnable(plugin);

        for (UUID playerUUID : getArena().getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.setHealth(player.getMaxHealth());
            plugin.getArenaManager().getRollbackManager().restore(player, plugin);
        }

        getArena().getPlayers().clear();
    }

}
