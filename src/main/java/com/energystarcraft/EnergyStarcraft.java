package com.energystarcraft;

import com.energystarcraft.blockentity.EnergyForgeTileEntity;
import com.energystarcraft.menu.EnergyForgeContainer;
import com.energystarcraft.registry.ModBlockEntities;
import com.energystarcraft.screen.EnergyForgeScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = EnergyStarcraft.MOD_ID,
        name = "Energy Starcraft",
        version = "1.0.1",
        acceptedMinecraftVersions = "[1.12.2]"
)
public class EnergyStarcraft implements IGuiHandler {
    public static final String MOD_ID = "energystarcraft";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final int NETHER_STAR_COST = 350_000_000;
    public static final int GUI_ID = 0;

    @Instance
    public static EnergyStarcraft INSTANCE;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModBlockEntities.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, this);
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == GUI_ID) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof EnergyForgeTileEntity) {
                return new EnergyForgeContainer(player, (EnergyForgeTileEntity) te);
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == GUI_ID) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof EnergyForgeTileEntity) {
                return new EnergyForgeScreen(player, (EnergyForgeTileEntity) te);
            }
        }
        return null;
    }
}
