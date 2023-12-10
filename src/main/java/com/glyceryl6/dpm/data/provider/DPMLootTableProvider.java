package com.glyceryl6.dpm.data.provider;

import com.glyceryl6.dpm.data.loot.DPMChestLoot;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class DPMLootTableProvider {

    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(DPMChestLoot::new, LootContextParamSets.CHEST)));
    }

}