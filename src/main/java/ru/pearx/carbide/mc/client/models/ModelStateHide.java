package ru.pearx.carbide.mc.client.models;

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/*
 * Created by mrAppleXZ on 20.09.17 22:27.
 */
public class ModelStateHide implements IModelState
{
    private List<String> groupsToHide;
    private boolean invert = false;

    public ModelStateHide(String... groups)
    {
        groupsToHide = Arrays.asList(groups);
    }
    public ModelStateHide(boolean invert, String... groups)
    {
        groupsToHide = Arrays.asList(groups);
        this.invert = invert;
    }

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if(part.isPresent())
        {
            Iterator<String> it = Models.getParts(part.get());
            while (it.hasNext())
            {
                String s = it.next();
                boolean keep = invert;
                for(String cond : groupsToHide)
                {
                    if((s.startsWith(cond + "_") || s.equals(cond)) == !invert)
                    {
                        keep = !keep;
                        break;
                    }
                }
                return keep ? Optional.empty() : Optional.of(TRSRTransformation.identity());
            }
        }
        //keep the part
        return Optional.empty();
    }
}
