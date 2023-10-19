package net.csbupdate;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.csbupdate.api.CSBRenderer;
import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.opengl.GL11;


public class CSBDefaultRenderer implements CSBRenderer {
    @Override
    public double getPriority() {
        return 1050d;
    }


    private static final Identifier WHITE = new Identifier("textures/misc/white.png");

    @Override
    public ActionResult render(ClientWorld world, Camera camera, BlockHitResult hitResult/*, float delta*/) {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Vec3d cameraPos = camera.getPos();
        VoxelShape shape = blockState.getOutlineShape(world, blockPos, ShapeContext.of(camera.getFocusedEntity()));
        if (CSBConfig.isAdjustBoundingBoxByLinkedBlocks())
            shape = adjustShapeByLinkedBlocks(world, blockState, blockPos, shape);
        drawOutlinedBoundingBox(shape, blockPos.getX() - cameraPos.getX(), blockPos.getY() - cameraPos.getY(), blockPos.getZ() - cameraPos.getZ(), getOutlineRed(), getOutlineGreen(), getOutlineBlue(), getOutlineAlpha());
        drawBlinkingBlock(shape, blockPos.getX() - cameraPos.getX(), blockPos.getY() - cameraPos.getY(), blockPos.getZ() - cameraPos.getZ(), getInnerRed(), getInnerGreen(), getInnerBlue(), getInnerAlpha());
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        return ActionResult.SUCCESS;
    }

    private void drawOutlinedBoundingBox(VoxelShape voxelShapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexConsumer = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(getOutlineThickness());
        vertexConsumer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        voxelShapeIn.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float k = (float)(maxX - minX);
            float l = (float)(maxY - minY);
            float m = (float)(maxZ - minZ);
            float n = MathHelper.sqrt(k * k + l * l + m * m);
            k /= n;
            l /= n;
            m /= n;
            vertexConsumer.vertex((float)(minX + xIn), (float)(minY + yIn), (float)(minZ + zIn)).color(red, green, blue, alpha).normal(k, l, m).next();
            vertexConsumer.vertex((float)(maxX + xIn), (float)(maxY + yIn), (float)(maxZ + zIn)).color(red, green, blue, alpha).normal(k, l, m).next();
        });
//        voxelShapeIn.forEachEdge((k, l, m, n, o, p)  -> {
//            vertexConsumer.vertex( (float)(k + xIn), (float)(l + yIn), (float)(m + zIn)).next();
//            vertexConsumer.vertex( (float)(n + xIn), (float)(o + yIn), (float)(p + zIn)).next();
//        });
        tessellator.draw();
//        for (Box box : voxelShapeIn.getBoundingBoxes()) {
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z1 - 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y1 - 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//            vertexConsumer.begin(1, VertexFormats.POSITION);
//            vertexConsumer.vertex((float) (box.x1 - 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            vertexConsumer.vertex((float) (box.x2 + 0.005 + xIn), (float) (box.y2 + 0.005 + yIn), (float) (box.z2 + 0.005 + zIn)).next();
//            tessellator.draw();
//        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    private void drawBlinkingBlock(VoxelShape voxelShapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexConsumer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShaderTexture(0, WHITE);
        RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.disableCull();
        VoxelShape shape = voxelShapeIn.getBoundingBoxes().stream()
                .map(box -> box.expand(0.005, 0.005, 0.005))
                .map(VoxelShapes::cuboid)
                .reduce(VoxelShapes::union)
                .orElse(VoxelShapes.empty()).simplify();
        for (Box box : shape.getBoundingBoxes()) {
            box(tessellator, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, xIn, yIn, zIn);
        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    private void box(Tessellator tessellator, BufferBuilder vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, double xIn, double yIn, double zIn) {
//        1 for min and 2 for max
//        x1 -= 0.005;
//        y1 -= 0.005;
//        z1 -= 0.005;
//        x2 += 0.005;
//        y2 += 0.005;
//        z2 += 0.005;
//        vertexConsumer.begin(7, VertexFormats.POSITION);
        vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y1 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        tessellator.draw();

        //Down
//        vertexConsumer.begin(7, VertexFormats.POSITION);
        vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y2 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y2 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y2 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y2 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y2 + yIn), (float) (z1 + zIn)).next();
        tessellator.draw();

        //North
//        vertexConsumer.begin(7, VertexFormats.POSITION);
        vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y2 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y2 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        tessellator.draw();

        //South
//        vertexConsumer.begin(7, VertexFormats.POSITION);
        vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y1 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y2 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y2 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z2 + zIn)).next();
        tessellator.draw();

        //West
////        vertexConsumer.begin(7, VertexFormats.POSITION);
        vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y2 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y2 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x1 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        tessellator.draw();

        //East
//        vertexConsumer.begin(7, VertexFormats.POSITION);
        vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y2 + yIn), (float) (z1 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y2 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y1 + yIn), (float) (z2 + zIn)).next();
        vertexConsumer.vertex((float) (x2 + xIn), (float) (y1 + yIn), (float) (z1 + zIn)).next();
        tessellator.draw();
    }

    private VoxelShape adjustShapeByLinkedBlocks(ClientWorld world, BlockState blockState, BlockPos blockPos, VoxelShape shape) {
        try {
            if (blockState.getBlock() instanceof ChestBlock) {
                // Chests
                Block block = blockState.getBlock();
                Direction facing = ChestBlock.getFacing(blockState);
                BlockState anotherChestState = world.getBlockState(blockPos.offset(facing, 1));
                if (anotherChestState.getBlock() == block)
                    if (blockPos.offset(facing, 1).offset(ChestBlock.getFacing(anotherChestState)).equals(blockPos))
                        return VoxelShapes.union(shape, anotherChestState.getOutlineShape(world, blockPos).offset(facing.getOffsetX(), facing.getOffsetY(), facing.getOffsetZ()));
            } else if (blockState.getBlock() instanceof DoorBlock) {
                // Doors
                Block block = blockState.getBlock();
                if (world.getBlockState(blockPos.up(1)).getBlock() == block) {
                    BlockState otherState = world.getBlockState(blockPos.up(1));
                    if (otherState.get(DoorBlock.POWERED).equals(blockState.get(DoorBlock.POWERED)) && otherState.get(DoorBlock.FACING).equals(blockState.get(DoorBlock.FACING)) && otherState.get(DoorBlock.HINGE).equals(blockState.get(DoorBlock.HINGE))) {
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(0, 1, 0));
                    }
                }
                if (world.getBlockState(blockPos.down(1)).getBlock() == block) {
                    BlockState otherState = world.getBlockState(blockPos.down(1));
                    if (otherState.get(DoorBlock.POWERED).equals(blockState.get(DoorBlock.POWERED)) && otherState.get(DoorBlock.FACING).equals(blockState.get(DoorBlock.FACING)) && otherState.get(DoorBlock.HINGE).equals(blockState.get(DoorBlock.HINGE)))
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(0, -1, 0));
                }
            } else if (blockState.getBlock() instanceof BedBlock) {
                // Beds
                Block block = blockState.getBlock();
                Direction direction = blockState.get(HorizontalFacingBlock.FACING);
                BlockState otherState = world.getBlockState(blockPos.offset(direction));
                if (blockState.get(BedBlock.PART).equals(BedPart.FOOT) && otherState.getBlock() == block) {
                    if (otherState.get(BedBlock.PART).equals(BedPart.HEAD))
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
                }
                otherState = world.getBlockState(blockPos.offset(direction.getOpposite()));
                direction = direction.getOpposite();
                if (blockState.get(BedBlock.PART).equals(BedPart.HEAD) && otherState.getBlock() == block) {
                    if (otherState.get(BedBlock.PART).equals(BedPart.FOOT))
                        return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
                }
            } else if (blockState.getBlock() instanceof PistonBlock && blockState.get(PistonBlock.EXTENDED)) {
                // Piston Base
                Block block = blockState.getBlock();
                Direction direction = blockState.get(FacingBlock.FACING);
                BlockState otherState = world.getBlockState(blockPos.offset(direction));
                if (otherState.get(PistonHeadBlock.TYPE).equals(block == Blocks.PISTON ? PistonType.DEFAULT : PistonType.STICKY) && direction.equals(otherState.get(FacingBlock.FACING)))
                    return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos).offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
            } else if (blockState.getBlock() instanceof PistonHeadBlock) {
                // Piston Arm
//                Block block = blockState.getBlock();
                Direction direction = blockState.get(FacingBlock.FACING);
                BlockState otherState = world.getBlockState(blockPos.offset(direction.getOpposite()));
                if (otherState.getBlock() instanceof PistonBlock && direction == otherState.get(FacingBlock.FACING) && otherState.get(PistonBlock.EXTENDED))
                    return VoxelShapes.union(shape, otherState.getOutlineShape(world, blockPos.offset(direction.getOpposite())).offset(direction.getOpposite().getOffsetX(), direction.getOpposite().getOffsetY(), direction.getOpposite().getOffsetZ()));
            }
        } catch (Exception ignored) {
        }
        return shape;
    }
}
