package ru.pearx.carbide.mc.client.models;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.CarbideMC;
import ru.pearx.carbide.mc.client.models.processors.IQuadProcessor;
import ru.pearx.carbide.mc.client.models.processors.IVertexProcessor;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mrAppleXZ on 10.04.17 8:55.
 */
@SideOnly(Side.CLIENT)
public class OvModel extends PXModel
{
    public static Field unpQuadData;

    static
    {
        try
        {
            unpQuadData = UnpackedBakedQuad.class.getDeclaredField("unpackedData");
            unpQuadData.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            CarbideMC.getLog().error("Can't locate the 'unpackedData' field!");
        }
    }

    protected List<IQuadProcessor> quadProcessors = new ArrayList<>();
    protected List<IVertexProcessor> vertexProcessors = new ArrayList<>();
    private ResourceLocation baseModel;
    private IBakedModel baked;

    private boolean flipV = true;
    private boolean disableSides = true;

    @Override
    public void bake()
    {
        IModel mdl;
        try
        {
            mdl = ModelLoaderRegistry.getModel(getBaseModel());
            if (flipV)
            {
                mdl = mdl.process(ImmutableMap.of("flip-v", "true"));
            }
            baked = mdl.bake(getModelState(this, mdl), DefaultVertexFormats.ITEM,
                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if (shouldDisableSides() && side != null)
            return DUMMY_LIST;

        List<BakedQuad> l = new ArrayList<>();
        for(EnumFacing face : EnumFacing.values())
            l.addAll(getBaked().getQuads(state, face, rand));
        l.addAll(getBaked().getQuads(state, null, rand));
        process(l, state, side, rand);
        return l;
    }

    protected void process(List<BakedQuad> quads, @Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        for (IQuadProcessor proc : quadProcessors)
            if ((proc.processState() && state != null) || (proc.processStack() && state == null))
                proc.process(quads, state, side, rand, this);
        List<IVertexProcessor> validVertexProcs = vertexProcessors.parallelStream()
                .filter(proc -> (proc.processState() && state != null) || (proc.processStack() && state == null))
                .collect(Collectors.toList());
        if (!validVertexProcs.isEmpty())
        {
            for (IVertexProcessor proc : validVertexProcs)
                proc.preProcess(quads, state, side, rand, this);
            List<BakedQuad> processed = quads.parallelStream().map(q ->
            {
                boolean flag = false;
                for (IVertexProcessor proc : validVertexProcs)
                {
                    if (proc.shouldProcess(q, state, side, rand, this))
                    {
                        flag = true;
                        break;
                    }
                }
                if (flag)
                {
                    UnpackedBakedQuad.Builder bld = new UnpackedBakedQuad.Builder(q.getFormat());
                    bld.setQuadTint(q.getTintIndex());
                    bld.setQuadOrientation(q.getFace());
                    bld.setTexture(q.getSprite());
                    bld.setApplyDiffuseLighting(q.shouldApplyDiffuseLighting());
                    for (IVertexProcessor proc : validVertexProcs)
                        if(proc.shouldProcess(q, state, side, rand, this))
                            proc.processQuad(bld, q, state, side, rand, this);
                    if (q instanceof UnpackedBakedQuad)
                    {
                        try
                        {
                            float[][][] data = (float[][][]) unpQuadData.get(q);
                            for (int i = 0; i < 4; i++)
                            {
                                for (int e = 0; e < q.getFormat().getElementCount(); e++)
                                {
                                    float[] lst = Arrays.copyOf(data[i][e], data[i][e].length);
                                    for (IVertexProcessor proc : validVertexProcs)
                                        if(proc.shouldProcess(q, state, side, rand, this))
                                            lst = proc.processVertex(bld, q, lst, i, e, state, side, rand, this);
                                    bld.put(e, lst);
                                }
                            }

                        }
                        catch (IllegalAccessException e)
                        {
                            e.printStackTrace();
                        }
                    } else
                    {
                        for (int i = 0; i < 4; i++)
                        {
                            for (int e = 0; e < q.getFormat().getElementCount(); e++)
                            {
                                float[] lst = new float[q.getFormat().getElement(e).getElementCount()];
                                LightUtil.unpack(q.getVertexData(), lst, q.getFormat(), i, e);
                                for (IVertexProcessor proc : validVertexProcs)
                                    if(proc.shouldProcess(q, state, side, rand, this))
                                        lst = proc.processVertex(bld, q, lst, i, e, state, side, rand, this);
                                bld.put(e, lst);
                            }
                        }
                    }
                    return bld.build();
                }
                return q;
            }).filter((q) -> shouldDisableSides() || Objects.equals(side, q.getFace())).collect(Collectors.toList());
            quads.clear();
            quads.addAll(processed);
        }
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return getBaked().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return getBaked().isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return getBaked().isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return getBaked().getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return getBaked().getItemCameraTransforms();
    }

    public boolean shouldFlipV()
    {
        return flipV;
    }

    public void setShouldFlipV(boolean flipV)
    {
        this.flipV = flipV;
    }

    protected IBakedModel getBaked()
    {
        return baked;
    }

    protected void setBaked(IBakedModel baked)
    {
        this.baked = baked;
    }

    public void setBaseModel(ResourceLocation loc)
    {
        baseModel = loc;
    }

    public ResourceLocation getBaseModel()
    {
        return baseModel;
    }

    public boolean shouldDisableSides()
    {
        return disableSides;
    }

    public void setShouldDisableSides(boolean disableSides)
    {
        this.disableSides = disableSides;
    }

    public IModelState getModelState(IPXModel th, IModel model)
    {
        return TRSRTransformation.identity();
    }
}
