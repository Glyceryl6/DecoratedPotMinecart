package com.glyceryl6.dpm.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DecoratedPotPatterns.class)
public interface AccessDecoratedPotPatterns {

    @Accessor("ITEM_TO_POT_TEXTURE")
    static Map<Item, ResourceKey<String>> getItemToPotTexture() {
        throw new AssertionError();
    }

}