/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.material.Attachable
 *  org.bukkit.material.Button
 *  org.bukkit.material.Chest
 *  org.bukkit.material.DetectorRail
 *  org.bukkit.material.DirectionalContainer
 *  org.bukkit.material.Door
 *  org.bukkit.material.EnderChest
 *  org.bukkit.material.Furnace
 *  org.bukkit.material.Ladder
 *  org.bukkit.material.Lever
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.PoweredRail
 *  org.bukkit.material.PressurePlate
 *  org.bukkit.material.Rails
 *  org.bukkit.material.RedstoneTorch
 *  org.bukkit.material.RedstoneWire
 *  org.bukkit.material.Sign
 *  org.bukkit.material.Torch
 */
package com.bergerkiller.bukkit.common.internal.legacy;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.legacy.CommonHangingSignDataFix;
import com.bergerkiller.bukkit.common.internal.legacy.CommonSignDataFix;
import com.bergerkiller.bukkit.common.internal.legacy.CommonTargetDataFix;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialDataToIBlockData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;
import org.bukkit.material.Button;
import org.bukkit.material.Chest;
import org.bukkit.material.DetectorRail;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.Door;
import org.bukkit.material.EnderChest;
import org.bukkit.material.Furnace;
import org.bukkit.material.Ladder;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PoweredRail;
import org.bukkit.material.PressurePlate;
import org.bukkit.material.Rails;
import org.bukkit.material.RedstoneTorch;
import org.bukkit.material.RedstoneWire;
import org.bukkit.material.Sign;
import org.bukkit.material.Torch;

public class IBlockDataToMaterialData {
    public static final Map<Object, MaterialData> INTERNAL_IBLOCKDATA_TO_MATERIALDATA = new HashMap<Object, MaterialData>();
    private static final Map<Material, MaterialDataBuilder> materialdata_builders = new EnumMap<Material, MaterialDataBuilder>(Material.class);
    private static final MaterialDataBuilder DEFAULT_BUILDER = (material_type, legacy_data_type, legacy_data_value) -> {
        if (legacy_data_type == null) {
            return new MaterialData(Material.AIR);
        }
        return legacy_data_type.getNewData(legacy_data_value);
    };
    private static final Byte DEFAULT_DATA = 0;
    private static final Map<Material, Byte> materialdata_default_data = new EnumMap<Material, Byte>(Material.class);
    private static final FastMethod<MaterialData> craftbukkitGetMaterialdata = new FastMethod();
    private static final EnumMap<Material, Material> materialToLegacyCache;

    private static void initMaterialDataMap() {
        MaterialData materialData = new MaterialData(MaterialsByName.getMaterial("LEGACY_DOUBLE_STEP"));
        for (int data = 0; data < 8; data = (byte)(data + 1)) {
            materialData.setData((byte)data);
            BlockStateHandle iblockdata = MaterialDataToIBlockData.getIBlockData(materialData);
            IBlockDataToMaterialData.storeMaterialData(iblockdata.set("waterlogged", (Object)true), materialData);
            IBlockDataToMaterialData.storeMaterialData(iblockdata.set("waterlogged", (Object)false), materialData);
        }
        IBlockDataToMaterialData.storeMaterialDataGen("LEGACY_REDSTONE_COMPARATOR_OFF", 0, 7);
        IBlockDataToMaterialData.storeMaterialDataGen("LEGACY_REDSTONE_COMPARATOR_ON", 0, 7);
        IBlockDataToMaterialData.storeMaterialDataGen("LEGACY_PORTAL", 1, 2);
        new CustomMaterialDataBuilder<Button>(){

            @Override
            public Button create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                return new Button(material_type, legacy_data_value);
            }

            @Override
            public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Button button) {
                iblockdata = iblockdata.set("powered", (Object)button.isPowered());
                BlockFace facing = button.getFacing();
                if (!FaceUtil.isVertical(facing)) {
                    return Arrays.asList(iblockdata.set("face", (Object)"WALL").set("facing", (Object)facing));
                }
                iblockdata = iblockdata.set("face", (Object)(facing == BlockFace.UP ? "FLOOR" : "CEILING"));
                return Arrays.asList(iblockdata.set("facing", (Object)BlockFace.NORTH), iblockdata.set("facing", (Object)BlockFace.EAST), iblockdata.set("facing", (Object)BlockFace.SOUTH), iblockdata.set("facing", (Object)BlockFace.WEST));
            }
        }.setTypes("JUNGLE_BUTTON", "SPRUCE_BUTTON", "ACACIA_BUTTON", "BIRCH_BUTTON", "DARK_OAK_BUTTON", "OAK_BUTTON", "STONE_BUTTON").addTypesIf(Common.evaluateMCVersion(">=", "1.16"), "CRIMSON_BUTTON", "WARPED_BUTTON", "POLISHED_BLACKSTONE_BUTTON").addTypesIf(Common.evaluateMCVersion(">=", "1.17"), "POLISHED_BLACKSTONE_BUTTON").addTypesIf(Common.evaluateMCVersion(">=", "1.19"), "MANGROVE_BUTTON").addTypesIf(Common.evaluateMCVersion(">=", "1.19.3"), "BAMBOO_BUTTON").addTypesIf(Common.evaluateMCVersion(">=", "1.19.4"), "CHERRY_BUTTON").addTypesIf(Common.evaluateMCVersion(">=", "1.21.2"), "PALE_OAK_BUTTON").setDataValues(0, 1, 2, 3, 4, 5, 8, 9, 10, 11, 12, 13).build();
        new CustomMaterialDataBuilder<Lever>(){

            @Override
            public Lever create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                return new Lever(material_type, legacy_data_value);
            }

            @Override
            public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Lever button) {
                iblockdata = iblockdata.set("powered", (Object)button.isPowered());
                BlockFace attached = button.getAttachedFace();
                if (attached == BlockFace.UP) {
                    BlockFace facing = (button.getData() & 7) == 7 ? BlockFace.NORTH : BlockFace.EAST;
                    iblockdata = iblockdata.set("face", (Object)"ceiling");
                    return Arrays.asList(iblockdata.set("facing", (Object)facing), iblockdata.set("facing", (Object)facing.getOppositeFace()));
                }
                if (attached == BlockFace.DOWN) {
                    BlockFace facing = (button.getData() & 7) == 5 ? BlockFace.NORTH : BlockFace.EAST;
                    iblockdata = iblockdata.set("face", (Object)"floor");
                    return Arrays.asList(iblockdata.set("facing", (Object)facing), iblockdata.set("facing", (Object)facing.getOppositeFace()));
                }
                iblockdata = iblockdata.set("face", (Object)"wall");
                iblockdata = iblockdata.set("facing", (Object)button.getFacing());
                return Collections.singletonList(iblockdata);
            }
        }.setTypes("LEVER").setDataValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).build();
        new CustomMaterialDataBuilder<PressurePlate>(){

            @Override
            public PressurePlate create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                return new PressurePlate(material_type, legacy_data_value);
            }

            @Override
            public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, PressurePlate plate) {
                return Arrays.asList(iblockdata.set("powered", (Object)plate.isPressed()));
            }
        }.setTypes("JUNGLE_PRESSURE_PLATE", "SPRUCE_PRESSURE_PLATE", "ACACIA_PRESSURE_PLATE", "BIRCH_PRESSURE_PLATE", "DARK_OAK_PRESSURE_PLATE").addTypesIf(Common.evaluateMCVersion(">=", "1.16"), "CRIMSON_PRESSURE_PLATE", "WARPED_PRESSURE_PLATE", "POLISHED_BLACKSTONE_PRESSURE_PLATE").addTypesIf(Common.evaluateMCVersion(">=", "1.17"), "POLISHED_BLACKSTONE_PRESSURE_PLATE").addTypesIf(Common.evaluateMCVersion(">=", "1.19"), "MANGROVE_PRESSURE_PLATE").addTypesIf(Common.evaluateMCVersion(">=", "1.19.3"), "BAMBOO_PRESSURE_PLATE").addTypesIf(Common.evaluateMCVersion(">=", "1.19.4"), "CHERRY_PRESSURE_PLATE").addTypesIf(Common.evaluateMCVersion(">=", "1.21.2"), "PALE_OAK_PRESSURE_PLATE").setDataValues(0, 1).build();
        for (final boolean burning : new boolean[]{true, false}) {
            new CustomMaterialDataBuilder<Furnace>(){

                @Override
                public Material toLegacy(Material m) {
                    return CommonLegacyMaterials.getLegacyMaterial(burning ? "BURNING_FURNACE" : "FURNACE");
                }

                @Override
                public Furnace create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new Furnace(this.toLegacy(material_type), legacy_data_value);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Furnace furnace) {
                    return Collections.singletonList(iblockdata.set("facing", (Object)furnace.getFacing()).set("lit", (Object)burning));
                }
            }.setTypes("FURNACE").setDataValues(2, 3, 4, 5).build();
        }
        new CustomMaterialDataBuilder<Torch>(){

            @Override
            public Torch create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                return new Torch(legacy_data_type, legacy_data_value);
            }

            @Override
            public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Torch torch) {
                return Collections.singletonList(iblockdata.set("facing", (Object)torch.getFacing()));
            }
        }.setTypes("WALL_TORCH").setDataValues(1, 2, 3, 4).build();
        for (final boolean lit : new boolean[]{true, false}) {
            new CustomMaterialDataBuilder<RedstoneTorch>(){

                @Override
                public Material toLegacy(Material m) {
                    return CommonLegacyMaterials.getLegacyMaterial(lit ? "REDSTONE_TORCH_ON" : "REDSTONE_TORCH_OFF");
                }

                @Override
                public RedstoneTorch create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new RedstoneTorch(this.toLegacy(material_type), legacy_data_value);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, RedstoneTorch torch) {
                    return Collections.singletonList(iblockdata.set("facing", (Object)torch.getFacing()).set("lit", (Object)lit));
                }
            }.setTypes("REDSTONE_WALL_TORCH").setDataValues(1, 2, 3, 4).build();
        }
        new CustomMaterialDataBuilder<RedstoneWire>(){

            @Override
            public RedstoneWire create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                return new RedstoneWire(material_type, legacy_data_value);
            }

            @Override
            public List<BlockStateHandle> createStates(BlockStateHandle wire_data, RedstoneWire wire) {
                String[] SIDE_VALUES = new String[]{"up", "side", "none"};
                ArrayList<BlockStateHandle> variants = new ArrayList<BlockStateHandle>(81);
                wire_data = wire_data.set("power", (Object)wire.getData());
                for (String side_north : SIDE_VALUES) {
                    wire_data = wire_data.set("north", (Object)side_north);
                    for (String side_east : SIDE_VALUES) {
                        wire_data = wire_data.set("east", (Object)side_east);
                        for (String side_south : SIDE_VALUES) {
                            wire_data = wire_data.set("south", (Object)side_south);
                            for (String side_west : SIDE_VALUES) {
                                wire_data = wire_data.set("west", (Object)side_west);
                                variants.add(wire_data);
                            }
                        }
                    }
                }
                return variants;
            }

            @Override
            public Material fromLegacy(Material legacyMaterial) {
                return CommonLegacyMaterials.getMaterial("REDSTONE_WIRE");
            }
        }.setTypes("LEGACY_REDSTONE_WIRE").setDataValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).build();
        Material[] materialArray = CommonLegacyMaterials.getAllByName("LEGACY_CHEST", "LEGACY_ENDER_CHEST", "LEGACY_TRAPPED_CHEST");
        Material[] modern_types = CommonLegacyMaterials.getAllByName("CHEST", "ENDER_CHEST", "TRAPPED_CHEST");
        for (int n = 0; n < materialArray.length; ++n) {
            final Material legacy_type = materialArray[n];
            final Material modern_type = modern_types[n];
            final boolean isEnderChest = n == 1;
            new CustomMaterialDataBuilder<DirectionalContainer>(){

                @Override
                public DirectionalContainer create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    if (isEnderChest) {
                        EnderChest chest = new EnderChest();
                        chest.setData(legacy_data_value);
                        return chest;
                    }
                    return new Chest(material_type, legacy_data_value);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle chest_data, DirectionalContainer chest) {
                    chest_data = chest_data.set("facing", (Object)chest.getFacing());
                    return Arrays.asList(chest_data.set("waterlogged", (Object)true).set("type", (Object)"SINGLE"), chest_data.set("waterlogged", (Object)true).set("type", (Object)"LEFT"), chest_data.set("waterlogged", (Object)true).set("type", (Object)"RIGHT"), chest_data.set("waterlogged", (Object)false).set("type", (Object)"SINGLE"), chest_data.set("waterlogged", (Object)false).set("type", (Object)"LEFT"), chest_data.set("waterlogged", (Object)false).set("type", (Object)"RIGHT"));
                }

                @Override
                public Material fromLegacy(Material legacyMaterial) {
                    return modern_type;
                }

                @Override
                public Material toLegacy(Material material) {
                    return legacy_type;
                }
            }.setTypes(legacy_type, modern_type).setDataValues(2, 3, 4, 5).build();
        }
        new CustomMaterialDataBuilder<Ladder>(){

            @Override
            public Ladder create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                return new Ladder(legacy_data_type, legacy_data_value);
            }

            @Override
            public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Ladder ladder) {
                BlockStateHandle base = iblockdata.set("facing", (Object)ladder.getFacing());
                return Arrays.asList(base.set("waterlogged", (Object)false), base.set("waterlogged", (Object)true));
            }
        }.setTypes("LADDER").setDataValues(2, 3, 4, 5).build();
        if (CommonCapabilities.HAS_MATERIAL_SIGN_TYPES) {
            new CustomMaterialDataBuilder<Sign>(){

                @Override
                public Sign create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new CommonSignDataFix(material_type, legacy_data_value, true);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Sign sign) {
                    BlockStateHandle base = iblockdata.set("facing", (Object)sign.getFacing());
                    return Arrays.asList(base.set("waterlogged", (Object)false), base.set("waterlogged", (Object)true));
                }

                @Override
                public Material fromLegacy(Material legacyMaterial) {
                    return CommonLegacyMaterials.getMaterial("OAK_WALL_SIGN");
                }

                @Override
                public Material toLegacy(Material material) {
                    return CommonLegacyMaterials.getLegacyMaterial("WALL_SIGN");
                }
            }.setTypes("ACACIA_WALL_SIGN", "BIRCH_WALL_SIGN", "DARK_OAK_WALL_SIGN", "JUNGLE_WALL_SIGN", "OAK_WALL_SIGN", "SPRUCE_WALL_SIGN", "LEGACY_WALL_SIGN").addTypesIf(CommonBootstrap.evaluateMCVersion(">=", "1.16"), "CRIMSON_WALL_SIGN", "WARPED_WALL_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.19"), "MANGROVE_WALL_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.19.3"), "BAMBOO_WALL_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.19.4"), "CHERRY_WALL_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.21.2"), "PALE_OAK_WALL_SIGN").setDataValues(2, 3, 4, 5).build();
            new CustomMaterialDataBuilder<Sign>(){

                @Override
                public Sign create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new CommonSignDataFix(material_type, legacy_data_value, false);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Sign sign) {
                    BlockStateHandle base = iblockdata.set("rotation", (Object)sign.getData());
                    return Arrays.asList(base.set("waterlogged", (Object)false), base.set("waterlogged", (Object)true));
                }

                @Override
                public Material fromLegacy(Material legacyMaterial) {
                    return CommonLegacyMaterials.getMaterial("OAK_SIGN");
                }

                @Override
                public Material toLegacy(Material material) {
                    return CommonLegacyMaterials.getLegacyMaterial("SIGN_POST");
                }
            }.setTypes("ACACIA_SIGN", "BIRCH_SIGN", "DARK_OAK_SIGN", "JUNGLE_SIGN", "OAK_SIGN", "SPRUCE_SIGN", "LEGACY_SIGN_POST").addTypesIf(CommonBootstrap.evaluateMCVersion(">=", "1.16"), "CRIMSON_SIGN", "WARPED_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.19"), "MANGROVE_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.19.3"), "BAMBOO_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.19.4"), "CHERRY_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.21.2"), "PALE_OAK_SIGN").setDataValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).build();
        } else {
            new CustomMaterialDataBuilder<Sign>(){

                @Override
                public Sign create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new CommonSignDataFix(material_type, legacy_data_value, true);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Sign sign) {
                    BlockStateHandle base = iblockdata.set("facing", (Object)sign.getFacing());
                    return Arrays.asList(base.set("waterlogged", (Object)false), base.set("waterlogged", (Object)true));
                }
            }.setTypes("WALL_SIGN", "LEGACY_WALL_SIGN").setDataValues(2, 3, 4, 5).build();
            new CustomMaterialDataBuilder<Sign>(){

                @Override
                public Sign create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new CommonSignDataFix(material_type, legacy_data_value, false);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, Sign sign) {
                    BlockStateHandle base = iblockdata.set("rotation", (Object)sign.getData());
                    return Arrays.asList(base.set("waterlogged", (Object)false), base.set("waterlogged", (Object)true));
                }
            }.setTypes("SIGN", "LEGACY_SIGN_POST").setDataValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).build();
        }
        if (Common.evaluateMCVersion(">=", "1.16")) {
            new CustomMaterialDataBuilder<CommonTargetDataFix>(){

                @Override
                public CommonTargetDataFix create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new CommonTargetDataFix(material_type, legacy_data_value);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, CommonTargetDataFix target) {
                    return Collections.singletonList(iblockdata.set("power", (Object)target.getData()));
                }
            }.setTypes("TARGET").setDataValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).build();
        }
        if (Common.evaluateMCVersion(">=", "1.17")) {
            new RailMaterialDataBuilder<Rails>("RAIL"){

                @Override
                public Rails create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new Rails(legacy_data_type, legacy_data_value);
                }
            }.build();
            new RailMaterialDataBuilder<DetectorRail>("DETECTOR_RAIL"){

                @Override
                public DetectorRail create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new DetectorRail(legacy_data_type, legacy_data_value);
                }

                @Override
                protected BlockStateHandle apply(BlockStateHandle iblockdata, DetectorRail rails) {
                    return iblockdata.set("powered", (Object)rails.isPressed());
                }
            }.build();
            new RailMaterialDataBuilder<PoweredRail>("POWERED_RAIL"){

                @Override
                public PoweredRail create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new PoweredRail(legacy_data_type, legacy_data_value);
                }

                @Override
                protected BlockStateHandle apply(BlockStateHandle iblockdata, PoweredRail rails) {
                    return iblockdata.set("powered", (Object)rails.isPowered());
                }
            }.build();
            new RailMaterialDataBuilder<PoweredRail>("ACTIVATOR_RAIL"){

                @Override
                public PoweredRail create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new PoweredRail(legacy_data_type, legacy_data_value);
                }

                @Override
                protected BlockStateHandle apply(BlockStateHandle iblockdata, PoweredRail rails) {
                    return iblockdata.set("powered", (Object)rails.isPowered());
                }
            }.build();
        }
        if (Common.evaluateMCVersion(">=", "1.19.3")) {
            new CustomMaterialDataBuilder<CommonHangingSignDataFix>(){

                @Override
                public CommonHangingSignDataFix create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new CommonHangingSignDataFix(material_type, legacy_data_value, false);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, CommonHangingSignDataFix target) {
                    ArrayList<BlockStateHandle> states = new ArrayList<BlockStateHandle>(4);
                    BlockStateHandle base = iblockdata.set("rotation", (Object)target.getData());
                    for (boolean waterlogged : new boolean[]{false, true}) {
                        base = base.set("waterlogged", (Object)waterlogged);
                        for (boolean attached : new boolean[]{false, true}) {
                            states.add(base.set("attached", (Object)attached));
                        }
                    }
                    return states;
                }

                @Override
                public Material toLegacy(Material material) {
                    return CommonLegacyMaterials.getLegacyMaterial("SIGN_POST");
                }
            }.setTypes("OAK_HANGING_SIGN", "SPRUCE_HANGING_SIGN", "BIRCH_HANGING_SIGN", "JUNGLE_HANGING_SIGN", "ACACIA_HANGING_SIGN", "DARK_OAK_HANGING_SIGN", "MANGROVE_HANGING_SIGN", "BAMBOO_HANGING_SIGN", "CRIMSON_HANGING_SIGN", "WARPED_HANGING_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.19.4"), "CHERRY_HANGING_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.21.2"), "PALE_OAK_HANGING_SIGN").setDataValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).build();
            new CustomMaterialDataBuilder<CommonHangingSignDataFix>(){

                @Override
                public CommonHangingSignDataFix create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new CommonHangingSignDataFix(material_type, legacy_data_value, true);
                }

                @Override
                public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, CommonHangingSignDataFix target) {
                    BlockStateHandle base = iblockdata.set("facing", (Object)target.getFacing());
                    return Arrays.asList(base.set("waterlogged", (Object)false), base.set("waterlogged", (Object)true));
                }

                @Override
                public Material toLegacy(Material material) {
                    return CommonLegacyMaterials.getLegacyMaterial("SIGN_POST");
                }
            }.setTypes("OAK_WALL_HANGING_SIGN", "SPRUCE_WALL_HANGING_SIGN", "BIRCH_WALL_HANGING_SIGN", "JUNGLE_WALL_HANGING_SIGN", "ACACIA_WALL_HANGING_SIGN", "DARK_OAK_WALL_HANGING_SIGN", "MANGROVE_WALL_HANGING_SIGN", "BAMBOO_WALL_HANGING_SIGN", "CRIMSON_WALL_HANGING_SIGN", "WARPED_WALL_HANGING_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.19.4"), "CHERRY_WALL_HANGING_SIGN").addTypesIf(Common.evaluateMCVersion(">=", "1.21.2"), "PALE_OAK_WALL_HANGING_SIGN").setDataValues(0, 1, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 2, 3, 4, 5).build();
        }
    }

    private static void storeMaterialDataGen(String legacyTypeName, int data_start, int data_end) {
        MaterialData materialdata = new MaterialData(CommonLegacyMaterials.getMaterial(legacyTypeName));
        for (int data = data_start; data <= data_end; ++data) {
            materialdata.setData((byte)data);
            IBlockDataToMaterialData.storeMaterialData(MaterialDataToIBlockData.getIBlockData(materialdata), materialdata);
        }
    }

    private static void storeMaterialData(BlockStateHandle iblockdata, MaterialData materialdata) {
        INTERNAL_IBLOCKDATA_TO_MATERIALDATA.put(iblockdata.getRaw(), materialdata.clone());
    }

    private static void storeMaterialDataDefault(String name, int data) {
        materialdata_default_data.put(MaterialsByName.getLegacyMaterial(name), (byte)data);
    }

    public static Material toLegacy(Material material) {
        if (materialToLegacyCache != null) {
            return materialToLegacyCache.get(material);
        }
        if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return material;
        }
        if (MaterialsByName.isLegacy(material)) {
            return material;
        }
        Material legacy = materialdata_builders.getOrDefault(material, DEFAULT_BUILDER).toLegacy(material);
        if (legacy == material) {
            legacy = CraftBukkitToLegacy.toLegacy(material);
        }
        return legacy;
    }

    public static MaterialData getMaterialData(BlockStateHandle iblockdata) {
        return craftbukkitGetMaterialdata.invoke(null, iblockdata.getRaw());
    }

    public static MaterialData createMaterialData(Material legacy_data_type) {
        return IBlockDataToMaterialData.createMaterialData(legacy_data_type, legacy_data_type, materialdata_default_data.getOrDefault(legacy_data_type, DEFAULT_DATA));
    }

    public static MaterialData createMaterialData(Material material_type, Material legacy_data_type, byte legacy_data_value) {
        Attachable att;
        MaterialData result = materialdata_builders.getOrDefault(legacy_data_type, DEFAULT_BUILDER).create(material_type, legacy_data_type, legacy_data_value);
        if (result instanceof Attachable && (att = (Attachable)result).getAttachedFace() == null) {
            att.setFacingDirection(BlockFace.NORTH);
        }
        return result;
    }

    private static void storeBuilders(MaterialDataBuilder builder, String ... typeNames) {
        for (String typeName : typeNames) {
            Material type = MaterialsByName.getMaterial(typeName);
            if (type == null) continue;
            materialdata_builders.put(type, builder);
        }
    }

    static {
        Map iblockdataToMaterialdata_map = Collections.emptyMap();
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            try {
                Field f;
                Class<?> craftLegacyClass = Class.forName(Resolver.resolveClassPath("org.bukkit.craftbukkit.legacy.CraftLegacy"), true, IBlockDataToMaterialData.class.getClassLoader());
                if (CommonBootstrap.evaluateMCVersion(">=", "26.1")) {
                    try {
                        f = craftLegacyClass.getDeclaredField("stateToMaterial");
                    }
                    catch (NoSuchFieldException ex) {
                        f = craftLegacyClass.getDeclaredField("dataToMaterial");
                    }
                } else {
                    f = craftLegacyClass.getDeclaredField("dataToMaterial");
                }
                f.setAccessible(true);
                iblockdataToMaterialdata_map = (Map)LogicUtil.unsafeCast(f.get(null));
                f.setAccessible(false);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize CraftLegacy dataToMaterial", t);
            }
        }
        INTERNAL_IBLOCKDATA_TO_MATERIALDATA.putAll(iblockdataToMaterialdata_map);
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(CommonUtil.getClass("org.bukkit.craftbukkit.util.CraftMagicNumbers"));
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            craftbukkitGetMaterialdata.init(new MethodDeclaration(resolver, "public static org.bukkit.material.MaterialData getMaterialData(net.minecraft.world.level.block.state.BlockState iblockdata) {\n    Object materialdata_raw = com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData.INTERNAL_IBLOCKDATA_TO_MATERIALDATA.get(iblockdata);\n    org.bukkit.material.MaterialData materialdata = (org.bukkit.material.MaterialData) materialdata_raw;\n    org.bukkit.Material type = CraftMagicNumbers.getMaterial(iblockdata.getBlock());\n    org.bukkit.Material data_type;\n    byte data_value;\n    if (materialdata != null) {\n        data_type = materialdata.getItemType();\n        data_value = materialdata.getData();\n    } else {\n        data_type = org.bukkit.craftbukkit.legacy.CraftLegacy.toLegacy(type);\n        data_value = org.bukkit.craftbukkit.legacy.CraftLegacy.toLegacyData(iblockdata);\n    }\n    return com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData.createMaterialData(type, data_type, data_value);\n}"));
        } else {
            craftbukkitGetMaterialdata.init(new MethodDeclaration(resolver, "public static org.bukkit.material.MaterialData getMaterialData(net.minecraft.world.level.block.state.BlockState iblockdata) {\n    net.minecraft.world.level.block.Block block = iblockdata.getBlock();\n    org.bukkit.Material data_type = CraftMagicNumbers.getMaterial(block);\n    byte data_value = (byte) block.toLegacyData(iblockdata);\n    return com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData.createMaterialData(data_type, data_type, data_value);\n}"));
        }
        IBlockDataToMaterialData.storeBuilders((material_type, legacy_data_type, legacy_data_value) -> new PressurePlate(material_type, legacy_data_value), "LEGACY_GOLD_PLATE", "LEGACY_IRON_PLATE");
        IBlockDataToMaterialData.storeBuilders((material_type, legacy_data_type, legacy_data_value) -> new Door(material_type, legacy_data_value), "LEGACY_JUNGLE_DOOR", "LEGACY_ACACIA_DOOR", "LEGACY_DARK_OAK_DOOR", "LEGACY_SPRUCE_DOOR", "LEGACY_BIRCH_DOOR");
        IBlockDataToMaterialData.storeMaterialDataDefault("FURNACE", 2);
        IBlockDataToMaterialData.storeMaterialDataDefault("BURNING_FURNACE", 2);
        IBlockDataToMaterialData.storeMaterialDataDefault("REDSTONE_TORCH_OFF", 5);
        IBlockDataToMaterialData.storeMaterialDataDefault("REDSTONE_TORCH_ON", 5);
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            IBlockDataToMaterialData.initMaterialDataMap();
        }
        IBlockDataToMaterialData.storeMaterialData(CraftMagicNumbersHandle.getBlockDataFromMaterial(Material.AIR), new MaterialData(CommonLegacyMaterials.getLegacyMaterial("AIR")));
        EnumMap<Material, Material> initMaterialToLegacy = new EnumMap<Material, Material>(Material.class);
        for (Material material : MaterialsByName.getAllMaterials()) {
            initMaterialToLegacy.put(material, IBlockDataToMaterialData.toLegacy(material));
        }
        materialToLegacyCache = initMaterialToLegacy;
    }

    private static abstract class CustomMaterialDataBuilder<T extends MaterialData>
    implements MaterialDataBuilder {
        private Material[] types;
        private int[] data_values;

        private CustomMaterialDataBuilder() {
        }

        public abstract T create(Material var1, Material var2, byte var3);

        public abstract List<BlockStateHandle> createStates(BlockStateHandle var1, T var2);

        public CustomMaterialDataBuilder<T> setTypes(String ... names) {
            this.types = CommonLegacyMaterials.getAllByName(names);
            return this;
        }

        public CustomMaterialDataBuilder<T> addTypesIf(boolean condition, String ... names) {
            if (condition) {
                this.types = LogicUtil.appendArray(this.types, CommonLegacyMaterials.getAllByName(names));
            }
            return this;
        }

        public CustomMaterialDataBuilder<T> setTypes(Material ... types) {
            this.types = types;
            return this;
        }

        public CustomMaterialDataBuilder<T> setDataValues(int ... values) {
            this.data_values = values;
            return this;
        }

        public void build() {
            for (Material type : this.types) {
                materialdata_builders.put(type, this);
                T materialdata = this.create(type, type, (byte)this.data_values[0]);
                BlockStateHandle baseIBlockData = this.getIBlockData(type);
                for (int data_value : this.data_values) {
                    materialdata.setData((byte)data_value);
                    for (BlockStateHandle iBlockData : this.createStates(baseIBlockData, materialdata)) {
                        IBlockDataToMaterialData.storeMaterialData(iBlockData, materialdata);
                    }
                }
            }
        }
    }

    private static interface MaterialDataBuilder {
        public MaterialData create(Material var1, Material var2, byte var3);

        default public BlockStateHandle getIBlockData(Material material) {
            if (CommonLegacyMaterials.isLegacy(material)) {
                material = this.fromLegacy(material);
            }
            return CraftMagicNumbersHandle.getBlockDataFromMaterial(material);
        }

        default public Material fromLegacy(Material legacyMaterial) {
            return legacyMaterial;
        }

        default public Material toLegacy(Material material) {
            return material;
        }
    }

    private static class CraftBukkitToLegacy {
        private static final SafeMethod<Material> craftbukkitToLegacy = new SafeMethod(CommonUtil.getClass("org.bukkit.craftbukkit.legacy.CraftLegacy"), "toLegacy", Material.class);

        private CraftBukkitToLegacy() {
        }

        public static Material toLegacy(Material material) {
            return craftbukkitToLegacy.invoke(null, material);
        }
    }

    private static abstract class RailMaterialDataBuilder<T extends Rails>
    extends CustomMaterialDataBuilder<T> {
        public RailMaterialDataBuilder(String materialName) {
            this.setTypes(materialName);
            if (materialName.equals("RAIL")) {
                this.setDataValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            } else {
                this.setDataValues(0, 1, 2, 3, 4, 5, 8, 9, 10, 11, 12, 13);
            }
        }

        protected BlockStateHandle apply(BlockStateHandle iblockdata, T rails) {
            return iblockdata;
        }

        @Override
        public List<BlockStateHandle> createStates(BlockStateHandle iblockdata, T rails) {
            BlockFace dir = rails.getDirection();
            BlockStateHandle base = rails.isOnSlope() ? iblockdata.set("shape", (Object)("ascending_" + dir.name().toLowerCase(Locale.ENGLISH))) : (dir == BlockFace.NORTH || dir == BlockFace.SOUTH ? iblockdata.set("shape", (Object)"north_south") : (dir == BlockFace.EAST || dir == BlockFace.WEST ? iblockdata.set("shape", (Object)"east_west") : iblockdata.set("shape", (Object)dir.getOppositeFace().name().toLowerCase(Locale.ENGLISH))));
            base = this.apply(base, rails);
            return Arrays.asList(base.set("waterlogged", (Object)false), base.set("waterlogged", (Object)true));
        }
    }
}

