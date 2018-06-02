package ru.pearx.carbide.mc.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/*
 * Created by mrAppleXZ on 27.05.18 13:56.
 */
public class CapabilityStorageEmpty<T> implements Capability.IStorage<T>
{
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side)
    {
        return null;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt)
    {

    }
}
