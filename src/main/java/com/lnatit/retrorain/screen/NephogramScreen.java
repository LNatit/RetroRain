package com.lnatit.retrorain.screen;

import com.lnatit.retrorain.data.Nepho;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NephogramScreen extends Screen
{
    private Nepho.Type[][] map = new Nepho.Type[20][20];

    protected NephogramScreen(Component title) {
        super(GameNarrator.NO_TITLE);
    }
}
