package com.glyceryl6.dpm.crafting;

import com.glyceryl6.dpm.DPM;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DecoratedPotMinecartRecipe extends CustomRecipe {

    public DecoratedPotMinecartRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        List<ItemStack> inputs = new ArrayList<>();
        for (int j = 0; j < container.getContainerSize(); j++) {
            ItemStack slotStack = container.getItem(j);
            if (!slotStack.isEmpty()) {
                inputs.add(slotStack);
            }
        }

        boolean flag1 = false, flag2 = false;
        for (ItemStack input : inputs) {
            if (input.getItem() == Items.DECORATED_POT) {
                flag1 = true; break;
            }
        }

        for (ItemStack input : inputs) {
            if (input.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DecoratedPotBlock) {
                flag2 = true; break;
            }
        }

        return inputs.size() == 2 && flag1 && flag2;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack decoratedPotMinecartStack = DPM.DECORATED_POT_MINECART.get().getDefaultInstance();
        ItemStack decoratedPotStack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).getItem() == Items.DECORATED_POT) {
                decoratedPotStack = container.getItem(i); break;
            }
        }

        CompoundTag compoundTag = decoratedPotStack.getOrCreateTagElement("BlockEntityTag");
        if (!compoundTag.isEmpty() && decoratedPotMinecartStack.getOrCreateTagElement("Decorations").isEmpty()) {
            decoratedPotMinecartStack.addTagElement("Decorations", compoundTag);
        }

        return decoratedPotMinecartStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DPM.DECORATED_POT_MINECART_SERIALIZER.get();
    }

}
