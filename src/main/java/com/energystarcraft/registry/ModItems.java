package com.energystarcraft.registry;

import com.energystarcraft.EnergyStarcraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnergyStarcraft.MOD_ID)
public final class ModItems {
    public static final Item ENERGY_FORGE_ITEM = new ItemBlock(ModBlocks.ENERGY_FORGE)
            .setRegistryName("energy_forge")
            .setCreativeTab(ModCreativeTabs.MAIN_TAB);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ENERGY_FORGE_ITEM);
    }

    private ModItems() {
    }
}
