package tv.quaint.utilitybase.config.storage;

import de.leonhard.storage.Config;
import tv.quaint.utilitybase.PluginBase;
import tv.quaint.utilitybase.utils.MainUtils;

import java.io.File;

public class ConfiguredStorage {
    public Config config;
    public String cstring = "storage.yml";
    public File cfile = new File(PluginBase.getDataFolder(), cstring);

    public ConfiguredStorage() {
        reloadConfig();
    }

    public Config loadConfig() {
        return MainUtils.loadConfigNoDefault(cfile);
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public void saveValue(StorageComponent component) {
        config.set(component.key, component.value);
    }

    public double getValue(String key) {
        reloadConfig();

        if (! config.getSection("balances").singleLayerKeySet().contains(key)) {
            StorageManager.createFirstComponent(key);
        }

        return config.getDouble("balances." + key);
    }
}
