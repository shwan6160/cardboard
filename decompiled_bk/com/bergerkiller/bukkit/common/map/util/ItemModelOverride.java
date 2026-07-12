/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.IndentedStringBuilder;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.map.util.ItemModelPredicate;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public interface ItemModelOverride
extends ItemModelPredicate {
    public List<ItemModel.MinecraftModel> getOverrideModels();

    default public boolean hasValidOverrideModels() {
        for (ItemModel.MinecraftModel model : this.getOverrideModels()) {
            if (!model.hasValidModels()) continue;
            return true;
        }
        return false;
    }

    public Optional<CommonItemStack> getItemStack();

    public static ItemModelOverride of(final @Nullable CommonItemStack itemStack, final List<ItemModel.MinecraftModel> models) {
        return new PrintableItemModelOverride(){

            @Override
            public boolean isMatching(CommonItemStack item) {
                return true;
            }

            @Override
            public boolean isMatchingAlways() {
                return true;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                return Optional.of(item);
            }

            @Override
            public List<ItemModel.MinecraftModel> getOverrideModels() {
                return models;
            }

            @Override
            public Optional<CommonItemStack> getItemStack() {
                return Optional.ofNullable(itemStack);
            }

            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("ItemModelOverride {");
                IndentedStringBuilder ind = str.indent();
                ind.append("\ntype: fallback").append("\nitem ").append(itemStack).append("\nmodels: [");
                ind.indent().appendLines(models);
                ind.append("\n]");
                str.append("\n}");
            }
        };
    }

    public static ItemModelOverride of(final @Nullable CommonItemStack itemStack, final ItemModelPredicate predicate, final List<ItemModel.MinecraftModel> models) {
        return new PrintableItemModelOverride(){

            @Override
            public boolean isMatching(CommonItemStack item) {
                return predicate.isMatching(item);
            }

            @Override
            public boolean isMatchingAlways() {
                return predicate.isMatchingAlways();
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                return predicate.tryMakeMatching(item);
            }

            @Override
            public List<ItemModel.MinecraftModel> getOverrideModels() {
                return models;
            }

            @Override
            public Optional<CommonItemStack> getItemStack() {
                return Optional.ofNullable(itemStack);
            }

            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("ItemModelOverride {");
                IndentedStringBuilder ind = str.indent();
                str.indent().append("\ntype: predicate").append("\nitem ").append(itemStack).append("\npredicate: ").append(predicate).append("\nmodels: [");
                ind.indent().appendLines(models);
                ind.append("\n]");
                str.append("\n}");
            }
        };
    }

    public static interface PrintableItemModelOverride
    extends ItemModelOverride,
    IndentedStringBuilder.AppendableToString {
    }
}

