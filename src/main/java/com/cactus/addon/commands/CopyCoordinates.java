package com.cactus.addon.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class CopyCoordinates extends Command {
    public CopyCoordinates() {
        super("copycoordinates", "Copies your current coordinates to the clipboard.", "coords", "cc");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (mc.player == null) return 0;

            BlockPos pos = mc.player.getBlockPos();
            String coords = String.format("%d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
            
            mc.keyboard.setClipboard(coords);
            info("Copied coordinates to clipboard: §b" + coords);
            
            return SINGLE_SUCCESS;
        });

        builder.then(literal("chunk").executes(context -> {
            if (mc.player == null) return 0;

            BlockPos pos = mc.player.getBlockPos();
            int relX = pos.getX() & 15;
            int relZ = pos.getZ() & 15;
            String coords = relX + ", " + relZ;

            mc.keyboard.setClipboard(coords);
            info("Copied chunk-relative position: §b" + coords);

            return SINGLE_SUCCESS;
        }));

        builder.then(literal("raw").executes(context -> {
            if (mc.player == null) return 0;

            BlockPos pos = mc.player.getBlockPos();
            String coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();

            mc.keyboard.setClipboard(coords);
            info("Copied raw coordinates: §b" + coords);

            return SINGLE_SUCCESS;
        }));
    }
}