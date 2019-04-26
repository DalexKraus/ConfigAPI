package at.dalex.configapi;

public class ConfigInstruction {

    private String configId;
    private String configurationSection;
    private Object value;

    public ConfigInstruction(String configId, String configurationSection, Object value) {
        this.configId = configId;
        this.configurationSection = configurationSection;
        this.value = value;
    }

    public String getConfigId() {
        return configId;
    }

    public String getConfigurationSection() {
        return configurationSection;
    }

    public Object getValue() {
        return value;
    }
}