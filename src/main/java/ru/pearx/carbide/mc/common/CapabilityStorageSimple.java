package ru.pearx.carbide.mc.common;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/*
 * Created by mrAppleXZ on 27.05.18 12:07.
 */
public class CapabilityStorageSimple<NBT extends NBTBase, T extends INBTSerializable<NBT>> implements Capability.IStorage<T>
{
    private Class<NBT> nbtClass;

    public CapabilityStorageSimple(Class<NBT> nbtClass)
    {
        this.nbtClass = nbtClass;
    }

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side)
    {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt)
    {
        instance.deserializeNBT(nbtClass.cast(nbt));
    }
}
