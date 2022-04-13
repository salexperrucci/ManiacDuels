package org.salexperrucci.duels.arena.state;

import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.arena.Arena;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ArenaState implements Listener {

    private Arena arena;
    private CommonStateListener commonStateListener;

    public void onEnable(DuelsPlugin plugin) {
        commonStateListener = new CommonStateListener(plugin, arena);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(commonStateListener, plugin);
    }

    public void onDisable(DuelsPlugin plugin) {
        HandlerList.unregisterAll(this);
        HandlerList.unregisterAll(commonStateListener);
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }

}
