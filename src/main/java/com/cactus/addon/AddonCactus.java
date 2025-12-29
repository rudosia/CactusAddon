package com.cactus.addon;

import com.cactus.addon.commands.*;
import com.cactus.addon.hud.*;
import com.cactus.addon.modules.*;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

import org.slf4j.Logger;

public class AddonCactus extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Cactus");
    public static final HudGroup HUD_GROUP = new HudGroup("Cactus");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Addon: Cactus Addon");

        // Modules
        Modules.get().add(new KillSound());
        Modules.get().add(new Confuse());
        Modules.get().add(new AutoFarm());
        Modules.get().add(new AutoCope());
        Modules.get().add(new AutoEZ());
        Modules.get().add(new AimAssist());
        Modules.get().add(new AutoSell());
        Modules.get().add(new ChunkESP());
        Modules.get().add(new ItemSpiner());
        Modules.get().add(new AutoDripstone());
        Modules.get().add(new HitboxDesync());

        // Commands
        Commands.add(new CommandExample());
        Commands.add(new CopyCoordinates());
        Commands.add(new Teleport());

        // HUD
        Hud.get().register(HudExample.INFO);
        Hud.get().register(Brand.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.cactus.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("MeteorDevelopment", "meteor-addon-template");
    }
}