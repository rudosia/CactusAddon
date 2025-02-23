package com.cactus.addon.hud;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class Brand extends HudElement {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public Brand() {
        super(INFO);
    }

    private final Setting<SettingColor> textColor = sgGeneral.add(new ColorSetting.Builder()
            .name("Text Color")
            .description("Changes the text color.")
            .defaultValue(new SettingColor(255, 255, 255, 255))
            .build()
    );

    public static final HudElementInfo<Brand> INFO = new HudElementInfo<>(AddonCactus.HUD_GROUP, "Brand", "Displays the cactus client brand.", Brand::new);

    @Override
    public void render(HudRenderer renderer) {
        setSize(renderer.textWidth("[Cactus Addon]", true), renderer.textHeight(true));

        renderer.text("[Cactus Addon]", x, y, textColor.get(), true);
    }
}
