package ru.pearx.carbide.mc.client.models.processors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.client.models.IPXModel;

import javax.annotation.Nullable;
import java.util.List;

/*
 * Created by mrAppleXZ on 22.07.17 18:03.
 */
@SideOnly(Side.CLIENT)
public interface IVertexProcessor
{
    void preProcess(List<BakedQuad> quads, @Nullable IBlockState state, @Nullable EnumFacing side, long rand, IPXModel model);

    default void processQuad(UnpackedBakedQuad.Builder bld, BakedQuad quad, @Nullable IBlockState state, @Nullable EnumFacing side, long rand, IPXModel model)
    {
    }

    float[] processVertex(UnpackedBakedQuad.Builder bld, BakedQuad quad, float[] data, int vert, int element, @Nullable IBlockState state, @Nullable EnumFacing side, long rand, IPXModel model);

    boolean processState();

    boolean processStack();

    default boolean shouldProcess(BakedQuad quad, @Nullable IBlockState state, @Nullable EnumFacing side, long rand, IPXModel model)
    {
        return true;
    }

    default boolean isSingleUse()
    {
        return false;
    }
}
