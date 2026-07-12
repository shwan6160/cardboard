/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.debug;

import com.bergerkiller.bukkit.tc.debug.DebugToolType;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTypeListDestinations;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTypeRails;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTypeTrackDistance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DebugToolTypeRegistry {
    private static final List<ToolTypeItem> registry = new ArrayList<ToolTypeItem>();

    public static Optional<DebugToolType> match(String debugToolName) {
        for (ToolTypeItem item : registry) {
            if (!item.condition.test(debugToolName)) continue;
            return Optional.of(item.factory.apply(debugToolName));
        }
        return Optional.empty();
    }

    public static void register(Supplier<DebugToolType> constructor) {
        String debugToolName = constructor.get().getIdentifier();
        DebugToolTypeRegistry.register(n -> n.equalsIgnoreCase(debugToolName), n -> (DebugToolType)constructor.get());
    }

    public static void register(Predicate<String> condition, Function<String, DebugToolType> factory) {
        registry.add(new ToolTypeItem(condition, factory));
    }

    static {
        DebugToolTypeRegistry.register(DebugToolTypeRails::new);
        DebugToolTypeRegistry.register(DebugToolTypeListDestinations::new);
        DebugToolTypeRegistry.register(n -> n.startsWith("Destination "), n -> new DebugToolTypeListDestinations(n.substring(12)));
        DebugToolTypeRegistry.register(DebugToolTypeTrackDistance::new);
    }

    private static class ToolTypeItem {
        public final Predicate<String> condition;
        public final Function<String, DebugToolType> factory;

        public ToolTypeItem(Predicate<String> condition, Function<String, DebugToolType> factory) {
            this.condition = condition;
            this.factory = factory;
        }
    }
}

