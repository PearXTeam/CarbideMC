package ru.pearx.carbide.mc.client.models.processors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.client.models.IPXModel;

import javax.annotation.Nullable;
import java.util.List;

/*
 * Created by mrAppleXZ on 07.07.17 9:00.
 */
@SideOnly(Side.CLIENT)
public interface IQuadProcessor
{
    void process(List<BakedQuad> quads, @Nullable IBlockState state, @Nullable EnumFacing side, long rand, IPXModel model);

    boolean processState();

    boolean processStack();

    default boolean isSingleUse()
    {
        return false;
    }
}
