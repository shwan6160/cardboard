/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.bungeecord;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.GsonBuilder;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapterFactory;
import com.bergerkiller.bukkit.common.dep.gson.internal.Excluder;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

final class GsonInjections {
    private GsonInjections() {
    }

    public static Field field(@NotNull Class<?> klass, @NotNull String name) throws NoSuchFieldException {
        Field field = klass.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static boolean injectGson(@NotNull Gson existing, @NotNull Consumer<GsonBuilder> accepter) {
        try {
            Field factoriesField = GsonInjections.field(Gson.class, "factories");
            Field builderFactoriesField = GsonInjections.field(GsonBuilder.class, "factories");
            Field builderHierarchyFactoriesField = GsonInjections.field(GsonBuilder.class, "hierarchyFactories");
            GsonBuilder builder = new GsonBuilder();
            accepter.accept(builder);
            List existingFactories = (List)factoriesField.get(existing);
            ArrayList newFactories = new ArrayList();
            newFactories.addAll((List)builderFactoriesField.get(builder));
            Collections.reverse(newFactories);
            newFactories.addAll((List)builderHierarchyFactoriesField.get(builder));
            ArrayList<TypeAdapterFactory> modifiedFactories = new ArrayList<TypeAdapterFactory>(existingFactories);
            int index = GsonInjections.findExcluderIndex(modifiedFactories);
            Collections.reverse(newFactories);
            for (TypeAdapterFactory newFactory : newFactories) {
                modifiedFactories.add(index, newFactory);
            }
            factoriesField.set(existing, modifiedFactories);
            return true;
        }
        catch (IllegalAccessException | NoSuchFieldException ex) {
            return false;
        }
    }

    private static int findExcluderIndex(@NotNull List<TypeAdapterFactory> factories) {
        int size = factories.size();
        for (int i = 0; i < size; ++i) {
            TypeAdapterFactory factory = factories.get(i);
            if (!(factory instanceof Excluder)) continue;
            return i + 1;
        }
        return 0;
    }
}

