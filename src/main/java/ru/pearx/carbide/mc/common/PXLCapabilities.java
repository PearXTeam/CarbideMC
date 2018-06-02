package ru.pearx.carbide.mc.common;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import ru.pearx.carbide.mc.CarbideMC;
import ru.pearx.carbide.mc.common.capability.CapabilityStorageEmpty;
import ru.pearx.carbide.mc.common.caps.animation.AnimationStateManager;
import ru.pearx.carbide.mc.common.caps.animation.IAnimationStateManager;

import javax.annotation.Nullable;

/*
 * Created by mrAppleXZ on 21.09.17 10:38.
 */
public class PXLCapabilities
{
    @CapabilityInject(IAnimationStateManager.class)
    public static final Capability<IAnimationStateManager> ASM = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IAnimationStateManager.class, new CapabilityStorageEmpty<>(), AnimationStateManager.class);
    }
}
