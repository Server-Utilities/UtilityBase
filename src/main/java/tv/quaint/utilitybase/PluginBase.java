package tv.quaint.utilitybase;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tv.quaint.utilitybase.config.ConfigHandler;
import tv.quaint.utilitybase.utils.MessagingUtils;
import tv.quaint.utilitybase.utils.commands.SimpleCommand;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;

public class PluginBase implements ModInitializer {
    public static Logger LOGGER;
    public static String NAME;
    public static PluginBase INSTANCE;
    public static MinecraftServer SERVER;
    public static ConfigHandler CONFIG;

    public static boolean ADD_STORAGE;

    public PluginBase(String name, boolean addStorageModule) {
        NAME = name;
        LOGGER = LoggerFactory.getLogger(name);
        ADD_STORAGE = addStorageModule;
    }

    @Override
    public void onInitialize() {
        MessagingUtils.info("Initializing...");

        INSTANCE = this;

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        CONFIG = new ConfigHandler(ADD_STORAGE);
        CONFIG = CONFIG.handle();

        MessagingUtils.info("Initialized!");
    }

    public void registerCommand(SimpleCommand command) {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            command.registerAs(dispatcher);
        });
    }

    public static File getWorkingDir() {
        return Paths.get("").toAbsolutePath().toFile();
    }

    public static File getDataFolder() {
        return new File(getWorkingDir(), NAME + File.separator);
    }

    public InputStream getResource(String fileName) {
        return this.getClass().getResourceAsStream(fileName);
    }
}
