package com.glyceryl6.dpm.client;

import com.glyceryl6.dpm.entity.MinecartDecoratedPot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class DecoratedPotMinecartRenderer extends MinecartRenderer<MinecartDecoratedPot> {

    private final DecoratedPotDisplayModel<MinecartDecoratedPot> potModel;

    public DecoratedPotMinecartRenderer(EntityRendererProvider.Context context) {
        super(context, DPMModelLayers.DECORATED_POT_MINECART);
        this.potModel = new DecoratedPotDisplayModel<>(context);
    }

    @Override
    public void render(MinecartDecoratedPot entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        long i = (long)entity.getId() * 493286711L;
        i = i * i * 4392167121L + i * 98761L;
        float f = (((float)(i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f1 = (((float)(i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f2 = (((float)(i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        poseStack.translate(f, f1, f2);
        double d0 = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double d1 = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double d2 = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
        Vec3 vec3 = entity.getPos(d0, d1, d2);
        float f3 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        if (vec3 != null) {
            Vec3 vec31 = entity.getPosOffs(d0, d1, d2, 0.3F);
            Vec3 vec32 = entity.getPosOffs(d0, d1, d2, -0.3F);
            if (vec31 == null) {
                vec31 = vec3;
            }

            if (vec32 == null) {
                vec32 = vec3;
            }

            poseStack.translate(vec3.x - d0, (vec31.y + vec32.y) / 2.0F - d1, vec3.z - d2);
            Vec3 vec33 = vec32.add(-vec31.x, -vec31.y, -vec31.z);
            if (vec33.length() != 0.0F) {
                vec33 = vec33.normalize();
                entityYaw = (float)(Math.atan2(vec33.z, vec33.x) * 180.0F / Math.PI);
                f3 = (float)(Math.atan(vec33.y) * 73.0F);
            }
        }

        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-f3));
        float f5 = (float)entity.getHurtTime() - partialTicks;
        float f6 = Math.max(entity.getDamage() - partialTicks, 0.0F);
        if (f5 > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0F * (float)entity.getHurtDir()));
        }

        poseStack.pushPose();
        ItemStack[] decorations = entity.getDecorations();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate(-0.5D, -0.2D, -0.5D);
        VertexConsumer vertexConsumer = this.potModel.baseMaterial.buffer(buffer, RenderType::entitySolid);
        this.potModel.neck.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        this.potModel.top.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        this.potModel.bottom.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        this.renderSide(this.potModel.frontSide, poseStack, buffer, packedLight, getMaterial(decorations[0].getItem()));
        this.renderSide(this.potModel.backSide, poseStack, buffer, packedLight, getMaterial(decorations[1].getItem()));
        this.renderSide(this.potModel.leftSide, poseStack, buffer, packedLight, getMaterial(decorations[2].getItem()));
        this.renderSide(this.potModel.rightSide, poseStack, buffer, packedLight, getMaterial(decorations[3].getItem()));
        poseStack.popPose();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer1 = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer1, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    @Nullable
    private static Material getMaterial(Item item) {
        Material material = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getResourceKey(item));
        if (material == null) {
            material = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getResourceKey(Items.BRICK));
        }

        return material;
    }

    private void renderSide(ModelPart modelPart, PoseStack poseStack, MultiBufferSource buffer, int packedLight, @Nullable Material material) {
        if (material == null) {
            material = getMaterial(Items.BRICK);
        }

        if (material != null) {
            modelPart.render(poseStack, material.buffer(buffer, RenderType::entitySolid), packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

}