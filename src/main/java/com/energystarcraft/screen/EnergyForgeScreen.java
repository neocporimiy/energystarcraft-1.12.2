package com.energystarcraft.screen;

import com.energystarcraft.blockentity.EnergyForgeTileEntity;
import com.energystarcraft.menu.EnergyForgeContainer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class EnergyForgeScreen extends GuiContainer {

    private static final NumberFormat NUM = NumberFormat.getNumberInstance(Locale.US);

    private static final int W = EnergyForgeContainer.GUI_W;
    private static final int H = EnergyForgeContainer.GUI_H;

    private static final int BAR_X = EnergyForgeContainer.BAR_X;
    private static final int BAR_Y = EnergyForgeContainer.BAR_Y;
    private static final int BAR_W = EnergyForgeContainer.BAR_W;
    private static final int BAR_H = EnergyForgeContainer.BAR_H;

    private static final int OUT_X = EnergyForgeContainer.OUTPUT_SLOT_X;
    private static final int OUT_Y = EnergyForgeContainer.OUTPUT_SLOT_Y;

    private static final int INV_START_X = EnergyForgeContainer.INV_START_X;
    private static final int INV_START_Y = EnergyForgeContainer.INV_START_Y;
    private static final int HOTBAR_Y    = EnergyForgeContainer.HOTBAR_Y;

    private static final int DIVIDER_Y = EnergyForgeContainer.DIVIDER_Y;

    private static final int BG_DARK        = 0xFF1A1226;
    private static final int BG_PANEL       = 0xFF3D283C;
    private static final int EDGE_BRIGHT    = 0xFFB8B8FF;
    private static final int EDGE_GLOW      = 0xFF9D4EE8;
    private static final int EDGE_DARK      = 0xFF1F1030;
    private static final int SLOT_BG        = 0xFF272800;
    private static final int SLOT_INNER     = 0xFF4C4C50;
    private static final int BAR_EMPTY      = 0xFF0E0515;
    private static final int BAR_FILL_LOW   = 0xFF7A2738;
    private static final int BAR_FILL_HI    = 0xFFB08EFF;
    private static final int BAR_DONE       = 0xFFFFD700;
    private static final int TEXT_LABEL     = 0xFF50FF9D;
    private static final int TEXT_VALUE     = 0xFFE8B8FF;
    private static final int TEXT_MUTED     = 0xFF7A3059;
    private static final int TEXT_READY     = 0xFFFFD700;
    private static final int TEXT_TITLE     = 0xFFFFD8FF;

    private static final int SPARKLE_COUNT = 14;
    private final Sparkle[] sparkles = new Sparkle[SPARKLE_COUNT];
    private final Random    random   = new Random();

    private final EnergyForgeTileEntity tileEntity;

    private float animTime;
    private float smoothedEnergyHeight;
    private long  lastTimeMs = System.currentTimeMillis();

    public EnergyForgeScreen(EntityPlayer player, EnergyForgeTileEntity tileEntity) {
        super(new EnergyForgeContainer(player, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize = W;
        this.ySize = H;
        for (int i = 0; i < SPARKLE_COUNT; i++) {
            sparkles[i] = new Sparkle(random);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft -= 10;
        this.smoothedEnergyHeight = this.getForgeContainer().getScaledEnergy(BAR_H);
    }

    private EnergyForgeContainer getForgeContainer() {
        return (EnergyForgeContainer) this.inventorySlots;
    }

    private void updateAnimations() {
        long  now          = System.currentTimeMillis();
        float deltaSeconds = (float) (now - lastTimeMs) / 1000f;
        lastTimeMs = now;
        if (deltaSeconds > 0.1f) deltaSeconds = 0.1f;

        animTime += deltaSeconds;

        float target = getForgeContainer().getScaledEnergy(BAR_H);
        float speed  = Math.min(1f, 8f * deltaSeconds);
        smoothedEnergyHeight += (target - smoothedEnergyHeight) * speed;

        for (Sparkle s : sparkles) s.update(random, deltaSeconds);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        updateAnimations();
        int x = this.guiLeft;
        int y = this.guiTop;

        drawPanelBackground(x, y);
        drawSparkles(x, y);
        drawDivider(x, y);
        drawBar(x, y);

        drawSlot(x + OUT_X - 1, y + OUT_Y - 1, true);

        drawInventory(x, y);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawLabels();
        drawTooltips(mouseX, mouseY);
    }

    private void drawPanelBackground(int x, int y) {
        Gui.drawRect(x, y, x + W, y + H, BG_DARK);

        for (int i = 0; i < 24; i++) {
            int alpha = 32 - i;
            if (alpha <= 0) continue;
            Gui.drawRect(x, y + i, x + W, y + i + 1,
                    (alpha << 24) | (EDGE_GLOW & 0xFFFFFF));
        }

        Gui.drawRect(x + 3, y + 3, x + W - 3, y + DIVIDER_Y - 3, BG_PANEL);

        for (int i = 0; i < 12; i++) {
            int alpha = 21 - i;
            if (alpha <= 0) continue;
            Gui.drawRect(x + 3, y + 3 + i, x + W - 3, y + 4 + i,
                    (alpha << 24) | 0xFFFFFF);
        }

        fancyBevel(x + 3, y + 3, W - 6, DIVIDER_Y - 6);
        fancyBevel(x,     y,     W,     H);
    }

    private void drawSparkles(int ox, int oy) {
        for (Sparkle s : sparkles) {
            float life  = s.life / s.maxLife;
            float alpha = (float) Math.sin(life * Math.PI);
            if (alpha <= 0f) continue;

            int a     = (int) (alpha * 220f);
            int color = (a << 24) | (s.color & 0xFFFFFF);
            int px    = ox + 5 + (int) s.x;
            int py    = oy + 5 + (int) s.y;

            Gui.drawRect(px, py, px + 1, py + 1, color);
            if (alpha > 0.7f) {
                int dimA = (int) (alpha * 80f);
                int dim  = (dimA << 24) | (s.color & 0xFFFFFF);
                Gui.drawRect(px - 1, py,     px,     py + 1, dim);
                Gui.drawRect(px + 1, py,     px + 2, py + 1, dim);
                Gui.drawRect(px,     py - 1, px + 1, py,     dim);
                Gui.drawRect(px,     py + 1, px + 1, py + 2, dim);
            }
        }
    }

    private void drawDivider(int x, int y) {
        Gui.drawRect(x + 3, y + DIVIDER_Y,     x + W - 3, y + DIVIDER_Y + 1, EDGE_DARK);
        Gui.drawRect(x + 3, y + DIVIDER_Y + 1, x + W - 3, y + DIVIDER_Y + 2, EDGE_GLOW);
    }

    private void drawBar(int ox, int oy) {
        int x = ox + BAR_X;
        int y = oy + BAR_Y;

        Gui.drawRect(x - 2, y - 2, x + BAR_W + 2, y + BAR_H + 2, EDGE_DARK);
        Gui.drawRect(x - 1, y - 1, x + BAR_W + 1, y + BAR_H + 1, EDGE_GLOW);
        Gui.drawRect(x, y, x + BAR_W, y + BAR_H, BAR_EMPTY);

        int filled = Math.max(0, Math.min(BAR_H, Math.round(smoothedEnergyHeight)));
        if (filled > 0) {
            int pct = getForgeContainer().getEnergyPercent();
            int fy  = y + BAR_H - filled;

            for (int row = 0; row < filled; row++) {
                float t     = (float) row / Math.max(1, filled);
                int   color = (pct >= 100)
                        ? gentlePulse(BAR_DONE)
                        : mix(BAR_FILL_LOW, BAR_FILL_HI, t);
                Gui.drawRect(x, fy + row, x + BAR_W, fy + row + 1, color);
            }

            int hl = mix(BAR_FILL_HI, 0xFFFFFFFF, 0.5f);
            Gui.drawRect(x, fy, x + 2, y + BAR_H, hl);
            int shadow = mix(BAR_FILL_LOW, 0xFF000000, 0.4f);
            Gui.drawRect(x + BAR_W - 2, fy, x + BAR_W, y + BAR_H, shadow);
        }

        for (int i = 1; i < 4; i++) {
            int ly = y + BAR_H - BAR_H * i / 4;
            Gui.drawRect(x, ly, x + BAR_W, ly + 1, 0x40FFFFFF);
        }
    }

    private void drawSlot(int x, int y, boolean withGlow) {
        if (withGlow && (getForgeContainer().getEnergyPercent() >= 100 || getForgeContainer().getCraftingStatus() == 1)) {
            float p = (float) (Math.sin(animTime * 2.0) * 0.5 + 0.5);
            int   a = (int) (60f + p * 80f);
            Gui.drawRect(x - 2, y - 2, x + 22, y + 22, (a << 24) | 0xFFD700);
        }
        Gui.drawRect(x - 1, y - 1, x + 21, y + 21, EDGE_DARK);
        Gui.drawRect(x,      y,      x + 20, y + 1,  EDGE_GLOW);
        Gui.drawRect(x,      y,      x + 1,  y + 20, EDGE_GLOW);
        Gui.drawRect(x,      y + 19, x + 20, y + 20, EDGE_BRIGHT);
        Gui.drawRect(x + 19, y,      x + 20, y + 20, EDGE_BRIGHT);
        Gui.drawRect(x + 1, y + 1, x + 19, y + 19, SLOT_BG);
    }

    private void drawInventory(int ox, int oy) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawInvSlot(
                        ox + INV_START_X + col * 18 - 1,
                        oy + INV_START_Y + row * 18 - 1);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawInvSlot(
                    ox + INV_START_X + col * 18 - 1,
                    oy + HOTBAR_Y - 1);
        }
    }

    private void drawInvSlot(int x, int y) {
        Gui.drawRect(x - 1, y - 1, x + 19, y + 19, EDGE_DARK);
        Gui.drawRect(x,      y,      x + 18, y + 1,  EDGE_GLOW);
        Gui.drawRect(x,      y,      x + 1,  y + 18, EDGE_GLOW);
        Gui.drawRect(x,      y + 17, x + 18, y + 18, EDGE_BRIGHT);
        Gui.drawRect(x + 17, y,      x + 18, y + 18, EDGE_BRIGHT);
        Gui.drawRect(x + 1, y + 1, x + 17, y + 17, SLOT_INNER);
    }

    private void drawLabels() {
        String titleStr = this.tileEntity.getDisplayName().getUnformattedText();
        int    tx       = (W - this.fontRenderer.getStringWidth(titleStr)) / 2;
        this.fontRenderer.drawString(titleStr, tx + 1, 7, 0xFF000000, false);
        this.fontRenderer.drawString(titleStr, tx,     6, TEXT_TITLE,  false);

        int textX = BAR_X + BAR_W + 6;

        this.fontRenderer.drawString("ENERGY",
                textX, BAR_Y, TEXT_LABEL, false);
        this.fontRenderer.drawString(NUM.format(getForgeContainer().getEnergyStored()),
                textX, BAR_Y + 10, TEXT_VALUE, false);
        this.fontRenderer.drawString("/ " + NUM.format(getForgeContainer().getMaxEnergyStored()),
                textX, BAR_Y + 20, TEXT_MUTED, false);
        this.fontRenderer.drawString(getForgeContainer().getEnergyPercent() + "%",
                textX, BAR_Y + 33, TEXT_VALUE, false);

        boolean ready       = getForgeContainer().getEnergyPercent() >= 100;
        String  status      = ready ? "READY!" : "Charging...";
        int     statusColor = ready ? gentlePulse(TEXT_READY) : TEXT_MUTED;
        this.fontRenderer.drawString(status,
                textX, BAR_Y + 43, statusColor, false);
    }

    private void drawTooltips(int mouseX, int mouseY) {
        if (this.isPointInRegion(BAR_X, BAR_Y, BAR_W, BAR_H, mouseX, mouseY)) {
            this.drawHoveringText(Arrays.asList(
                    TextFormatting.GOLD + NUM.format(getForgeContainer().getEnergyStored()) + " / " +
                            NUM.format(getForgeContainer().getMaxEnergyStored()) + " FE",
                    TextFormatting.LIGHT_PURPLE + String.valueOf(getForgeContainer().getEnergyPercent()) + "% charged",
                    TextFormatting.DARK_PURPLE + "350,000,000 FE = 1 Nether Star"),
                    mouseX - this.guiLeft, mouseY - this.guiTop);
            return;
        }

        boolean hoveringEmptyOutput =
                this.isPointInRegion(OUT_X, OUT_Y, 16, 16, mouseX, mouseY) &&
                this.inventorySlots.getSlot(0).getStack().isEmpty();

        if (hoveringEmptyOutput) {
            int pct = getForgeContainer().getEnergyPercent();
            String line = (pct >= 100)
                    ? TextFormatting.GOLD + "Ready to craft!"
                    : TextFormatting.LIGHT_PURPLE + "Need " + NUM.format(
                            getForgeContainer().getMaxEnergyStored() -
                            getForgeContainer().getEnergyStored()) + " more FE";
            this.drawHoveringText(Arrays.asList(line), mouseX - this.guiLeft, mouseY - this.guiTop);
        }
    }

    private int gentlePulse(int color) {
        float p = (float) (Math.sin(animTime * 1.5) * 0.5 + 0.5);
        int   a = (int) (220f + p * 35f);
        return (a << 24) | (color & 0xFFFFFF);
    }

    private void fancyBevel(int x, int y, int w, int h) {
        Gui.drawRect(x,         y,         x + w,     y + 1,     EDGE_BRIGHT);
        Gui.drawRect(x,         y,         x + 1,     y + h,     EDGE_BRIGHT);
        Gui.drawRect(x + 1,     y + 1,     x + w - 1, y + 2,     EDGE_GLOW);
        Gui.drawRect(x + 1,     y + 1,     x + 2,     y + h - 1, EDGE_GLOW);
        Gui.drawRect(x,         y + h - 1, x + w,     y + h,     EDGE_DARK);
        Gui.drawRect(x + w - 1, y,         x + w,     y + h,     EDGE_DARK);
    }

    private static int mix(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r  = (int) ((a >> 16 & 0xFF) + ((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * t);
        int gr = (int) ((a >>  8 & 0xFF) + ((b >>  8 & 0xFF) - (a >>  8 & 0xFF)) * t);
        int bl = (int) ((a       & 0xFF) + ((b       & 0xFF) - (a       & 0xFF)) * t);
        return 0xFF000000 | r << 16 | gr << 8 | bl;
    }

    private static class Sparkle {
        float x, y;
        float life, maxLife;
        int   color;

        private static final int[] COLORS = {
                TEXT_TITLE,
                TEXT_VALUE,
                BAR_FILL_HI,
                0xFFFFFFFF,
                BAR_DONE
        };

        Sparkle(Random r) { respawn(r); }

        void update(Random r, float dt) {
            life += dt;
            if (life >= maxLife) respawn(r);
        }

        void respawn(Random r) {
            x       = r.nextFloat() * 165f;
            y       = r.nextFloat() * 62f;
            life    = 0f;
            maxLife = 2f + r.nextFloat() * 3f;
            color   = COLORS[r.nextInt(COLORS.length)];
        }
    }
}
