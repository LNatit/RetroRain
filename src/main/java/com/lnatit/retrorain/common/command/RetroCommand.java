package com.lnatit.retrorain.common.command;

import com.lnatit.retrorain.common.data.DataRegistry;
import com.lnatit.retrorain.common.network.PhaseUpdatePacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.mojang.text2speech.Narrator.LOGGER;


public class RetroCommand {
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
//                        .then(Commands.literal("setmana")
//                                .executes(
//                                        context -> {
//                                            ChunkPos pos = context.getSource().getPlayer().chunkPosition();
//                                            LevelChunk chunk = context.getSource().getLevel().getChunk(pos.x, pos.z);
//                                            chunk.setData(DataRegistry.MANA, 10);
//                                            return 0;
//                                        }
//                                )
//                        )
//                        .then(Commands.literal("getmana")
//                                .executes(
//                                        context -> {
//                                            ChunkPos pos = context.getSource().getPlayer().chunkPosition();
//                                            LevelChunk chunk = context.getSource().getLevel().getChunk(pos.x, pos.z);
//                                            LOGGER.info("Mana is {}", chunk.getData(DataRegistry.MANA));
//                                            return chunk.getData(DataRegistry.MANA);
//                                        }
//                                )
//                        )
        );
    }

    private static int executeBuiltin(CommandContext<CommandSourceStack> ctx, boolean raining, boolean retro) throws CommandSyntaxException {
        PacketDistributor.sendToPlayer(
                ctx.getSource().getPlayer(),
                new PhaseUpdatePacket(raining, retro)
        );
        return 0;
    }
}
