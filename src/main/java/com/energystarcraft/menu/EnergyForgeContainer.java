package com.energystarcraft.menu;

import com.energystarcraft.blockentity.EnergyForgeTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnergyForgeContainer extends Container {
    public static final int GUI_W = 176;
    public static final int GUI_H = 166;
    public static final int BAR_X = 8;
    public static final int BAR_Y = 17;
    public static final int BAR_W = 16;
    public static final int BAR_H = 52;
    public static final int DIVIDER_Y = 75;
    public static final int OUTPUT_SLOT_X = 110;
    public static final int OUTPUT_SLOT_Y = 31;
    public static final int INV_START_X = 8;
    public static final int INV_START_Y = 83;
    public static final int HOTBAR_Y = 141;
    public static final int MAX_ENERGY = 350_000_000;

    private final EnergyForgeTileEntity tileEntity;
    private int energyCache = -1;
    private int craftingCache = -1;

    @SideOnly(Side.CLIENT)
    private int energyReceived;
    @SideOnly(Side.CLIENT)
    private int craftingStatus;

    public EnergyForgeContainer(EntityPlayer player, EnergyForgeTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlotToContainer(new Slot(tileEntity, 0, OUTPUT_SLOT_X, OUTPUT_SLOT_Y) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(
                        player.inventory,
                        col + row * 9 + 9,
                        INV_START_X + col * 18,
                        INV_START_Y + row * 18
                ));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(player.inventory, col, INV_START_X + col * 18, HOTBAR_Y));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int energy = this.tileEntity.energyStorage.getEnergyStored();
        if (energy != this.energyCache) {
            this.energyCache = energy;
            for (IContainerListener listener : this.listeners) {
                listener.sendWindowProperty(this, 0, energy & 0xFF);
                listener.sendWindowProperty(this, 1, (energy >> 8) & 0xFF);
                listener.sendWindowProperty(this, 2, (energy >> 16) & 0xFF);
                listener.sendWindowProperty(this, 3, (energy >> 24) & 0xFF);
            }
        }
        int crafting = this.tileEntity.isCrafting() ? 1 : 0;
        if (crafting != this.craftingCache) {
            this.craftingCache = crafting;
            for (IContainerListener listener : this.listeners) {
                listener.sendWindowProperty(this, 4, crafting);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            this.energyReceived = 0;
        }
        if (id >= 0 && id <= 3) {
            this.energyReceived |= (data & 0xFF) << (id * 8);
        } else if (id == 4) {
            this.craftingStatus = data;
        }
    }

    @SideOnly(Side.CLIENT)
    public int getEnergyStored() {
        return this.energyReceived;
    }

    public int getMaxEnergyStored() {
        return MAX_ENERGY;
    }

    @SideOnly(Side.CLIENT)
    public int getEnergyPercent() {
        int max = getMaxEnergyStored();
        if (max == 0) {
            return 0;
        }
        return (int) ((long) getEnergyStored() * 100L / max);
    }

    @SideOnly(Side.CLIENT)
    public int getScaledEnergy(int height) {
        int max = getMaxEnergyStored();
        if (max == 0) {
            return 0;
        }
        return (int) ((long) getEnergyStored() * height / max);
    }

    @SideOnly(Side.CLIENT)
    public int getCraftingStatus() {
        return this.craftingStatus;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            boolean moved;
            if (index == 0) {
                moved = this.mergeItemStack(itemstack1, 1, 37, true);
            } else if (index < 28) {
                moved = this.mergeItemStack(itemstack1, 28, 37, false);
            } else {
                moved = this.mergeItemStack(itemstack1, 1, 28, false);
            }

            if (!moved) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }
}
