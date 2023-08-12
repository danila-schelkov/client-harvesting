package com.vorono4ka.reap.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public final class CropEvents {
    public static ActionResult useBlock(PlayerEntity ignoredPlayer, World world, Hand ignoredHand, BlockHitResult blockHitResult) {
        if (harvest(world, blockHitResult)) {
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static boolean harvest(World world, BlockHitResult blockHitResult) {
        BlockPos blockPos = blockHitResult.getBlockPos();

        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        // check if block is in config harvestable

        Fertilizable fertilizable = getFertilizable(block);
        if (fertilizable == null) return false;

        if (fertilizable.isFertilizable(world, blockPos, blockState, world.isClient)) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return false;

        boolean blockBroken = interactionManager.updateBlockBreakingProgress(blockPos, blockHitResult.getSide());
        if (!blockBroken) return false;

//        boolean blockSet = world.setBlockState(blockPos, block.getStateManager().getDefaultState());
//        System.out.println(blockSet);

        return true;
    }

    private static Fertilizable getFertilizable(Block block) {
        if (block instanceof Fertilizable) {
            return (Fertilizable) block;
        }

        if (block instanceof NetherWartBlock) {
            return new Fertilizable() {

                @Override
                public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
                    return state.hasRandomTicks();
                }

                @Override
                public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
                    return false;
                }

                @Override
                public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {

                }
            };
        }
        return null;
    }
}
