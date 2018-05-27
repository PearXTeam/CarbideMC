package ru.pearx.carbide.mc.common.misc;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;

/*
 * Created by mrAppleXZ on 26.05.18 22:58.
 */
public final class CoordUtils
{
    private CoordUtils() {}

    public static BlockPos parseCoords(String s)
    {
        try
        {
            String[] from = s.split(" ");
            if (from.length == 3)
            {
                return new BlockPos(Integer.parseInt(from[0]), Integer.parseInt(from[1]), Integer.parseInt(from[2]));
            }
        }
        catch(NumberFormatException ex)
        {
            return null;
        }
        return null;
    }

    public static int getHorizontalDegrees(EnumFacing face)
    {
        switch (face)
        {
            case NORTH: return 0;
            case WEST: return 90;
            case SOUTH: return 180;
            case EAST: return 270;
            default: return 0;
        }
    }

    public static int getHorizontalDegrees(Rotation rot)
    {
        switch (rot)
        {
            case NONE: return 180;
            case CLOCKWISE_90: return 90;
            case CLOCKWISE_180: return 0;
            case COUNTERCLOCKWISE_90: return 270;
            default: return 0;
        }
    }

    public static BlockPos.MutableBlockPos transformPos(BlockPos.MutableBlockPos pos, @Nullable Mirror mir, @Nullable Rotation rot)
    {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        boolean flag = true;
        if(mir != null)
        {
            switch (mir)
            {
                case LEFT_RIGHT:
                    pos.setPos(x, y, -z);
                    break;
                case FRONT_BACK:
                    pos.setPos(-x, y, z);
                    break;
                default:
                    flag = false;
                    break;
            }
        }
        if(flag)
        {
            x = pos.getX();
            y = pos.getY();
            z = pos.getZ();
        }
        if(rot != null)
        {
            switch (rot)
            {
                case CLOCKWISE_90:
                    pos.setPos(-z, y, x);
                    break;
                case CLOCKWISE_180:
                    pos.setPos(-x, y, -z);
                    break;
                case COUNTERCLOCKWISE_90:
                    pos.setPos(z, y, -x);
                    break;
            }
        }
        return pos;
    }

    public static BlockPos transformPos(BlockPos pos, @Nullable Mirror mir, @Nullable Rotation rot)
    {
        return transformPos(new BlockPos.MutableBlockPos(pos), mir, rot);
    }

    public static Vector3d transformVec(Vector3d vec, @Nullable Mirror mir, @Nullable Rotation rot)
    {
        double x = vec.getX(), y = vec.getY(), z = vec.getZ();
        boolean flag = true;
        if(mir != null)
        {
            switch (mir)
            {
                case LEFT_RIGHT:
                    vec.setZ(-z);
                    break;
                case FRONT_BACK:
                    vec.setX(-x);
                    break;
                default:
                    flag = false;
                    break;
            }
        }
        if(flag)
        {
            x = vec.getX();
            y = vec.getY();
            z = vec.getZ();
        }
        if(rot != null)
        {
            switch (rot)
            {
                case CLOCKWISE_90:
                    vec.set(-z, y, x);
                    break;
                case CLOCKWISE_180:
                    vec.set(-x, y, -z);
                    break;
                case COUNTERCLOCKWISE_90:
                    vec.set(z, y, -x);
                    break;
            }
        }
        return vec;
    }

    public static EnumFacing.Axis rotateAxis(EnumFacing.Axis ax, Rotation rot)
    {
        switch (rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (ax)
                {
                    case X:
                        return EnumFacing.Axis.Z;
                    case Z:
                        return EnumFacing.Axis.X;
                }
                break;
        }
        return ax;
    }

    public static Rotation getReverseRotation(Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_90:
                return Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90:
                return Rotation.CLOCKWISE_90;
            case CLOCKWISE_180:
                return Rotation.CLOCKWISE_180;
            default:
                return rot;
        }
    }

    public static Rotation rotationFromFacing(EnumFacing facing)
    {
        switch (facing)
        {
            case NORTH:
                return Rotation.NONE;
            case EAST:
                return Rotation.CLOCKWISE_90;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case WEST:
                return Rotation.COUNTERCLOCKWISE_90;
        }
        return Rotation.NONE;
    }
}
