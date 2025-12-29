package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class AutoSell extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> sellCommand = sgGeneral.add(new StringSetting.Builder()
        .name("Sell Command")
        .description("Command to run when threshold is reached.")
        .defaultValue("sellall SHULKER_BOX")
        .build()
    );

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("Trigger Items")
        .description("Items that count towards the threshold.")
        .defaultValue(Items.SHULKER_BOX)
        .build()
    );

    private final Setting<Integer> threshold = sgGeneral.add(new IntSetting.Builder()
        .name("Trigger Threshold")
        .description("How many slots of the trigger items are needed to sell.")
        .defaultValue(10)
        .min(1)
        .sliderMax(36)
        .build()
    );

    private final Setting<Integer> sellDelay = sgGeneral.add(new IntSetting.Builder()
        .name("Sell Delay")
        .description("Ticks to wait before running the sell command.")
        .defaultValue(10)
        .min(1)
        .sliderMax(100)
        .build()
    );

    private final Setting<Integer> dropDelay = sgGeneral.add(new IntSetting.Builder()
        .name("Drop Delay")
        .description("Ticks to wait after command before dropping shulkers.")
        .defaultValue(10)
        .min(1)
        .sliderMax(100)
        .build()
    );

    private int sellTimer = -1;
    private int dropTimer = -1;

    public AutoSell() {
        super(AddonCactus.CATEGORY, "Auto Sell", "Sells specific items when full and drops shulkers.");
    }

    @Override
    public void onDeactivate() {
        sellTimer = -1;
        dropTimer = -1;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        int triggerItemSlots = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && items.get().contains(stack.getItem())) {
                triggerItemSlots++;
            }
        }

        if (triggerItemSlots >= threshold.get() && sellTimer == -1 && dropTimer == -1) {
            sellTimer = sellDelay.get();
        }

        if (sellTimer > 0) {
            sellTimer--;
        } else if (sellTimer == 0) {
            mc.player.networkHandler.sendChatCommand(sellCommand.get());
            sellTimer = -1;
            dropTimer = dropDelay.get();
        }

        if (dropTimer > 0) {
            dropTimer--;
        } else if (dropTimer == 0) {
            dropAllShulkers();
            dropTimer = -1;
        }
    }

    private void dropAllShulkers() {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isShulker(stack)) {
                InvUtils.drop().slot(i);
                count++;
            }
        }
        //if (count > 0) info("Dropped " + count + " shulker boxes.");
    }

    private boolean isShulker(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) return false;
        return ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock;
    }
}