package ru.pearx.carbide.mc.client.models.connected;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/*
 * Created by mrAppleXZ on 01.06.18 15:33.
 */
@FunctionalInterface
public interface ConnectedTextureGetter
{
    TextureAtlasSprite getTexture(boolean north, boolean east, boolean south, boolean west);

    static byte indexFromSides(boolean north, boolean east, boolean south, boolean west)
    {
        byte res = 0b0;
        if(west)
            res |= 0b1;
        if(south)
            res |= 0b10;
        if(east)
            res |= 0b100;
        if(north)
            res |= 0b1000;
        return res;
    }
}
