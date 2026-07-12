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
import com.bergerkiller.bukkit.common.map.util.ItemModelOverride;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public interface ItemModelPredicate {
    public static final ItemModelPredicate ALWAYS_TRUE_PREDICATE = new ItemModelPredicate(){

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
    };

    public boolean isMatching(CommonItemStack var1);

    default public boolean isMatchingAlways() {
        return false;
    }

    public Optional<CommonItemStack> tryMakeMatching(CommonItemStack var1);

    public static final class ModelChain
    implements ItemModelPredicate,
    IndentedStringBuilder.AppendableToString {
        private final List<ModelChain> allChainLeafs;
        @Nullable
        private final ModelChain parent;
        @Nullable
        private final CommonItemStack itemStack;
        private final ItemModelPredicate predicate;
        @Nullable
        private List<ItemModel.MinecraftModel> allModels = null;
        private List<ItemModel.MinecraftModel> models = Collections.emptyList();

        public static ModelChain newRoot(@Nullable CommonItemStack itemStack) {
            return new ModelChain(null, new ArrayList<ModelChain>(), itemStack, ALWAYS_TRUE_PREDICATE);
        }

        public ModelChain next(ItemModelPredicate predicate) {
            CommonItemStack nextItem = this.itemStack == null ? null : (CommonItemStack)predicate.tryMakeMatching(this.itemStack).orElse(null);
            return new ModelChain(this, this.allChainLeafs, nextItem, predicate);
        }

        public List<ModelChain> getAllLeafs() {
            return this.allChainLeafs;
        }

        private ModelChain(@Nullable ModelChain parent, List<ModelChain> allChainLeafs, @Nullable CommonItemStack itemStack, ItemModelPredicate predicate) {
            this.allChainLeafs = allChainLeafs;
            this.parent = parent;
            this.itemStack = itemStack;
            this.predicate = predicate;
            allChainLeafs.add(this);
        }

        public boolean hasModels() {
            return !this.models.isEmpty();
        }

        public void addModel(ItemModel.MinecraftModel model) {
            this.addModels(Collections.singletonList(model));
        }

        public void addModels(List<ItemModel.MinecraftModel> models) {
            this.models = LogicUtil.combineUnmodifiableLists(this.models, models);
        }

        public ItemModelOverride collectAsOverride() {
            List<ItemModel.MinecraftModel> allModels = this.collectAllModels();
            if (this.isSinglePredicate()) {
                ItemModelOverride asOverride;
                ItemModelPredicate predicate = this.predicate;
                if (predicate instanceof ItemModelOverride && allModels.equals((asOverride = (ItemModelOverride)predicate).getOverrideModels())) {
                    return asOverride;
                }
                return ItemModelOverride.of(this.itemStack, predicate, allModels);
            }
            return ItemModelOverride.of(this.itemStack, this, allModels);
        }

        public List<ItemModel.MinecraftModel> collectAllModels() {
            List<ItemModel.MinecraftModel> parentModels;
            List<ItemModel.MinecraftModel> allModels = this.allModels;
            if (allModels != null) {
                return allModels;
            }
            ModelChain parent = this.parent;
            if (parent == null || (parentModels = parent.collectAllModels()).isEmpty()) {
                this.allModels = this.models;
                return this.allModels;
            }
            this.allModels = LogicUtil.combineUnmodifiableLists(parentModels, this.models);
            return this.allModels;
        }

        private boolean isSinglePredicate() {
            ModelChain parent = this.parent;
            return parent != null && parent.parent == null;
        }

        @Override
        public boolean isMatching(CommonItemStack item) {
            ModelChain parent = this.parent;
            if (parent == null) {
                return true;
            }
            return parent.isMatching(item) && this.predicate.isMatching(item);
        }

        private List<ItemModelPredicate> collectAllPredicates() {
            ModelChain parent = this.parent;
            if (parent == null) {
                return Collections.emptyList();
            }
            return LogicUtil.combineUnmodifiableLists(parent.collectAllPredicates(), Collections.singletonList(this.predicate));
        }

        @Override
        public boolean isMatchingAlways() {
            ModelChain parent = this.parent;
            if (parent == null) {
                return true;
            }
            return parent.isMatchingAlways() && this.predicate.isMatchingAlways();
        }

        @Override
        public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
            ModelChain parent = this.parent;
            if (parent == null) {
                return Optional.of(item);
            }
            return parent.tryMakeMatching(item).flatMap(this.predicate::tryMakeMatching);
        }

        public String toString() {
            return IndentedStringBuilder.toString(this);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("ItemModelPredicate.Chain {");
            IndentedStringBuilder ind = str.indent();
            ind.append("\npredicates: [");
            ind.indent().appendLines(this.collectAllPredicates());
            ind.append("\n]");
            str.append("\n}");
        }
    }
}

