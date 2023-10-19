package net.csbupdate.mixin;

import net.csbupdate.api.CSBRenderer;
import net.csbupdate.gui.CSBInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.csbupdate.CSB.HSBtoRGB;
import static net.csbupdate.CSB.RENDERERS;
import static net.csbupdate.CSBConfig.*;

@SuppressWarnings("unused")
@Mixin(WorldRenderer.class)
public abstract class
MixinWorldRenderer implements CSBInfo {

    @Unique
    private boolean render = false;
    @Unique
    private float r = 0f;
    @Unique
    private float g = 0f;
    @Unique
    private float b = 0f;
    @Unique
    private float a = 0f;
    @Unique
    private float blinkingAlpha = 0f;
    @Shadow
    private ClientWorld world;

    @Shadow
    @Final
    private MinecraftClient client;

//    @Shadow
//    private static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
//    }

    @Shadow protected abstract void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state);

    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            ordinal = 0))
    private void onDrawShapeOutline(WorldRenderer worldRenderer, MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState) {
        if (!isEnabled()) {
            this.drawBlockOutline(matrixStack, vertexConsumer, entity, d, e, f, blockPos, blockState);
//            drawShapeOutline(matrixStack, vertexConsumer, blockState.getOutlineShape(this.world, blockPos, ShapeContext.of(entity)), d, e, f, 0.0F, 0.0F, 0.0F, 0.4F);
            return;
        }
        render = true;
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderWorldBorder(Lnet/minecraft/client/render/Camera;)V",
                    shift = At.Shift.AFTER))
    private void renderWorldBorder(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (render) {
            r = getRed();
            g = getGreen();
            b = getBlue();
            a = getAlpha();
            if (rainbow) {
                final double millis = System.currentTimeMillis() % 10000L / 10000.0f;
                final int color = HSBtoRGB((float) millis, 0.8f, 0.8f);
                r = (color >> 16 & 255) / 255.0f;
                g = (color >> 8 & 255) / 255.0f;
                b = (color & 255) / 255.0f;
            }
            blinkingAlpha = getBlinkSpeed() > 0 ? getBlinkAlpha() * (float) Math.abs(Math.sin(System.currentTimeMillis() / 100.0D * getBlinkSpeed())) : getBlinkAlpha();
            BlockHitResult hitResult = (BlockHitResult) client.crosshairTarget;
            assert hitResult != null;
            BlockPos blockPos = hitResult.getBlockPos();
            for (CSBRenderer renderer : RENDERERS) {
                ActionResult result = Objects.requireNonNull(renderer.render(world, camera, hitResult/*, delta*/));
                if (result != ActionResult.PASS)
                    break;
            }
            render = false;
        }
    }

    @Override
    public float getOutlineRed() {
        return r;
    }

    @Override
    public float getOutlineGreen() {
        return g;
    }

    @Override
    public float getOutlineBlue() {
        return b;
    }

    @Override
    public float getOutlineAlpha() {
        return a;
    }

    @Override
    public float getInnerRed() {
        return r;
    }

    @Override
    public float getInnerGreen() {
        return g;
    }

    @Override
    public float getInnerBlue() {
        return b;
    }

    @Override
    public float getInnerAlpha() {
        return blinkingAlpha;
    }
}
