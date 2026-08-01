package com.energystarcraft.registry;

import com.energystarcraft.EnergyStarcraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnergyStarcraft.MOD_ID, value = Side.CLIENT)
public final class ModClientModels {
    @SubscribeEvent
    public static void registerItemModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                ModItems.ENERGY_FORGE_ITEM, 0,
                new ModelResourceLocation(EnergyStarcraft.MOD_ID + ":energy_forge", "inventory"));
    }

    private ModClientModels() {
    }
}
