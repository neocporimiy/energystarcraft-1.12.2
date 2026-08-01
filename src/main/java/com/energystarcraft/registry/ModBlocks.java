package com.energystarcraft.registry;

import com.energystarcraft.EnergyStarcraft;
import com.energystarcraft.block.EnergyForgeBlock;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnergyStarcraft.MOD_ID)
public final class ModBlocks {
    public static final Block ENERGY_FORGE = new EnergyForgeBlock()
            .setRegistryName("energy_forge")
            .setUnlocalizedName("energystarcraft.energy_forge");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ENERGY_FORGE);
    }

    private ModBlocks() {
    }
}
