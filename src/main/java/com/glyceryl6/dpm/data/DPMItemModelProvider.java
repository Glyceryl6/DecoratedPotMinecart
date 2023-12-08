package com.glyceryl6.dpm.data;

import com.glyceryl6.dpm.DPM;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DPMItemModelProvider extends ItemModelProvider {

    public DPMItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, DPM.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(DPM.DECORATED_POT_MINECART.get());
    }

}