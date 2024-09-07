package com.lnatit.retrorain.screen;

import com.lnatit.retrorain.data.Nepho;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

public class NephogramScreen extends Screen
{
    public static final ResourceLocation NEPHOGRAM = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/nephogram.png");

    private Nepho.Type[][] map = new Nepho.Type[20][20];

    public NephogramScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        else if (keyCode == 108) {
            this.saveAll();
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int left = ( this.width - 181 ) / 2;
        int top = ( this.height - 181 ) / 2;
        guiGraphics.blit(NEPHOGRAM, left, top, 0, 0, 181, 181);
        left += 1;
        top += 1;
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                if (map[y][x] == Nepho.Type.RAIN) {
                    guiGraphics.fill(left, top, left + 8, top + 8, 0x2695dc);
                }
                else if (map[y][x] == Nepho.Type.RETRO) {
                    guiGraphics.fill(left, top, left + 8, top + 8, 0xdc5c5c);
                }
                left += 9;
            }
            top += 9;
        }

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, "Confirm - [Enter]", 0, 0, 16777215);
        guiGraphics.drawString(this.font, "   Exit - [Esc]", 0, 20, 16777215);
    }

    private void saveAll() {

    }
}
