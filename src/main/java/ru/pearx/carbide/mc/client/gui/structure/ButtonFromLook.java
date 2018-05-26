package ru.pearx.carbide.mc.client.gui.structure;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import ru.pearx.carbide.mc.CarbideMC;
import ru.pearx.carbide.mc.client.gui.controls.common.Button;
import ru.pearx.carbide.mc.client.gui.controls.common.TextBox;

/*
 * Created by mrAppleXZ on 28.09.17 21:02.
 */
public class ButtonFromLook extends Button
{
    public ButtonFromLook(TextBox box)
    {
        super(new ResourceLocation(CarbideMC.MODID, "textures/gui/button.png"), I18n.format("misc.gui.structure.looking_at"), () ->
        {
            BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
            box.setText(pos.getX() + " " + pos.getY() + " " + pos.getZ());
        });
        setSize(box.getWidth(), box.getHeight());
        setPos(box.getX(), box.getY() + box.getHeight());
    }
}