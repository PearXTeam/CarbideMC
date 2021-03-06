package ru.pearx.carbide.mc.client.gui.drawables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.carbide.mc.client.gui.IGuiScreen;

/**
 * Created by mrAppleXZ on 23.04.17 12:55.
 */
@SideOnly(Side.CLIENT)
public class AnimatedDrawable implements IGuiDrawable
{
    private int elemW, elemH, texElemW, texElemH, texW, texH, msDivider;
    private int cycle = 0;
    private int totalCycles;
    private ResourceLocation tex;
    private int xOfffset, yOffset;

    public AnimatedDrawable(ResourceLocation tex, int elemW, int elemH, int texElemW, int texElemH, int texW, int texH, int msDivider)
    {
        this(tex, elemW, elemH, texElemW, texElemH, texW, texH, msDivider, 0, 0);
    }

    public AnimatedDrawable(ResourceLocation tex, int elemW, int elemH, int texElemW, int texElemH, int texW, int texH, int msDivider, int xOffset, int yOffset)
    {
        this.elemW = elemW;
        this.elemH = elemH;
        this.texW = texW;
        this.texH = texH;
        this.tex = tex;
        this.texElemW = texElemW;
        this.texElemH = texElemH;
        this.msDivider = msDivider;
        totalCycles = texH / texElemH;
        this.xOfffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public int getWidth()
    {
        return elemW;
    }

    @Override
    public int getHeight()
    {
        return elemH;
    }

    @Override
    public void draw(IGuiScreen screen, int x, int y)
    {
        x += xOfffset;
        y += yOffset;
        cycle = (int)(System.currentTimeMillis() / msDivider % totalCycles);

        Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
        GuiScreen.drawScaledCustomSizeModalRect(x, y, 0, cycle * texElemH, texElemW, texElemH, elemW, elemH, texW, texH);
    }
}
