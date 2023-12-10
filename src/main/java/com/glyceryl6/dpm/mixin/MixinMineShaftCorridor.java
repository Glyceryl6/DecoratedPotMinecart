package com.glyceryl6.dpm.mixin;

import com.glyceryl6.dpm.DPM;
import com.glyceryl6.dpm.entity.MinecartDecoratedPot;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(MineshaftPieces.MineShaftCorridor.class)
public abstract class MixinMineShaftCorridor extends MineshaftPieces.MineShaftPiece {

    public MixinMineShaftCorridor(StructurePieceType type, CompoundTag compoundTag) {
        super(type, compoundTag);
    }

    @Inject(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/structures/MineshaftPieces$MineShaftCorridor;maybePlaceCobWeb(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;FIII)V", ordinal = 7, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos, CallbackInfo ci, int i, int j, int k, int l, int i1, BlockState state, int j1, int k1) {
        if (random.nextInt(100) < 5) {
            this.createDecoratedPot(level, box, random, 2, 0, k1 - 1, DPM.DESERT_ABANDONED_MINESHAFT_POT);
        }

        if (random.nextInt(100) < 5) {
            this.createDecoratedPot(level, box, random, 0, 0, k1 + 1, DPM.DESERT_ABANDONED_MINESHAFT_POT);
        }
    }

    public void createDecoratedPot(WorldGenLevel level, BoundingBox box, RandomSource random, int x, int y, int z, ResourceLocation lootTable) {
        BlockPos worldPos = this.getWorldPos(x, y, z);
        if (box.isInside(worldPos) && level.getBlockState(worldPos).isAir() && !level.getBlockState(worldPos.below()).isAir()) {
            RailShape railShape = random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST;
            BlockState blockState = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, railShape);
            this.placeBlock(level, blockState, x, y, z, box);
            double xn = (double) worldPos.getX() + 0.5;
            double yn = (double) worldPos.getY() + 0.5;
            double zn = (double) worldPos.getZ() + 0.5;
            MinecartDecoratedPot minecart = new MinecartDecoratedPot(level.getLevel(), xn, yn, zn);
            Map<Item, ResourceKey<String>> itemToPotTexture = AccessDecoratedPotPatterns.getItemToPotTexture();
            List<Item> itemList = new ArrayList<>(itemToPotTexture.keySet());
            Item back = itemList.get(random.nextInt(itemList.size()));
            Item left = itemList.get(random.nextInt(itemList.size()));
            Item right = itemList.get(random.nextInt(itemList.size()));
            Item front = itemList.get(random.nextInt(itemList.size()));
            minecart.setDecorationsFromItemTags(new DecoratedPotBlockEntity.Decorations(back, left, right, front));
            minecart.setLootTable(lootTable, random.nextLong());
            level.addFreshEntity(minecart);
        }
    }

}