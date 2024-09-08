package com.lnatit.retrorain.common.command;

import com.lnatit.retrorain.common.data.CellPos;
import com.lnatit.retrorain.common.data.DataRegistry;
import com.lnatit.retrorain.common.data.Nepho;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

import static com.mojang.text2speech.Narrator.LOGGER;

public class NephoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(
                Commands.literal("nepho")
                        .requires(source -> source.isPlayer() && source.hasPermission(2))
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
                        .then(Commands.literal("get")
                                .executes(NephoCommand::printNepho)
                        )
        );
    }

    private static int setCellNepho(Nepho.Type type, CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerLevel level = ctx.getSource().getLevel();
        CellPos cellPos = new CellPos(BlockPos.containing(ctx.getSource().getPosition()));
        Nepho.setCell(level, cellPos, type);
        return 0;
    }

    private static int printNepho(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerLevel level = ctx.getSource().getLevel();
        CellPos pos = new CellPos(BlockPos.containing(ctx.getSource().getPosition()));
        LevelChunk chunk = level.getChunk(pos.getChunkX(), pos.getChunkZ());
        Nepho data = chunk.getData(DataRegistry.NEPHO);
        LOGGER.info("Data map:");
        LOGGER.info(data.map().toString());
        return 0;
    }
}
