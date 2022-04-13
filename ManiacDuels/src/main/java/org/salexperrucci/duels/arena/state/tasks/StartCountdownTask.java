package org.salexperrucci.duels.arena.state.tasks;

import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.arena.Arena;
import org.salexperrucci.duels.arena.state.ActiveGameState;
import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class StartCountdownTask extends BukkitRunnable {

    private final DuelsPlugin plugin;
    private final Arena arena;
    private int secondsUntilStart;

    @Override
    public void run() {
        if (secondsUntilStart <= 0) {
            arena.setState(new ActiveGameState(), plugin);
            cancel();
            return;
        }

        arena.sendMessage("&aStarting in " + secondsUntilStart + "...");

        secondsUntilStart--;
    }
}
