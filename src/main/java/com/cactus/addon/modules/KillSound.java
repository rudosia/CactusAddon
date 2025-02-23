package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import com.cactus.addon.utils.RejectsUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class KillSound extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgKill = settings.createGroup("Kill Sound");
    private final SettingGroup sgTotem = settings.createGroup("Totem Sound");

    public KillSound() {
        super(AddonCactus.CATEGORY, "Kill Sound", "Plays a sound when you kill any entity.");
    }

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final Setting<Integer> activateDistance = sgGeneral.add(new IntSetting.Builder()
            .name("Activate Distance")
            .description("How far away the sound can be activated from.")
            .defaultValue(25)
            .min(0)
            .max(300)
            .build()
    );

    private final Setting<Boolean> inFOV = sgGeneral.add(new BoolSetting.Builder()
            .name("in FOV")
            .description("Only plays if the player is in fov.")
            .build()
    );

    private final Setting<Boolean> enableKillSound = sgKill.add(new BoolSetting.Builder()
            .name("Kill Sound")
            .description("Toggle the kill sound.")
            .build()
    );

    private final Setting<KillSoundOption> killSound = sgKill.add(new EnumSetting.Builder<KillSoundOption>()
            .name("Sound")
            .description("The sound to play when you kill an entity.")
            .defaultValue(KillSoundOption.Amethyst_Break)
            .visible(enableKillSound::get)
            .build()
    );

    private final Setting<Integer> killVolume = sgKill.add(new IntSetting.Builder()
            .name("Kill Volume")
            .description("The volume of the kill sound.")
            .defaultValue(100)
            .min(0)
            .max(300)
            .visible(enableKillSound::get)
            .build()
    );

    private final Setting<Integer> killPitch = sgKill.add(new IntSetting.Builder()
            .name("Kill Pitch")
            .description("The pitch of the kill sound.")
            .defaultValue(100)
            .min(0)
            .max(200)
            .visible(enableKillSound::get)
            .build()
    );

    private final Setting<Boolean> enableTotemSound = sgTotem.add(new BoolSetting.Builder()
            .name("Totem Sound")
            .description("Toggle the totem sound.")
            .build()
    );

    private final Setting<KillSoundOption> totemSound = sgTotem.add(new EnumSetting.Builder<KillSoundOption>()
            .name("Sound")
            .description("The sound to play when you kill an entity.")
            .defaultValue(KillSoundOption.Amethyst_Break)
            .visible(enableTotemSound::get)
            .build()
    );

    private final Setting<Integer> totemVolume = sgTotem.add(new IntSetting.Builder()
            .name("Totem Volume")
            .description("The volume of the kill sound.")
            .defaultValue(100)
            .min(0)
            .max(300)
            .visible(enableTotemSound::get)
            .build()
    );

    private final Setting<Integer> totemPitch = sgTotem.add(new IntSetting.Builder()
            .name("Totem Pitch")
            .description("The pitch of the kill sound.")
            .defaultValue(100)
            .min(0)
            .max(200)
            .visible(enableTotemSound::get)
            .build()
    );

    public enum KillSoundOption {
        Amethyst_Break(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK),
        Wither_Death(SoundEvents.ENTITY_WITHER_DEATH),
        Level_Up(SoundEvents.ENTITY_PLAYER_LEVELUP),
        Anvil_Land(SoundEvents.BLOCK_ANVIL_LAND),
        Guardian_Curse(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE),
        Guardian_Death(SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH);

        public final SoundEvent soundEvent;

        KillSoundOption(SoundEvent soundEvent) {
            this.soundEvent = soundEvent;
        }

        public SoundEvent getSoundEvent() {
            return soundEvent;
        }
    }

    @EventHandler
    private void onReceive(PacketEvent.Receive event) {
        if (event.packet instanceof EntityStatusS2CPacket packet && packet.getStatus() == 3 && enableKillSound.get()) {
            Entity entity = packet.getEntity(mc.world);
            boolean checkInFOV = !RejectsUtils.inFov(entity, 120);
            if (inFOV.get() && checkInFOV) {return;}
            if (mc.player != null && mc.world != null && entity instanceof PlayerEntity) {
                if (entity != mc.player && mc.player.getPos().distanceTo(entity.getPos()) <= activateDistance.get()) {
                    SoundEvent selectedSound = killSound.get().getSoundEvent();
                    mc.player.playSound(selectedSound, killVolume.get() / 100.0F, killPitch.get() / 100.0F);
                }
            }
        }
        if (event.packet instanceof EntityStatusS2CPacket packet && packet.getStatus() == 35 && enableTotemSound.get()) {
            Entity entity = packet.getEntity(mc.world);
            boolean checkInFOV = !RejectsUtils.inFov(entity, 120);
            if (inFOV.get() && checkInFOV) {return;}
            if (mc.player != null && mc.world != null && entity instanceof PlayerEntity) {
                if (entity != mc.player && mc.player.getPos().distanceTo(entity.getPos()) <= activateDistance.get()) {
                    SoundEvent selectedSound = totemSound.get().getSoundEvent();
                    mc.player.playSound(selectedSound, totemVolume.get() / 100.0F, totemPitch.get() / 100.0F);
                }
            }
        }
    }

}
