package com.ytg123.rtlfix;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
public class CommonMain implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "rtlfix";
    public static final String MOD_NAME = "RTL Fix";

    public static final Identifier clientModEnabledQuestionPacketID = new Identifier("rtlfix", "isclientmodenabled");
    public static final Identifier clientModEnabledAnswerPacketID = new Identifier("rtlfix", "clientmodenabled");

    public static final List<PlayerEntity> blackListedPlayers = new ArrayList<>();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        ServerSidePacketRegistry.INSTANCE.register(clientModEnabledAnswerPacketID, (packetContext, attachedData) -> {
            boolean isEnabled = attachedData.readBoolean();
            log(Level.INFO, "Received Answer, answer is "+ isEnabled);
            packetContext.getTaskQueue().execute(() -> {
                if (isEnabled) {
                    blackListedPlayers.add(packetContext.getPlayer());
                    log(Level.INFO, "Added to Blacklist");
                }
            });
        });
        //TODO: Initializer
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
}
