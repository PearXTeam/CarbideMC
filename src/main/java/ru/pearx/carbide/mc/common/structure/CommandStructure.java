package ru.pearx.carbide.mc.common.structure;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import ru.pearx.carbide.mc.CarbideMC;
import ru.pearx.carbide.mc.common.networking.packets.CPacketOpenStructureCreationGui;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Created by mrAppleXZ on 21.08.17 0:24.
 */
public class CommandStructure extends CommandBase
{
    @Override
    public String getName()
    {
        return "cstructure";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "command.cstructure.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(args.length >= 1)
        {
            switch (args[0])
            {
                case "get":
                {
                    if(sender instanceof EntityPlayerMP)
                    {
                        CarbideMC.NETWORK.sendTo(new CPacketOpenStructureCreationGui(), (EntityPlayerMP) sender);
                    }
                    break;
                }
                case "spawn":
                {
                    if (args.length >= 5)
                    {
                        String name = args[1];
                        BlockPos pos = parseBlockPos(sender, args, 2, false);
                        Mirror mir = args.length >= 6 ? parseMirror(args[5]) : Mirror.NONE;
                        Rotation rot = args.length >= 7 ? parseRotation(args[6]) : Rotation.NONE;
                        NBTTagCompound tag;
                        try
                        {
                            try
                            {
                                tag = StructureApi.INSTANCE.getStructureNbt(new ResourceLocation(name));
                            }
                            catch(FileNotFoundException e)
                            {
                                tag = StructureApi.INSTANCE.getStructureNbt(name);
                            }
                        }
                        catch (IOException e)
                        {
                            CarbideMC.getLog().error("An IOException occurred when spawning a structure.", e);
                            throw new CommandException("command.cstructure.ioexception", e.toString());
                        }
                        StructureApi.INSTANCE.spawnStructure(tag, pos, mir, rot, (WorldServer)sender.getEntityWorld(), sender.getEntityWorld().rand);
                        notifyCommandListener(sender, this, "command.cstructure.success");
                        return;
                    }
                }
            }
        }
        throw new WrongUsageException(getUsage(sender));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if(args.length == 1)
        {
            return Arrays.asList("get", "spawn");
        }
        switch (args[0])
        {
            case "spawn":
                if(args.length >= 3 && args.length <= 5)
                {
                    return getTabCompletionCoordinate(args, 2, targetPos);
                }
                break;
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    private Mirror parseMirror(String s)
    {
        switch (s)
        {
            case "none":
                return Mirror.NONE;
            case "left_right":
                return Mirror.LEFT_RIGHT;
            case "front_back":
                return Mirror.FRONT_BACK;
            default:
                return Mirror.NONE;
        }
    }

    private Rotation parseRotation(String s)
    {
        switch (s)
        {
            case "0":
                return Rotation.NONE;
            case "90":
                return Rotation.CLOCKWISE_90;
            case "180":
                return Rotation.CLOCKWISE_180;
            case "270":
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                return Rotation.NONE;
        }
    }
}
