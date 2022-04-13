package org.salexperrucci.duels.config;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigurationFile {

    private final File file;
    private final YamlConfiguration configuration;

    /**
     * @param plugin The class of the owner of the file
     * @param fileName The file name (without file extension)
     */
    @SneakyThrows
    public ConfigurationFile(JavaPlugin plugin, String fileName) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        this.file = new File(plugin.getDataFolder(), fileName + ".yml");
        this.configuration = new YamlConfiguration();
        if (!file.exists()) {
            file.createNewFile();
        }
        this.configuration.load(file);
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    @SneakyThrows
    public void saveConfig() {
        this.configuration.save(file);
    }

}
