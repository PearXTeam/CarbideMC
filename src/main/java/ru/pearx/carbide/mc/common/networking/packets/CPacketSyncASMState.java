package ru.pearx.carbide.mc.common.networking.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.common.PXLCapabilities;
import ru.pearx.carbide.mc.common.caps.animation.IAnimationStateManager;
import ru.pearx.carbide.mc.common.networking.ByteBufTools;

/*
 * Created by mrAppleXZ on 21.09.17 12:14.
 */
public class CPacketSyncASMState implements IMessage
{
    public BlockPos pos;
    public int element;
    public int state;

    public CPacketSyncASMState()
    {
    }

    public CPacketSyncASMState(BlockPos pos, int element, int state)
    {
        this.pos = pos;
        this.element = element;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = ByteBufTools.readBlockPos(buf);
        element = buf.readInt();
        state = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufTools.writeBlockPos(buf, pos);
        buf.writeInt(element);
        buf.writeInt(state);
    }

    public static class Handler implements IMessageHandler<CPacketSyncASMState, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(CPacketSyncASMState message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos);
                if(te != null && te.hasCapability(PXLCapabilities.ASM, null))
                {
                    IAnimationStateManager manager = te.getCapability(PXLCapabilities.ASM, null);
                    manager.changeState(message.element, message.state);
                }
            });
            return null;
        }
    }
}
