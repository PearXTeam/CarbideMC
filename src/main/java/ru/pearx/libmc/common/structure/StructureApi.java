package ru.pearx.libmc.common.structure;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import ru.pearx.lib.ResourceUtils;
import ru.pearx.libmc.PXLMC;
import ru.pearx.libmc.common.blocks.PXLBlocks;
import ru.pearx.libmc.common.structure.processors.IStructureProcessor;
import ru.pearx.libmc.common.structure.processors.StructureProcessor;
import ru.pearx.libmc.common.structure.processors.StructureProcessorData;

import javax.annotation.Nullable;
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
                        NBTTagCompound block = new NBTTagCompound();
                        block.setInteger("x", relativePos.getX());
                        block.setInteger("y", relativePos.getY());
                        block.setInteger("z", relativePos.getZ());//
                        block.setString("id", state.getBlock().getRegistryName().toString());
                        block.setInteger("meta", state.getBlock().getMetaFromState(state));

                        TileEntity te = world.getTileEntity(pos);
                        if (te != null)
                        {
                            NBTTagCompound tiletag = te.serializeNBT();
                            tiletag.removeTag("x");
                            tiletag.removeTag("y");
                            tiletag.removeTag("z");
                            block.setTag("tile", tiletag);
                        }

                        blocks.appendTag(block);
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

        Path p = Paths.get("pxlmc_structures", name + ".dat");
        if(Files.notExists(p.getParent()))
            try
            {
                Files.createDirectory(p.getParent());
            }
            catch (IOException e)
            {
                PXLMC.getLog().error("Can't create " + p.getParent() + " directory!", e);
            }
        try(OutputStream str = Files.newOutputStream(p))
        {
            CompressedStreamTools.writeCompressed(root, str);
        }
        catch (IOException e)
        {
            PXLMC.getLog().error("An IOException occurred when creating the " + p + " structure file!", e);
        }
    }

    public NBTTagCompound getStructureNbt(String fileName) throws IOException
    {
        try(InputStream str = Files.newInputStream(Paths.get("pxlmc_structures", fileName + ".dat")))
        {
            return CompressedStreamTools.readCompressed(str);
        }
    }

    public  NBTTagCompound getStructureNbt(ResourceLocation loc) throws IOException
    {
        String s = "assets/" + loc.getResourceDomain() + "/structures/" + loc.getResourcePath() + ".dat";
        try(InputStream str = ResourceUtils.getResource(s))
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
                relPos = PXLMC.transformPos(relPos, mir, rot);
                absPos.setPos(at.getX() + relPos.getX(), at.getY() + relPos.getY(), at.getZ() + relPos.getZ());
                String id = block.getString("id");
                if(!bcache.containsKey(id))
                    bcache.put(id, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id)));
                Block b = bcache.get(id);
                IBlockState state = b.getStateFromMeta(block.getInteger("meta"));
                if(mir != null) state = state.withMirror(mir);
                if(rot != null) state = state.withRotation(rot);
                w.setBlockState(absPos, state, 2);
                if (block.hasKey("tile"))
                {
                    NBTTagCompound tile = block.getCompoundTag("tile");
                    tile.setInteger("x", absPos.getX());
                    tile.setInteger("y", absPos.getY());
                    tile.setInteger("z", absPos.getZ());
                    TileEntity te = TileEntity.create(w, tile);
                    if(te != null)
                    {
                        if (mir != null) te.mirror(mir);
                        if (rot != null) te.rotate(rot);
                    }
                    w.setTileEntity(absPos, te);
                }
            }
        }
        {
            NBTTagList entities = tag.getTagList("entities", Constants.NBT.TAG_COMPOUND);
            for(NBTBase nbt : entities)
            {
                NBTTagCompound entity = (NBTTagCompound) nbt;
                entity.setInteger("Dimension", w.provider.getDimension());
                NBTTagList pos = entity.getTagList("Pos", Constants.NBT.TAG_DOUBLE);
                pos.set(0, new NBTTagDouble(pos.getDoubleAt(0) + at.getX()));
                pos.set(1, new NBTTagDouble(pos.getDoubleAt(1) + at.getY()));
                pos.set(2, new NBTTagDouble(pos.getDoubleAt(2) + at.getZ()));
                if(entity.hasKey("TileX", Constants.NBT.TAG_INT))
                    entity.setInteger("TileX", entity.getInteger("TileX") + at.getX());
                if(entity.hasKey("TileY", Constants.NBT.TAG_INT))
                    entity.setInteger("TileY", entity.getInteger("TileY") + at.getY());
                if(entity.hasKey("TileZ", Constants.NBT.TAG_INT))
                    entity.setInteger("TileZ", entity.getInteger("TileZ") + at.getZ());

                Entity e = EntityList.createEntityFromNBT(entity, w);
                if(e != null)
                    w.spawnEntity(e);
            }
        }
        {
            NBTTagList procs = tag.getTagList("processors", Constants.NBT.TAG_COMPOUND);
            for(NBTBase nbt : procs)
            {
                NBTTagCompound proc = (NBTTagCompound) nbt;
                IStructureProcessor processor = StructureProcessor.REGISTRY.getValue(new ResourceLocation(proc.getString("processor")));
                BlockPos absPos = new BlockPos(proc.getInteger("x") + at.getX(), proc.getInteger("y") + at.getY(), proc.getInteger("z") + at.getZ());
                StructureProcessorData dat = processor.loadData(proc, absPos);
                processor.process(dat, w, rand);
            }
        }
    }
}
