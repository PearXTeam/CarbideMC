package ru.pearx.carbide.mc.client.models;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/*
 * Created by mrAppleXZ on 24.12.17 10:26.
 */
@SideOnly(Side.CLIENT)
public abstract class PXModel implements IPXModel
{
    private WeakReference<ItemStack> stack;

    public static class OverrideList extends ItemOverrideList
    {
        public OverrideList(List<ItemOverride> overridesIn)
        {
            super(overridesIn);
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
        {
            if (originalModel instanceof IPXModel)
                ((IPXModel) originalModel).setStack(stack);
            return super.handleItemState(originalModel, stack, world, entity);
        }
    }

    public static final OverrideList OVERRIDE_LIST = new OverrideList(Collections.emptyList());

    public static final ImmutableList<BakedQuad> DUMMY_LIST = ImmutableList.of();

    @Override
    public void setStack(ItemStack stack)
    {
        this.stack = new WeakReference<>(stack);
    }

    @Override
    public ItemStack getStack()
    {
        if (stack != null)
            return stack.get();
        return null;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return OVERRIDE_LIST;
    }
}
