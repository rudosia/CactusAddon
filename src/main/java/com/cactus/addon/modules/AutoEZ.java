package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AutoEZ extends Module {
    public AutoEZ() {
        super(AddonCactus.CATEGORY, "Auto EZ", "Sends message after enemy dies.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgKill = settings.createGroup("Kill");
    private final SettingGroup sgPop = settings.createGroup("Pop");

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("Enemy Range")
        .description("Only send message if enemy died inside this range.")
        .defaultValue(25)
        .min(0)
        .sliderRange(0, 50)
        .build()
    );
    private final Setting<Integer> tickDelay = sgGeneral.add(new IntSetting.Builder()
        .name("Delay")
        .description("How many ticks to wait between sending messages.")
        .defaultValue(50)
        .min(0)
        .sliderRange(0, 100)
        .build()
    );

    private final Setting<Boolean> kill = sgKill.add(new BoolSetting.Builder()
        .name("Kill")
        .description("Should we send a message when enemy dies")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<String>> killMessages = sgKill.add(new StringListSetting.Builder()
        .name("Kill Messages")
        .description("Messages to send when killing an enemy with Cactus message mode on")
        .defaultValue(List.of("This kill was brought to you by cactus addon!", "Cactus Addon Forever!", "Thank you <NAME>!"))
        .build()
    );

    private final Setting<Boolean> pop = sgPop.add(new BoolSetting.Builder()
        .name("Pop")
        .description("Should we send a message when enemy pops a totem")
        .defaultValue(true)
        .build()
    );
    
    private final Setting<List<String>> popMessages = sgPop.add(new StringListSetting.Builder()
        .name("Pop Messages")
        .description("Messages to send when popping an enemy")
        .defaultValue(List.of("Thank you <NAME>!", "Cactus Addon Forever!"))
        .build()
    );

    private final Random r = new Random();
    private int lastNum;
    private int lastPop;
    private boolean lastState;
    private String targetName = null;
    private final List<QueuedMessage> messageQueue = new LinkedList<>();
    private int timer = 0;

    @Override
    public void onActivate() {
        lastState = false;
        lastNum = -1;
        messageQueue.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        
        timer++;
        
        if (kill.get() && anyDead(range.get())) {
            if (!lastState) {
                lastState = true;
                sendKillMessage();
            }
        } else {
            lastState = false;
        }

        if (timer >= tickDelay.get() && !messageQueue.isEmpty()) {
            QueuedMessage msg = messageQueue.get(0);
            ChatUtils.sendPlayerMsg(msg.message);
            timer = 0;

            if (msg.isKillMsg) messageQueue.clear();
            else messageQueue.remove(0);
        }
    }

    @EventHandler
    private void onReceive(PacketEvent.Receive event) {
        if (event.packet instanceof EntityStatusS2CPacket packet && packet.getStatus() == 35) {
            Entity entity = packet.getEntity(mc.world);
            if (pop.get() && mc.player != null && entity instanceof PlayerEntity target) {
                if (target != mc.player && !Friends.get().isFriend(target)) {
                    if (mc.player.distanceTo(target) <= range.get()) {
                        sendPopMessage(target.getName().getString());
                    }
                }
            }
        }
    }

    private boolean anyDead(double range) {
        if (mc.world == null) return false;
        for (PlayerEntity pl : mc.world.getPlayers()) {
            if (pl != mc.player && !Friends.get().isFriend(pl) && pl.getHealth() <= 0) {
                if (mc.player.distanceTo(pl) <= range) {
                    targetName = pl.getName().getString();
                    return true;
                }
            }
        }
        return false;
    }

    private void sendKillMessage() {
        if (!killMessages.get().isEmpty()) {
            int num = r.nextInt(killMessages.get().size());
            if (num == lastNum) num = (num + 1) % killMessages.get().size();
            lastNum = num;
            messageQueue.add(0, new QueuedMessage(killMessages.get().get(num), true));
        }
    }

    private void sendPopMessage(String name) {
        if (!popMessages.get().isEmpty()) {
            int num = r.nextInt(popMessages.get().size());
            if (num == lastPop) num = (num + 1) % popMessages.get().size();
            lastPop = num;
            messageQueue.add(new QueuedMessage(popMessages.get().get(num).replace("<NAME>", name), false));
        }
    }

    private record QueuedMessage(String message, boolean isKillMsg) {}

}