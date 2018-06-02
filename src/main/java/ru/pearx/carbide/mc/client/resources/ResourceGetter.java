package ru.pearx.carbide.mc.client.resources;

import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;

/*
 * Created by mrAppleXZ on 31.05.18 16:50.
 */
@FunctionalInterface
public interface ResourceGetter<T>
{
    T get(ResourceLocation id);
}
