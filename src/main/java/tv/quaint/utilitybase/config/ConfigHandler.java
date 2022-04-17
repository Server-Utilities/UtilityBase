package tv.quaint.utilitybase.config;

import de.leonhard.storage.Config;
import tv.quaint.utilitybase.PluginBase;
import tv.quaint.utilitybase.config.storage.ConfiguredMongo;
import tv.quaint.utilitybase.config.storage.ConfiguredMaria;
import tv.quaint.utilitybase.config.storage.ConfiguredStorage;
import tv.quaint.utilitybase.utils.MainUtils;
import tv.quaint.utilitybase.utils.sql.MariaUtils;
import tv.quaint.utilitybase.utils.sql.MongoUtils;

import java.io.File;

public class ConfigHandler {
    public Config config;
    public String assetsString = "assets" + File.separator;
    public String cstring = "config.yml";
    public File cfile = new File(PluginBase.getDataFolder(), cstring);
    public StorageSettings storageSettings;

    public ConfiguredMongo configuredMongo;
    public ConfiguredMaria configuredMaria;

    public ConfiguredStorage configuredStorage;
    public StorageSettings.Type currentType = StorageSettings.Type.NULL;

    public boolean withStorage;

    public ConfigHandler(boolean withStorage) {
        this.withStorage = withStorage;
        reloadConfig();
    }

    public Config loadConfig() {
        return MainUtils.loadConfigFromSelf(cfile, assetsString + cstring);
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public ConfigHandler handle() {
        reloadConfig();

        if (withStorage) {
            storageSettings = new StorageSettings().setType(config.getString("storage.type"));

            // Load Storage SETTINGS
            if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
                configuredMongo = new ConfiguredMongo();
                configuredMongo = configuredMongo.parseFromConfigSection(config.getSection("storage.database"));
            }
            if (storageSettings.type.equals(StorageSettings.Type.STORAGE)) {
                configuredStorage = new ConfiguredStorage();
            }
            if (storageSettings.type.equals(StorageSettings.Type.MARIA)) {
                configuredMaria = new ConfiguredMaria();
                configuredMaria = configuredMaria.parseFromConfigSection(config.getSection("storage.database"));
            }

            // Load Storage DATABASES
            if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
                MongoUtils.database = MongoUtils.loadDatabase();
            }
            if (storageSettings.type.equals(StorageSettings.Type.MARIA)) {
                try {
                    MariaUtils.verifyTables();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            currentType = storageSettings.type;
        }

        return this;
    }
}
