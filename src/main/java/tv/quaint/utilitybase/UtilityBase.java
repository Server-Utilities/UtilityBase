package tv.quaint.utilitybase;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tv.quaint.utilitybase.utils.MessagingUtils;

public class UtilityBase extends PluginBase {
    public UtilityBase(ModInitializer plugin, boolean addStorageModule) {
        super(plugin.getClass().getSimpleName(), addStorageModule);
    }
}
