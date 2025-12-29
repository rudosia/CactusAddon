package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ChunkESP extends Module {

    public ChunkESP() {
        super(AddonCactus.CATEGORY, "Chunk ESP", "Borders with white intersection highlights.");
    }

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
            .name("Main Color")
            .description("Color of the main chunk corners.")
            .defaultValue(new SettingColor(120, 150, 255, 200))
            .build()
    );

    private final Setting<SettingColor> centerColor = sgGeneral.add(new ColorSetting.Builder()
            .name("Center Color")
            .description("Color of the center (8th block) lines.")
            .defaultValue(new SettingColor(255, 255, 0, 150))
            .build()
    );

    private final Setting<SettingColor> intersectColor = sgGeneral.add(new ColorSetting.Builder()
            .name("Intersect Color")
            .description("The color of the line at your current height.")
            .defaultValue(new SettingColor(255, 255, 255, 240))
            .build()
    );

    private final Setting<SettingColor> gridColor = sgGeneral.add(new ColorSetting.Builder()
            .name("Grid Color")
            .description("Color of the internal grid lines.")
            .defaultValue(new SettingColor(120, 150, 255, 95))
            .build()
    );

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (mc.player == null || mc.world == null) return;

        BlockPos playerPos = mc.player.getBlockPos();
        ChunkPos chunkPos = new ChunkPos(playerPos);

        int x1 = chunkPos.x * 16;
        int z1 = chunkPos.z * 16;
        int x2 = x1 + 16;
        int z2 = z1 + 16;

        double minY = mc.world.getBottomY();
        double maxY = mc.world.getTopYInclusive();

        event.renderer.line(x1, minY, z1, x1, maxY, z1, lineColor.get());
        event.renderer.line(x2, minY, z1, x2, maxY, z1, lineColor.get());
        event.renderer.line(x1, minY, z2, x1, maxY, z2, lineColor.get());
        event.renderer.line(x2, minY, z2, x2, maxY, z2, lineColor.get());

        for (int i = 1; i < 16; i++) {
            SettingColor color = (i == 8) ? centerColor.get() : gridColor.get();
            event.renderer.line(x1 + i, minY, z1, x1 + i, maxY, z1, color);
            event.renderer.line(x1 + i, minY, z2, x1 + i, maxY, z2, color);
            event.renderer.line(x1, minY, z1 + i, x1, maxY, z1 + i, color);
            event.renderer.line(x2, minY, z1 + i, x2, maxY, z1 + i, color);
        }

        for (double h = minY; h <= maxY; h += 16) {
            drawHorizontalSquare(event, x1, x2, z1, z2, h, centerColor.get());
        }

        double intersectY = Math.floor(mc.player.getY());
        drawHorizontalSquare(event, x1, x2, z1, z2, intersectY, intersectColor.get());

        double eyeY = Math.floor(mc.player.getEyeY());
        if (eyeY != intersectY) {
            drawHorizontalSquare(event, x1, x2, z1, z2, eyeY, intersectColor.get());
        }
    }

    private void drawHorizontalSquare(Render3DEvent event, double x1, double x2, double z1, double z2, double y, SettingColor color) {
        event.renderer.line(x1, y, z1, x2, y, z1, color);
        event.renderer.line(x1, y, z2, x2, y, z2, color);
        event.renderer.line(x1, y, z1, x1, y, z2, color);
        event.renderer.line(x2, y, z1, x2, y, z2, color);
    }
}