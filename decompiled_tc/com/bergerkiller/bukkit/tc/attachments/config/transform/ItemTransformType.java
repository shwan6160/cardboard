/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.config.transform;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.bukkit.tc.attachments.VirtualArmorStandItemEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayItemEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualHybridItemEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.config.ObjectPosition;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ArmorStandItemTransformType;
import com.bergerkiller.bukkit.tc.attachments.config.transform.HybridItemTransformType;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.bukkit.inventory.ItemStack;

public interface ItemTransformType {
    public String typeName();

    default public String serializedName() {
        return this.category().name() + "_" + this.serializedNameWithoutCategory();
    }

    public String serializedNameWithoutCategory();

    public Category category();

    public ItemTransformType switchCategory(Category var1);

    public VirtualSpawnableObject create(AttachmentManager var1, ItemStack var2);

    public void update(VirtualSpawnableObject var1, ItemStack var2);

    default public void load(VirtualSpawnableObject entity, ConfigurationNode config, ObjectPosition position) {
        this.update(entity, (ItemStack)config.get("item", ItemStack.class));
    }

    public boolean canUpdate(VirtualSpawnableObject var1);

    public static ItemTransformType deserialize(String name) {
        ItemTransformType type = (ItemTransformType)Category.typesBySerializedName.get(name);
        if (type == null && (type = (ItemTransformType)Category.typesBySerializedName.get(name.toUpperCase(Locale.ENGLISH))) == null) {
            type = Category.ARMORSTAND.defaultType();
        }
        if (type.category() != Category.ARMORSTAND && !CommonCapabilities.HAS_DISPLAY_ENTITY) {
            type = type.switchCategory(Category.ARMORSTAND);
        }
        return type;
    }

    public static ItemTransformType deserialize(ConfigurationNode config, String key) {
        String name = (String)config.get(key, String.class, null);
        if (name == null) {
            ItemTransformType defaultType = CommonCapabilities.HAS_DISPLAY_ENTITY ? Category.HYBRID.defaultType() : Category.ARMORSTAND.defaultType();
            config.set(key, (Object)defaultType.serializedName());
            return defaultType;
        }
        return ItemTransformType.deserialize(name);
    }

    public static enum Category {
        DISPLAY("display \u24b9", ItemDisplayMode.HEAD, Display::new),
        ARMORSTAND("armorstand \u24b6", ArmorStandItemTransformType.HEAD, ArmorStand::new),
        HYBRID("hybrid \u24b9/\u24b6", HybridItemTransformType.ARMORSTAND_HEAD, Hybrid::new);

        private final String name;
        private final ItemTransformType defaultType;
        private final List<ItemTransformType> types;
        private static final Map<String, ItemTransformType> typesBySerializedName;

        private <T> Category(String name, T defaultEnumType, Function<T, ItemTransformType> ctor) {
            this(name, ctor.apply(defaultEnumType), (List)Stream.of(CommonUtil.getClassConstants(defaultEnumType.getClass())).map(ctor).collect(StreamUtil.toUnmodifiableList()));
        }

        private Category(String name, ItemTransformType defaultType, List<ItemTransformType> types) {
            this.name = name;
            this.defaultType = defaultType;
            this.types = types;
        }

        public ItemTransformType defaultType() {
            return this.defaultType;
        }

        public List<ItemTransformType> types() {
            return this.types;
        }

        public String toString() {
            return this.name;
        }

        static {
            typesBySerializedName = new HashMap<String, ItemTransformType>();
            for (Category category : Category.values()) {
                for (ItemTransformType type : category.types()) {
                    typesBySerializedName.put(category.name() + "_" + type.serializedNameWithoutCategory(), type);
                }
            }
            for (ItemTransformType type : ARMORSTAND.types()) {
                typesBySerializedName.put(type.serializedNameWithoutCategory(), type);
            }
        }
    }

    public static class Hybrid
    implements ItemTransformType {
        private final HybridItemTransformType transformType;

        public Hybrid(HybridItemTransformType transformType) {
            this.transformType = transformType;
        }

        @Override
        public String typeName() {
            return this.transformType.toString();
        }

        @Override
        public String serializedNameWithoutCategory() {
            return this.transformType.name();
        }

        @Override
        public Category category() {
            return Category.HYBRID;
        }

        @Override
        public ItemTransformType switchCategory(Category newCategory) {
            switch (newCategory.ordinal()) {
                case 1: {
                    return new ArmorStand(this.transformType.armorStandTransform());
                }
                case 0: {
                    return new Display(this.transformType.displayMode());
                }
                case 2: {
                    return this;
                }
            }
            return newCategory.defaultType();
        }

        @Override
        public VirtualSpawnableObject create(AttachmentManager manager, ItemStack item) {
            VirtualHybridItemEntity entity = new VirtualHybridItemEntity(manager);
            entity.setItem(this.transformType, item);
            return entity;
        }

        @Override
        public void update(VirtualSpawnableObject entity, ItemStack item) {
            if (!this.canUpdate(entity)) {
                throw new UnsupportedOperationException("Incompatible virtual entity");
            }
            ((VirtualHybridItemEntity)entity).setItem(this.transformType, item);
        }

        @Override
        public void load(VirtualSpawnableObject entity, ConfigurationNode config, ObjectPosition position) {
            ItemTransformType.super.load(entity, config, position);
            VirtualHybridItemEntity hybrid = (VirtualHybridItemEntity)entity;
            hybrid.setClip((Double)config.getOrDefault("position.clip", (Object)0.0));
            hybrid.setBrightness(VirtualDisplayEntity.loadBrightnessFromConfig(config));
        }

        @Override
        public boolean canUpdate(VirtualSpawnableObject entity) {
            return entity instanceof VirtualHybridItemEntity;
        }

        public String toString() {
            return "ItemTransformType.Hybrid{" + this.transformType.name() + "}";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Hybrid) {
                return ((Hybrid)o).transformType == this.transformType;
            }
            return false;
        }
    }

    public static class Display
    implements ItemTransformType {
        private final ItemDisplayMode mode;

        public Display(ItemDisplayMode mode) {
            this.mode = mode;
        }

        @Override
        public String typeName() {
            return this.mode.toString();
        }

        @Override
        public String serializedNameWithoutCategory() {
            return this.mode.name();
        }

        @Override
        public Category category() {
            return Category.DISPLAY;
        }

        @Override
        public ItemTransformType switchCategory(Category newCategory) {
            switch (newCategory.ordinal()) {
                case 1: {
                    if (this.mode == ItemDisplayMode.HEAD) {
                        return new ArmorStand(ArmorStandItemTransformType.HEAD);
                    }
                    if (this.mode == ItemDisplayMode.THIRD_PERSON_LEFT_HAND) {
                        return new ArmorStand(ArmorStandItemTransformType.LEFT_HAND);
                    }
                    if (this.mode != ItemDisplayMode.THIRD_PERSON_RIGHT_HAND) break;
                    return new ArmorStand(ArmorStandItemTransformType.RIGHT_HAND);
                }
                case 0: {
                    return this;
                }
                case 2: {
                    if (this.mode == ItemDisplayMode.HEAD) {
                        return new Hybrid(HybridItemTransformType.DISPLAY_HEAD);
                    }
                    if (this.mode != ItemDisplayMode.THIRD_PERSON_RIGHT_HAND) break;
                    return new Hybrid(HybridItemTransformType.DISPLAY_RIGHT_HAND);
                }
            }
            return newCategory.defaultType();
        }

        @Override
        public VirtualSpawnableObject create(AttachmentManager manager, ItemStack item) {
            VirtualDisplayItemEntity entity = new VirtualDisplayItemEntity(manager);
            entity.setItem(this.mode, item);
            return entity;
        }

        @Override
        public void update(VirtualSpawnableObject entity, ItemStack item) {
            if (!this.canUpdate(entity)) {
                throw new UnsupportedOperationException("Incompatible virtual entity");
            }
            ((VirtualDisplayItemEntity)entity).setItem(this.mode, item);
        }

        @Override
        public void load(VirtualSpawnableObject entity, ConfigurationNode config, ObjectPosition position) {
            ItemTransformType.super.load(entity, config, position);
            VirtualDisplayItemEntity itemDisplay = (VirtualDisplayItemEntity)entity;
            itemDisplay.setScale(position.size);
            itemDisplay.setClip((Double)config.getOrDefault("position.clip", (Object)0.0));
            itemDisplay.setBrightness(VirtualDisplayEntity.loadBrightnessFromConfig(config));
        }

        @Override
        public boolean canUpdate(VirtualSpawnableObject entity) {
            return entity instanceof VirtualDisplayItemEntity;
        }

        public String toString() {
            return "ItemTransformType.Display{" + this.mode.name() + "}";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Display) {
                return ((Display)o).mode == this.mode;
            }
            return false;
        }
    }

    public static class ArmorStand
    implements ItemTransformType {
        private final ArmorStandItemTransformType transformType;

        public ArmorStand(ArmorStandItemTransformType transformType) {
            this.transformType = transformType;
        }

        @Override
        public String typeName() {
            return this.transformType.toString();
        }

        @Override
        public String serializedNameWithoutCategory() {
            return this.transformType.name();
        }

        @Override
        public String serializedName() {
            return this.serializedNameWithoutCategory();
        }

        @Override
        public Category category() {
            return Category.ARMORSTAND;
        }

        @Override
        public ItemTransformType switchCategory(Category newCategory) {
            switch (newCategory.ordinal()) {
                case 1: {
                    return this;
                }
                case 0: {
                    if (this.transformType.isHead()) {
                        return new Display(ItemDisplayMode.HEAD);
                    }
                    if (this.transformType.isLeftHand()) {
                        return new Display(ItemDisplayMode.THIRD_PERSON_LEFT_HAND);
                    }
                    if (!this.transformType.isRightHand()) break;
                    return new Display(ItemDisplayMode.THIRD_PERSON_RIGHT_HAND);
                }
                case 2: {
                    if (this.transformType.isHead()) {
                        return new Hybrid(this.transformType.isSmallArmorStand() ? HybridItemTransformType.ARMORSTAND_HEAD_SMALL : HybridItemTransformType.ARMORSTAND_HEAD);
                    }
                    if (!this.transformType.isRightHand() && !this.transformType.isLeftHand()) break;
                    return new Hybrid(this.transformType.isSmallArmorStand() ? HybridItemTransformType.ARMORSTAND_RIGHT_HAND_SMALL : HybridItemTransformType.ARMORSTAND_RIGHT_HAND);
                }
            }
            return newCategory.defaultType();
        }

        @Override
        public VirtualSpawnableObject create(AttachmentManager manager, ItemStack item) {
            VirtualArmorStandItemEntity entity = new VirtualArmorStandItemEntity(manager);
            entity.setItem(this.transformType, item);
            return entity;
        }

        @Override
        public void update(VirtualSpawnableObject entity, ItemStack item) {
            if (!this.canUpdate(entity)) {
                throw new UnsupportedOperationException("Incompatible virtual entity");
            }
            ((VirtualArmorStandItemEntity)entity).setItem(this.transformType, item);
        }

        @Override
        public boolean canUpdate(VirtualSpawnableObject entity) {
            return entity instanceof VirtualArmorStandItemEntity;
        }

        public String toString() {
            return "ItemTransformType.ArmorStand{" + this.transformType.name() + "}";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof ArmorStand) {
                return ((ArmorStand)o).transformType == this.transformType;
            }
            return false;
        }
    }
}

