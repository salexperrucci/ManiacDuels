package org.salexperrucci.duels.arena.state;

import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.arena.state.tasks.StartCountdownTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartingArenaState extends ArenaState {

    @Getter
    private StartCountdownTask startCountdownTask;

    @Override
    public void onEnable(DuelsPlugin plugin) {
        super.onEnable(plugin);

        this.startCountdownTask = new StartCountdownTask(plugin, getArena(), 5);
        this.startCountdownTask.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void onDisable(DuelsPlugin plugin) {
        super.onDisable(plugin);

        startCountdownTask.cancel();
    }

}
