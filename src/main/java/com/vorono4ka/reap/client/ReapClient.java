package com.vorono4ka.reap.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReapClient implements ClientModInitializer {
    public static final String MOD_ID = "reap";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client mod initialized!");

        UseBlockCallback.EVENT.register(CropEvents::useBlock);
    }
}
