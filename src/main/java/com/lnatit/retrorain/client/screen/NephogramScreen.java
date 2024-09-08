package com.lnatit.retrorain.client.screen;

import com.lnatit.retrorain.common.data.CellPos;
import com.lnatit.retrorain.common.data.Nepho;
import com.lnatit.retrorain.common.network.ChunkNephoPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

public class NephogramScreen extends Screen {
    public static final ResourceLocation NEPHOGRAM = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/nephogram.png");
    public static final ResourceLocation RAIN_WIDGET = ResourceLocation.fromNamespaceAndPath(MOD_ID, "rain_widget");
    public static final ResourceLocation RETRO_WIDGET = ResourceLocation.fromNamespaceAndPath(MOD_ID, "retro_widget");

    private int left;
    private int top;

    private Level level;
    private CellPos origin;
    private Player player;

    public NephogramScreen(Level level, Player player) {
        super(GameNarrator.NO_TITLE);
        this.level = level;
        this.player = player;
        this.origin = CellPos.chunkOrigin(player.chunkPosition()).offset(-8, -8);
    }

    @Override
    protected void init() {
        super.init();
        this.left = (this.width - 181) / 2;
        this.top = (this.height - 181) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int left = this.left + 1;
        int top = this.top + 1;
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                if (getType(x, y) == Nepho.Type.RAIN) {
                    guiGraphics.blitSprite(RAIN_WIDGET, left, top, 8, 8);
                } else if (getType(x, y) == Nepho.Type.RETRO) {
                    guiGraphics.blitSprite(RETRO_WIDGET, left, top, 8, 8);
                }
                left += 9;
            }
            left = this.left + 1;
            top += 9;
        }

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, "Exit - [Esc]", 5, 15, 16777215);
        int left = (this.width - 181) / 2;
        int top = (this.height - 181) / 2;
        guiGraphics.blit(NEPHOGRAM, left, top, 0, 0, 181, 181);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private Nepho.Type getType(int x, int z) {
        return Nepho.getCell(level, origin.offset(x, z));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button < 0 || button > 2) return false;
        int x = (int) ((Math.round(mouseX) - this.left) / 9);
        int y = (int) ((Math.round(mouseY) - this.top) / 9);
        if (x >= 0 && y >= 0 && x < 20 && y < 20) {
            Nepho.Type type = Nepho.getCell(level, origin.offset(x, y));
            if (button == 0) type = type.next();
            else type = type.pre();
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT))
                return Nepho.setChunk(level, origin.offset(x, y), type);
            return Nepho.setCell(level, origin.offset(x, y), type);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
