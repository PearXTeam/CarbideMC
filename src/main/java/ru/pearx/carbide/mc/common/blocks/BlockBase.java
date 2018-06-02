package ru.pearx.carbide.mc.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.client.models.IModelProvider;

/**
 * Created by mrAppleXZ on 08.04.17 19:01.
 */
public class BlockBase extends Block implements IModelProvider
{
    private boolean emptyStateMapper;

    public BlockBase(Material materialIn)
    {
        super(materialIn);
    }

    protected boolean isEmptyStateMapper()
    {
        return emptyStateMapper;
    }

    protected void setEmptyStateMapper(boolean emptyStateMapper)
    {
        this.emptyStateMapper = emptyStateMapper;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setupModels()
    {
        if(isEmptyStateMapper())
        {
            StateMap.Builder bld = new StateMap.Builder();
            for (IProperty prop : getBlockState().getProperties())
            {
                bld.ignore(prop);
            }
            ModelLoader.setCustomStateMapper(this, bld.build());
        }
    }
}
