package at.dalex.configapi;

import at.dalex.configapi.annotation.ConfigField;
import at.dalex.configapi.annotation.ConfigFile;
import at.dalex.configapi.annotation.ConfigSerialize;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigAPI {

    private ArrayList<Object> instructionContainers = new ArrayList<>();
    private static Logger logger = Logger.getLogger("ConfigAPI");

    private File configFile;
    private FileConfiguration configuration;
    private String identifier;

    private ConfigAPI(File configFile, String identifier) {
        this.configFile = configFile;
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
        this.identifier = identifier;
    }

    public void writeToConfigSection(String section, Object object) {
        configuration.set(section, object);
    }

    public static ConfigAPI newInstance(File configFile, String identifier) {
        //if (!configFile.canWrite() || !configFile.canRead()) {
        //    throw new RuntimeException("File is not accessible!\n" +
       //             "WritePerm: " + configFile.canWrite() + " ReadPerm: " + configFile.canRead());
        //}
        return new ConfigAPI(configFile, identifier);
    }

    public void registerInstructionContainer(Object container) {
        if (!instructionContainers.contains(container)) {
            instructionContainers.add(container);
        }
    }

    public void unregisterInstructionContainer(Object container) {
        instructionContainers.remove(container);
    }

    public void saveConfig() {
        ArrayList<ConfigInstruction> allInstructions = new ArrayList<>();
        for (Object container : this.instructionContainers) {
            ArrayList<ConfigInstruction> containerInstructions = getAllConfigInstructions(container, this.identifier);
            if (containerInstructions != null)
                allInstructions.addAll(containerInstructions);
        }
        //Apply changes
        for (ConfigInstruction instruction : allInstructions) {
            Object instructionValue = instruction.getValue();
            Object value;
            //Convert any type of lists into a list containing the String values of the entries
            if (instructionValue instanceof List) {
                ArrayList<String> stringList = new ArrayList<>();
                for (Object obj : (List) instructionValue) stringList.add(obj.toString());
                value = stringList;
            }
            else value = instructionValue;
            configuration.set(instruction.getConfigurationSection(), value);
        }
        //Save config
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            System.err.println("[ConfigAPI] Unable to write config to disk!");
            e.printStackTrace();
        }
    }

    private ArrayList<ConfigInstruction> getAllConfigInstructions(Object container, String configId) {
        //We'll handle serializing objects differently
        if (container.getClass().isAnnotationPresent(ConfigSerialize.class))
            return null;

        ArrayList<ConfigInstruction> instructions = new ArrayList<>();
        for (Field field : container.getClass().getDeclaredFields()) {
            ConfigFile configFileAnnotation = field.getAnnotation(ConfigFile.class);
            ConfigField configFieldAnnotation = field.getAnnotation(ConfigField.class);

            //Check if proper ConfigField annotation is set
            if (configFieldAnnotation == null)
                continue;

            //Warn if no ConfigFile annotation is present
            if (configFileAnnotation == null) {
                logger.warning("No ConfigFile annotation is present at field '" + field.getName() + "' " +
                        "in class '" + field.getClass().getName() + "', field will not be saved!");
                continue;
            }

            //Check if config ids match
            if (!configFileAnnotation.id().equals(configId))
                continue;

            //Make accessible if it isn't
            if (!field.isAccessible())
                field.setAccessible(true);

            try {
                ConfigInstruction fieldInstruction = new ConfigInstruction(configFileAnnotation.id(),
                        configFieldAnnotation.location(), field.get(container));
                instructions.add(fieldInstruction);
            } catch (IllegalAccessException e) {
                System.err.println("Unable to access field '" + field.getName() + "' in class '"
                        + container.getClass().getName() + "'!");
                e.printStackTrace();
            }
        }
        return instructions;
    }
}
