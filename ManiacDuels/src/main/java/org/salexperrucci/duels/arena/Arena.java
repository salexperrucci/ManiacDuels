package org.salexperrucci.duels.arena;

import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.arena.state.ActiveGameState;
import org.salexperrucci.duels.arena.state.ArenaState;
import org.salexperrucci.duels.arena.state.StartingArenaState;
import org.salexperrucci.duels.arena.state.WaitingArenaState;
import org.salexperrucci.duels.utiliy.Colorize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Arena {

    private String displayName;
    private String configName;
    private Location spawnLocationOne;
    private Location spawnLocationTwo;

    private ArenaState arenaState;

    @Getter
    private List<UUID> players;

    private final int MAX_PLAYERS = 2;

    public void setState(ArenaState arenaState, DuelsPlugin plugin) {
        if (this.arenaState.getClass() == arenaState.getClass()) return;

        this.arenaState.onDisable(plugin);

        this.arenaState = arenaState;
        this.arenaState.setArena(this);
        this.arenaState.onEnable(plugin);
    }

    public void addPlayer(Player player, DuelsPlugin plugin) {
        this.players.add(player.getUniqueId());
        sendMessage("&a" + player.getDisplayName() + " joined.");

        plugin.getArenaManager().getRollbackManager().save(player);
        player.setGameMode(GameMode.SURVIVAL);

        if (players.size() == 1) {
            player.teleport(spawnLocationOne);
        } else {
            player.teleport(spawnLocationTwo);
        }

        if (players.size() == MAX_PLAYERS) {
            setState(new StartingArenaState(), plugin);
        }
    }

    public void removePlayer(Player player, DuelsPlugin plugin) {
        this.players.remove(player.getUniqueId());
        sendMessage("&c" + player.getDisplayName() + " quit.");

        plugin.getArenaManager().getRollbackManager().restore(player, null);

        if (arenaState instanceof ActiveGameState) {
            ((ActiveGameState) arenaState).getAlivePlayers().remove(player.getUniqueId());
        } else if (arenaState instanceof StartingArenaState) {
            setState(new WaitingArenaState(), plugin);
        }
    }

    public boolean isPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void sendMessage(String message) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) {
                continue;
            }

            Colorize.sendMessage(player, message);
        }
    }

}
