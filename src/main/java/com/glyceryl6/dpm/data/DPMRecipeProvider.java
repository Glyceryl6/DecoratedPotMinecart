package com.glyceryl6.dpm.data;

import com.glyceryl6.dpm.DPM;
import com.glyceryl6.dpm.crafting.DecoratedPotMinecartRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DPMRecipeProvider extends RecipeProvider {

    public DPMRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        SpecialRecipeBuilder.special(DecoratedPotMinecartRecipe::new).save(recipeOutput, DPM.prefix("decorated_pot_minecart"));
    }

}