package com.glyceryl6.dpm.event;

import com.glyceryl6.dpm.DPM;
import com.glyceryl6.dpm.client.DPMModelLayers;
import com.glyceryl6.dpm.client.DecoratedPotMinecartRenderer;
import com.glyceryl6.dpm.data.provider.DPMItemModelProvider;
import com.glyceryl6.dpm.data.provider.DPMLootTableProvider;
import com.glyceryl6.dpm.data.provider.DPMRecipeProvider;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = DPM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DPMEventSubscriber {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new DPMItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new DPMRecipeProvider(output, provider));
        generator.addProvider(event.includeServer(), DPMLootTableProvider.create(output));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DPM.MINECART_DECORATED_POT.get(), DecoratedPotMinecartRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DPMModelLayers.DECORATED_POT_MINECART, MinecartModel::createBodyLayer);
        event.registerLayerDefinition(DPMModelLayers.DECORATED_POT_MINECART_BASE, DecoratedPotRenderer::createBaseLayer);
        event.registerLayerDefinition(DPMModelLayers.DECORATED_POT_MINECART_SIDES, DecoratedPotRenderer::createSidesLayer);
    }

}
