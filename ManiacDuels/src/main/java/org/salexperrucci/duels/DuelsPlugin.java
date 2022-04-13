package org.salexperrucci.duels;

import org.salexperrucci.duels.arena.ArenaManager;
import org.salexperrucci.duels.commands.ArenaCommand;
import org.salexperrucci.duels.commands.DuelsCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class DuelsPlugin extends JavaPlugin {

    @Getter
    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        super.onEnable();

        this.arenaManager = new ArenaManager(this);

        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("duels").setExecutor(new DuelsCommand(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();


    }

}
