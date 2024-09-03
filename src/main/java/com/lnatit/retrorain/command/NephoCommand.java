package com.lnatit.retrorain.command;

import com.lnatit.retrorain.data.CellPos;
import com.lnatit.retrorain.data.Nepho;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class NephoCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(
                Commands.literal("nepho")
                        .requires(source -> source.isPlayer() && source.hasPermission(2))
                        .then(Commands.literal("open")
                                      .executes(NephoCommand::openNephogram)
                        )
                        .then(Commands.literal("set")
                                      .then(Commands.literal("clear")
                                                    .executes(
                                                            context -> setCellNepho(Nepho.Type.CLEAR,
                                                                                    context
                                                            )
                                                    )
                                      )
                                      .then(Commands.literal("rain")
                                                    .executes(
                                                            context -> setCellNepho(Nepho.Type.RAIN,
                                                                                    context
                                                            )
                                                    )
                                      )
                                      .then(Commands.literal("retro")
                                                    .executes(
                                                            context -> setCellNepho(Nepho.Type.RETRO,
                                                                                    context
                                                            )
                                                    )
                                      )
                        )
        );
    }

    private static int openNephogram(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
//        Player player = ctx.getSource().getPlayerOrException();
//        Level level = ctx.getSource().getLevel();
//        LevelChunk center = level.getChunkAt(player.getOnPos());
//
//
//
//        PacketDistributor.sendToPlayer(
//                player,
//                new ChunkNephoPacket()
//        );
        return 0;
    }

    private static int setCellNepho(Nepho.Type type, CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Level level = ctx.getSource().getLevel();
        BlockPos pos = BlockPos.containing(ctx.getSource().getPosition());
        Nepho.setCell(level, new CellPos(pos), type);
        return 0;
    }
}
