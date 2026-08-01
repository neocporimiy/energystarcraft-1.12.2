package com.energystarcraft.registry;

import com.energystarcraft.blockentity.EnergyForgeTileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlockEntities {
    public static void register() {
        GameRegistry.registerTileEntity(EnergyForgeTileEntity.class, "energystarcraft:energy_forge");
    }

    private ModBlockEntities() {
    }
}
