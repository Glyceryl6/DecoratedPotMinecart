package com.glyceryl6.dpm;

import com.glyceryl6.dpm.crafting.DecoratedPotMinecartRecipe;
import com.glyceryl6.dpm.entity.MinecartDecoratedPot;
import com.glyceryl6.dpm.item.DecoratedPotMinecart;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.Locale;

@Mod(DPM.MOD_ID)
public class DPM {

    public static final String MOD_ID = "dpm";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);

    public static final DeferredItem<Item> DECORATED_POT_MINECART = ITEMS.register("decorated_pot_minecart", DecoratedPotMinecart::new);

    public static final DeferredHolder<EntityType<?>, EntityType<MinecartDecoratedPot>> MINECART_DECORATED_POT =
            ENTITY_TYPES.register("minecart_decorated_pot", () -> EntityType.Builder.<MinecartDecoratedPot>of(MinecartDecoratedPot::new, MobCategory.MISC)
                    .sized(0.98F, 0.7F).clientTrackingRange(8).build("minecart_decorated_pot"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DecoratedPotMinecartRecipe>> DECORATED_POT_MINECART_SERIALIZER =
            RECIPE_SERIALIZERS.register("crafting_decorated_pot_minecart", () -> new SimpleCraftingRecipeSerializer<>(DecoratedPotMinecartRecipe::new));

    public static final ResourceLocation DESERT_ABANDONED_MINESHAFT_POT = prefix("pots/desert_abandoned_mineshaft_pot");

    public DPM(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(DECORATED_POT_MINECART);
        }
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MOD_ID, name.toLowerCase(Locale.ROOT));
    }

}