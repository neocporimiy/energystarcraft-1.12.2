package com.energystarcraft.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public final class ModCreativeTabs {
    public static final CreativeTabs MAIN_TAB = new CreativeTabs("energystarcraft.main") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.ENERGY_FORGE_ITEM);
        }
    };

    private ModCreativeTabs() {
    }
}
