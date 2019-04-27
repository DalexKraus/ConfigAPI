package at.dalex.configapi;

import at.dalex.configapi.testenv.VariableTest;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    private ConfigAPI config;

    @Override
    public void onEnable() {
        saveResource("TestConfig.yml", false);
        this.config = ConfigAPI.newInstance(new File("plugins/DConfigAPI/TestConfig.yml"), "testConfig");

        VariableTest test = new VariableTest();
        config.registerInstructionContainer(test);

        config.saveConfig();
    }

    @Override
    public void onDisable() {

    }
}
