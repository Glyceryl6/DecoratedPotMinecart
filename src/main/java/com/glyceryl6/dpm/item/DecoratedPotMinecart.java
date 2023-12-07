package com.glyceryl6.dpm.item;

import com.glyceryl6.dpm.entity.MinecartDecoratedPot;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DecoratedPotMinecart extends Item {

    public DecoratedPotMinecart() {
        super(new Item.Properties().stacksTo(1));
        DispenserBlock.registerBehavior(this, new DispenseItemBehaviors());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        if (!clickedState.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        } else {
            ItemStack stack = context.getItemInHand();
            if (level instanceof ServerLevel serverLevel) {
                RailShape railShape = clickedState.getBlock() instanceof BaseRailBlock baseRailBlock
                        ? baseRailBlock.getRailDirection(clickedState, level, clickedPos, null) : RailShape.NORTH_SOUTH;
                double d0 = railShape.isAscending() ? 0.5F : 0.0F;
                double x = (double)clickedPos.getX() + 0.5;
                double y = (double)clickedPos.getY() + 0.0625 + d0;
                double z = (double)clickedPos.getZ() + 0.5;
                MinecartDecoratedPot minecart = new MinecartDecoratedPot(serverLevel, x, y, z);
                CompoundTag decorationsTag = stack.getTagElement("Decorations");
                if (decorationsTag != null && !decorationsTag.isEmpty()) {
                    minecart.setDecorations(DecoratedPotBlockEntity.Decorations.load(decorationsTag));
                }

                EntityType.<MinecartDecoratedPot>createDefaultStackConfig(serverLevel, stack, context.getPlayer()).accept(minecart);
                serverLevel.addFreshEntity(minecart);
                serverLevel.gameEvent(GameEvent.ENTITY_PLACE, clickedPos, GameEvent.Context.of(context.getPlayer(), serverLevel.getBlockState(clickedPos.below())));
            }

            stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        DecoratedPotBlockEntity.Decorations decorations = DecoratedPotBlockEntity.Decorations.load(stack.getTagElement("Decorations"));
        if (!decorations.equals(DecoratedPotBlockEntity.Decorations.EMPTY)) {
            tooltipComponents.add(CommonComponents.EMPTY);
            Stream.of(decorations.front(), decorations.left(), decorations.right(), decorations.back())
                    .forEach(item -> tooltipComponents.add(new ItemStack(item, 1)
                            .getHoverName().plainCopy().withStyle(ChatFormatting.GRAY)));
        }
    }

    @SuppressWarnings("resource")
    private static class DispenseItemBehaviors extends DefaultDispenseItemBehavior {

        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(BlockSource source, ItemStack stack) {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            ServerLevel serverLevel = source.level();
            Vec3 vec3 = source.center();
            double d0 = vec3.x() + (double)direction.getStepX() * 1.125;
            double d1 = Math.floor(vec3.y()) + (double)direction.getStepY();
            double d2 = vec3.z() + (double)direction.getStepZ() * 1.125;
            BlockPos relativePos = source.pos().relative(direction);
            BlockState relativeState = serverLevel.getBlockState(relativePos);
            RailShape railShape = relativeState.getBlock() instanceof BaseRailBlock baseRailBlock
                    ? baseRailBlock.getRailDirection(relativeState, serverLevel, relativePos, null) : RailShape.NORTH_SOUTH;
            double d3;
            if (relativeState.is(BlockTags.RAILS)) {
                d3 = railShape.isAscending() ? 0.6F : 0.1F;
            } else {
                if (!relativeState.isAir() || !serverLevel.getBlockState(relativePos.below()).is(BlockTags.RAILS)) {
                    return this.defaultDispenseItemBehavior.dispense(source, stack);
                }

                BlockState relativeBelowState = serverLevel.getBlockState(relativePos.below());
                RailShape railShapeBelow = relativeBelowState.getBlock() instanceof BaseRailBlock baseRailBlock
                        ? baseRailBlock.getRailDirection(relativeBelowState, serverLevel, relativePos.below(), null) : RailShape.NORTH_SOUTH;
                d3 = direction != Direction.DOWN && railShapeBelow.isAscending() ? -0.4F : -0.9F;
            }

            MinecartDecoratedPot minecart = new MinecartDecoratedPot(serverLevel, d0, d1 + d3, d2);
            CompoundTag decorationsTag = stack.getTagElement("Decorations");
            if (decorationsTag != null && !decorationsTag.isEmpty()) {
                minecart.setDecorations(DecoratedPotBlockEntity.Decorations.load(decorationsTag));
            }

            EntityType.<MinecartDecoratedPot>createDefaultStackConfig(serverLevel, stack, null).accept(minecart);
            serverLevel.addFreshEntity(minecart);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.level().levelEvent(1000, source.pos(), 0);
        }

    }

}