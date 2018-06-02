package ru.pearx.carbide.mc.client.resources;

import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/*
 * Created by mrAppleXZ on 31.05.18 12:20.
 */
public final class ResourceInjector
{
    private ResourceInjector()
    {
    }

    public static <T> void fillResources(Class<?> classToFill, Class<T> type, ResourceGetter<? extends T> getter)
    {
        String globalId = "minecraft";
        {
            InjectResource[] hlds = classToFill.getAnnotationsByType(InjectResource.class);
            if (hlds.length > 0)
                globalId = hlds[0].value();
        }
        for (Field f : classToFill.getDeclaredFields())
        {
            if (Modifier.isStatic(classToFill.getModifiers()) && type.isAssignableFrom(f.getType()))
            {
                InjectResource[] hlds = f.getAnnotationsByType(InjectResource.class);
                if (hlds.length > 0)
                {
                    String modid;
                    String name;
                    String s = hlds[0].value();
                    int ind = s.indexOf(':');
                    if (ind > -1)
                    {
                        modid = s.substring(0, ind);
                        name = s.substring(ind + 1);
                    }
                    else
                    {
                        modid = globalId;
                        name = s;
                    }

                    T t = getter.get(new ResourceLocation(modid, name));
                    if(f.getType().isInstance(t))
                    {
                        try
                        {
                            f.set(null, t);
                        }
                        catch (IllegalAccessException e)
                        {
                            //todo
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
