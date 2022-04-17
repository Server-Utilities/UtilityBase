package tv.quaint.utilitybase.config.storage;

import tv.quaint.utilitybase.PluginBase;
import tv.quaint.utilitybase.config.StorageSettings;
import tv.quaint.utilitybase.utils.sql.MariaUtils;
import tv.quaint.utilitybase.utils.sql.MongoUtils;

public class StorageManager {
    public static StorageComponent getComponent(String of) {
        StorageSettings storageSettings = PluginBase.CONFIG.storageSettings;
        if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
            return new StorageComponent(of, MongoUtils.getStorageComponentValue(of));
        }
        if (storageSettings.type.equals(StorageSettings.Type.STORAGE)) {
            return new StorageComponent(of, PluginBase.CONFIG.configuredStorage.getValue(of));
        }
        if (storageSettings.type.equals(StorageSettings.Type.MARIA)) {
            return new StorageComponent(of, MariaUtils.getStorageComponentValue(of));
        }

        return new StorageComponent(of, 0d);
    }

    public static void saveComponent(StorageComponent component) {
        StorageSettings storageSettings = PluginBase.CONFIG.storageSettings;
        if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
            MongoUtils.saveStorageComponent(component);
        }
        if (storageSettings.type.equals(StorageSettings.Type.STORAGE)) {
            PluginBase.CONFIG.configuredStorage.saveValue(component);
        }
        if (storageSettings.type.equals(StorageSettings.Type.MARIA)) {
            MariaUtils.saveStorageComponent(component);
        }
    }

    public static void createFirstComponent(String key) {
        StorageComponent component = new StorageComponent(key, PluginBase.CONFIG.storageSettings.startingAmount);

        StorageSettings storageSettings = PluginBase.CONFIG.storageSettings;
        if (storageSettings.type.equals(StorageSettings.Type.MONGO)) {
            MongoUtils.createFirstStorageComponent(component);
        }
        if (storageSettings.type.equals(StorageSettings.Type.STORAGE)) {
            PluginBase.CONFIG.configuredStorage.saveValue(component);
        }
        if (storageSettings.type.equals(StorageSettings.Type.MARIA)) {
            MariaUtils.createFirstStorageComponent(component);
        }
    }
}
