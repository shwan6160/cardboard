/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.templates.TemplateResolver;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Common {
    private static final Set<String> CAPABILITIES = Stream.of("Common:Capabilities", "Common:EntityController:SetBlockActivationEnabled", "Common:PacketPlayOutUpdateAttributes:createZeroMaxHealth", "Common:Yaml:ChangeListeners", "Common:Yaml:CloneAndSetToWithFixes", "Common:IPermissionEnum", "Common:FishingHookFixes1.16", "Common:Chunk:FutureProvider", "Common:IntCuboid", "Common:WorldBlockBorder", "Common:EntityController:FixedOnDieDuringTeleport", "Common:WorldUtil:getDefaultNetherPortalSearchRadius", "Common:Item:CreatePlayerHeadUsingGameProfile", "Common:VehicleMountController:Spectating", "Common:WorldUtil:getWorldLevelFile", "Common:EntityController:forceControllerInitialization", "Common:EntityNetworkController:HasOnPassengersChanged", "Common:Localization:InitDefaults", "Common:BlockData:EmissionBlockParameter", "Common:SignChangeTracker", "Common:EntityUtil:GetSetEquipmentSlot", "Common:ChatText:MultiLineSupport", "Common:EntityUtil:PortalWaitDelay", "Common:MapDisplay:BoundsChangeViewFix", "Common:SlimeHandle", "Common:PlayerGameInfo", "Common:Yaml:BetterChangeListeners", "Common:DisplayEntity:Brightness", "Common:PacketPlayOutEntityEquipment:OwnerType", "Common:BlockDataStateRename", "Common:Advancement:RewardDisabler", "Common:Sound:StopSoundPacket", "Common:Sound:CloudParser", "Common:Event:PlayerAdvancementProgressEvent", "Common:Yaml:ChildWithLiteralName", "Common:ChunkUtil:getChunkViewers", "Common:SignChangeTracker:FormattedText", "Common:ConnectionResetAwaitTeleport", "Common:EntityController:PositionPassenger", "Common:CommonItemStack", "Common:EquipmentSlot:IsSupportedCheck", "Common:Attributes:RemoveAllModifiers", "Common:Attributes:GetAllAttributes", "Common:Player:SetSkinMetadata", "Common:PacketPlayInBlockPlace:RotationApi", "Common:PlayerInstancePhase", "Common:SignEditTextEvent", "Common:PacketListener:SetPacket", "Common:Packet:MinecartMovementPacket", "Common:WorldUtil:HasFeatureFlag", "Common:ResourcePack:ItemModel", "Common:NBTUtil:LoadSaveEquipment", "Common:NBT:ChatTextSerialization", "Module:RegionFlagTracker", "Common:CommonItemStack:ItemModel", "Common:CommonItemStack:MimicAsType", "Common:CommonItemStack:AddGlint", "Common:JSONSerializer:NullItemStack", "Common:Yaml:ListOfListFix", "Common:Cloud:QuotedArgumentParserFromParser", "Minecraft:GameProfile:Immutable", "Common:EntityUtil:detectEquipmentChanges", "Common:Math:OrientedBoundingBox:HasOverlapTest", "Common:Math:VectorList", "Common:Packet:PlayerRotationPacket", "Common:PlayerConnection:AwaitingTeleportId", "Common:EntityController:isPlayerTakeable", "Common:Block:RayTraceUtilImprovements", "Common:BlockData:GetInteractableBox", "Common:MapResourcePack:OpenResource", "Common:LoadableWorld", "Common:DamageSource:CreateVehicleDamageEvent").collect(Collectors.toSet());
    public static final int VERSION = 20001;
    public static final String MC_VERSION;
    public static final String NMS_ROOT;
    public static final String CB_ROOT;
    public static final String COMMON_ROOT = "com.bergerkiller.bukkit.common";
    public static final CommonServer SERVER;
    public static final TemplateResolver TEMPLATE_RESOLVER;
    public static final boolean IS_SPIGOT_SERVER;
    public static final boolean IS_PAPERSPIGOT_SERVER;
    public static final boolean IS_PURPUR_SERVER;
    public static final boolean IS_COMPATIBLE;
    public static final ModuleLogger LOGGER;
    public static final boolean IS_TEST_MODE;

    @Deprecated
    public static void bootstrap() {
        CommonBootstrap.initCommonServerAssertCompatibility();
    }

    public static int getVersion() {
        return 20001;
    }

    public static void loadClasses(String ... classNames) {
        for (String className : classNames) {
            try {
                Common.loadInner(Class.forName(className));
            }
            catch (ExceptionInInitializerError error) {
                throw new RuntimeException("An error occurred trying to initialize class '" + className + "':", error);
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException("Could not load class '" + className + "' - Update needed?");
            }
        }
    }

    private static void loadInner(Class<?> clazz) {
        for (Class<?> subclass : clazz.getDeclaredClasses()) {
            Common.loadInner(subclass);
        }
    }

    protected static void handleReflectionMissing(String type, String name, Class<?> source) {
        String msg = type + " '" + name + "' does not exist in class file " + source.getSimpleName();
        Exception ex = new Exception();
        for (StackTraceElement elem : ex.getStackTrace()) {
            if (!elem.getClassName().startsWith("com.bergerkiller.bukkit.common.reflection")) continue;
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, msg + " (Update BKCommonLib?)", ex);
            return;
        }
        Logging.LOGGER_REFLECTION.log(Level.SEVERE, msg, ex);
    }

    public static boolean evaluateMCVersion(String operand, String version) {
        return CommonBootstrap.evaluateMCVersion(operand, version);
    }

    public static boolean hasCapability(String capability) {
        return CAPABILITIES.contains(capability);
    }

    static {
        NMS_ROOT = StringUtil.join(".", "net", "minecraft", "server");
        CB_ROOT = StringUtil.join(".", "org", "bukkit", "craftbukkit");
        IS_TEST_MODE = CommonBootstrap.isTestMode();
        LOGGER = Logging.LOGGER;
        SERVER = CommonBootstrap.initCommonServer();
        MC_VERSION = SERVER.getMinecraftVersion();
        IS_SPIGOT_SERVER = CommonBootstrap.isSpigotServer();
        IS_PAPERSPIGOT_SERVER = CommonBootstrap.isPaperServer();
        IS_PURPUR_SERVER = CommonBootstrap.isPurpurServer();
        IS_COMPATIBLE = CommonBootstrap.initCommonServerCheckCompatibility();
        TEMPLATE_RESOLVER = CommonBootstrap.initTemplates();
    }
}

