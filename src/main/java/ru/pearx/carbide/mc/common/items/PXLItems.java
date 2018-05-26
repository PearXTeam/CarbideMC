package ru.pearx.carbide.mc.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import ru.pearx.carbide.mc.CarbideMC;
import ru.pearx.carbide.mc.common.blocks.PXLBlocks;
import ru.pearx.carbide.mc.client.models.IModelProvider;

/*
 * Created by mrAppleXZ on 20.08.17 23:25.
 */
@Mod.EventBusSubscriber(modid = CarbideMC.MODID)
@GameRegistry.ObjectHolder(CarbideMC.MODID)
public class PXLItems
{
    public static final ItemBlock structure_nothing = null;
    public static final ItemMultiblock multiblock = null;

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Item> e)
    {
        register(new ItemBlockBase(PXLBlocks.structure_nothing), e.getRegistry());
        register(new ItemMultiblock(), e.getRegistry());
    }

    private static void register(Item itm, IForgeRegistry<Item> reg)
    {
        if(itm instanceof IModelProvider)
            CarbideMC.PROXY.setupModels((IModelProvider) itm);
        reg.register(itm);
    }
}
