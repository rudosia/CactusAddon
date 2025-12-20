package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.events.render.HeldItemRendererEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public class ItemSpiner extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgAxises = settings.createGroup("Axises");

    private final Setting<Integer> rotationSpeed = sgGeneral.add(new IntSetting.Builder()
            .name("Rotation Speed")
            .description("Controls the speed of the spinning.")
            .defaultValue(3)
            .min(0)
            .max(25)
            .build()
    );

    private final Setting<Boolean> reverseDirection = sgGeneral.add(new BoolSetting.Builder()
            .name("Reverse Direction")
            .description("Reverse the direction of rotation.")
            .defaultValue(false)
            .build()
    );

    public final Setting<EnabledHands> enabledHands = sgGeneral.add(new EnumSetting.Builder<EnabledHands>()
            .name("Enabled Hands")
            .description("Enable what hands are affected.")
            .defaultValue(EnabledHands.Both)
            .build()
    );

    private final Setting<Boolean> X = sgAxises.add(new BoolSetting.Builder()
            .name("X")
            .description("Spin on the X axis.")
            .build()
    );

    private final Setting<Boolean> Y = sgAxises.add(new BoolSetting.Builder()
            .name("Y")
            .description("Spin on the Y axis.")
            .build()
    );

    private final Setting<Boolean> Z = sgAxises.add(new BoolSetting.Builder()
            .name("Z")
            .description("Spin on the Z axis.")
            .build()
    );

    private double spinAngle = 0;

    public ItemSpiner() {
        super(AddonCactus.CATEGORY, "Item Spiner", "Spins the items in your hands.");
    }

    @EventHandler
    private void onHeldItemRender(HeldItemRendererEvent event) {
        spinAngle += rotationSpeed.get();

        if (spinAngle >= 360) {
            spinAngle -= 360;
        }

        if (!(enabledHands.get() == EnabledHands.None)) {
            double tempSpinAngle = reverseDirection.get() ? -spinAngle : spinAngle;
            if (event.hand == Hand.MAIN_HAND) {
                if (enabledHands.get() == EnabledHands.Both || enabledHands.get() == EnabledHands.Mainhand) {
                    if (X.get()) {
                        rotate(event.matrix, new Vector3d(tempSpinAngle, 0, 0));
                    }
                    if (Y.get()) {
                        rotate(event.matrix, new Vector3d(0, tempSpinAngle, 0));
                    }
                    if (Z.get()) {
                        rotate(event.matrix, new Vector3d(0, 0, tempSpinAngle));
                    }
                }
            } else {
                if (enabledHands.get() == EnabledHands.Both || enabledHands.get() == EnabledHands.Offhand) {
                    if (X.get()) {
                        rotate(event.matrix, new Vector3d(tempSpinAngle, 0, 0));
                    }
                    if (Y.get()) {
                        rotate(event.matrix, new Vector3d(0, tempSpinAngle, 0));
                    }
                    if (Z.get()) {
                        rotate(event.matrix, new Vector3d(0, 0, tempSpinAngle));
                    }
                }
            }
        }
    }

    private void rotate(MatrixStack matrix, Vector3d rotation) {
        if (rotation.x != 0) {
            matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) rotation.x));
        }
        if (rotation.y != 0) {
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) rotation.y));
        }
        if (rotation.z != 0) {
            matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rotation.z));
        }
    }

    public enum EnabledHands {
        Both,
        Mainhand,
        Offhand,
        None
    }
}
