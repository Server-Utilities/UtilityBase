package tv.quaint.utilitybase.utils;

import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import tv.quaint.utilitybase.PluginBase;

public class MessagingUtils {
    public static void info(String string) {
        String[] split = string.split("\n");
        for (String s : split) {
            PluginBase.LOGGER.info(s);
        }
    }

    public static void infoWithDirector(String director, String string) {
        String[] split = string.split("\n");
        for (String s : split) {
            PluginBase.LOGGER.info(director + " ><> " + s);
        }
    }

    public static void warn(String string) {
        String[] split = string.split("\n");
        for (String s : split) {
            PluginBase.LOGGER.warn(s);
        }
    }

    public static void warnWithDirector(String director, String string) {
        String[] split = string.split("\n");
        for (String s : split) {
            PluginBase.LOGGER.warn(director + " ><> " + s);
        }
    }

    public static void severe(String string) {
        String[] split = string.split("\n");
        for (String s : split) {
            PluginBase.LOGGER.error(s);
        }
    }

    public static void severeWithDirector(String director, String string) {
        String[] split = string.split("\n");
        for (String s : split) {
            PluginBase.LOGGER.error(director + " ><> " + s);
        }
    }

    public static void sendMessageAs(Entity entity, String message) {
        for (ServerPlayerEntity p : MainUtils.getOnlinePlayers()) {
            p.sendMessage(TextUtils.newText(message), MessageType.CHAT, entity.getUuid());
        }
    }
}
