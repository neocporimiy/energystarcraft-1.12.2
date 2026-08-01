package com.energystarcraft.blockentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class EnergyForgeEnergyStorage extends EnergyStorage {
    public static final int MAX_CAPACITY = 350_000_000;
    public static final int MAX_RECEIVE = 1_000_000;
    private final Runnable onChanged;

    public EnergyForgeEnergyStorage(Runnable onChanged) {
        super(MAX_CAPACITY, MAX_RECEIVE, 0);
        this.onChanged = onChanged;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received > 0) {
            this.onChanged.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && extracted > 0) {
            this.onChanged.run();
        }
        return extracted;
    }

    public int extractInternal(int amount) {
        int extracted = Math.min(getEnergyStored(), amount);
        if (extracted > 0) {
            this.energy = getEnergyStored() - extracted;
            this.onChanged.run();
        }
        return extracted;
    }

    public void writeEnergyToNBT(NBTTagCompound compound) {
        compound.setInteger("Energy", getEnergyStored());
    }

    public void readEnergyFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("Energy")) {
            this.energy = Math.min(MAX_CAPACITY, Math.max(0, compound.getInteger("Energy")));
        }
    }
}
