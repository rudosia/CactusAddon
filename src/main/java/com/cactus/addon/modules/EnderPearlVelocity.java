package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class EnderPearlVelocity extends Module {

    public EnderPearlVelocity() {
        super(AddonCactus.CATEGORY, "Ender Pearl Vel", "Boostest the velocity of thrown ender pearls.");
        INSTANCE = this;
    }

    private static EnderPearlVelocity INSTANCE;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
            .name("Multiplier")
            .description("The factor by which to multiply the ender pearl's velocity.")
            .defaultValue(1.5)
            .min(0.1)
            .sliderRange(0.1, 5.0)
            .build()
    );

    public static boolean isEnabled() {
        return INSTANCE != null && INSTANCE.isActive();
    }

    public static double getMultiplier() {
        return INSTANCE != null ? INSTANCE.multiplier.get() : 1.0;
    }
}
