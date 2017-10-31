package ru.pearx.libmc.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created by mrAppleXZ on 10.07.17 22:04.
 */
public class ItemStackUtils
{
    public static ItemStack extractAll(IItemHandler hand, int slot)
    {
        ItemStack stack = hand.getStackInSlot(slot);
        return hand.extractItem(slot, stack.getCount(), false);
    }

    public static void clear(IItemHandler hand)
    {
        for(int i = 0; i < hand.getSlots(); i++)
            extractAll(hand, i);
    }

    public static void drop(IItemHandler hand, World world, BlockPos pos)
    {
        for(int i = 0; i < hand.getSlots(); i++)
        {
            ItemStack stack = hand.getStackInSlot(i);
            if(!stack.isEmpty())
            {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack));
            }
        }
    }

    public static final List<ItemStack> EMPTY_LIST = Collections.singletonList(ItemStack.EMPTY);
    public static List<ItemStack> getIngredientItems(Ingredient ing)
    {
        ItemStack[] stacks = ing.getMatchingStacks();
        if (stacks.length == 0)
            return EMPTY_LIST;
        else
        {
            List<ItemStack> matching = new ArrayList<>();
            for (ItemStack stack : stacks)
            {
                NonNullList<ItemStack> subs = NonNullList.create();
                stack.getItem().getSubItems(stack.getItem().getCreativeTab() == null ? CreativeTabs.SEARCH : stack.getItem().getCreativeTab(), subs);
                for (ItemStack sub : subs)
                    if (ing.apply(sub))
                        matching.add(sub);
            }
            return matching;
        }
    }

    public static boolean isCraftingMatrixMatches(InventoryCrafting inv, int width, int height, NonNullList<Ingredient> ingredients, boolean mirrored)
    {
        for (int x = 0; x <= inv.getWidth() - width; x++)
        {
            for (int y = 0; y <= inv.getHeight() - height; ++y)
            {
                if (checkMatch(inv, width, height, ingredients, x, y, false))
                {
                    return true;
                }

                if (mirrored && checkMatch(inv, width, height, ingredients, x, y, true))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean checkMatch(InventoryCrafting inv, int width, int height, NonNullList<Ingredient> ingredients, int startX, int startY, boolean mirror)
    {
        for (int x = 0; x < inv.getWidth(); x++)
        {
            for (int y = 0; y < inv.getHeight(); y++)
            {
                int subX = x - startX;
                int subY = y - startY;
                Ingredient target = Ingredient.EMPTY;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height)
                {
                    if (mirror)
                    {
                        target = ingredients.get(width - subX - 1 + subY * width);
                    }
                    else
                    {
                        target = ingredients.get(subX + subY * width);
                    }
                }

                if (!target.apply(inv.getStackInRowAndColumn(x, y)))
                {
                    return false;
                }
            }
        }

        return true;
    }

}
