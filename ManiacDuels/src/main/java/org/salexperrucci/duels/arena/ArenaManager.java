package org.salexperrucci.duels.arena;

import org.salexperrucci.duels.PlayerRollbackManager;
import org.salexperrucci.duels.arena.state.WaitingArenaState;
import org.salexperrucci.duels.config.ConfigurationFile;
import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.config.ConfigurationUtility;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArenaManager {

    @Getter
    private final DuelsPlugin plugin;

    private final List<Arena> arenaList = new ArrayList<>();
    private final ConfigurationFile arenaConfigurationFile;

    @Getter
    private final ArenaSetupManager arenaSetupManager;

    @Getter
    private final PlayerRollbackManager rollbackManager;

    public ArenaManager(DuelsPlugin plugin) {
        this.plugin = plugin;
        this.arenaConfigurationFile = new ConfigurationFile(plugin, "arenas");

        for (String arenaConfigName : this.arenaConfigurationFile.getConfiguration().getKeys(false)) {
            ConfigurationSection section = this.arenaConfigurationFile.getConfiguration().getConfigurationSection(arenaConfigName);

            String displayName = section.getString("displayName");
            Location spawnLocationOne = ConfigurationUtility.readLocation(section.getConfigurationSection("spawnLocationOne"));
            Location spawnLocationTwo = ConfigurationUtility.readLocation(section.getConfigurationSection("spawnLocationTwo"));

            Arena arena = new Arena(arenaConfigName, displayName, spawnLocationOne, spawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
            this.arenaList.add(arena);
        }

        this.arenaSetupManager = new ArenaSetupManager(this);
        this.rollbackManager = new PlayerRollbackManager();

        plugin.getServer().getPluginManager().registerEvents(arenaSetupManager, plugin);
    }

    public void saveArenaToConfig(Arena arena) {
        this.arenaList.removeIf(existing -> existing.getConfigName().equalsIgnoreCase(arena.getConfigName()));
        this.arenaList.add(arena);

        YamlConfiguration configuration = this.arenaConfigurationFile.getConfiguration();

        configuration.set(arena.getConfigName(), null);

        configuration.set(arena.getConfigName() + ".displayName", arena.getDisplayName());
        ConfigurationUtility.saveLocation(arena.getSpawnLocationOne(), configuration.createSection(arena.getConfigName() + ".spawnLocationOne"));
        ConfigurationUtility.saveLocation(arena.getSpawnLocationTwo(), configuration.createSection(arena.getConfigName() + ".spawnLocationTwo"));

        this.arenaConfigurationFile.saveConfig();
    }

    public List<Arena> getArenas() {
        return arenaList;
    }

    public Optional<Arena> findArena(String displayName) {
        return getArenas().stream().filter(arena -> arena.getDisplayName().equalsIgnoreCase(displayName)).findAny();
    }

    public Optional<Arena> findOpenArena() {
        return getArenas().stream().filter(arena -> arena.getArenaState() instanceof WaitingArenaState).findAny();
    }

    public Optional<Arena> findPlayerArena(Player player) {
        return getArenas().stream().filter(arena -> arena.getPlayers().contains(player.getUniqueId())).findAny();
    }

    public void deleteArena(Arena arena) {
        this.arenaConfigurationFile.getConfiguration().set(arena.getConfigName(), null);
        this.arenaConfigurationFile.saveConfig();

        this.arenaList.remove(arena);
    }

}
