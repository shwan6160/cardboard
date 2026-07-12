/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.world.LoadableWorld;
import com.bergerkiller.templates.TemplateResolver;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public interface CommonServer {
    public boolean init();

    public void postInit(PostInitEvent var1);

    public void enable(CommonPlugin var1);

    public void disable(CommonPlugin var1);

    public String getServerDetails();

    public String getServerVersion();

    public String getServerName();

    public boolean isMojangMappings();

    public boolean isForgeServer();

    public String getServerDescription();

    public String getMinecraftVersion();

    public String getMinecraftVersionMajor();

    public String getMinecraftVersionPre();

    public boolean evaluateMCVersion(String var1, String var2);

    public LoadableWorld getLoadableWorld(World var1);

    public LoadableWorld findLoadableWorld(String var1);

    public Collection<LoadableWorld> getLoadableWorlds();

    public File getWorldRegionFolder(String var1);

    public File getWorldFolder(String var1);

    public File getWorldLevelFile(String var1);

    public Collection<String> getLoadableWorldsLegacy();

    public boolean isLoadableWorld(String var1);

    public String getNMSRoot();

    public String getCBRoot();

    public boolean isCustomEntityType(EntityType var1);

    public void addVariables(Map<String, String> var1);

    public static String cleanVersion(String mc_version) {
        String clean_version = mc_version;
        int pre_idx = clean_version.indexOf("-pre");
        if (pre_idx != -1) {
            clean_version = clean_version.substring(0, pre_idx);
        }
        return clean_version;
    }

    public static String preVersion(String mc_version) {
        int pre_idx = mc_version.indexOf("-pre");
        if (pre_idx != -1) {
            return mc_version.substring(pre_idx + 4);
        }
        return null;
    }

    public static class PostInitEvent {
        private final TemplateResolver templateResolver;
        private String incompatibleReason = null;

        public PostInitEvent(TemplateResolver templateResolver) {
            this.templateResolver = templateResolver;
        }

        public String getIncompatibleReason() {
            return this.incompatibleReason;
        }

        public boolean isCompatible() {
            return this.incompatibleReason == null;
        }

        public TemplateResolver getResolver() {
            return this.templateResolver;
        }

        public void signalIncompatible(String reason) {
            this.incompatibleReason = reason;
        }
    }
}

