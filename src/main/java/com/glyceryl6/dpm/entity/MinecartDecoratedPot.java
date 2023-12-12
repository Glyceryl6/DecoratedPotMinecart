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
    private static final EntityDataAccessor<ItemStack> FRONT_PATTERN = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> BACK_PATTERN = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> LEFT_PATTERN = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> RIGHT_PATTERN = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DROP_STACK = SynchedEntityData.defineId(MinecartDecoratedPot.class, EntityDataSerializers.ITEM_STACK);

    public MinecartDecoratedPot(EntityType<? extends MinecartDecoratedPot> type, Level level) {
        super(type, level);
    }

    public MinecartDecoratedPot(Level level, double x, double y, double z) {
        super(DPM.MINECART_DECORATED_POT.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FRONT_PATTERN, Items.BRICK.getDefaultInstance());
        this.entityData.define(BACK_PATTERN, Items.BRICK.getDefaultInstance());
        this.entityData.define(LEFT_PATTERN, Items.BRICK.getDefaultInstance());
        this.entityData.define(RIGHT_PATTERN, Items.BRICK.getDefaultInstance());
        this.entityData.define(DROP_STACK, DPM.DECORATED_POT_MINECART.get().getDefaultInstance());
    }

    public ItemStack[] getDecorations() {
        return new ItemStack[] {
                this.entityData.get(FRONT_PATTERN), this.entityData.get(BACK_PATTERN),
                this.entityData.get(LEFT_PATTERN), this.entityData.get(RIGHT_PATTERN)
        };
    }

    public void setDecorationsInDefault(Item front, Item back, Item left, Item right) {
        this.decorations = new DecoratedPotBlockEntity.Decorations(back, left, right, front);
        this.entityData.set(FRONT_PATTERN, front.getDefaultInstance());
        this.entityData.set(BACK_PATTERN, back.getDefaultInstance());
        this.entityData.set(LEFT_PATTERN, left.getDefaultInstance());
        this.entityData.set(RIGHT_PATTERN, right.getDefaultInstance());
    }

    public void setDecorationsFromItemTags(DecoratedPotBlockEntity.Decorations decorations) {
        this.entityData.set(FRONT_PATTERN, decorations.front().getDefaultInstance());
        this.entityData.set(BACK_PATTERN, decorations.back().getDefaultInstance());
        this.entityData.set(LEFT_PATTERN, decorations.left().getDefaultInstance());
        this.entityData.set(RIGHT_PATTERN, decorations.right().getDefaultInstance());
    }

    public void setDropStack(ItemStack stack) {
        this.entityData.set(DROP_STACK, stack);
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
            boolean isCreative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
            if (isCreative || !isCreative && this.getDamage() > 40.0F) {
                this.destroy(source);
            }

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
        compoundTag.put("FrontPattern", this.entityData.get(FRONT_PATTERN).save(new CompoundTag()));
        compoundTag.put("BackPattern", this.entityData.get(BACK_PATTERN).save(new CompoundTag()));
        compoundTag.put("LeftPattern", this.entityData.get(LEFT_PATTERN).save(new CompoundTag()));
        compoundTag.put("RightPattern", this.entityData.get(RIGHT_PATTERN).save(new CompoundTag()));
        compoundTag.put("DropStack", this.entityData.get(DROP_STACK).save(new CompoundTag()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.decorations = DecoratedPotBlockEntity.Decorations.load(compoundTag);
        this.entityData.set(FRONT_PATTERN, ItemStack.of(compoundTag.getCompound("FrontPattern")));
        this.entityData.set(BACK_PATTERN, ItemStack.of(compoundTag.getCompound("BackPattern")));
        this.entityData.set(LEFT_PATTERN, ItemStack.of(compoundTag.getCompound("LeftPattern")));
        this.entityData.set(RIGHT_PATTERN, ItemStack.of(compoundTag.getCompound("RightPattern")));
        this.entityData.set(DROP_STACK, ItemStack.of(compoundTag.getCompound("DropItem")));
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return null;
    }

    @Override
    public void destroy(Item item) {
        this.kill();
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack dropStack = this.getPickResult();
            if (this.hasCustomName()) {
                dropStack.setHoverName(this.getCustomName());
            }

            this.spawnAtLocation(dropStack);
        }
    }

    @Override
    public void destroy(DamageSource source) {
        GameRules gameRules = this.level().getGameRules();
        boolean isUseTool = source.getEntity() instanceof Player player &&
                player.getItemInHand(player.getUsedItemHand()).is(ItemTags.TOOLS);
        boolean isCreative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
        if (gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if ((source.is(DamageTypeTags.IS_PROJECTILE) && gameRules.getBoolean(GameRules.RULE_PROJECTILESCANBREAKBLOCKS)) || isUseTool) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.DECORATED_POT_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
                this.decorations.sorted().map(Item::getDefaultInstance).forEach(this::spawnAtLocation);
                this.chestVehicleDestroyed(source, this.level(), this);
                if (!isCreative) {
                    this.spawnAtLocation(Items.MINECART);
                }
            } else {
                if (isCreative) {
                    this.chestVehicleDestroyed(source, this.level(), this);
                } else {
                    super.destroy(source);
                }
            }
        }

        this.kill();
    }

    @Override
    public ItemStack getPickResult() {
        return this.entityData.get(DROP_STACK);
    }

    @Override
    public Item getDropItem() {
        return this.entityData.get(DROP_STACK).getItem();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

}