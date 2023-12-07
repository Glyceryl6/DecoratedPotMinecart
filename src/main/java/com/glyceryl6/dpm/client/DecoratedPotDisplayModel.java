package com.glyceryl6.dpm.client;

import com.glyceryl6.dpm.entity.MinecartDecoratedPot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;

import java.util.Objects;

public class DecoratedPotDisplayModel<T extends MinecartDecoratedPot> extends EntityModel<T> {

    public final ModelPart neck;
    public final ModelPart frontSide;
    public final ModelPart backSide;
    public final ModelPart leftSide;
    public final ModelPart rightSide;
    public final ModelPart top;
    public final ModelPart bottom;
    public final Material baseMaterial = Objects.requireNonNull(Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BASE));

    public DecoratedPotDisplayModel(EntityRendererProvider.Context context) {
        ModelPart base = context.bakeLayer(DPMModelLayers.DECORATED_POT_MINECART_BASE);
        this.neck = base.getChild("neck");
        this.top = base.getChild("top");
        this.bottom = base.getChild("bottom");
        ModelPart sides = context.bakeLayer(DPMModelLayers.DECORATED_POT_MINECART_SIDES);
        this.frontSide = sides.getChild("front");
        this.backSide = sides.getChild("back");
        this.leftSide = sides.getChild("left");
        this.rightSide = sides.getChild("right");
    }

    @Override
    public void setupAnim(MinecartDecoratedPot entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.neck.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.top.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.frontSide.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.backSide.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftSide.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightSide.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}