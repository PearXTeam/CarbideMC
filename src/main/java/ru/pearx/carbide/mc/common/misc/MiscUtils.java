package ru.pearx.carbide.mc.common.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.Random;

/*
 * Created by mrAppleXZ on 27.05.18 13:02.
 */
public class MiscUtils
{
    public static void fillBlockWithLoot(WorldServer world, Random rand, BlockPos pos, EnumFacing facing, ResourceLocation loot_table, float luck)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) instanceof IItemHandlerModifiable)
        {
            IItemHandlerModifiable hand = (IItemHandlerModifiable) te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
            LootTable table = world.getLootTableManager().getLootTableFromLocation(loot_table);
            List<ItemStack> items = table.generateLootForPools(rand, new LootContext(luck, world, world.getLootTableManager(), null, null, null));
            for (int i = 0; i < hand.getSlots(); i++)
            {
                if (items.size() <= 0)
                    break;
                int index = rand.nextInt(items.size());
                hand.setStackInSlot(i, items.get(index));
                items.remove(index);
            }
        }
    }
}
