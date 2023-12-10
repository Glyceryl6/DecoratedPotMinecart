package com.glyceryl6.dpm.client;

import com.glyceryl6.dpm.DPM;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DPMModelLayers {

    public static final ModelLayerLocation DECORATED_POT_MINECART = new ModelLayerLocation(DPM.DECORATED_POT_MINECART.getId(), "main");
    public static final ModelLayerLocation DECORATED_POT_MINECART_BASE = register("decorated_pot_base");
    public static final ModelLayerLocation DECORATED_POT_MINECART_SIDES = register("decorated_pot_sides");

    private static ModelLayerLocation register(String path) {
        return new ModelLayerLocation(DPM.prefix(path), "main");
    }

}