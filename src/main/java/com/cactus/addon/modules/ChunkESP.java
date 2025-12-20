package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public class ChunkESP extends Module {

    public ChunkESP() {
        super(AddonCactus.CATEGORY, "Chunk ESP", "Simple chunk border renderer.");
    }
    
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
            .name("Line Color")
            .description("Color of the chunk border lines.")
            .defaultValue(new SettingColor(255, 0, 0, 255)) // Solid red
            .build()
    );

    private final Setting<Boolean> drawTop = sgGeneral.add(new BoolSetting.Builder()
            .name("Draw Top")
            .description("Draws the top face of the chunk box.")
            .defaultValue(true)
            .build()
    );
    

@EventHandler
private void onRender3D(Render3DEvent event) {
    if (mc.player == null || mc.world == null) return;

    BlockPos playerPos = mc.player.getBlockPos();
    ChunkPos chunkPos = new ChunkPos(playerPos);

    int chunkX = chunkPos.x * 16;
    int chunkZ = chunkPos.z * 16;
    
    double minY = mc.world.getBottomY();
    double maxY = mc.world.getTopYInclusive();

    Box chunkBox = new Box(chunkX, minY, chunkZ, chunkX + 16, maxY, chunkZ + 16);

    event.renderer.box(chunkBox, lineColor.get(), lineColor.get(), ShapeMode.Lines, 0);

    if (drawTop.get()) {
        event.renderer.box(new Box(chunkX, maxY - 0.1, chunkZ, chunkX + 16, maxY, chunkZ + 16),
                lineColor.get(), lineColor.get(), ShapeMode.Lines, 0);
    }
}
}
