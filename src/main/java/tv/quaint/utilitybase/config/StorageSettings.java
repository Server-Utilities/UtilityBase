package tv.quaint.utilitybase.config;

import tv.quaint.utilitybase.utils.MessagingUtils;

import java.util.Locale;

public class StorageSettings {
    public enum Type {
        MONGO,
        STORAGE,
        MARIA,
        NULL,
        ;
    }

    public Type type;
    public double startingAmount = 0;

    public StorageSettings() {
        MessagingUtils.info("StorageConfiguration loaded!");
    }

    public StorageSettings setType(String unparsed) {
        unparsed = unparsed.toLowerCase(Locale.ROOT);
        switch (unparsed){
            case "mongo", "mongodb" -> {
                this.type = Type.MONGO;

            }
            case "sql", "mysql", "maria", "mariadb" -> {
                this.type = Type.MARIA;
            }
            default -> {
                this.type = Type.STORAGE;
            }
        }

        return this;
    }

    public void setStartingAmount(double amount) {
        this.startingAmount = amount;
    }

    public void logInfo(String string) {
        MessagingUtils.infoWithDirector("StorageSettings", string);
    }
}
