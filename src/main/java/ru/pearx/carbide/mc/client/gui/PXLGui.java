package ru.pearx.carbide.mc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.client.gui.controls.Control;
import ru.pearx.carbide.mc.client.gui.controls.GuiControlContainer;

import java.io.IOException;

/**
 * Created by mrAppleXZ on 16.04.17 20:02.
 */
@SideOnly(Side.CLIENT)
public class PXLGui extends GuiScreen implements IGuiScreen
{
    public GuiControlContainer gui;

    public PXLGui(GuiControlContainer cg)
    {
        gui = cg;
    }

    public PXLGui(Control cont)
    {
        gui = new GuiControlContainer(cont, this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        if(gui != null)
        {
            gui.invokeRender(0);
            gui.invokeRender2();
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();

        SharedGuiMethods.handleKeyboardInput(gui);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        SharedGuiMethods.keyTyped( gui, typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        SharedGuiMethods.handleMouseInput(this, gui);
    }

    @Override
    public void drawTooltip(ItemStack stack, int x, int y)
    {
        SharedGuiMethods.drawTooltip(stack, x, y, this::renderToolTip);
    }

    @Override
    public void drawHovering(String text, int x, int y)
    {
        SharedGuiMethods.drawHovering(text, x, y, this::drawHoveringText);
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getMouseX()
    {
        return SharedGuiMethods.getMouseX(this);
    }

    @Override
    public int getMouseY()
    {
        return SharedGuiMethods.getMouseY(this);
    }

    @Override
    public RenderItem getRenderItem()
    {
        return itemRender;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        gui.invokeClose();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        gui.invokeInit();
    }

    @Override
    public void updateScreen()
    {
        gui.invokeUpdate();
    }

    @Override
    public void close()
    {
        if(Minecraft.getMinecraft().currentScreen == this)
            Minecraft.getMinecraft().displayGuiScreen(null);
    }
}
