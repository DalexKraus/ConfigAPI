package at.dalex.configapi;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigAPI {

    private File configFile;
    private FileConfiguration configuration;

    private ConfigAPI(File configFile) {
        this.configFile = configFile;
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    public static ConfigAPI newInstance(File configFile) {
        if (!configFile.canWrite() || !configFile.canRead()) {
            throw new RuntimeException("File is not accessible!\n" +
                    "WritePerm: " + configFile.canWrite() + " ReadPerm: " + configFile.canRead());
        }
        if (!configFile.exists()) {
            if (!configFile.mkdirs())
                throw new RuntimeException("Could not create directories!");
        }
        return new ConfigAPI(configFile);
    }
}
