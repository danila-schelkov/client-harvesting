package com.vorono4ka.reap.client;

import com.vorono4ka.config.ModConfig;
import com.vorono4ka.utilities.ArrayUtils;
import com.vorono4ka.utilities.PropertyManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Optional;

public final class CropEvents {
    public static final String AGE_PROPERTY_NAME = "age";

    public static ActionResult useBlock(PlayerEntity player, World world, Hand ignoredHand, BlockHitResult blockHitResult) {
        if (player instanceof ClientPlayerEntity && harvest(world, player, blockHitResult)) {
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static boolean harvest(World world, PlayerEntity player, BlockHitResult blockHitResult) {
        BlockPos blockPos = blockHitResult.getBlockPos();

        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        RegistryEntry<Block> registryEntry = blockState.getRegistryEntry();
        Optional<RegistryKey<Block>> key = registryEntry.getKey();
        if (key.isEmpty()) return false;

        String blockId = key.get().getValue().toString();
        if (!ArrayUtils.contains(ModConfig.harvestingWhitelist, blockId)) return false;

        Optional<IntProperty> ageOptional = PropertyManager.getPropertyByName(blockState, AGE_PROPERTY_NAME);
        if (ageOptional.isEmpty()) return false;

        IntProperty ageProperty = ageOptional.get();
        Integer maxPropertyValue = PropertyManager.getMaxPropertyValue(ageProperty);
        Integer propertyValue = blockState.get(ageProperty);

        if (!propertyValue.equals(maxPropertyValue)) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return false;

        if (!breakBlock(interactionManager, blockHitResult)) return false;

        pickSeedItem(interactionManager, player, block.asItem());
        placeSeedItem(interactionManager, player, blockHitResult);

        return true;
    }

    private static boolean breakBlock(ClientPlayerInteractionManager interactionManager, BlockHitResult blockHitResult) {
        return interactionManager.updateBlockBreakingProgress(blockHitResult.getBlockPos(), blockHitResult.getSide());
    }

    private static void pickSeedItem(ClientPlayerInteractionManager interactionManager, PlayerEntity player, Item seedItem) {
        ItemStack seedItemStack = new ItemStack(seedItem);
        PlayerInventory inventory = player.getInventory();
        int slotWithStack = inventory.getSlotWithStack(seedItemStack);
        if (slotWithStack == -1) {
            player.sendMessage(Text.translatable("actions.reap.pick_seed_item.failed"), true);
            return;
        }

        pickFromInventory(interactionManager, inventory, slotWithStack);
    }

    private static void pickFromInventory(ClientPlayerInteractionManager interactionManager, PlayerInventory inventory, int slotWithStack) {
        if (PlayerInventory.isValidHotbarIndex(slotWithStack)) {
            inventory.selectedSlot = slotWithStack;
        } else {
            interactionManager.pickFromInventory(slotWithStack);
        }
    }

    private static void placeSeedItem(ClientPlayerInteractionManager interactionManager, PlayerEntity player, BlockHitResult blockHitResult) {
        interactionManager.interactBlock(((ClientPlayerEntity) player), player.getActiveHand(), blockHitResult);
    }
}
