package com.glyceryl6.dpm.entity;

import com.glyceryl6.dpm.DPM;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;

@SuppressWarnings({"resource", "ConstantConditions"})
public class MinecartDecoratedPot extends AbstractMinecartContainer implements Hopper {

    private DecoratedPotBlockEntity.Decorations decorations = DecoratedPotBlockEntity.Decorations.EMPTY;
    private static final EntityDataAccessor<ItemStack> FRONT_STACK = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> BACK_STACK = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> LEFT_STACK = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> RIGHT_STACK = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);

    public MinecartDecoratedPot(EntityType<? extends MinecartDecoratedPot> type, Level level) {
        super(type, level);
    }

    public MinecartDecoratedPot(Level level, double x, double y, double z) {
        super(DPM.MINECART_DECORATED_POT.get(), x, y, z, level);
    }

    public ItemStack[] getDecorations() {
        return new ItemStack[] {
                this.entityData.get(FRONT_STACK),
                this.entityData.get(BACK_STACK),
                this.entityData.get(LEFT_STACK),
                this.entityData.get(RIGHT_STACK)
        };
    }

    public void setDecorationsInDefault(Item front, Item back, Item left, Item right) {
        this.entityData.set(FRONT_STACK, front.getDefaultInstance());
        this.entityData.set(BACK_STACK, back.getDefaultInstance());
        this.entityData.set(LEFT_STACK, left.getDefaultInstance());
        this.entityData.set(RIGHT_STACK, right.getDefaultInstance());
    }

    public void setDecorationsFromItemTags(DecoratedPotBlockEntity.Decorations decorations) {
        this.entityData.set(FRONT_STACK, decorations.front().getDefaultInstance());
        this.entityData.set(BACK_STACK, decorations.back().getDefaultInstance());
        this.entityData.set(LEFT_STACK, decorations.left().getDefaultInstance());
        this.entityData.set(RIGHT_STACK, decorations.right().getDefaultInstance());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FRONT_STACK, Items.BRICK.getDefaultInstance());
        this.entityData.define(BACK_STACK, Items.BRICK.getDefaultInstance());
        this.entityData.define(LEFT_STACK, Items.BRICK.getDefaultInstance());
        this.entityData.define(RIGHT_STACK, Items.BRICK.getDefaultInstance());
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Override
    public double getLevelX() {
        return this.getX();
    }

    @Override
    public double getLevelY() {
        return this.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return this.getZ();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.markHurt();
            this.setDamage(this.getDamage() + amount * 10.0F);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            this.destroy(source);
            return true;
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        ItemStack innerStack = this.getItem(0);
        boolean flag = innerStack.getCount() < innerStack.getMaxStackSize();
        if (!handStack.isEmpty() && (innerStack.isEmpty() || ItemStack.isSameItemSameTags(handStack, innerStack) && flag)) {
            player.awardStat(Stats.ITEM_USED.get(handStack.getItem()));
            ItemStack increaseStack = player.isCreative() ? handStack.copyWithCount(1) : handStack.split(1);

            float f;
            if (this.getItem(0).isEmpty()) {
                this.setItem(0, increaseStack);
                f = (float) increaseStack.getCount() / (float) increaseStack.getMaxStackSize();
            } else {
                innerStack.grow(1);
                f = (float) innerStack.getCount() / (float) innerStack.getMaxStackSize();
            }

            this.level().playSound(null, this.blockPosition(), SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * f);
            if (this.level() instanceof ServerLevel serverLevel) {
                double x = this.position().x;
                double y = this.position().y + 1.2F;
                double z = this.position().z;
                serverLevel.sendParticles(ParticleTypes.DUST_PLUME, x, y, z, 7, 0.0F, 0.0F, 0.0F, 0.0F);
            }
        } else {
            this.level().playSound(null, this.blockPosition(), SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.isAlive() && HopperBlockEntity.suckInItems(this.level(), this)) {
            this.setChanged();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.decorations.save(compoundTag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.decorations = DecoratedPotBlockEntity.Decorations.load(compoundTag);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return null;
    }

    @Override
    public void destroy(DamageSource source) {
        GameRules gameRules = this.level().getGameRules();
        boolean isUseTool = source.getEntity() instanceof Player player &&
                player.getItemInHand(player.getUsedItemHand()).is(ItemTags.TOOLS);
        if (gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if ((source.is(DamageTypeTags.IS_PROJECTILE) && gameRules.getBoolean(GameRules.RULE_PROJECTILESCANBREAKBLOCKS)) || isUseTool) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.DECORATED_POT_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
                this.decorations.sorted().map(Item::getDefaultInstance).forEach(this::spawnAtLocation);
                this.chestVehicleDestroyed(source, this.level(), this);
                this.spawnAtLocation(Items.MINECART);
                this.kill();
            } else {
                super.destroy(source);
            }
        }
    }

    @Override
    public ItemStack getPickResult() {
        CompoundTag compoundTag = this.decorations.save(new CompoundTag());
        ItemStack pickResult = new ItemStack(DPM.DECORATED_POT_MINECART.get());
        pickResult.addTagElement("Decorations", compoundTag);
        return pickResult;
    }

    @Override
    public Item getDropItem() {
        CompoundTag compoundTag = this.decorations.save(new CompoundTag());
        ItemStack pickResult = new ItemStack(DPM.DECORATED_POT_MINECART.get());
        pickResult.addTagElement("Decorations", compoundTag);
        return pickResult.getItem();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

}