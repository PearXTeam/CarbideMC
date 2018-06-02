package ru.pearx.carbide.mc.client.particle;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector3d;

/**
 * Created by mrAppleXZ on 28.05.17 18:45.
 */
@SideOnly(Side.CLIENT)
public abstract class ParticleMovingTo extends PXParticle
{
    protected Vector3d movingVector;
    protected float speed;

    protected ParticleMovingTo(Vector3d loc, Vector3d locTo, float speed)
    {
        super(loc.getX(), loc.getY(), loc.getZ());
        this.speed = speed;
        setCanCollide(false);
        Vector3d vec = new Vector3d(locTo.getX() - loc.getX(), locTo.getY() - loc.getY(), locTo.getZ() - loc.getZ());
        double length = vec.length();
        setMaxAge(MathHelper.ceil(length / speed));
        this.motionX = (vec.getX() / length) * speed;
        this.motionY = (vec.getY() / length) * speed;
        this.motionZ = (vec.getZ() / length) * speed;
    }
}
