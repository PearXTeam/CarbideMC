package ru.pearx.carbide.mc.common.structure.multiblock;

/*
 * Created by mrAppleXZ on 13.11.17 17:08.
 */

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import ru.pearx.carbide.mc.common.blocks.BlockMultiblockPart;

/**
 * A multiblock event. All the events are handled by {@link IMultiblockMaster#handleEvent(IMultiblockEvent, IMultiblockPart)} and sent usually by a {@link BlockMultiblockPart}. Events cam be sent by {@link Multiblock#sendEventToMaster(IBlockAccess, BlockPos, IMultiblockEvent, Object)}
 * @param <T> Return type.
 */
public interface IMultiblockEvent<T>
{
    /**
     * Gets the event ID.
     */
    String getId();

    /**
     * Casts a type to the event return type.
     */
    default <R> T cast(R r)
    {
        //noinspection unchecked
        return (T)r;
    }
}
