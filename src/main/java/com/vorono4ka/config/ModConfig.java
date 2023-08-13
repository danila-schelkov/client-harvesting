package com.vorono4ka.config;

import com.vorono4ka.reap.client.ReapClient;
import com.vorono4ka.utilities.ArrayUtils;

import java.util.List;

public class ModConfig {
    private static final ModConfigProvider provider = new ModConfigProvider();
    private static SimpleConfig config;

    public static String[] harvestingWhitelist;

    public static void registerConfig() {
        initDefaults();
        loadConfig();
    }

    public static void loadConfig() {
        config = SimpleConfig.of(ReapClient.MOD_ID).provider(provider).request();

        assignValues();
    }

    private static void initDefaults() {
        provider.add("harvesting_whitelist", List.of(
            "minecraft:beetroots",
            "minecraft:carrots",
            "minecraft:cocoa",
            "minecraft:nether_wart",
            "minecraft:potatoes",
            "minecraft:wheat"
        ));
    }

    private static void assignValues() {
        String value = config.getOrDefault("harvesting_whitelist", "");
        harvestingWhitelist = ArrayUtils.splitToArray(value);
    }
}
