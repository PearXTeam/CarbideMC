package ru.pearx.carbide.mc.common.tiles;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.pearx.carbide.mc.CarbideMC;

/*
 * Created by mrAppleXZ on 14.11.17 13:46.
 */
public final class PXLTiles
{
    public static void setup()
    {
        GameRegistry.registerTileEntity(TileMultiblockSlave.class, new ResourceLocation(CarbideMC.MODID, "multiblock_slave").toString());
    }
}
