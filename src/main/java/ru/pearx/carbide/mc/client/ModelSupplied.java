package ru.pearx.carbide.mc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.Supplied;

/*
 * Created by mrAppleXZ on 13.12.17 21:09.
 */
@SideOnly(Side.CLIENT)
public class ModelSupplied extends Supplied<IBakedModel>
{
    public ModelSupplied(ModelResourceLocation loc)
    {
        super(() -> Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(loc));
    }
}
