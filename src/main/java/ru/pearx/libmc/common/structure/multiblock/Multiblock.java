package ru.pearx.libmc.common.structure.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.lang3.tuple.Pair;
import ru.pearx.libmc.PXLMC;
import ru.pearx.libmc.common.blocks.PXLBlocks;
import ru.pearx.libmc.common.structure.blockarray.BlockArray;
import ru.pearx.libmc.common.structure.processors.IStructureProcessor;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * Created by mrAppleXZ on 14.11.17 13:34.
 */
public class Multiblock extends IForgeRegistryEntry.Impl<Multiblock>
{
    public static final IForgeRegistry<Multiblock> REGISTRY = new RegistryBuilder<Multiblock>().setName(new ResourceLocation(PXLMC.MODID, "multiblock")).setType(Multiblock.class).create();

    private BlockArray structure;
    private BlockPos masterPos;
    private IBlockState masterState;

    public Multiblock(BlockArray structure, BlockPos masterPos, IBlockState masterState)
    {
        this.structure = structure;
        this.masterPos = masterPos;
        this.masterState = masterState;
    }

    public Multiblock()
    {
    }

    public BlockArray getStructure()
    {
        return structure;
    }

    public void setStructure(BlockArray structure)
    {
        this.structure = structure;
    }

    public BlockPos getMasterPos()
    {
        return masterPos;
    }

    public void setMasterPos(BlockPos masterPos)
    {
        this.masterPos = masterPos;
    }

    public IBlockState getMasterState()
    {
        return masterState;
    }

    public void setMasterState(IBlockState masterState)
    {
        this.masterState = masterState;
    }

    public void form(World w, BlockPos zeroPos, Rotation rot)
    {
        BlockPos.MutableBlockPos absMasterPos = new BlockPos.MutableBlockPos(getMasterPos());
        absMasterPos = PXLMC.transformPos(absMasterPos, null, rot);
        absMasterPos.setPos(absMasterPos.getX() + zeroPos.getX(), absMasterPos.getY() + zeroPos.getY(), absMasterPos.getZ() + zeroPos.getZ());

        BlockPos.MutableBlockPos absPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos relPos = new BlockPos.MutableBlockPos();
        IBlockState slaveState = PXLBlocks.multiblock_slave.getDefaultState();
        for (BlockPos p : getStructure().getMap().keySet())
        {
            relPos.setPos(p.getX(), p.getY(), p.getZ());
            relPos = PXLMC.transformPos(relPos, null, rot);
            absPos.setPos(relPos.getX() + zeroPos.getX(), relPos.getY() + zeroPos.getY(), relPos.getZ() + zeroPos.getZ());
            if (!p.equals(getMasterPos()))
            {
                w.setBlockState(absPos, slaveState);
                TileEntity te = w.getTileEntity(absPos);
                if (te != null && te instanceof IMultiblockSlave)
                {
                    ((IMultiblockSlave) te).setMasterPos(absMasterPos);
                }
            }
        }

        w.setBlockState(absMasterPos, getMasterState());
        TileEntity te = w.getTileEntity(absMasterPos);
        if (te != null && te instanceof IMultiblockMaster)
        {
            IMultiblockMaster master = (IMultiblockMaster) te;
            master.setRotation(rot);
            master.setSlavesPositions(getStructure().getMap().keySet().stream().filter((pos) -> !pos.equals(getMasterPos())).collect(Collectors.toList()));
        }
    }

    public Optional<Rotation> checkStructure(World w, BlockPos pos)
    {
        return getStructure().check(w, pos);
    }

    public Optional<Rotation> tryForm(World w, BlockPos pos)
    {
        Optional<Rotation> opt = checkStructure(w, pos);
        opt.ifPresent(rotation -> form(w, pos, rotation));
        return opt;
    }

    public static <T>T sendEventToMaster(World world, BlockPos pos, IMultiblockEvent<T> evt, T def)
    {
        TileEntity te = world.getTileEntity(pos);
        if(te != null && te instanceof IMultiblockSlave)
        {
            IMultiblockSlave slave = (IMultiblockSlave) te;
            TileEntity master = te.getWorld().getTileEntity(slave.getMasterPos());
            if(master != null && master instanceof IMultiblockMaster)
            {
                return ((IMultiblockMaster) master).handleEvent(evt, slave);
            }
        }
        return def;
    }
}