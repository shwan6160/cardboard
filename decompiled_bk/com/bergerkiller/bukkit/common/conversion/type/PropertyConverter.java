/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.core.DirectionHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.ItemEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PropertyConverter<T>
extends Converter<Object, T> {
    @Deprecated
    public static final PropertyConverter<Integer> toItemId;
    public static final PropertyConverter<Material> toItemMaterial;
    public static final PropertyConverter<Integer> toPaintingFacingId;
    public static final PropertyConverter<Object> toPaintingFacing;
    public static final PropertyConverter<EntityType> toMinecartType;
    public static final PropertyConverter<UUID> toGameProfileId;
    public static final PropertyConverter<Object> toGameProfileFromId;

    public PropertyConverter(Class<?> outputType) {
        super(Object.class, outputType);
    }

    static {
        CommonBootstrap.initCommonServer();
        toItemId = new PropertyConverter<Integer>(Integer.class){

            @Override
            public Integer convertInput(Object value) {
                Material mat = (Material)toItemMaterial.convert(value);
                if (mat == null) {
                    return null;
                }
                return mat.getId();
            }
        };
        toItemMaterial = new PropertyConverter<Material>(Material.class){

            @Override
            public Material convertInput(Object value) {
                if (ItemEntityHandle.T.isAssignableFrom(value)) {
                    value = ((Template.Method)ItemEntityHandle.T.getItemStack.raw).invoke(value);
                }
                if (ItemStackHandle.T.isAssignableFrom(value)) {
                    value = ItemStackHandle.T.getItem.invoke(value);
                }
                if (ItemHandle.T.isAssignableFrom(value)) {
                    return WrapperConversion.toMaterialFromItemHandle(value);
                }
                if (value instanceof ItemStack) {
                    return ((ItemStack)value).getType();
                }
                if (value instanceof Block) {
                    return ((Block)value).getType();
                }
                return (Material)Conversion.toMaterial.convert(value);
            }
        };
        toPaintingFacingId = new PropertyConverter<Integer>(Integer.class){

            @Override
            public Integer convertInput(Object value) {
                if (value instanceof Number) {
                    return ((Number)value).intValue();
                }
                if (DirectionHandle.T.isAssignableFrom(value)) {
                    DirectionHandle face = DirectionHandle.createHandle(value);
                    for (int i = 0; i < PaintingFacingHelper.faces.length; ++i) {
                        if (!PaintingFacingHelper.faces[i].equals(face)) continue;
                        return i;
                    }
                    return null;
                }
                return null;
            }
        };
        toPaintingFacing = new PropertyConverter<Object>(CommonUtil.getClass("net.minecraft.core.Direction")){

            @Override
            public Object convertInput(Object value) {
                Integer id = (Integer)toPaintingFacingId.convert(value);
                if (id != null) {
                    int idInt = id;
                    if (LogicUtil.isInBounds(PaintingFacingHelper.faces, idInt)) {
                        return PaintingFacingHelper.faces[idInt].getRaw();
                    }
                }
                return null;
            }
        };
        toMinecartType = new PropertyConverter<EntityType>(EntityType.class){

            @Override
            public EntityType convertInput(Object value) {
                if (value instanceof String) {
                    String key = ((String)value).toUpperCase(Locale.ENGLISH);
                    return MinecartMaterialHelper.matNameToMinecartType.getOrDefault(key, null);
                }
                if (EntityHandle.T.isAssignableFrom(value)) {
                    value = WrapperConversion.toEntity(value);
                }
                if (value instanceof Minecart) {
                    return ((Minecart)value).getType();
                }
                if (value instanceof CommonMinecart) {
                    return ((CommonMinecart)value).getType();
                }
                Material material = (Material)Conversion.toMaterial.convert(value);
                if (material == null) {
                    return null;
                }
                return (EntityType)MinecartMaterialHelper.matToMinecartType.get(material);
            }
        };
        toGameProfileId = new PropertyConverter<UUID>(UUID.class){

            @Override
            public UUID convertInput(Object value) {
                if (GameProfileHandle.T.isAssignableFrom(value)) {
                    return GameProfileHandle.T.getId.invoke(value);
                }
                return null;
            }
        };
        toGameProfileFromId = new PropertyConverter<Object>(CommonUtil.getClass("com.mojang.authlib.GameProfile")){

            @Override
            public Object convertInput(Object value) {
                if (value instanceof String) {
                    String name = (String)value;
                    return Template.Handle.getRaw(CommonUtil.getGameProfile(name));
                }
                if (value instanceof UUID) {
                    UUID uuid = (UUID)value;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.getUniqueId().equals(uuid)) continue;
                        return Template.Handle.getRaw(PlayerUtil.getGameProfile(player));
                    }
                    return null;
                }
                return null;
            }
        };
    }

    private static class MinecartMaterialHelper {
        private static final EnumMap<Material, EntityType> matToMinecartType = new EnumMap(Material.class);
        private static final HashMap<String, EntityType> matNameToMinecartType = new HashMap();

        private MinecartMaterialHelper() {
        }

        private static void storeMinecartTypes(CommonEntityType type, String ... materialNames) {
            if (type == CommonEntityType.UNKNOWN) {
                throw new IllegalArgumentException("Unknown entity type registered!");
            }
            for (String materialName : materialNames) {
                matNameToMinecartType.put(materialName, type.entityType);
                matNameToMinecartType.put(materialName.replace("_", ""), type.entityType);
                Material mat = MaterialsByName.getMaterial(materialName);
                if (mat == null) continue;
                matToMinecartType.put(mat, type.entityType);
            }
        }

        static {
            MinecartMaterialHelper.storeMinecartTypes(CommonEntityType.MINECART, "MINECART", "LEGACY_MINECART");
            MinecartMaterialHelper.storeMinecartTypes(CommonEntityType.HOPPER_MINECART, "HOPPER", "HOPPER_MINECART", "LEGACY_HOPPER_MINECART");
            MinecartMaterialHelper.storeMinecartTypes(CommonEntityType.CHEST_MINECART, "CHEST", "STORAGE", "STORAGE_MINECART", "CHEST_MINECART", "LEGACY_CHEST", "LEGACY_STORAGE_MINECART");
            MinecartMaterialHelper.storeMinecartTypes(CommonEntityType.COMMAND_BLOCK_MINECART, "COMMAND_BLOCK", "COMMAND_BLOCK_MINECART", "LEGACY_COMMAND", "LEGACY_COMMAND_MINECART");
            MinecartMaterialHelper.storeMinecartTypes(CommonEntityType.FURNACE_MINECART, "FURNACE", "FURNACE_MINECART", "LEGACY_FURNACE", "LEGACY_POWERED_MINECART");
            MinecartMaterialHelper.storeMinecartTypes(CommonEntityType.TNT_MINECART, "TNT", "TNT_MINECART", "LEGACY_TNT", "LEGACY_EXPLOSIVE_MINECART");
        }
    }

    private static class PaintingFacingHelper {
        private static final DirectionHandle[] faces = new DirectionHandle[]{DirectionHandle.DOWN, DirectionHandle.UP, DirectionHandle.NORTH, DirectionHandle.SOUTH, DirectionHandle.WEST, DirectionHandle.EAST};

        private PaintingFacingHelper() {
        }
    }
}

