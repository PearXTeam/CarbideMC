package ru.pearx.carbide.mc.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.client.gui.DrawingTools;
import ru.pearx.carbide.Color;

/**
 * Created by mrAppleXZ on 28.05.17 20:19.
 */
@SideOnly(Side.CLIENT)
public class ParticleTrail extends PXParticle
{
    private float baseAlpha;

    public ParticleTrail(PXParticle p, float scale, int age)
    {
        super(p.getX(), p.getY(), p.getZ());
        setMotionFixed(true);
        setLightEnabled(p.isLightEnabled());
        setCanCollide(false);
        setColorRed(p.getColorRed());
        setColorGreen(p.getColorGreen());
        setColorBlue(p.getColorBlue());
        setAlphaF(p.getColorAlpha());
        setScale(p.getScale() * scale);
        setMaxAge(age);
        this.baseAlpha = getColorAlpha();
    }

    @Override
    public void onUpdate()
    {
        setAlphaF(baseAlpha * (1 - ((float)getAge() / getMaxAge())));
        super.onUpdate();
    }

    @Override
    public boolean shouldDisableDepth()
    {
        return true;
    }
}
