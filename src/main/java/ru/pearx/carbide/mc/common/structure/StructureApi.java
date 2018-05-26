package ru.pearx.carbide.mc.common.structure;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;
import ru.pearx.carbide.mc.CarbideMC;
import ru.pearx.carbide.mc.common.structure.processors.IStructureProcessor;
import ru.pearx.carbide.mc.common.structure.processors.StructureProcessor;
import ru.pearx.carbide.mc.common.structure.processors.StructureProcessorData;
import ru.pearx.carbide.ResourceUtils;
import ru.pearx.carbide.mc.common.blocks.PXLBlocks;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * Created by mrAppleXZ on 20.08.17 23:19.
 */
public enum StructureApi
{
    INSTANCE;

    public final void createStructure(String name, BlockPos from, BlockPos to, World world, List<Pair<ResourceLocation, StructureProcessorData>> procs)
    {
        int frx = from.getX(), fry = from.getY(), frz = from.getZ(), tx = to.getX(), ty = to.getY(), tz = to.getZ();
        if (from.getX() > to.getX())
        {
            frx = to.getX();
            tx = from.getX();
        }
        if (from.getY() > to.getY())
        {
            fry = to.getY();
            ty = from.getY();
        }
        if (from.getZ() > to.getZ())
        {
            frz = to.getZ();
            tz = from.getZ();
        }
        from = new BlockPos(frx, fry, frz);
        to = new BlockPos(tx, ty, tz);

        NBTTagCompound root = new NBTTagCompound();
        BlockPos size = to.subtract(from);
        root.setInteger("sizeX", size.getX());
        root.setInteger("sizeY", size.getY());
        root.setInteger("sizeZ", size.getZ());
        BlockPos centerPos = new BlockPos((to.getX() - from.getX()) / 2 + from.getX(), from.getY(), (to.getZ() - from.getZ()) / 2 + from.getZ());

        {
            NBTTagList blocks = new NBTTagList();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos relativePos = new BlockPos.MutableBlockPos();
            for (int x = from.getX(); x <= to.getX(); x++)
            {
                for (int y = from.getY(); y <= to.getY(); y++)
                {
                    for (int z = from.getZ(); z <= to.getZ(); z++)
                    {
                        pos.setPos(x, y, z);
                        relativePos.setPos(pos.getX() - centerPos.getX(), pos.getY() - centerPos.getY(), pos.getZ() - centerPos.getZ());
                        IBlockState state = world.getBlockState(pos);
                        if (state.getBlock() == PXLBlocks.structure_nothing)
                            continue;
                        blocks.appendTag(BlockUtils.serializeBlock(state, relativePos, world.getTileEntity(pos)));
                    }
                }
            }
            root.setTag("blocks", blocks);
        }
        {
            NBTTagList entities = new NBTTagList();
            for(Entity e : world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(from, to.add(1, 1, 1))))
            {
                if(!(e instanceof EntityPlayer))
                {
                    NBTTagCompound tag = e.serializeNBT();
                    NBTTagList pos = tag.getTagList("Pos", Constants.NBT.TAG_DOUBLE);
                    pos.set(0, new NBTTagDouble(pos.getDoubleAt(0) - centerPos.getX()));
                    pos.set(1, new NBTTagDouble(pos.getDoubleAt(1) - centerPos.getY()));
                    pos.set(2, new NBTTagDouble(pos.getDoubleAt(2) - centerPos.getZ()));
                    tag.removeTag("Dimension");
                    tag.removeTag("UUIDMost");
                    tag.removeTag("UUIDLeast");
                    if(tag.hasKey("TileX", Constants.NBT.TAG_INT))
                        tag.setInteger("TileX", tag.getInteger("TileX") - centerPos.getX());
                    if(tag.hasKey("TileY", Constants.NBT.TAG_INT))
                        tag.setInteger("TileY", tag.getInteger("TileY") - centerPos.getY());
                    if(tag.hasKey("TileZ", Constants.NBT.TAG_INT))
                        tag.setInteger("TileZ", tag.getInteger("TileZ") - centerPos.getZ());
                    entities.appendTag(tag);
                }
            }
            root.setTag("entities", entities);
        }
        {
            NBTTagList lst = new NBTTagList();
            for(Pair<ResourceLocation, StructureProcessorData> p : procs)
            {
                StructureProcessorData dat = p.getRight();
                NBTTagCompound tag = dat.serialize();
                tag.setInteger("x", dat.getAbsolutePos().getX() - centerPos.getX());
                tag.setInteger("y", dat.getAbsolutePos().getY() - centerPos.getY());
                tag.setInteger("z", dat.getAbsolutePos().getZ() - centerPos.getZ());
                tag.setString("processor", p.getLeft().toString());
                lst.appendTag(tag);
            }
            root.setTag("processors", lst);
        }

        Path p = Paths.get("carbide_structures", name + ".dat");
        if(Files.notExists(p.getParent()))
            try
            {
                Files.createDirectory(p.getParent());
            }
            catch (IOException e)
            {
                CarbideMC.getLog().error("Can't create " + p.getParent() + " directory!", e);
            }
        try(OutputStream str = Files.newOutputStream(p))
        {
            CompressedStreamTools.writeCompressed(root, str);
        }
        catch (IOException e)
        {
            CarbideMC.getLog().error("An IOException occurred when creating the " + p + " structure file!", e);
        }
    }

    public NBTTagCompound getStructureNbt(String fileName) throws IOException
    {
        try(InputStream str = Files.newInputStream(Paths.get("carbide_structures", fileName + ".dat")))
        {
            return CompressedStreamTools.readCompressed(str);
        }
    }

    public  NBTTagCompound getStructureNbt(ResourceLocation loc) throws IOException
    {
        String s = "assets/" + loc.getResourceDomain() + "/structures/" + loc.getResourcePath() + ".dat";
        try(InputStream str = CarbideMC.class.getClassLoader().getResourceAsStream(s))
        {
            if(str == null)
                throw new FileNotFoundException("Can't find the structure file at " + s + "!");
            return CompressedStreamTools.readCompressed(str);
        }
    }

    public Vec3i getStructureSize(NBTTagCompound tag)
    {
        return new Vec3i(tag.getInteger("sizeX"), tag.getInteger("sizeY"), tag.getInteger("sizeZ"));
    }

    public void spawnStructure(NBTTagCompound tag, BlockPos at, @Nullable Mirror mir, @Nullable Rotation rot, WorldServer w, Random rand)
    {
        {
            NBTTagList blocks = tag.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
            BlockPos.MutableBlockPos absPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos relPos = new BlockPos.MutableBlockPos();
            Map<String, Block> bcache = new HashMap<>();
            for (NBTBase base : blocks)
            {
                NBTTagCompound block = (NBTTagCompound) base;
                relPos.setPos(block.getInteger("x"), block.getInteger("y"), block.getInteger("z"));
                relPos = CarbideMC.transformPos(relPos, mir, rot);
                absPos.setPos(at.getX() + relPos.getX(), at.getY() + relPos.getY(), at.getZ() + relPos.getZ());


                BlockPair pair = BlockUtils.deserializeBlock(block, absPos, bcache, w);
                if(mir != null) pair.setState(pair.getState().withMirror(mir));
                if(rot != null) pair.setState(pair.getState().withRotation(rot));
                w.setBlockState(absPos, pair.getState(), 2);
                if(pair.getTile() != null)
                {
                    if (mir != null) pair.getTile().mirror(mir);
                    if (rot != null) pair.getTile().rotate(rot);
                }
                w.setTileEntity(absPos, pair.getTile());
            }
        }
        {
            NBTTagList entities = tag.getTagList("entities", Constants.NBT.TAG_COMPOUND);
            Vector3d relVec = new Vector3d();
            BlockPos.MutableBlockPos relTile = new BlockPos.MutableBlockPos();
            for(NBTBase nbt : entities)
            {
                NBTTagCompound entity = (NBTTagCompound) nbt;
                entity.setInteger("Dimension", w.provider.getDimension());
                NBTTagList pos = entity.getTagList("Pos", Constants.NBT.TAG_DOUBLE);
                relVec.set(pos.getDoubleAt(0), pos.getDoubleAt(1), pos.getDoubleAt(2));
                relVec = CarbideMC.transformVec(relVec, mir, rot);
                pos.set(0, new NBTTagDouble(relVec.getX() + at.getX()));
                pos.set(1, new NBTTagDouble(relVec.getY() + at.getY()));
                pos.set(2, new NBTTagDouble(relVec.getZ() + at.getZ()));
                if(entity.hasKey("TileX", Constants.NBT.TAG_INT) && entity.hasKey("TileY", Constants.NBT.TAG_INT) && entity.hasKey("TileZ", Constants.NBT.TAG_INT))
                {
                    relTile.setPos(entity.getInteger("TileX"), entity.getInteger("TileY"), entity.getInteger("TileZ"));
                    relTile = CarbideMC.transformPos(relTile, mir, rot);
                    entity.setInteger("TileX", relTile.getX() + at.getX());
                    entity.setInteger("TileY", relTile.getY() + at.getY());
                    entity.setInteger("TileZ", relTile.getZ() + at.getZ());
                }

                Entity e = EntityList.createEntityFromNBT(entity, w);
                if(e != null)
                {
                    //hack
                    if(e instanceof EntityHanging)
                    {
                        EnumFacing face = ((EntityHanging) e).facingDirection;
                        if(face != null)
                        {
                            if (mir != null)
                                face = mir.mirror(face);
                            if (rot != null)
                                face = rot.rotate(face);
                        }
                        ((EntityHanging) e).updateFacingWithBoundingBox(face);
                    }
                    else
                    {
                        if (mir != null)
                            e.rotationYaw = e.getMirroredYaw(mir);
                        if (rot != null)
                            e.rotationYaw = e.getRotatedYaw(rot);
                    }
                    w.spawnEntity(e);
                }
            }
        }
        {
            NBTTagList procs = tag.getTagList("processors", Constants.NBT.TAG_COMPOUND);
            BlockPos.MutableBlockPos relPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos absPos = new BlockPos.MutableBlockPos();
            for(NBTBase nbt : procs)
            {
                NBTTagCompound proc = (NBTTagCompound) nbt;
                IStructureProcessor processor = StructureProcessor.REGISTRY.getValue(new ResourceLocation(proc.getString("processor")));

                relPos.setPos(proc.getInteger("x"), proc.getInteger("y"), proc.getInteger("z"));
                relPos = CarbideMC.transformPos(relPos, mir, rot);
                absPos.setPos(relPos.getX() + at.getX(), relPos.getY() + at.getY(), relPos.getZ() + at.getZ());
                StructureProcessorData dat = processor.loadData(proc, absPos);
                processor.process(dat, w, rand);
            }
        }
    }
}
