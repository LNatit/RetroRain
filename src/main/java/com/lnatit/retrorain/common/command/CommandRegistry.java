package com.lnatit.retrorain.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class CommandRegistry
{
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        RetroCommand.register(dispatcher);
        NephoCommand.register(dispatcher);
    }
}
