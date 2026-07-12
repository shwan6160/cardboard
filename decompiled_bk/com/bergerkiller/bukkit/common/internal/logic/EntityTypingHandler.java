/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler_1_13;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler_1_14;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler_1_8;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.logging.Level;
import org.bukkit.entity.Entity;

public abstract class EntityTypingHandler
implements LibraryComponent {
    public static final EntityTypingHandler INSTANCE = ((LibraryComponentSelector)LibraryComponentSelector.forModule(EntityTypingHandler.class).runFirst(CommonBootstrap::initServer)).addVersionOption(null, "1.12.2", EntityTypingHandler_1_8::new).addVersionOption("1.13", "1.13.2", EntityTypingHandler_1_13::new).addVersionOption("1.14", null, EntityTypingHandler_1_14::new).update();

    public abstract EntityTrackerEntryHook getEntityTrackerEntryHook(Object var1);

    public abstract Object hookEntityTrackerEntry(Object var1);

    public abstract EntityTrackerEntryHandle createEntityTrackerEntry(EntityTracker var1, Entity var2);

    public abstract Class<?> getClassFromEntityTypes(Object var1);

    public static void initConfigurationPartRecurse(Object config) {
        Class<?> configurationPartType;
        try {
            configurationPartType = Class.forName("io.papermc.paper.configuration.ConfigurationPart");
        }
        catch (ClassNotFoundException e) {
            return;
        }
        for (Field f : config.getClass().getFields()) {
            Object childConfigPart;
            Class<?> fieldType = f.getType();
            if (!configurationPartType.isAssignableFrom(fieldType)) continue;
            try {
                f.setAccessible(true);
                if (f.get(config) != null) {
                }
            }
            catch (Throwable t) {}
            continue;
            Constructor<?> ctor_noarg = null;
            Constructor<?> ctor_parentarg = null;
            try {
                ctor_noarg = fieldType.getConstructor(new Class[0]);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                ctor_parentarg = fieldType.getConstructor(config.getClass());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (ctor_noarg == null && ctor_parentarg == null) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find constructor for " + fieldType.getName());
                continue;
            }
            try {
                if (ctor_noarg != null) {
                    childConfigPart = ctor_noarg.newInstance(new Object[0]);
                } else {
                    if (ctor_parentarg == null) continue;
                    childConfigPart = ctor_parentarg.newInstance(config);
                }
                f.set(config, childConfigPart);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to construct field " + f.getName() + " type " + fieldType.getName(), t);
                continue;
            }
            EntityTypingHandler.initConfigurationPartRecurse(childConfigPart);
        }
    }
}

