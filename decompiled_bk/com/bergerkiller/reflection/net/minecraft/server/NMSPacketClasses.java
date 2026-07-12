/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Difficulty
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InventoryClickType;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.WindowType;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundCustomPayloadPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundDisconnectPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundKeepAlivePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundResourcePackPushPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ServerboundClientInformationPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ServerboundKeepAlivePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ServerboundResourcePackPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddPaintingPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddPlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAnimatePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBlockDestructionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBlockEventPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBlockUpdatePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundContainerClosePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundContainerSetContentPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundContainerSetDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundCooldownPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundCustomSoundPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityEventPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLevelEventPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLevelParticlesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLoginPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveVehiclePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundOpenScreenPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundResetScorePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRespawnPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRotateHeadPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetCameraPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEquipmentPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetExperiencePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetHealthPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetObjectivePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetScorePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetTimePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSoundPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundTabListPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityWeatherHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutUpdateSignHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundAttackPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundChatPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundClientCommandPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundContainerClickPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundContainerClosePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundInteractPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMovePlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMoveVehiclePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPaddleBoatPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerActionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerCommandPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerInputPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundSignUpdatePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundSwingPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundUseItemOnPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundUseItemPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeInstanceHandle;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;

public class NMSPacketClasses {

    public static class NMSServerboundClientTickEndPacket
    extends NMSPacket {
    }

    public static class NMSClientboundBundlePacket
    extends NMSPacket {
        @Override
        public boolean isOutGoing() {
            return true;
        }
    }

    public static class NMSPacketPlayOutUpdateSign
    extends NMSPacket {
        public final FieldAccessor<World> world;
        public final FieldAccessor<IntVector3> position;
        public final FieldAccessor<ChatText[]> lines;

        public NMSPacketPlayOutUpdateSign() {
            this.world = PacketPlayOutUpdateSignHandle.T.world.toFieldAccessor();
            this.position = PacketPlayOutUpdateSignHandle.T.position.toFieldAccessor();
            this.lines = PacketPlayOutUpdateSignHandle.T.lines.toFieldAccessor();
        }
    }

    public static class NMSClientboundLevelParticlesPacket
    extends NMSPacket {
        public final FieldAccessor<Double> x = new SafeDirectField<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundLevelParticlesPacketHandle.T.getPosX.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundLevelParticlesPacketHandle.T.setPosX.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Double> y = new SafeDirectField<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundLevelParticlesPacketHandle.T.getPosY.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundLevelParticlesPacketHandle.T.setPosY.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Double> z = new SafeDirectField<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundLevelParticlesPacketHandle.T.getPosZ.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundLevelParticlesPacketHandle.T.setPosZ.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> randomX;
        public final FieldAccessor<Float> randomY;
        public final FieldAccessor<Float> randomZ;
        public final FieldAccessor<Float> speed;
        public final FieldAccessor<Integer> particleCount;
        public final FieldAccessor<Boolean> overrideLimiter;

        public NMSClientboundLevelParticlesPacket() {
            this.randomX = ClientboundLevelParticlesPacketHandle.T.randomX.toFieldAccessor();
            this.randomY = ClientboundLevelParticlesPacketHandle.T.randomY.toFieldAccessor();
            this.randomZ = ClientboundLevelParticlesPacketHandle.T.randomZ.toFieldAccessor();
            this.speed = ClientboundLevelParticlesPacketHandle.T.speed.toFieldAccessor();
            this.particleCount = ClientboundLevelParticlesPacketHandle.T.count.toFieldAccessor();
            this.overrideLimiter = ClientboundLevelParticlesPacketHandle.T.overrideLimiter.toFieldAccessor();
        }

        @Override
        public CommonPacket newInstance() {
            return new CommonPacket(((Template.StaticMethod)ClientboundLevelParticlesPacketHandle.T.createNew.raw).invoke(), this);
        }
    }

    public static class NMSClientboundLevelEventPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> effectId;
        public final TranslatorFieldAccessor<IntVector3> position;
        public final FieldAccessor<Integer> data;
        public final FieldAccessor<Boolean> noRelativeVolume;

        public NMSClientboundLevelEventPacket() {
            this.effectId = ClientboundLevelEventPacketHandle.T.effectId.toFieldAccessor();
            this.position = ClientboundLevelEventPacketHandle.T.position.toFieldAccessor();
            this.data = ClientboundLevelEventPacketHandle.T.data.toFieldAccessor();
            this.noRelativeVolume = ClientboundLevelEventPacketHandle.T.globalEvent.toFieldAccessor();
        }
    }

    public static class NMSClientboundContainerSetContentPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> windowId;
        public final FieldAccessor<List<ItemStack>> items;

        public NMSClientboundContainerSetContentPacket() {
            this.windowId = ClientboundContainerSetContentPacketHandle.T.windowId.toFieldAccessor();
            this.items = ClientboundContainerSetContentPacketHandle.T.items.toFieldAccessor();
        }
    }

    public static class NMSClientboundContainerSetDataPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> windowId;
        public final FieldAccessor<Integer> id;
        public final FieldAccessor<Integer> value;

        public NMSClientboundContainerSetDataPacket() {
            this.windowId = ClientboundContainerSetDataPacketHandle.T.windowId.toFieldAccessor();
            this.id = ClientboundContainerSetDataPacketHandle.T.id.toFieldAccessor();
            this.value = ClientboundContainerSetDataPacketHandle.T.value.toFieldAccessor();
        }
    }

    public static class NMSClientboundMoveVehiclePacket
    extends NMSPacket {
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>(){

            @Override
            public Double get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getPosX.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>(){

            @Override
            public Double get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getPosY.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>(){

            @Override
            public Double get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getPosZ.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>(){

            @Override
            public Float get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getYaw.invoke(o);
            }

            @Override
            public boolean set(Object o, Float aFloat) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>(){

            @Override
            public Float get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getPitch.invoke(o);
            }

            @Override
            public boolean set(Object o, Float aFloat) {
                return false;
            }
        };

        public CommonPacket newInstance(double posX, double posY, double posZ, float yaw, float pitch) {
            return ClientboundMoveVehiclePacketHandle.createNew(posX, posY, posZ, yaw, pitch).toCommonPacket();
        }
    }

    public static class NMSClientboundSetTimePacket
    extends NMSPacket {
        public final FieldAccessor<Long> age;

        public NMSClientboundSetTimePacket() {
            this.age = ClientboundSetTimePacketHandle.T.gameTime.toFieldAccessor();
        }
    }

    public static class NMSClientboundSetHealthPacket
    extends NMSPacket {
        public final FieldAccessor<Float> health;
        public final FieldAccessor<Integer> food;
        public final FieldAccessor<Float> foodSaturation;

        public NMSClientboundSetHealthPacket() {
            this.health = ClientboundSetHealthPacketHandle.T.health.toFieldAccessor();
            this.food = ClientboundSetHealthPacketHandle.T.food.toFieldAccessor();
            this.foodSaturation = ClientboundSetHealthPacketHandle.T.foodSaturation.toFieldAccessor();
        }
    }

    public static class NMSClientboundUpdateAttributesPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;

        public NMSClientboundUpdateAttributesPacket() {
            this.entityId = ClientboundUpdateAttributesPacketHandle.T.entityId.toFieldAccessor();
        }

        public CommonPacket newInstance(int entityId, Collection<AttributeInstanceHandle> attributes) {
            return ClientboundUpdateAttributesPacketHandle.createNew(entityId, attributes).toCommonPacket();
        }
    }

    public static class NMSClientboundForgetLevelChunkPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> x = new SafeDirectField<Integer>(){

            @Override
            public Integer get(Object instance) {
                return ClientboundForgetLevelChunkPacketHandle.T.getCx.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                ClientboundForgetLevelChunkPacketHandle.createHandle(instance).setCx(value);
                return true;
            }
        };
        public final FieldAccessor<Integer> z = new SafeDirectField<Integer>(){

            @Override
            public Integer get(Object instance) {
                return ClientboundForgetLevelChunkPacketHandle.T.getCz.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                ClientboundForgetLevelChunkPacketHandle.createHandle(instance).setCz(value);
                return true;
            }
        };
    }

    public static class NMSClientboundBlockEntityDataPacket
    extends NMSPacket {
        public final TranslatorFieldAccessor<IntVector3> position;
        public final FieldAccessor<BlockStateType> type;
        public final FieldAccessor<CommonTagCompound> data;

        public NMSClientboundBlockEntityDataPacket() {
            this.position = ClientboundBlockEntityDataPacketHandle.T.position.toFieldAccessor();
            this.type = ClientboundBlockEntityDataPacketHandle.T.type.toFieldAccessor();
            this.data = ClientboundBlockEntityDataPacketHandle.T.data.toFieldAccessor();
        }

        public CommonPacket newInstance(IntVector3 blockPosition, BlockStateType type, CommonTagCompound data) {
            return ClientboundBlockEntityDataPacketHandle.createNew(blockPosition, type, data).toCommonPacket();
        }
    }

    public static class NMSClientboundCommandSuggestionsPacket
    extends NMSPacket {
    }

    public static class NMSClientboundAwardStatsPacket
    extends NMSPacket {
    }

    @Deprecated
    public static class NMSClientboundSetDefaultSpawnPositionPacket
    extends NMSPacket {
        public final FieldAccessor<IntVector3> position = new SafeDirectField<IntVector3>(){

            @Override
            public IntVector3 get(Object o) {
                return ClientboundSetDefaultSpawnPositionPacketHandle.T.getSpawn.invoke(o).position();
            }

            @Override
            public boolean set(Object o, IntVector3 intVector3) {
                return false;
            }
        };
    }

    public static class NMSPacketPlayOutSpawnEntityWeather
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<Double> posX;
        public final FieldAccessor<Double> posY;
        public final FieldAccessor<Double> posZ;
        public final FieldAccessor<Integer> type;

        public NMSPacketPlayOutSpawnEntityWeather() {
            this.entityId = PacketPlayOutSpawnEntityWeatherHandle.T.entityId.toFieldAccessor();
            this.posX = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).getPosX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).setPosX(value);
                    return true;
                }
            };
            this.posY = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).getPosY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).setPosY(value);
                    return true;
                }
            };
            this.posZ = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).getPosZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).setPosZ(value);
                    return true;
                }
            };
            this.type = PacketPlayOutSpawnEntityWeatherHandle.T.type.toFieldAccessor();
        }
    }

    public static class NMSClientboundAddPaintingPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;

        public NMSClientboundAddPaintingPacket() {
            this.entityId = ClientboundAddPaintingPacketHandle.T.entityId.toFieldAccessor();
        }

        @Override
        protected boolean matchPacket(Object packetHandle) {
            return !CommonCapabilities.ENTITY_SPAWN_PACKETS_MERGED;
        }
    }

    public static class NMSClientboundAddMobPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<UUID> entityUUID;
        public final FieldAccessor<EntityType> entityType;
        public final FieldAccessor<Double> posX;
        public final FieldAccessor<Double> posY;
        public final FieldAccessor<Double> posZ;
        public final FieldAccessor<Double> motX;
        public final FieldAccessor<Double> motY;
        public final FieldAccessor<Double> motZ;
        public final FieldAccessor<Float> yaw;
        public final FieldAccessor<Float> pitch;
        public final FieldAccessor<Float> headYaw;

        public NMSClientboundAddMobPacket() {
            this.entityId = ClientboundAddMobPacketHandle.T.entityId.toFieldAccessor();
            this.entityUUID = ClientboundAddMobPacketHandle.T.entityUUID.toFieldAccessor().ignoreInvalid(new UUID(0L, 0L));
            this.entityType = new SafeDirectField<EntityType>(){

                @Override
                public EntityType get(Object instance) {
                    return ClientboundAddMobPacketHandle.createHandle(instance).getEntityType();
                }

                @Override
                public boolean set(Object instance, EntityType value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setEntityType(value);
                    return true;
                }
            };
            this.posX = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddMobPacketHandle.createHandle(instance).getPosX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setPosX(value);
                    return true;
                }
            };
            this.posY = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddMobPacketHandle.createHandle(instance).getPosY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setPosY(value);
                    return true;
                }
            };
            this.posZ = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddMobPacketHandle.createHandle(instance).getPosZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setPosZ(value);
                    return true;
                }
            };
            this.motX = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddMobPacketHandle.createHandle(instance).getMotX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setMotX(value);
                    return true;
                }
            };
            this.motY = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddMobPacketHandle.createHandle(instance).getMotY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setMotY(value);
                    return true;
                }
            };
            this.motZ = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddMobPacketHandle.createHandle(instance).getMotZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setMotZ(value);
                    return true;
                }
            };
            this.yaw = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundAddMobPacketHandle.createHandle(instance).getYaw());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setYaw(value.floatValue());
                    return true;
                }
            };
            this.pitch = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundAddMobPacketHandle.createHandle(instance).getPitch());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setPitch(value.floatValue());
                    return true;
                }
            };
            this.headYaw = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundAddMobPacketHandle.createHandle(instance).getHeadYaw());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundAddMobPacketHandle.createHandle(instance).setHeadYaw(value.floatValue());
                    return true;
                }
            };
        }

        @Override
        protected boolean matchPacket(Object packetHandle) {
            return !CommonCapabilities.ENTITY_SPAWN_PACKETS_MERGED;
        }
    }

    public static class NMSClientboundAddExperienceOrbPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<Double> posX;
        public final FieldAccessor<Double> posY;
        public final FieldAccessor<Double> posZ;
        public final FieldAccessor<Integer> experience;

        public NMSClientboundAddExperienceOrbPacket() {
            this.entityId = ClientboundAddExperienceOrbPacketHandle.T.entityId.toFieldAccessor();
            this.posX = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddExperienceOrbPacketHandle.createHandle(instance).getPosX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddExperienceOrbPacketHandle.createHandle(instance).setPosX(value);
                    return true;
                }
            };
            this.posY = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddExperienceOrbPacketHandle.createHandle(instance).getPosY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddExperienceOrbPacketHandle.createHandle(instance).setPosY(value);
                    return true;
                }
            };
            this.posZ = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddExperienceOrbPacketHandle.createHandle(instance).getPosZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddExperienceOrbPacketHandle.createHandle(instance).setPosZ(value);
                    return true;
                }
            };
            this.experience = ClientboundAddExperienceOrbPacketHandle.T.experience.toFieldAccessor();
        }
    }

    public static class NMSClientboundAddEntityPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<UUID> UUID;
        public final FieldAccessor<Double> posX;
        public final FieldAccessor<Double> posY;
        public final FieldAccessor<Double> posZ;
        public final FieldAccessor<Double> motX;
        public final FieldAccessor<Double> motY;
        public final FieldAccessor<Double> motZ;
        public final FieldAccessor<Float> pitch;
        public final FieldAccessor<Float> yaw;
        public final FieldAccessor<Integer> extraData;
        @Deprecated
        public final FieldAccessor<Integer> entityType;
        public final FieldAccessor<EntityType> bukkitEntityType;

        public NMSClientboundAddEntityPacket() {
            this.entityId = ClientboundAddEntityPacketHandle.T.entityId.toFieldAccessor();
            this.UUID = ClientboundAddEntityPacketHandle.T.entityUUID.toFieldAccessor().ignoreInvalid(new UUID(0L, 0L));
            this.posX = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddEntityPacketHandle.createHandle(instance).getPosX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setPosX(value);
                    return true;
                }
            };
            this.posY = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddEntityPacketHandle.createHandle(instance).getPosY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setPosY(value);
                    return true;
                }
            };
            this.posZ = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddEntityPacketHandle.createHandle(instance).getPosZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setPosZ(value);
                    return true;
                }
            };
            this.motX = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddEntityPacketHandle.createHandle(instance).getMotX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setMotX(value);
                    return true;
                }
            };
            this.motY = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddEntityPacketHandle.createHandle(instance).getMotY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setMotY(value);
                    return true;
                }
            };
            this.motZ = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddEntityPacketHandle.createHandle(instance).getMotZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setMotZ(value);
                    return true;
                }
            };
            this.pitch = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundAddEntityPacketHandle.createHandle(instance).getPitch());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setPitch(value.floatValue());
                    return true;
                }
            };
            this.yaw = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundAddEntityPacketHandle.createHandle(instance).getYaw());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setYaw(value.floatValue());
                    return true;
                }
            };
            this.extraData = ClientboundAddEntityPacketHandle.T.extraData.toFieldAccessor();
            this.entityType = new SafeDirectField<Integer>(){

                @Override
                public Integer get(Object instance) {
                    return ClientboundAddEntityPacketHandle.createHandle(instance).getEntityTypeId();
                }

                @Override
                public boolean set(Object instance, Integer value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setEntityTypeId(value);
                    return true;
                }
            };
            this.bukkitEntityType = new SafeDirectField<EntityType>(){

                @Override
                public EntityType get(Object instance) {
                    return ClientboundAddEntityPacketHandle.createHandle(instance).getEntityType();
                }

                @Override
                public boolean set(Object instance, EntityType value) {
                    ClientboundAddEntityPacketHandle.createHandle(instance).setEntityType(value);
                    return true;
                }
            };
        }
    }

    public static class NMSClientboundContainerSetSlotPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> windowId;
        public final FieldAccessor<Integer> slot;
        public final FieldAccessor<ItemStack> item;

        public NMSClientboundContainerSetSlotPacket() {
            this.windowId = ClientboundContainerSetSlotPacketHandle.T.windowId.toFieldAccessor();
            this.slot = ClientboundContainerSetSlotPacketHandle.T.slot.toFieldAccessor();
            this.item = ClientboundContainerSetSlotPacketHandle.T.item.toFieldAccessor();
        }
    }

    public static class NMSClientboundCooldownPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> cooldown;

        public NMSClientboundCooldownPacket() {
            this.cooldown = ClientboundCooldownPacketHandle.T.cooldown.toFieldAccessor();
        }
    }

    public static class NMSClientboundChangeDifficultyPacket
    extends NMSPacket {
        public final FieldAccessor<Difficulty> difficulty;
        public final FieldAccessor<Boolean> hardcore;

        public NMSClientboundChangeDifficultyPacket() {
            this.difficulty = ClientboundChangeDifficultyPacketHandle.T.difficulty.toFieldAccessor();
            this.hardcore = ClientboundChangeDifficultyPacketHandle.T.hardcore.toFieldAccessor();
        }
    }

    @Deprecated
    public static class NMSClientboundSetPlayerTeamPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> method;
        public final FieldAccessor<String> name;
        public final FieldAccessor<ChatText> displayName;
        public final FieldAccessor<ChatText> prefix;
        public final FieldAccessor<ChatText> suffix;
        public final FieldAccessor<String> visibility;
        public final FieldAccessor<String> collisionRule;
        public final FieldAccessor<ChatColor> color;
        public final FieldAccessor<Collection<String>> players;
        public final FieldAccessor<Integer> teamOptionFlags;

        public NMSClientboundSetPlayerTeamPacket() {
            this.method = ClientboundSetPlayerTeamPacketHandle.T.method.toFieldAccessor();
            this.name = ClientboundSetPlayerTeamPacketHandle.T.name.toFieldAccessor();
            this.displayName = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getDisplayName, ClientboundSetPlayerTeamPacketHandle.T.setDisplayName);
            this.prefix = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getPrefix, ClientboundSetPlayerTeamPacketHandle.T.setPrefix);
            this.suffix = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getSuffix, ClientboundSetPlayerTeamPacketHandle.T.setSuffix);
            this.visibility = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getVisibility, ClientboundSetPlayerTeamPacketHandle.T.setVisibility);
            this.collisionRule = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getCollisionRule, ClientboundSetPlayerTeamPacketHandle.T.setCollisionRule);
            this.color = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getColor, ClientboundSetPlayerTeamPacketHandle.T.setColor);
            this.players = ClientboundSetPlayerTeamPacketHandle.T.players.toFieldAccessor();
            this.teamOptionFlags = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getTeamOptionFlags, ClientboundSetPlayerTeamPacketHandle.T.setTeamOptionFlags);
        }

        @Override
        public CommonPacket newInstance() {
            return ClientboundSetPlayerTeamPacketHandle.createNew().toCommonPacket();
        }
    }

    public static class NMSClientboundSetScorePacket
    extends NMSPacket {
        public final FieldAccessor<String> name;
        public final FieldAccessor<String> objName;
        public final FieldAccessor<Integer> value;

        public NMSClientboundSetScorePacket() {
            this.name = ClientboundSetScorePacketHandle.T.name.toFieldAccessor();
            this.objName = ClientboundSetScorePacketHandle.T.objName.toFieldAccessor();
            this.value = ClientboundSetScorePacketHandle.T.value.toFieldAccessor();
        }

        public static CommonPacket createNew(String name, String objectiveName, int value) {
            return ClientboundSetScorePacketHandle.createNew(name, objectiveName, value).toCommonPacket();
        }
    }

    public static class NMSClientboundResetScorePacket
    extends NMSPacket {
        public static CommonPacket createNew(String name, String objectiveName) {
            return ClientboundResetScorePacketHandle.createNew(name, objectiveName).toCommonPacket();
        }
    }

    public static class NMSClientboundSetObjectivePacket
    extends NMSPacket {
        public final FieldAccessor<String> name;
        public final FieldAccessor<ChatText> displayName;
        public final FieldAccessor<Object> criteria;
        public final FieldAccessor<Integer> action;

        public NMSClientboundSetObjectivePacket() {
            this.name = ClientboundSetObjectivePacketHandle.T.name.toFieldAccessor();
            this.displayName = ClientboundSetObjectivePacketHandle.T.displayName.toFieldAccessor();
            this.criteria = ClientboundSetObjectivePacketHandle.T.criteria.toFieldAccessor();
            this.action = ClientboundSetObjectivePacketHandle.T.action.toFieldAccessor();
        }
    }

    public static class NMSClientboundSetDisplayObjectivePacket
    extends NMSPacket {
        public final FieldAccessor<DisplaySlot> display;
        public final FieldAccessor<String> name;

        public NMSClientboundSetDisplayObjectivePacket() {
            this.display = ClientboundSetDisplayObjectivePacketHandle.T.display.toFieldAccessor();
            this.name = ClientboundSetDisplayObjectivePacketHandle.T.name.toFieldAccessor();
        }
    }

    public static class NMSClientboundRespawnPacket
    extends NMSPacket {
        public final FieldAccessor<DimensionType> dimensionType = new SafeDirectField<DimensionType>(){

            @Override
            public DimensionType get(Object instance) {
                return ClientboundRespawnPacketHandle.T.getDimensionType.invoke(instance);
            }

            @Override
            public boolean set(Object instance, DimensionType value) {
                return false;
            }
        };
        public final FieldAccessor<GameMode> gamemode = new SafeDirectField<GameMode>(){

            @Override
            public GameMode get(Object instance) {
                return ClientboundRespawnPacketHandle.T.getGamemode.invoke(instance);
            }

            @Override
            public boolean set(Object instance, GameMode value) {
                return false;
            }
        };
        public final FieldAccessor<Difficulty> difficulty = new SafeDirectField<Difficulty>(){

            @Override
            public Difficulty get(Object instance) {
                if (ClientboundRespawnPacketHandle.T.difficulty.isAvailable()) {
                    return ClientboundRespawnPacketHandle.T.difficulty.get(instance);
                }
                return Difficulty.NORMAL;
            }

            @Override
            public boolean set(Object instance, Difficulty value) {
                if (ClientboundRespawnPacketHandle.T.difficulty.isAvailable()) {
                    ClientboundRespawnPacketHandle.T.difficulty.set(instance, value);
                    return true;
                }
                return false;
            }
        };
    }

    @Deprecated
    public static class NMSClientboundResourcePackPopPacket
    extends NMSPacket {
    }

    @Deprecated
    public static class NMSClientboundResourcePackPushPacket
    extends NMSPacket {
        public final FieldAccessor<String> name;
        public final FieldAccessor<String> hash;

        public NMSClientboundResourcePackPushPacket() {
            this.name = ClientboundResourcePackPushPacketHandle.T.url.toFieldAccessor();
            this.hash = ClientboundResourcePackPushPacketHandle.T.hash.toFieldAccessor();
        }
    }

    public static class NMSClientboundRemoveMobEffectPacket
    extends NMSPacket {
        public CommonPacket newInstance(int entityId, PotionEffectType effectType) {
            return ClientboundRemoveMobEffectPacketHandle.createNew(entityId, effectType).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, Holder<MobEffectHandle> mobEffectList) {
            return ClientboundRemoveMobEffectPacketHandle.createNew(entityId, mobEffectList).toCommonPacket();
        }
    }

    public static class NMSClientboundPlayerRotationPacket
    extends NMSPacket {
        private static final boolean IS_SEPARATE_PACKET = CommonBootstrap.evaluateMCVersion(">=", "1.21.2");
        public final FieldAccessor<Float> yaw = new FieldAccessor<Float>(){

            @Override
            public Float get(Object instance) {
                return ClientboundPlayerRotationPacketHandle.T.getYaw.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new FieldAccessor<Float>(){

            @Override
            public Float get(Object instance) {
                return ClientboundPlayerRotationPacketHandle.T.getPitch.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> isYawRelative = new FieldAccessor<Boolean>(){

            @Override
            public Boolean get(Object instance) {
                return ClientboundPlayerRotationPacketHandle.T.isYawRelative.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> isPitchRelative = new FieldAccessor<Boolean>(){

            @Override
            public Boolean get(Object instance) {
                return ClientboundPlayerRotationPacketHandle.T.isPitchRelative.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };

        @Override
        protected boolean matchPacket(Object packetHandle) {
            return IS_SEPARATE_PACKET;
        }
    }

    public static class NMSClientboundPlayerPositionPacket
    extends NMSPacket {
        public final FieldAccessor<Double> x = new FieldAccessor<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getX.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Double> y = new FieldAccessor<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getY.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Double> z = new FieldAccessor<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getZ.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Float> yaw = new FieldAccessor<Float>(){

            @Override
            public Float get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getYaw.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new FieldAccessor<Float>(){

            @Override
            public Float get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getPitch.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Integer> teleportWaitTimer = new SafeDirectField<Integer>(){

            @Override
            public Integer get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.createHandle(instance).getTeleportWaitTimer();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };
    }

    public static class NMSClientboundTabListPacket
    extends NMSPacket {
        public final FieldAccessor<ChatText> header;
        public final FieldAccessor<ChatText> footer;

        public NMSClientboundTabListPacket() {
            this.header = ClientboundTabListPacketHandle.T.header.toFieldAccessor();
            this.footer = ClientboundTabListPacketHandle.T.footer.toFieldAccessor();
        }
    }

    public static class NMSClientboundPlayerInfoRemovePacket
    extends NMSPacket {
        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLAYER_INFO_PACKET_SPLIT) {
                return true;
            }
            return ClientboundPlayerInfoUpdatePacketHandle.isPlayerInfoRemovePacket(packetHandle);
        }
    }

    public static class NMSClientboundPlayerInfoUpdatePacket
    extends NMSPacket {
        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLAYER_INFO_PACKET_SPLIT) {
                return true;
            }
            return !ClientboundPlayerInfoUpdatePacketHandle.isPlayerInfoRemovePacket(packetHandle);
        }
    }

    public static class NMSClientboundOpenScreenPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> windowId;
        public final FieldAccessor<WindowType> windowType;
        public final FieldAccessor<ChatText> windowTitle;

        public NMSClientboundOpenScreenPacket() {
            this.windowId = ClientboundOpenScreenPacketHandle.T.windowId.toFieldAccessor();
            this.windowType = new SafeDirectField<WindowType>(){

                @Override
                public WindowType get(Object instance) {
                    return ClientboundOpenScreenPacketHandle.T.getWindowType.invoke(instance);
                }

                @Override
                public boolean set(Object instance, WindowType value) {
                    ClientboundOpenScreenPacketHandle.T.setWindowType.invoke(instance, (Object)value);
                    return true;
                }
            };
            this.windowTitle = ClientboundOpenScreenPacketHandle.T.windowTitle.toFieldAccessor();
        }
    }

    public static class NMSClientboundOpenSignEditorPacket
    extends NMSPacket {
        public final FieldAccessor<IntVector3> signPosition;

        public NMSClientboundOpenSignEditorPacket() {
            this.signPosition = ClientboundOpenSignEditorPacketHandle.T.signPosition.toFieldAccessor();
        }
    }

    public static class NMSClientboundSoundPacket
    extends NMSPacket {
        public final FieldAccessor<String> category = new SafeDirectField<String>(){

            @Override
            public String get(Object instance) {
                return ClientboundSoundPacketHandle.createHandle(instance).getCategory();
            }

            @Override
            public boolean set(Object instance, String value) {
                ClientboundSoundPacketHandle.createHandle(instance).setCategory(value);
                return true;
            }
        };
        public final FieldAccessor<ResourceKey<SoundEffect>> sound;
        public final FieldAccessor<Integer> x;
        public final FieldAccessor<Integer> y;
        public final FieldAccessor<Integer> z;
        public final FieldAccessor<Float> volume;
        public final FieldAccessor<Float> pitch;

        public NMSClientboundSoundPacket() {
            this.sound = ClientboundSoundPacketHandle.T.sound.toFieldAccessor();
            this.x = ClientboundSoundPacketHandle.T.x.toFieldAccessor();
            this.y = ClientboundSoundPacketHandle.T.y.toFieldAccessor();
            this.z = ClientboundSoundPacketHandle.T.z.toFieldAccessor();
            this.volume = ClientboundSoundPacketHandle.T.volume.toFieldAccessor();
            this.pitch = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundSoundPacketHandle.createHandle(instance).getPitch());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundSoundPacketHandle.createHandle(instance).setPitch(value.floatValue());
                    return true;
                }
            };
        }
    }

    public static class NMSClientboundAddPlayerPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<UUID> uuid;
        public final FieldAccessor<Double> posX;
        public final FieldAccessor<Double> posY;
        public final FieldAccessor<Double> posZ;
        public final FieldAccessor<Float> yaw;
        public final FieldAccessor<Float> pitch;
        public final FieldAccessor<Material> heldItemId;

        public NMSClientboundAddPlayerPacket() {
            this.entityId = ClientboundAddPlayerPacketHandle.T.entityId.toFieldAccessor();
            this.uuid = ClientboundAddPlayerPacketHandle.T.entityUUID.toFieldAccessor();
            this.posX = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddPlayerPacketHandle.createHandle(instance).getPosX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddPlayerPacketHandle.createHandle(instance).setPosX(value);
                    return true;
                }
            };
            this.posY = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddPlayerPacketHandle.createHandle(instance).getPosY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddPlayerPacketHandle.createHandle(instance).setPosY(value);
                    return true;
                }
            };
            this.posZ = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundAddPlayerPacketHandle.createHandle(instance).getPosZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundAddPlayerPacketHandle.createHandle(instance).setPosZ(value);
                    return true;
                }
            };
            this.yaw = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundAddPlayerPacketHandle.createHandle(instance).getYaw());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundAddPlayerPacketHandle.createHandle(instance).setYaw(value.floatValue());
                    return true;
                }
            };
            this.pitch = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundAddPlayerPacketHandle.createHandle(instance).getPitch());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundAddPlayerPacketHandle.createHandle(instance).setPitch(value.floatValue());
                    return true;
                }
            };
            this.heldItemId = ClientboundAddPlayerPacketHandle.T.heldItem.toFieldAccessor().ignoreInvalid(Material.AIR);
        }
    }

    public static class NMSClientboundSetPassengersPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<int[]> mountedEntityIds;

        public NMSClientboundSetPassengersPacket() {
            this.entityId = ClientboundSetPassengersPacketHandle.T.entityId.toFieldAccessor();
            this.mountedEntityIds = ClientboundSetPassengersPacketHandle.T.mountedEntityIds.toFieldAccessor();
        }

        public CommonPacket newInstanceHandles(Entity entity, List<EntityHandle> passengers) {
            int[] passengerIds = new int[passengers.size()];
            for (int i = 0; i < passengerIds.length; ++i) {
                passengerIds[i] = passengers.get(i).getId();
            }
            return ClientboundSetPassengersPacketHandle.createNew(entity.getEntityId(), passengerIds).toCommonPacket();
        }

        public CommonPacket newInstance(Entity vehicle, List<Entity> passengers) {
            int[] passengerIds = new int[passengers.size()];
            for (int i = 0; i < passengerIds.length; ++i) {
                passengerIds[i] = passengers.get(i).getEntityId();
            }
            return ClientboundSetPassengersPacketHandle.createNew(vehicle.getEntityId(), passengerIds).toCommonPacket();
        }
    }

    public static class NMSClientboundLevelChunkWithLightPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> x;
        public final FieldAccessor<Integer> z;
        public final FieldAccessor<CommonTagCompound> heightmaps;
        public final FieldAccessor<byte[]> buffer;
        public final FieldAccessor<List<BlockStateChange>> blockStates;

        public NMSClientboundLevelChunkWithLightPacket() {
            this.x = ClientboundLevelChunkWithLightPacketHandle.T.x.toFieldAccessor();
            this.z = ClientboundLevelChunkWithLightPacketHandle.T.z.toFieldAccessor();
            this.heightmaps = new SafeDirectField<CommonTagCompound>(){

                @Override
                public CommonTagCompound get(Object instance) {
                    return ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).getHeightmaps();
                }

                @Override
                public boolean set(Object instance, CommonTagCompound value) {
                    ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).setHeightmaps(value);
                    return true;
                }
            };
            this.buffer = new SafeDirectField<byte[]>(){

                @Override
                public byte[] get(Object instance) {
                    return ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).getBuffer();
                }

                @Override
                public boolean set(Object instance, byte[] value) {
                    ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).setBuffer(value);
                    return true;
                }
            };
            this.blockStates = new SafeDirectField<List<BlockStateChange>>(){

                @Override
                public List<BlockStateChange> get(Object instance) {
                    return ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).getBlockStates();
                }

                @Override
                public boolean set(Object instance, List<BlockStateChange> value) {
                    ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).setBlockStates(value);
                    return true;
                }
            };
        }
    }

    public static class NMSClientboundMapItemDataPacket
    extends NMSPacket {
    }

    public static class NMSClientboundLoginPacket
    extends NMSPacket {
        public final FieldAccessor<GameMode> gameMode = new SafeDirectField<GameMode>(){

            @Override
            public GameMode get(Object instance) {
                return ClientboundLoginPacketHandle.T.getGameMode.invoke(instance);
            }

            @Override
            public boolean set(Object instance, GameMode value) {
                return false;
            }
        };
        public final FieldAccessor<DimensionType> dimensionType = new SafeDirectField<DimensionType>(){

            @Override
            public DimensionType get(Object instance) {
                return ClientboundLoginPacketHandle.T.getDimensionType.invoke(instance);
            }

            @Override
            public boolean set(Object instance, DimensionType value) {
                return false;
            }
        };
        public final FieldAccessor<Difficulty> difficulty;

        public NMSClientboundLoginPacket() {
            this.difficulty = ClientboundLoginPacketHandle.T.difficulty.toFieldAccessor().ignoreInvalid(Difficulty.NORMAL);
        }
    }

    public static class NMSClientboundDisconnectPacket
    extends NMSPacket {
        public final FieldAccessor<ChatText> reason;

        public NMSClientboundDisconnectPacket() {
            this.reason = ClientboundDisconnectPacketHandle.T.reason.toFieldAccessor();
        }
    }

    public static class NMSClientboundKeepAlivePacket
    extends NMSPacket {
        public final FieldAccessor<Long> key;

        public NMSClientboundKeepAlivePacket() {
            this.key = FieldAccessor.wrapMethods(ClientboundKeepAlivePacketHandle.T.getKey, ClientboundKeepAlivePacketHandle.T.setKey);
        }
    }

    public static class NMSClientboundSetHeldSlotPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> slot;

        public NMSClientboundSetHeldSlotPacket() {
            this.slot = ClientboundSetHeldSlotPacketHandle.T.itemInHandIndex.toFieldAccessor();
        }
    }

    public static class NMSClientboundGameEventPacket
    extends NMSPacket {
    }

    public static class NMSClientboundExplodePacket
    extends NMSPacket {
    }

    public static class NMSClientboundSetExperiencePacket
    extends NMSPacket {
        public final FieldAccessor<Float> bar;
        public final FieldAccessor<Integer> level;
        public final FieldAccessor<Integer> totalXp;
        private final SafeConstructor<CommonPacket> constructor1;

        public NMSClientboundSetExperiencePacket() {
            this.bar = ClientboundSetExperiencePacketHandle.T.experienceProgress.toFieldAccessor();
            this.level = ClientboundSetExperiencePacketHandle.T.experienceLevel.toFieldAccessor();
            this.totalXp = ClientboundSetExperiencePacketHandle.T.totalExperience.toFieldAccessor();
            this.constructor1 = this.getPacketConstructor(Float.TYPE, Integer.TYPE, Integer.TYPE);
        }

        public CommonPacket newInstance(float bar, int level, int totalXp) {
            return this.constructor1.newInstance(Float.valueOf(bar), level, totalXp);
        }
    }

    @Deprecated
    public static class NMSClientboundSetEntityMotionPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<Vector> motion;

        public NMSClientboundSetEntityMotionPacket() {
            this.entityId = ClientboundSetEntityMotionPacketHandle.T.entityId.toFieldAccessor();
            this.motion = new SafeDirectField<Vector>(){

                @Override
                public Vector get(Object o) {
                    return ClientboundSetEntityMotionPacketHandle.T.getMotVector.invoke(o);
                }

                @Override
                public boolean set(Object o, Vector vector) {
                    return false;
                }
            };
        }

        @Deprecated
        public CommonPacket newInstance(Entity entity) {
            return ClientboundSetEntityMotionPacketHandle.createNew(entity).toCommonPacket();
        }

        @Deprecated
        public CommonPacket newInstance(int entityId, double motX, double motY, double motZ) {
            return ClientboundSetEntityMotionPacketHandle.createNew(entityId, motX, motY, motZ).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, Vector velocity) {
            return this.newInstance(entityId, velocity.getX(), velocity.getY(), velocity.getZ());
        }
    }

    public static class NMSClientboundEntityPositionSyncPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId = new FieldAccessor<Integer>(){

            @Override
            public Integer get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getEntityId();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };
        public final FieldAccessor<Double> x = new SafeDirectField<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Double> y = new SafeDirectField<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Double> z = new SafeDirectField<Double>(){

            @Override
            public Double get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>(){

            @Override
            public Float get(Object instance) {
                return Float.valueOf(ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getYaw());
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>(){

            @Override
            public Float get(Object instance) {
                return Float.valueOf(ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getPitch());
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> onGround = new SafeDirectField<Boolean>(){

            @Override
            public Boolean get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).isOnGround();
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };

        @Override
        @Deprecated
        public CommonPacket newInstance() {
            throw new UnsupportedOperationException("Not supported anymore");
        }

        public CommonPacket newInstance(Entity entity) {
            return ClientboundEntityPositionSyncPacketHandle.createNewForEntity(entity).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
            return ClientboundEntityPositionSyncPacketHandle.createNew(entityId, posX, posY, posZ, yaw, pitch, onGround).toCommonPacket();
        }
    }

    public static class NMSClientboundEntityEventPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<Byte> status;

        public NMSClientboundEntityEventPacket() {
            this.entityId = ClientboundEntityEventPacketHandle.T.entityId.toFieldAccessor();
            this.status = ClientboundEntityEventPacketHandle.T.eventId.toFieldAccessor();
        }
    }

    public static class NMSClientboundSetEntityDataPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<List<DataWatcher.PackedItem<Object>>> watchedObjects;

        public NMSClientboundSetEntityDataPacket() {
            this.entityId = ClientboundSetEntityDataPacketHandle.T.entityId.toFieldAccessor();
            this.watchedObjects = ClientboundSetEntityDataPacketHandle.T.metadataItems.toFieldAccessor();
        }

        public CommonPacket newForSpawn(int entityId, DataWatcher dataWatcher) {
            return ClientboundSetEntityDataPacketHandle.createForSpawn(entityId, dataWatcher).toCommonPacket();
        }

        public CommonPacket newForChanges(int entityId, DataWatcher dataWatcher) {
            return ClientboundSetEntityDataPacketHandle.createForChanges(entityId, dataWatcher).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, DataWatcher dataWatcher, boolean sendUnchangedData) {
            return ClientboundSetEntityDataPacketHandle.createNew(entityId, dataWatcher, sendUnchangedData).toCommonPacket();
        }
    }

    public static class NMSClientboundRotateHeadPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<Float> headYaw;

        public NMSClientboundRotateHeadPacket() {
            this.entityId = ClientboundRotateHeadPacketHandle.T.entityId.toFieldAccessor();
            this.headYaw = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundRotateHeadPacketHandle.createHandle(instance).getHeadYaw());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundRotateHeadPacketHandle.createHandle(instance).setHeadYaw(value.floatValue());
                    return true;
                }
            };
        }

        public CommonPacket newInstance(Entity entity, float headRotation) {
            return ClientboundRotateHeadPacketHandle.createNew(entity, headRotation).toCommonPacket();
        }
    }

    @Deprecated
    public static class NMSClientboundSetEquipmentPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;

        public NMSClientboundSetEquipmentPacket() {
            this.entityId = ClientboundSetEquipmentPacketHandle.T.entityId.toFieldAccessor();
        }

        public CommonPacket newInstance(int entityId, EquipmentSlot equipmentSlot, ItemStack item) {
            return ClientboundSetEquipmentPacketHandle.createNew(entityId, equipmentSlot, item).toCommonPacket();
        }
    }

    public static class NMSClientboundUpdateMobEffectPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<PotionEffectType> effect;
        public final FieldAccessor<Integer> effectAmplifier;
        public final FieldAccessor<Integer> effectDuration;
        public final FieldAccessor<Byte> effectFlags;

        public NMSClientboundUpdateMobEffectPacket() {
            this.entityId = ClientboundUpdateMobEffectPacketHandle.T.entityId.toFieldAccessor();
            this.effect = new SafeDirectField<PotionEffectType>(){

                @Override
                public PotionEffectType get(Object instance) {
                    return ClientboundUpdateMobEffectPacketHandle.createHandle(instance).getPotionEffectType();
                }

                @Override
                public boolean set(Object instance, PotionEffectType value) {
                    ClientboundUpdateMobEffectPacketHandle.createHandle(instance).setPotionEffectType(value);
                    return true;
                }
            };
            this.effectAmplifier = new SafeDirectField<Integer>(){

                @Override
                public Integer get(Object instance) {
                    return ClientboundUpdateMobEffectPacketHandle.createHandle(instance).getEffectAmplifier();
                }

                @Override
                public boolean set(Object instance, Integer value) {
                    ClientboundUpdateMobEffectPacketHandle.createHandle(instance).setEffectAmplifier(value);
                    return true;
                }
            };
            this.effectDuration = ClientboundUpdateMobEffectPacketHandle.T.effectDurationTicks.toFieldAccessor();
            this.effectFlags = ClientboundUpdateMobEffectPacketHandle.T.flags.toFieldAccessor();
        }

        public CommonPacket newInstance(int entityId, MobEffectInstanceHandle mobEffect) {
            return new CommonPacket(((Template.StaticMethod)ClientboundUpdateMobEffectPacketHandle.T.createNew.raw).invoke(entityId, mobEffect.getRaw(), false));
        }

        public CommonPacket newInstance(int entityId, PotionEffect effect) {
            return ClientboundUpdateMobEffectPacketHandle.createNew(entityId, effect).toCommonPacket();
        }
    }

    public static class NMSClientboundRemoveEntitiesPacket
    extends NMSPacket {
        public final FieldAccessor<int[]> entityIds = new SafeDirectField<int[]>(){

            @Override
            public int[] get(Object instance) {
                return ClientboundRemoveEntitiesPacketHandle.T.getEntityIds.invoke(instance);
            }

            @Override
            public boolean set(Object instance, int[] value) {
                ClientboundRemoveEntitiesPacketHandle.T.setMultipleEntityIds.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Integer> entityId = new SafeDirectField<Integer>(){

            @Override
            public Integer get(Object instance) {
                return ClientboundRemoveEntitiesPacketHandle.T.getSingleEntityId.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                ClientboundRemoveEntitiesPacketHandle.T.setSingleEntityId.invoke(instance, value);
                return true;
            }
        };

        public boolean canSupportMultipleEntityIds() {
            return CommonCapabilities.PACKET_DESTROY_MULTIPLE;
        }

        public CommonPacket newInstanceSingle(int entityId) {
            Object raw = ((Template.StaticMethod)ClientboundRemoveEntitiesPacketHandle.T.createNewSingle.raw).invoke(entityId);
            return new CommonPacket(raw, PacketType.OUT_ENTITY_DESTROY);
        }

        public CommonPacket newInstanceMultiple(int ... entityIds) {
            Object raw = ((Template.StaticMethod)ClientboundRemoveEntitiesPacketHandle.T.createNewMultiple.raw).invoke(entityIds);
            return new CommonPacket(raw, PacketType.OUT_ENTITY_DESTROY);
        }

        public CommonPacket newInstanceMultiple(Collection<Integer> entityIds) {
            return this.newInstanceMultiple((int[])Conversion.toIntArr.convert(entityIds));
        }

        public CommonPacket newInstanceMultiple(Entity ... entities) {
            int[] ids = new int[entities.length];
            for (int i = 0; i < ids.length; ++i) {
                ids[i] = entities[i].getEntityId();
            }
            return this.newInstanceMultiple(ids);
        }
    }

    public static class NMSClientboundMoveEntityPacketRot
    extends NMSClientboundMoveEntityPacket {
        public NMSClientboundMoveEntityPacketRot() {
            super(ClientboundMoveEntityPacketHandle.RotHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, float dyaw, float dpitch, boolean onGround) {
            return ClientboundMoveEntityPacketHandle.RotHandle.createNew(entityId, dyaw, dpitch, onGround).toCommonPacket();
        }
    }

    public static class NMSClientboundMoveEntityPacketPosRot
    extends NMSClientboundMoveEntityPacket {
        public NMSClientboundMoveEntityPacketPosRot() {
            super(ClientboundMoveEntityPacketHandle.PosRotHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, double dx, double dy, double dz, float dyaw, float dpitch, boolean onGround) {
            return ClientboundMoveEntityPacketHandle.PosRotHandle.createNew(entityId, dx, dy, dz, dyaw, dpitch, onGround).toCommonPacket();
        }
    }

    public static class NMSClientboundMoveEntityPacketPos
    extends NMSClientboundMoveEntityPacket {
        public NMSClientboundMoveEntityPacketPos() {
            super(ClientboundMoveEntityPacketHandle.PosHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, double dx, double dy, double dz, boolean onGround) {
            return ClientboundMoveEntityPacketHandle.PosHandle.createNew(entityId, dx, dy, dz, onGround).toCommonPacket();
        }
    }

    public static class NMSClientboundMoveEntityPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<Double> dx;
        public final FieldAccessor<Double> dy;
        public final FieldAccessor<Double> dz;
        public final FieldAccessor<Float> dyaw;
        public final FieldAccessor<Float> dpitch;
        public final FieldAccessor<Boolean> onGround;

        public NMSClientboundMoveEntityPacket() {
            this.entityId = ClientboundMoveEntityPacketHandle.T.entityId.toFieldAccessor();
            this.dx = new SafeDirectField<Double>(this){
                final /* synthetic */ NMSClientboundMoveEntityPacket this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public Double get(Object instance) {
                    return ClientboundMoveEntityPacketHandle.createHandle(instance).getDeltaX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundMoveEntityPacketHandle.createHandle(instance).setDeltaX(value);
                    return true;
                }
            };
            this.dy = new SafeDirectField<Double>(this){
                final /* synthetic */ NMSClientboundMoveEntityPacket this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public Double get(Object instance) {
                    return ClientboundMoveEntityPacketHandle.createHandle(instance).getDeltaY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundMoveEntityPacketHandle.createHandle(instance).setDeltaY(value);
                    return true;
                }
            };
            this.dz = new SafeDirectField<Double>(this){
                final /* synthetic */ NMSClientboundMoveEntityPacket this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public Double get(Object instance) {
                    return ClientboundMoveEntityPacketHandle.createHandle(instance).getDeltaZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundMoveEntityPacketHandle.createHandle(instance).setDeltaZ(value);
                    return true;
                }
            };
            this.dyaw = new SafeDirectField<Float>(this){
                final /* synthetic */ NMSClientboundMoveEntityPacket this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundMoveEntityPacketHandle.createHandle(instance).getYaw());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundMoveEntityPacketHandle.createHandle(instance).setYaw(value.floatValue());
                    return true;
                }
            };
            this.dpitch = new SafeDirectField<Float>(this){
                final /* synthetic */ NMSClientboundMoveEntityPacket this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundMoveEntityPacketHandle.createHandle(instance).getPitch());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundMoveEntityPacketHandle.createHandle(instance).setPitch(value.floatValue());
                    return true;
                }
            };
            this.onGround = ClientboundMoveEntityPacketHandle.T.onGround.toFieldAccessor();
        }

        protected NMSClientboundMoveEntityPacket(Class<?> packetClass) {
            super(packetClass);
            this.entityId = ClientboundMoveEntityPacketHandle.T.entityId.toFieldAccessor();
            this.dx = new /* invalid duplicate definition of identical inner class */;
            this.dy = new /* invalid duplicate definition of identical inner class */;
            this.dz = new /* invalid duplicate definition of identical inner class */;
            this.dyaw = new /* invalid duplicate definition of identical inner class */;
            this.dpitch = new /* invalid duplicate definition of identical inner class */;
            this.onGround = ClientboundMoveEntityPacketHandle.T.onGround.toFieldAccessor();
        }
    }

    public static class NMSClientboundCustomSoundPacket
    extends NMSPacket {
        public final FieldAccessor<ResourceKey<SoundEffect>> sound;
        public final FieldAccessor<String> category;
        public final FieldAccessor<Double> x;
        public final FieldAccessor<Double> y;
        public final FieldAccessor<Double> z;
        public final FieldAccessor<Float> volume;
        public final FieldAccessor<Float> pitch;

        public NMSClientboundCustomSoundPacket() {
            this.sound = ClientboundCustomSoundPacketHandle.T.sound.toFieldAccessor();
            this.category = new SafeDirectField<String>(){

                @Override
                public String get(Object instance) {
                    return ClientboundCustomSoundPacketHandle.createHandle(instance).getCategory();
                }

                @Override
                public boolean set(Object instance, String value) {
                    ClientboundCustomSoundPacketHandle.createHandle(instance).setCategory(value);
                    return false;
                }
            };
            this.x = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundCustomSoundPacketHandle.createHandle(instance).getX();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundCustomSoundPacketHandle.createHandle(instance).setX(value.floatValue());
                    return true;
                }
            };
            this.y = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundCustomSoundPacketHandle.createHandle(instance).getY();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundCustomSoundPacketHandle.createHandle(instance).setY(value.floatValue());
                    return true;
                }
            };
            this.z = new SafeDirectField<Double>(){

                @Override
                public Double get(Object instance) {
                    return ClientboundCustomSoundPacketHandle.createHandle(instance).getZ();
                }

                @Override
                public boolean set(Object instance, Double value) {
                    ClientboundCustomSoundPacketHandle.createHandle(instance).setZ(value.floatValue());
                    return true;
                }
            };
            this.volume = ClientboundCustomSoundPacketHandle.T.volume.toFieldAccessor();
            this.pitch = new SafeDirectField<Float>(){

                @Override
                public Float get(Object instance) {
                    return Float.valueOf(ClientboundCustomSoundPacketHandle.createHandle(instance).getPitch());
                }

                @Override
                public boolean set(Object instance, Float value) {
                    ClientboundCustomSoundPacketHandle.createHandle(instance).setPitch(value.floatValue());
                    return true;
                }
            };
        }
    }

    public static class NMSClientboundCustomPayloadPacket
    extends NMSPacket {
        public static final FieldAccessor<String> channel = new SafeDirectField<String>(){

            @Override
            public String get(Object instance) {
                return ClientboundCustomPayloadPacketHandle.createHandle(instance).getChannel();
            }

            @Override
            public boolean set(Object instance, String value) {
                return false;
            }
        };
        public static final FieldAccessor<byte[]> message = new SafeDirectField<byte[]>(){

            @Override
            public byte[] get(Object instance) {
                return ClientboundCustomPayloadPacketHandle.createHandle(instance).getMessage();
            }

            @Override
            public boolean set(Object instance, byte[] value) {
                return false;
            }
        };

        public static CommonPacket createNew(String channel, byte[] message) {
            return ClientboundCustomPayloadPacketHandle.createNew(channel, message).toCommonPacket();
        }
    }

    public static class NMSClientboundTakeItemEntityPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> collectedItemId;
        public final FieldAccessor<Integer> collectorEntityId;
        public final FieldAccessor<Integer> amount;

        public NMSClientboundTakeItemEntityPacket() {
            this.collectedItemId = ClientboundTakeItemEntityPacketHandle.T.collectedItemId.toFieldAccessor();
            this.collectorEntityId = ClientboundTakeItemEntityPacketHandle.T.collectorEntityId.toFieldAccessor();
            this.amount = ClientboundTakeItemEntityPacketHandle.T.amount.isAvailable() ? ClientboundTakeItemEntityPacketHandle.T.amount.toFieldAccessor() : null;
        }
    }

    public static class NMSClientboundContainerClosePacket
    extends NMSPacket {
        public final FieldAccessor<Integer> windowId;

        public NMSClientboundContainerClosePacket() {
            this.windowId = ClientboundContainerClosePacketHandle.T.windowId.toFieldAccessor();
        }
    }

    public static class NMSClientboundSetCameraPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;

        public NMSClientboundSetCameraPacket() {
            this.entityId = ClientboundSetCameraPacketHandle.T.entityId.toFieldAccessor();
        }
    }

    @Deprecated
    public static class NMSClientboundBossEventPacket
    extends NMSPacket {
    }

    public static class NMSClientboundBlockUpdatePacket
    extends NMSPacket {
        public final TranslatorFieldAccessor<IntVector3> position;
        public final TranslatorFieldAccessor<BlockData> blockData;

        public NMSClientboundBlockUpdatePacket() {
            this.position = ClientboundBlockUpdatePacketHandle.T.position.toFieldAccessor();
            this.blockData = ClientboundBlockUpdatePacketHandle.T.blockData.toFieldAccessor();
        }

        @Override
        public CommonPacket newInstance() {
            return ClientboundBlockUpdatePacketHandle.createNewNull().toCommonPacket();
        }

        public CommonPacket newInstance(IntVector3 position, BlockData blockData) {
            return ClientboundBlockUpdatePacketHandle.createNew(position, blockData).toCommonPacket();
        }
    }

    public static class NMSClientboundBlockDestructionPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> id;
        public final TranslatorFieldAccessor<IntVector3> position;
        public final FieldAccessor<Integer> progress;

        public NMSClientboundBlockDestructionPacket() {
            this.id = ClientboundBlockDestructionPacketHandle.T.id.toFieldAccessor();
            this.position = ClientboundBlockDestructionPacketHandle.T.position.toFieldAccessor();
            this.progress = ClientboundBlockDestructionPacketHandle.T.progress.toFieldAccessor();
        }
    }

    public static class NMSClientboundBlockEventPacket
    extends NMSPacket {
        public final TranslatorFieldAccessor<IntVector3> position;
        public final FieldAccessor<Integer> b0;
        public final FieldAccessor<Integer> b1;
        public final FieldAccessor<Material> block;

        public NMSClientboundBlockEventPacket() {
            this.position = ClientboundBlockEventPacketHandle.T.position.toFieldAccessor();
            this.b0 = ClientboundBlockEventPacketHandle.T.b0.toFieldAccessor();
            this.b1 = ClientboundBlockEventPacketHandle.T.b1.toFieldAccessor();
            this.block = ClientboundBlockEventPacketHandle.T.block.toFieldAccessor();
        }
    }

    public static class NMSClientboundSetEntityLinkPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> vehicleId;
        public final FieldAccessor<Integer> passengerId;

        public NMSClientboundSetEntityLinkPacket() {
            this.vehicleId = ClientboundSetEntityLinkPacketHandle.T.vehicleId.toFieldAccessor();
            this.passengerId = ClientboundSetEntityLinkPacketHandle.T.passengerId.toFieldAccessor();
        }

        public CommonPacket newInstanceMount(Entity passenger, Entity vehicle) {
            return new CommonPacket(ClientboundSetEntityLinkPacketHandle.createNewMount(passenger, vehicle).getRaw());
        }

        public CommonPacket newInstanceLeash(Entity leashedEntity, Entity holderEntity) {
            return new CommonPacket(ClientboundSetEntityLinkPacketHandle.createNewLeash(leashedEntity, holderEntity).getRaw());
        }
    }

    public static class NMSClientboundAnimatePacket
    extends NMSPacket {
        public final FieldAccessor<Integer> entityId;
        public final FieldAccessor<Integer> animation;

        public NMSClientboundAnimatePacket() {
            this.entityId = ClientboundAnimatePacketHandle.T.entityId.toFieldAccessor();
            this.animation = ClientboundAnimatePacketHandle.T.action.toFieldAccessor();
        }
    }

    public static class NMSClientboundUpdateAdvancementsPacket
    extends NMSPacket {
        public final FieldAccessor<Boolean> initial;

        public NMSClientboundUpdateAdvancementsPacket() {
            this.initial = ClientboundUpdateAdvancementsPacketHandle.T.initial.toFieldAccessor();
        }
    }

    public static class NMSClientboundPlayerAbilitiesPacket
    extends NMSPacket {
        public final FieldAccessor<Boolean> isInvulnerable;
        public final FieldAccessor<Boolean> isFlying;
        public final FieldAccessor<Boolean> canFly;
        public final FieldAccessor<Boolean> canInstantlyBuild;
        public final FieldAccessor<Float> flySpeed;
        public final FieldAccessor<Float> walkSpeed;

        public NMSClientboundPlayerAbilitiesPacket() {
            this.isInvulnerable = ClientboundPlayerAbilitiesPacketHandle.T.invulnerable.toFieldAccessor();
            this.isFlying = ClientboundPlayerAbilitiesPacketHandle.T.isFlying.toFieldAccessor();
            this.canFly = ClientboundPlayerAbilitiesPacketHandle.T.canFly.toFieldAccessor();
            this.canInstantlyBuild = ClientboundPlayerAbilitiesPacketHandle.T.instabuild.toFieldAccessor();
            this.flySpeed = ClientboundPlayerAbilitiesPacketHandle.T.flyingSpeed.toFieldAccessor();
            this.walkSpeed = ClientboundPlayerAbilitiesPacketHandle.T.walkingSpeed.toFieldAccessor();
        }

        public CommonPacket newInstance(PlayerAbilities abilities) {
            return ClientboundPlayerAbilitiesPacketHandle.createNew(abilities).toCommonPacket();
        }
    }

    public static class NMSServerboundContainerClickPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> windowId;
        public final FieldAccessor<Short> slot;
        public final FieldAccessor<Byte> button;
        public final FieldAccessor<InventoryClickType> mode;

        public NMSServerboundContainerClickPacket() {
            this.windowId = ServerboundContainerClickPacketHandle.T.windowId.toFieldAccessor();
            this.slot = ServerboundContainerClickPacketHandle.T.slot.toFieldAccessor();
            this.button = ServerboundContainerClickPacketHandle.T.button.toFieldAccessor();
            this.mode = ServerboundContainerClickPacketHandle.T.mode.toFieldAccessor();
        }
    }

    public static class NMSServerboundMoveVehiclePacket
    extends NMSPacket {
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>(){

            @Override
            public Double get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getPosX.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>(){

            @Override
            public Double get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getPosY.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>(){

            @Override
            public Double get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getPosZ.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>(){

            @Override
            public Float get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getYaw.invoke(o);
            }

            @Override
            public boolean set(Object o, Float aFloat) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>(){

            @Override
            public Float get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getPitch.invoke(o);
            }

            @Override
            public boolean set(Object o, Float aFloat) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> onGround = new SafeDirectField<Boolean>(){

            @Override
            public Boolean get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.isOnGround.invoke(o);
            }

            @Override
            public boolean set(Object o, Boolean aBool) {
                return false;
            }
        };

        public CommonPacket newInstance(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
            return ServerboundMoveVehiclePacketHandle.createNew(posX, posY, posZ, yaw, pitch, onGround).toCommonPacket();
        }
    }

    public static class NMSServerboundAttackPacket
    extends NMSPacket {
        @Override
        public boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.INTERACT_PACKET_ATTACK_SPLIT) {
                return true;
            }
            return ServerboundAttackPacketHandle.isAttackInteractionPacket(packetHandle);
        }
    }

    @Deprecated
    public static class NMSServerboundInteractPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> clickedEntityId = new SafeDirectField<Integer>(){

            @Override
            public Integer get(Object instance) {
                return ServerboundInteractPacketHandle.createHandle(instance).getUsedEntityId();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };

        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return ServerboundInteractPacketHandle.createHandle(packet.getHandle()).getHand(humanEntity);
        }

        @Override
        public boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.INTERACT_PACKET_ATTACK_SPLIT) {
                return true;
            }
            return !ServerboundAttackPacketHandle.isAttackInteractionPacket(packetHandle);
        }
    }

    public static class NMSServerboundSignUpdatePacket
    extends NMSPacket {
        public final FieldAccessor<IntVector3> position;
        public final FieldAccessor<ChatText[]> lines;

        public NMSServerboundSignUpdatePacket() {
            this.position = ServerboundSignUpdatePacketHandle.T.position.toFieldAccessor();
            this.lines = ServerboundSignUpdatePacketHandle.T.lines.toFieldAccessor();
        }

        public Block getBlock(CommonPacket packet, World world) {
            return BlockUtil.getBlock(world, this.position.get(packet.getHandle()));
        }

        public void setBlock(CommonPacket packet, Block block) {
            this.position.set(packet.getHandle(), new IntVector3(block));
        }
    }

    public static class NMSServerboundAcceptTeleportationPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> teleportId;

        public NMSServerboundAcceptTeleportationPacket() {
            this.teleportId = ServerboundAcceptTeleportationPacketHandle.T.teleportId.toFieldAccessor();
        }
    }

    public static class NMSServerboundCommandSuggestionPacket
    extends NMSPacket {
    }

    public static class NMSServerboundPlayerInputPacket
    extends NMSPacket {
        public final FieldAccessor<Float> sideways = new FieldAccessor<Float>(){

            @Override
            public Float get(Object instance) {
                return ServerboundPlayerInputPacketHandle.T.getSideways.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Float> forwards = new FieldAccessor<Float>(){

            @Override
            public Float get(Object instance) {
                return ServerboundPlayerInputPacketHandle.T.getForwards.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> jump = new FieldAccessor<Boolean>(){

            @Override
            public Boolean get(Object instance) {
                return ServerboundPlayerInputPacketHandle.T.isJump.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> unmount = new FieldAccessor<Boolean>(){

            @Override
            public Boolean get(Object instance) {
                return ServerboundPlayerInputPacketHandle.T.isUnmount.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };
    }

    public static class NMSServerboundTeleportToEntityPacket
    extends NMSPacket {
        public final FieldAccessor<UUID> uuid;

        public NMSServerboundTeleportToEntityPacket() {
            this.uuid = ServerboundTeleportToEntityPacketHandle.T.uuid.toFieldAccessor();
        }

        public CommonPacket newInstance(UUID uuid) {
            return ServerboundTeleportToEntityPacketHandle.createNew(uuid).toCommonPacket();
        }
    }

    public static class NMSServerboundClientInformationPacket
    extends NMSPacket {
        public final FieldAccessor<String> locale = new SafeDirectField<String>(){

            @Override
            public String get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getLocale.invoke(instance);
            }

            @Override
            public boolean set(Object instance, String value) {
                return false;
            }
        };
        public final FieldAccessor<Integer> view = new SafeDirectField<Integer>(){

            @Override
            public Integer get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getView.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };
        public final FieldAccessor<Object> chatVisibility = new SafeDirectField<Object>(){

            @Override
            public Object get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getChatVisibility.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Object value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> enableColors = new SafeDirectField<Boolean>(){

            @Override
            public Boolean get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getEnableColors.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };
        public final FieldAccessor<Integer> modelPartFlags = new SafeDirectField<Integer>(){

            @Override
            public Integer get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getModelPartFlags.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };
        public final FieldAccessor<HumanHand> mainHand = new SafeDirectField<HumanHand>(){

            @Override
            public HumanHand get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getMainHand.invoke(instance);
            }

            @Override
            public boolean set(Object instance, HumanHand value) {
                return false;
            }
        };
    }

    public static class NMSServerboundSetCreativeModeSlotPacket
    extends NMSPacket {
    }

    public static class NMSServerboundResourcePackPacket
    extends NMSPacket {
        public final FieldAccessor<Object> enumStatus;

        public NMSServerboundResourcePackPacket() {
            this.enumStatus = ServerboundResourcePackPacketHandle.T.status.toFieldAccessor();
        }
    }

    public static class NMSServerboundKeepAlivePacket
    extends NMSPacket {
        public final FieldAccessor<Long> key = new SafeDirectField<Long>(){

            @Override
            public Long get(Object instance) {
                return ServerboundKeepAlivePacketHandle.createHandle(instance).getKey();
            }

            @Override
            public boolean set(Object instance, Long value) {
                ServerboundKeepAlivePacketHandle.createHandle(instance).setKey(value);
                return true;
            }
        };
    }

    public static class NMSServerboundSetCarriedItemPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> slot;

        public NMSServerboundSetCarriedItemPacket() {
            this.slot = ServerboundSetCarriedItemPacketHandle.T.itemInHandIndex.toFieldAccessor();
        }
    }

    public static class NMSServerboundMovePlayerPacketPosRot
    extends NMSServerboundMovePlayerPacket {
        public NMSServerboundMovePlayerPacketPosRot() {
            super("PosRot");
        }
    }

    public static class NMSServerboundMovePlayerPacketPos
    extends NMSServerboundMovePlayerPacket {
        public NMSServerboundMovePlayerPacketPos() {
            super("Pos");
        }
    }

    public static class NMSServerboundMovePlayerPacketRot
    extends NMSServerboundMovePlayerPacket {
        public NMSServerboundMovePlayerPacketRot() {
            super("Rot");
        }
    }

    public static class NMSServerboundMovePlayerPacket
    extends NMSPacket {
        public final FieldAccessor<Double> x;
        public final FieldAccessor<Double> y;
        public final FieldAccessor<Double> z;
        public final FieldAccessor<Float> yaw;
        public final FieldAccessor<Float> pitch;
        public final FieldAccessor<Boolean> onGround;
        public final FieldAccessor<Boolean> hasPos;
        public final FieldAccessor<Boolean> hasLook;

        protected NMSServerboundMovePlayerPacket(String subType) {
            super(CommonUtil.getClass("net.minecraft.network.protocol.game.ServerboundMovePlayerPacket." + subType));
            this.x = ServerboundMovePlayerPacketHandle.T.x.toFieldAccessor();
            this.y = ServerboundMovePlayerPacketHandle.T.y.toFieldAccessor();
            this.z = ServerboundMovePlayerPacketHandle.T.z.toFieldAccessor();
            this.yaw = ServerboundMovePlayerPacketHandle.T.yaw.toFieldAccessor();
            this.pitch = ServerboundMovePlayerPacketHandle.T.pitch.toFieldAccessor();
            this.onGround = ServerboundMovePlayerPacketHandle.T.onGround.toFieldAccessor();
            this.hasPos = ServerboundMovePlayerPacketHandle.T.hasPos.toFieldAccessor();
            this.hasLook = ServerboundMovePlayerPacketHandle.T.hasLook.toFieldAccessor();
        }
    }

    public static class NMSServerboundPlayerCommandPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> playerId;
        public final FieldAccessor<Object> action;
        public final FieldAccessor<Integer> jumpBoost;

        public NMSServerboundPlayerCommandPacket() {
            this.playerId = ServerboundPlayerCommandPacketHandle.T.playerId.toFieldAccessor();
            this.action = ServerboundPlayerCommandPacketHandle.T.action.toFieldAccessor();
            this.jumpBoost = ServerboundPlayerCommandPacketHandle.T.data.toFieldAccessor();
        }
    }

    public static class NMSServerboundContainerButtonClickPacket
    extends NMSPacket {
        public final FieldAccessor<Integer> windowId;
        public final FieldAccessor<Integer> buttonId;

        public NMSServerboundContainerButtonClickPacket() {
            this.windowId = ServerboundContainerButtonClickPacketHandle.T.windowId.toFieldAccessor();
            this.buttonId = ServerboundContainerButtonClickPacketHandle.T.buttonId.toFieldAccessor();
        }
    }

    public static class NMSServerboundCustomPayloadPacket
    extends NMSPacket {
    }

    public static class NMSServerboundContainerClosePacket
    extends NMSPacket {
        public final FieldAccessor<Integer> windowId;

        public NMSServerboundContainerClosePacket() {
            this.windowId = ServerboundContainerClosePacketHandle.T.windowId.toFieldAccessor();
        }
    }

    public static class NMSServerboundClientCommandPacket
    extends NMSPacket {
        public final FieldAccessor<Object> command;

        public NMSServerboundClientCommandPacket() {
            this.command = ServerboundClientCommandPacketHandle.T.action.toFieldAccessor();
        }
    }

    public static class NMSServerboundChatPacket
    extends NMSPacket {
        public final FieldAccessor<String> message;

        public NMSServerboundChatPacket() {
            this.message = ServerboundChatPacketHandle.T.message.toFieldAccessor();
        }
    }

    public static class NMSServerboundPaddleBoatPacket
    extends NMSPacket {
        public final FieldAccessor<Boolean> leftPaddle;
        public final FieldAccessor<Boolean> rightPaddle;

        public NMSServerboundPaddleBoatPacket() {
            this.leftPaddle = ServerboundPaddleBoatPacketHandle.T.leftPaddle.toFieldAccessor();
            this.rightPaddle = ServerboundPaddleBoatPacketHandle.T.rightPaddle.toFieldAccessor();
        }
    }

    public static class NMSServerboundUseItemOnPacket
    extends NMSPacket {
        public final FieldAccessor<IntVector3> position = new SafeDirectField<IntVector3>(){

            @Override
            public IntVector3 get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getPosition.invoke(instance);
            }

            @Override
            public boolean set(Object instance, IntVector3 value) {
                ServerboundUseItemOnPacketHandle.T.setPosition.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<BlockFace> direction = new SafeDirectField<BlockFace>(){

            @Override
            public BlockFace get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getDirection.invoke(instance);
            }

            @Override
            public boolean set(Object instance, BlockFace value) {
                ServerboundUseItemOnPacketHandle.T.setDirection.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> deltaX = new SafeDirectField<Float>(){

            @Override
            public Float get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getDeltaX.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                ServerboundUseItemOnPacketHandle.T.setDeltaX.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> deltaY = new SafeDirectField<Float>(){

            @Override
            public Float get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getDeltaY.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                ServerboundUseItemOnPacketHandle.T.setDeltaY.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> deltaZ = new SafeDirectField<Float>(){

            @Override
            public Float get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getDeltaZ.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                ServerboundUseItemOnPacketHandle.T.setDeltaZ.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Long> timestamp;

        public NMSServerboundUseItemOnPacket() {
            this.timestamp = ServerboundUseItemOnPacketHandle.T.timestamp.toFieldAccessor().ignoreInvalid(0L);
        }

        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                return ServerboundUseItemOnPacketHandle.T.isBlockPlacePacket.invoke(packetHandle) == false;
            }
            return true;
        }

        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
            ServerboundUseItemOnPacketHandle.createHandle(packet.getHandle()).setHand(humanEntity, humanHand);
        }

        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return ServerboundUseItemOnPacketHandle.createHandle(packet.getHandle()).getHand(humanEntity);
        }
    }

    public static class NMSServerboundUseItemPacket
    extends NMSPacket {
        public final FieldAccessor<Long> timestamp;

        public NMSServerboundUseItemPacket() {
            this.timestamp = ServerboundUseItemPacketHandle.T.timestamp.toFieldAccessor().ignoreInvalid(0L);
        }

        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                return ServerboundUseItemOnPacketHandle.T.isBlockPlacePacket.invoke(packetHandle);
            }
            return true;
        }

        @Override
        public void preprocess(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                ServerboundUseItemOnPacketHandle.T.setBlockPlacePacket.invoke(packetHandle);
            }
        }

        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
            ServerboundUseItemPacketHandle.createHandle(packet.getHandle()).setHand(humanEntity, humanHand);
        }

        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return ServerboundUseItemPacketHandle.createHandle(packet.getHandle()).getHand(humanEntity);
        }
    }

    @Deprecated
    public static class NMSServerboundPlayerActionPacket
    extends NMSPacket {
        public final FieldAccessor<IntVector3> position;
        public final FieldAccessor<BlockFace> direction;
        public final FieldAccessor<ServerboundPlayerActionPacketHandle.ActionHandle> status;

        public NMSServerboundPlayerActionPacket() {
            this.position = ServerboundPlayerActionPacketHandle.T.position.toFieldAccessor();
            this.direction = ServerboundPlayerActionPacketHandle.T.direction.toFieldAccessor();
            this.status = ServerboundPlayerActionPacketHandle.T.digType.toFieldAccessor();
        }
    }

    public static class NMSServerboundSwingPacket
    extends NMSPacket {
        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
            ServerboundSwingPacketHandle.createHandle(packet.getHandle()).setHand(humanEntity, humanHand);
        }

        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return ServerboundSwingPacketHandle.createHandle(packet.getHandle()).getHand(humanEntity);
        }
    }

    public static class NMSServerboundPlayerAbilitiesPacket
    extends NMSPacket {
        public final FieldAccessor<Boolean> isFlying;

        public NMSServerboundPlayerAbilitiesPacket() {
            this.isFlying = ServerboundPlayerAbilitiesPacketHandle.T.isFlying.toFieldAccessor();
        }
    }

    public static class NMSPacket
    extends PacketType {
        private FastMethod<CommonPacket> _constructor0 = null;

        protected NMSPacket(Class<?> packetClass) {
            super(packetClass);
        }

        public NMSPacket() {
        }

        protected SafeConstructor<CommonPacket> getPacketConstructor(Class<?> ... args) {
            return this.getConstructor(args).translateOutput(Conversion.toCommonPacket);
        }

        @Override
        public CommonPacket newInstance() {
            if (this._constructor0 == null) {
                ClassResolver resolver = new ClassResolver();
                resolver.addImport(CommonPacket.class.getName());
                resolver.setDeclaredClass(this.getType());
                resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
                MethodDeclaration mDec = new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static CommonPacket newInstance() {\n#if exists " + this.getType().getName() + " private " + this.getType().getSimpleName() + "();\n    #require " + this.getType().getName() + " private " + this.getType().getSimpleName() + " createPacket:<init>()\n    Object packet = #createPacket();\n#elseif exists " + this.getType().getName() + " private " + this.getType().getSimpleName() + " (net.minecraft.network.FriendlyByteBuf serializer)\n    #require " + this.getType().getName() + " private " + this.getType().getSimpleName() + " createPacket:<init>(net.minecraft.network.FriendlyByteBuf serializer)\n    Object packet = #createPacket(com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer.INSTANCE);\n#elseif exists " + this.getType().getName() + " private " + this.getType().getSimpleName() + " (net.minecraft.network.RegistryFriendlyByteBuf byteBuf)\n    #require " + this.getType().getName() + " private " + this.getType().getSimpleName() + " createPacket:<init>(net.minecraft.network.RegistryFriendlyByteBuf byteBuf)\n    Object packet = #createPacket(com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer.INSTANCE);\n#else\n    #error No " + this.getType().getName() + " packet constructor found\n#endif\n    return new CommonPacket(packet);\n}", resolver));
                this._constructor0 = new FastMethod();
                this._constructor0.init(mDec);
                this._constructor0.forceInitialization();
            }
            return this._constructor0.invoke(null);
        }
    }
}

