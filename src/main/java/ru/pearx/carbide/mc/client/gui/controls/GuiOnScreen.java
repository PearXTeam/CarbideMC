package ru.pearx.carbide.mc.client.gui.controls;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by mrAppleXZ on 07.06.17 20:51.
 */
@SideOnly(Side.CLIENT)
public class GuiOnScreen extends Control
{
    @Override
    public int getX()
    {
        return (getGuiScreen().getWidth() - getWidth()) / 2;
    }

    @Override
    public int getY()
    {
        return (getGuiScreen().getHeight() - getHeight()) / 2;
    }
}
