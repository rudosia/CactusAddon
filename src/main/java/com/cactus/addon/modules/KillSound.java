package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class KillSound extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<KillSoundOption> killSound = sgGeneral.add(new EnumSetting.Builder<KillSoundOption>()
        .name("kill-sound")
        .description("The sound to play when you kill an entity.")
        .defaultValue(KillSoundOption.Amethyst_Break)
        .build());

    private final Setting<Integer> volume = sgGeneral.add(new IntSetting.Builder()
        .name("volume")
        .description("The volume of the kill sound.")
        .defaultValue(100)
        .min(0)
        .max(300)
        .build());

    private final Setting<Integer> pitch = sgGeneral.add(new IntSetting.Builder()
        .name("pitch")
        .description("The pitch of the kill sound.")
        .defaultValue(100)
        .min(0)
        .max(200)
        .build());

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

    public KillSound() {
        super(AddonCactus.CATEGORY, "Kill Sound", "Plays a sound when you kill any entity.");
    }

    @EventHandler
    private void onReceive(PacketEvent.Receive event) {
        if (event.packet instanceof EntityStatusS2CPacket packet && (packet.getStatus() == 35 || packet.getStatus() == 3)) {
            Entity entity = packet.getEntity(mc.world);
            if (mc.player != null && mc.world != null && entity instanceof PlayerEntity) {
                if (entity != mc.player && !Friends.get().isFriend((PlayerEntity) entity) &&
                    mc.player.getPos().distanceTo(entity.getPos()) <= 25) {
                    SoundEvent selectedSound = killSound.get().getSoundEvent();
                    mc.player.playSound(selectedSound, volume.get() / 100.0F, pitch.get() / 100.0F);
                }
            }
        }
    }
}
