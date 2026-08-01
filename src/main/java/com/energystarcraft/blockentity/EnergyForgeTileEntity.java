package com.energystarcraft.blockentity;

import com.energystarcraft.EnergyStarcraft;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyForgeTileEntity extends TileEntity implements ITickable, IInventory {
    private static final int OUTPUT_SLOT = 0;

    private final ItemStackHandler outputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            EnergyForgeTileEntity.this.markDirty();
            EnergyForgeTileEntity.this.syncToClient();
        }

        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

    public final EnergyForgeEnergyStorage energyStorage = new EnergyForgeEnergyStorage(() -> {
        this.markDirty();
        this.syncToClient();
    });

    private ItemStack getOutputStack() {
        return this.outputHandler.getStackInSlot(OUTPUT_SLOT);
    }

    public boolean canPlaceNetherStar() {
        ItemStack output = getOutputStack();
        return output.isEmpty() || (output.getItem() == Items.NETHER_STAR && output.getCount() < output.getMaxStackSize());
    }

    public boolean isCrafting() {
        return this.energyStorage.getEnergyStored() >= EnergyStarcraft.NETHER_STAR_COST && this.canPlaceNetherStar();
    }

    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote && this.isCrafting()) {
            this.performCraft();
        }
    }

    private void performCraft() {
        if (this.energyStorage.getEnergyStored() < EnergyStarcraft.NETHER_STAR_COST || !this.canPlaceNetherStar()) {
            return;
        }

        int extracted = this.energyStorage.extractInternal(EnergyStarcraft.NETHER_STAR_COST);
        if (extracted < EnergyStarcraft.NETHER_STAR_COST) {
            return;
        }

        ItemStack output = getOutputStack();
        if (output.isEmpty()) {
            this.outputHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(Items.NETHER_STAR, 1));
        } else {
            this.outputHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(Items.NETHER_STAR, output.getCount() + 1));
        }

        this.markDirty();
        this.syncToClient();
        EnergyStarcraft.LOGGER.debug("Energy Forge at {} crafted a Nether Star!", this.pos);
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    private void syncToClient() {
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    public void dropContents(World world, BlockPos pos) {
        if (!world.isRemote) {
            ItemStack stack = this.outputHandler.getStackInSlot(OUTPUT_SLOT);
            if (!stack.isEmpty()) {
                Block.spawnAsEntity(world, pos, stack);
                this.outputHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Output", this.outputHandler.serializeNBT());
        this.energyStorage.writeEnergyToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.outputHandler.deserializeNBT(compound.getCompoundTag("Output"));
        this.energyStorage.readEnergyFromNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY
                || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this.energyStorage);
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.outputHandler);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getOutputStack().isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.outputHandler.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return this.outputHandler.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.outputHandler.getStackInSlot(index);
        this.outputHandler.setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.outputHandler.setStackInSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.outputHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
    }

    @Override
    public String getName() {
        return this.getDisplayName().getUnformattedText();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("gui.energystarcraft.energy_forge");
    }
}
