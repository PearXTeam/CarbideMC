package ru.pearx.carbide.mc.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.pearx.carbide.Color;

/*
 * Created by mrAppleXZ on 28.05.18 18:57.
 */
public class PXParticle extends Particle
{
    private boolean lightEnabled = true;
    private boolean motionFixed = false;

    protected PXParticle(double posXIn, double posYIn, double posZIn)
    {
        super(Minecraft.getMinecraft().world, posXIn, posYIn, posZIn);
    }

    public boolean isLightEnabled()
    {
        return lightEnabled;
    }

    public void setLightEnabled(boolean lightEnabled)
    {
        this.lightEnabled = lightEnabled;
    }

    public boolean isMotionFixed()
    {
        return motionFixed;
    }

    public void setMotionFixed(boolean motionFixed)
    {
        this.motionFixed = motionFixed;
    }

    public World getWorld()
    {
        return world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public double getX()
    {
        return posX;
    }

    public void setX(double x)
    {
        this.posX = x;
    }

    public double getY()
    {
        return posY;
    }

    public void setY(double y)
    {
        this.posY = y;
    }

    public double getZ()
    {
        return posZ;
    }

    public void setZ(double z)
    {
        this.posZ = z;
    }

    public boolean canCollide()
    {
        return canCollide;
    }

    public void setCanCollide(boolean canCollide)
    {
        this.canCollide = canCollide;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public int getMaxAge()
    {
        return particleMaxAge;
    }

    public int getAge()
    {
        return particleAge;
    }

    public float getColorRed()
    {
        return particleRed;
    }

    public void setColorRed(float colorRed)
    {
        this.particleRed = colorRed;
    }

    public float getColorGreen()
    {
        return particleGreen;
    }

    public void setColorGreen(float colorGreen)
    {
        this.particleGreen = colorGreen;
    }

    public float getColorBlue()
    {
        return particleBlue;
    }

    public void setColorBlue(float colorBlue)
    {
        this.particleBlue = colorBlue;
    }

    public float getColorAlpha()
    {
        return particleAlpha;
    }

    public void setColorAlpha(float colorAlpha)
    {
        this.particleAlpha = colorAlpha;
    }

    public TextureAtlasSprite getSprite()
    {
        return particleTexture;
    }

    public void setSprite(TextureAtlasSprite sprite)
    {
        setParticleTexture(sprite);
    }

    public float getGravity()
    {
        return particleGravity;
    }

    public void setGravity(float gravity)
    {
        this.particleGravity = gravity;
    }

    public float getScale()
    {
        return particleScale;
    }

    public void setScale(float scale)
    {
        this.particleScale = scale;
    }

    public void setColor(Color c)
    {
        setRBGColorF(c.getRedCoefficient(), c.getGreenCoefficient(), c.getBlueCoefficient());
        setAlphaF(c.getAlphaCoefficient());
    }

    public void setColor(Color c, float alpha)
    {
        setRBGColorF(c.getRedCoefficient(), c.getGreenCoefficient(), c.getBlueCoefficient());
        setAlphaF(c.getAlphaCoefficient() * alpha);
    }

    public void setAngle(float angle)
    {
        this.prevParticleAngle = particleAngle;
        this.particleAngle = angle;
    }

    public float getAngle()
    {
        return particleAngle;
    }


    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float minU = 0, maxU = 1, minV = 0, maxV = 1;
        float scale = 0.1F * this.particleScale;

        if (this.particleTexture != null)
        {
            minU = this.particleTexture.getMinU();
            maxU = this.particleTexture.getMaxU();
            minV = this.particleTexture.getMinV();
            maxV = this.particleTexture.getMaxV();
        }

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        Vec3d[] avec3d = new Vec3d[]{new Vec3d((double) (-rotationX * scale - rotationXY * scale), (double) (-rotationZ * scale), (double) (-rotationYZ * scale - rotationXZ * scale)), new Vec3d((double) (-rotationX * scale + rotationXY * scale), (double) (rotationZ * scale), (double) (-rotationYZ * scale + rotationXZ * scale)), new Vec3d((double) (rotationX * scale + rotationXY * scale), (double) (rotationZ * scale), (double) (rotationYZ * scale + rotationXZ * scale)), new Vec3d((double) (rotationX * scale - rotationXY * scale), (double) (-rotationZ * scale), (double) (rotationYZ * scale - rotationXZ * scale))};

        if (this.particleAngle != 0.0F)
        {
            float angle = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float f9 = MathHelper.cos(angle * 0.5F);
            float f10 = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.x;
            float f11 = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.y;
            float f12 = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.z;
            Vec3d vec3d = new Vec3d((double) f10, (double) f11, (double) f12);

            for (int l = 0; l < 4; ++l)
            {
                avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double) (f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double) (2.0F * f9)));
            }
        }
        int skylight = 240, blocklight = 192;
        if(isLightEnabled())
        {
            int brightness = this.getBrightnessForRender(partialTicks);
            skylight = brightness >> 16 & 65535;
            blocklight = brightness & 65535;
        }
        buffer.pos((double) x + avec3d[0].x, (double) y + avec3d[0].y, (double) z + avec3d[0].z).tex(maxU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(skylight, blocklight).endVertex();
        buffer.pos((double) x + avec3d[1].x, (double) y + avec3d[1].y, (double) z + avec3d[1].z).tex(maxU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(skylight, blocklight).endVertex();
        buffer.pos((double) x + avec3d[2].x, (double) y + avec3d[2].y, (double) z + avec3d[2].z).tex(minU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(skylight, blocklight).endVertex();
        buffer.pos((double) x + avec3d[3].x, (double) y + avec3d[3].y, (double) z + avec3d[3].z).tex(minU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(skylight, blocklight).endVertex();
    }

    @Override
    public void onUpdate()
    {
        if(isMotionFixed())
        {
            prevPosX = this.posX;
            prevPosY = this.posY;
            prevPosZ = this.posZ;

            if (particleAge++ >= particleMaxAge)
            {
                setExpired();
            }

            move(motionX, motionY, motionZ);
        }
        else
        {
            super.onUpdate();
        }
    }

    @Override
    public int getFXLayer()
    {
        return 1;
    }
}
