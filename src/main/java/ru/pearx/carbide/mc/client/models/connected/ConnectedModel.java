package ru.pearx.carbide.mc.client.models.connected;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.CarbideMC;
import ru.pearx.carbide.mc.client.models.BakedQuadWNT;
import ru.pearx.carbide.mc.client.models.IPXModel;
import ru.pearx.carbide.mc.client.models.OvModel;
import ru.pearx.carbide.mc.client.models.processors.IQuadProcessor;
import ru.pearx.carbide.mc.common.blocks.controllers.ConnectionsController;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by mrAppleXZ on 14.05.17 22:10.
 */
@SideOnly(Side.CLIENT)
public class ConnectedModel extends OvModel
{
    private ConnectedTextureGetter textureGetter;

    public ConnectedModel()
    {
        setShouldFlipV(false);
        setBaseModel(new ResourceLocation(CarbideMC.MODID, "block/cube_all_colored"));
        quadProcessors.add(new IQuadProcessor()
        {
            @Override
            public void process(List<BakedQuad> quads, @Nullable IBlockState state, @Nullable EnumFacing side, long rand, IPXModel model)
            {
                for(ListIterator<BakedQuad> it = quads.listIterator(); it.hasNext();)
                {
                    BakedQuad q = it.next();
                    it.set(new BakedQuadWNT(q, getDefaultSprite()));
                }
            }

            @Override
            public boolean processState()
            {
                return false;
            }

            @Override
            public boolean processStack()
            {
                return true;
            }

            @Override
            public boolean isSingleUse()
            {
                return true;
            }
        });
        quadProcessors.add(new IQuadProcessor()
        {
            @Override
            public void process(List<BakedQuad> quads, @Nullable IBlockState state, @Nullable EnumFacing side, long rand, IPXModel model)
            {
                for(ListIterator<BakedQuad> it = quads.listIterator(); it.hasNext();)
                {
                    BakedQuad q = it.next();
                    TextureAtlasSprite sprite = null;
                    if(state instanceof IExtendedBlockState)
                    {
                        IExtendedBlockState ext = (IExtendedBlockState) state;
                        switch (q.getFace())
                        {
                            case SOUTH:
                                sprite = getSprite(EnumFacing.UP, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.WEST, ext);
                                break;
                            case NORTH:
                                sprite = getSprite(EnumFacing.UP, EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.EAST, ext);
                                break;
                            case UP:
                                sprite = getSprite(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, ext);
                                break;
                            case DOWN:
                                sprite = getSprite(EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST, ext);
                                break;
                            case EAST:
                                sprite = getSprite(EnumFacing.UP, EnumFacing.NORTH, EnumFacing.DOWN, EnumFacing.SOUTH, ext);
                                break;
                            case WEST:
                                sprite = getSprite(EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.DOWN, EnumFacing.NORTH, ext);
                                break;
                        }
                    }
                    it.set(new BakedQuadWNT(q, sprite == null ? getDefaultSprite() : sprite));
                }
            }

            @Override
            public boolean processState()
            {
                return true;
            }

            @Override
            public boolean processStack()
            {
                return false;
            }
        });
    }

    public ConnectedTextureGetter getTextureGetter()
    {
        return textureGetter;
    }

    public void setTextureGetter(ConnectedTextureGetter textureGetter)
    {
        this.textureGetter = textureGetter;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return getDefaultSprite();
    }

    private TextureAtlasSprite getDefaultSprite()
    {
        return getTextureGetter().getTexture(false, false, false, false);
    }

    private TextureAtlasSprite getSprite(EnumFacing north, EnumFacing east, EnumFacing south, EnumFacing west, IExtendedBlockState state)
    {
        return getTextureGetter().getTexture(state.getValue(ConnectionsController.PROPS.get(north)), state.getValue(ConnectionsController.PROPS.get(east)), state.getValue(ConnectionsController.PROPS.get(south)), state.getValue(ConnectionsController.PROPS.get(west)));
    }
}
