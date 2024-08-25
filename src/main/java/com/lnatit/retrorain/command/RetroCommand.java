package com.lnatit.retrorain.command;

import com.lnatit.retrorain.network.PhaseUpdatePacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.network.PacketDistributor;


public class RetroCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(
                Commands.literal("retro")
                        .then(Commands.literal("set")
                                      .requires(CommandSourceStack::isPlayer)
                                      .then(Commands.literal("clear")
                                                    .executes(
                                                            context -> executeBuiltin(context, false, false)
                                                    ))
                                      .then(Commands.literal("rain")
                                                    .executes(
                                                            context -> executeBuiltin(context, true, false)
                                                    ))
                                      .then(Commands.literal("retro")
                                                    .executes(
                                                            context -> executeBuiltin(context, true, true)
                                                    ))

                        )
        );
    }

    private static int executeBuiltin(CommandContext<CommandSourceStack> context, boolean raining, boolean retro) throws CommandSyntaxException {
        PacketDistributor.sendToPlayer(
                context.getSource().getPlayer(),
                new PhaseUpdatePacket(raining, retro)
        );
        return 0;


    }
}
