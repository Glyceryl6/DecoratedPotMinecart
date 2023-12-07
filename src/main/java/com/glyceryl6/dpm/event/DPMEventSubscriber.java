package com.glyceryl6.dpm.event;

import com.glyceryl6.dpm.DPM;
import com.glyceryl6.dpm.client.DPMModelLayers;
import com.glyceryl6.dpm.client.DecoratedPotMinecartRenderer;
import com.glyceryl6.dpm.data.DPMItemModelProvider;
import com.glyceryl6.dpm.data.DPMRecipeProvider;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DPM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DPMEventSubscriber {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new DPMItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new DPMRecipeProvider(output));
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
