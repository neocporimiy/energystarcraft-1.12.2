package com.energystarcraft.block;

import com.energystarcraft.EnergyStarcraft;
import com.energystarcraft.blockentity.EnergyForgeTileEntity;
import com.energystarcraft.registry.ModCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyForgeBlock extends Block {
    public EnergyForgeBlock() {
        super(Material.IRON);
        this.setHardness(3.5F);
        this.setResistance(6.0F);
        this.setSoundType(SoundType.METAL);
        this.setLightLevel(7.0F / 15.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setCreativeTab(ModCreativeTabs.MAIN_TAB);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new EnergyForgeTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(EnergyStarcraft.INSTANCE, EnergyStarcraft.GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof EnergyForgeTileEntity) {
            ((EnergyForgeTileEntity) te).dropContents(world, pos);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
