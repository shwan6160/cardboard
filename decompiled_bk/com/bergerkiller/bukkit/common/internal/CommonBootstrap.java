/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.DimensionResourceKeyConversion;
import com.bergerkiller.bukkit.common.conversion.type.EntityPoseConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.ItemSlotConversion;
import com.bergerkiller.bukkit.common.conversion.type.JOMLConversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_17_Conversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_18_2_Conversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_8_8_Conversion;
import com.bergerkiller.bukkit.common.conversion.type.MapConversion;
import com.bergerkiller.bukkit.common.conversion.type.NBTConversion;
import com.bergerkiller.bukkit.common.conversion.type.PropertyConverter;
import com.bergerkiller.bukkit.common.conversion.type.ScoreboardDisplaySlotConversion;
import com.bergerkiller.bukkit.common.conversion.type.SerializedEnumConversion;
import com.bergerkiller.bukkit.common.conversion.type.TeamColorConversion;
import com.bergerkiller.bukkit.common.conversion.type.TextColorChatColorConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.CommonClasses;
import com.bergerkiller.bukkit.common.internal.logging.CommonLog4jTestLogging;
import com.bergerkiller.bukkit.common.internal.logic.EmptyBlockGetterInit;
import com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializerInit;
import com.bergerkiller.bukkit.common.internal.logic.ScopedProblemReporterInit;
import com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItemInit;
import com.bergerkiller.bukkit.common.server.ArclightServer;
import com.bergerkiller.bukkit.common.server.ArclightServerLegacy;
import com.bergerkiller.bukkit.common.server.Bukkit4FabricServer;
import com.bergerkiller.bukkit.common.server.CatServerServer;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.server.CraftBukkitServer;
import com.bergerkiller.bukkit.common.server.MagmaServer;
import com.bergerkiller.bukkit.common.server.MagmaServerLegacy;
import com.bergerkiller.bukkit.common.server.MohistServer;
import com.bergerkiller.bukkit.common.server.NachoSpigotServer;
import com.bergerkiller.bukkit.common.server.PurpurServer;
import com.bergerkiller.bukkit.common.server.SpigotServer;
import com.bergerkiller.bukkit.common.server.SportBukkitServer;
import com.bergerkiller.bukkit.common.server.UniverseServer;
import com.bergerkiller.bukkit.common.server.UnknownServer;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.ListTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledFieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledMethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.templates.TemplateResolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.bukkit.Bukkit;

public class CommonBootstrap {
    public static boolean WARN_WHEN_INIT_SERVER = false;
    public static boolean WARN_WHEN_INIT_TEMPLATES = false;
    private static boolean _hasInitTemplates = false;
    private static boolean _hasInitTestServer = false;
    private static boolean _isSpigotServer = false;
    private static boolean _isPaperServer = false;
    private static boolean _isPurpurServer = false;
    private static CommonServer _commonServer = null;
    private static boolean _isInitializingCommonServer = false;
    private static TemplateResolver _templateResolver;
    private static boolean _isCompatible;
    private static String _incompatibleReason;

    public static boolean evaluateMCVersion(String operand, String version) {
        return CommonBootstrap.initCommonServer().evaluateMCVersion(operand, version);
    }

    public static boolean isSpigotServer() {
        CommonBootstrap.initCommonServer();
        return _isSpigotServer;
    }

    public static boolean isPaperServer() {
        CommonBootstrap.initCommonServer();
        return _isPaperServer;
    }

    public static boolean isPurpurServer() {
        CommonBootstrap.initCommonServer();
        return _isPurpurServer;
    }

    public static boolean verifyShadedAssets(Logger logger) {
        try {
            Class.forName("com.bergerkiller.mountiplex.MountiplexUtil");
        }
        catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "BKCommonLib jar lacks required shaded dependencies. Please redownload the correct jar!");
            logger.log(Level.SEVERE, "If your BKCommonLib.jar is less than 5 MB then you probably downloaded the wrong file.");
            logger.log(Level.SEVERE, "If using a FTP client, make sure the file is fully transferred to the server.");
            return false;
        }
        return true;
    }

    public static boolean isCommonServerInitialized() {
        return _commonServer != null && _isCompatible;
    }

    public static boolean initCommonServerCheckCompatibility() {
        CommonBootstrap.initCommonServer();
        return _isCompatible;
    }

    public static void initCommonServerAssertCompatibility() {
        if (!CommonBootstrap.initCommonServerCheckCompatibility()) {
            throw new UnsupportedOperationException(_incompatibleReason);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CommonServer initCommonServer() {
        if (_commonServer == null) {
            if (_isInitializingCommonServer) {
                throw new UnsupportedOperationException("CommonServer is already being initialized. Fix your code!");
            }
            _templateResolver = new TemplateResolver();
            Resolver.getPackageNameCache().addDefaultPackage("net.minecraft.server").addDefaultPackage("net.minecraft.core").addDefaultPackage("org.bukkit.craftbukkit").addDefaultPackage("com.mojang.authlib").addDefaultPackage("org.spigotmc");
            _isInitializingCommonServer = true;
            try {
                CommonServer server = null;
                if (CommonBootstrap.isTestMode()) {
                    boolean log4jExists;
                    try {
                        Class.forName("org.apache.logging.log4j.spi.ExtendedLogger");
                        log4jExists = true;
                    }
                    catch (ClassNotFoundException ex) {
                        log4jExists = false;
                    }
                    if (log4jExists) {
                        CommonLog4jTestLogging.initLog4j();
                    }
                    if (!(server = new SpigotServer()).init()) {
                        server = null;
                    }
                } else {
                    ArrayList<CraftBukkitServer> servers = new ArrayList<CraftBukkitServer>();
                    servers.add(new UniverseServer());
                    servers.add(new MohistServer());
                    servers.add(new MagmaServer());
                    servers.add(new MagmaServerLegacy());
                    servers.add(new ArclightServer());
                    servers.add(new ArclightServerLegacy());
                    servers.add(new CatServerServer());
                    servers.add(new Bukkit4FabricServer());
                    servers.add(new NachoSpigotServer());
                    servers.add(new PurpurServer());
                    servers.add(new SpigotServer());
                    servers.add(new SportBukkitServer());
                    servers.add(new CraftBukkitServer());
                    for (CommonServer commonServer : servers) {
                        try {
                            if (!commonServer.init()) continue;
                            server = commonServer;
                            break;
                        }
                        catch (Throwable t) {
                            Logging.LOGGER.log(Level.SEVERE, "An error occurred during server type detection:", t);
                        }
                    }
                }
                if (server == null) {
                    server = new UnknownServer();
                    server.init();
                }
                CommonServer.PostInitEvent event = new CommonServer.PostInitEvent(_templateResolver);
                try {
                    CommonBootstrap.initServerResolvers(server);
                    server.postInit(event);
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "An error occurred during server bootstrapping:", t);
                    if (event.isCompatible()) {
                        event.signalIncompatible("Server bootstrapping failed: " + t.getMessage());
                    }
                    server = new UnknownServer();
                    try {
                        server.init();
                        server.postInit(new CommonServer.PostInitEvent(_templateResolver));
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
                _commonServer = server;
                _isCompatible = event.isCompatible();
                _incompatibleReason = event.getIncompatibleReason();
                _isSpigotServer = _commonServer instanceof SpigotServer;
                _isPaperServer = _commonServer instanceof SpigotServer && ((SpigotServer)_commonServer).isPaperServer();
                _isPurpurServer = _commonServer instanceof PurpurServer;
            }
            finally {
                _isInitializingCommonServer = false;
            }
            if (_isCompatible) {
                boolean oldWarnTemplates = WARN_WHEN_INIT_TEMPLATES;
                WARN_WHEN_INIT_TEMPLATES = true;
                CommonBootstrap.initResolvers(_commonServer);
                WARN_WHEN_INIT_TEMPLATES = oldWarnTemplates;
            }
        }
        return _commonServer;
    }

    public static TemplateResolver initTemplates() {
        if (_hasInitTemplates) {
            return _templateResolver;
        }
        _hasInitTemplates = true;
        if (!CommonBootstrap.initCommonServerCheckCompatibility()) {
            return _templateResolver;
        }
        if (WARN_WHEN_INIT_TEMPLATES) {
            Logging.LOGGER.log(Level.WARNING, "WARN_WHEN_INIT_TEMPLATES", new RuntimeException("Initializing templates"));
        }
        _templateResolver.load();
        Resolver.registerClassDeclarationResolver(_templateResolver);
        MountiplexUtil.registerUnloader(_templateResolver::unload);
        return _templateResolver;
    }

    public static boolean isTestMode() {
        return _hasInitTestServer || Bukkit.getServer() == null;
    }

    public static void initServer() {
        CommonBootstrap.initCommonServerAssertCompatibility();
        if (!_hasInitTestServer && Bukkit.getServer() == null) {
            _hasInitTestServer = true;
            if (WARN_WHEN_INIT_SERVER) {
                Logging.LOGGER.log(Level.WARNING, "WARN_WHEN_INIT_SERVER", new RuntimeException("Initializing server"));
            }
            PrintStream oldout = System.out;
            PrintStream olderr = System.err;
            try {
                TestServerFactory.initTestServer();
            }
            finally {
                System.setOut(oldout);
                System.setErr(olderr);
            }
            Logging.LOGGER.log(Level.INFO, "Test running on " + Common.SERVER.getServerDetails() + " (Java " + System.getProperty("java.version") + ")");
        }
    }

    private static void initServerResolvers(CommonServer server) {
        if (server instanceof ClassPathResolver) {
            Resolver.registerClassResolver((ClassPathResolver)((Object)server));
        }
        if (server instanceof FieldNameResolver) {
            Resolver.registerFieldResolver((FieldNameResolver)((Object)server));
        }
        if (server instanceof MethodNameResolver) {
            Resolver.registerMethodResolver((MethodNameResolver)((Object)server));
        }
        if (server instanceof CompiledFieldNameResolver) {
            Resolver.registerCompiledFieldResolver((CompiledFieldNameResolver)((Object)server));
        }
        if (server instanceof CompiledMethodNameResolver) {
            Resolver.registerCompiledMethodResolver((CompiledMethodNameResolver)((Object)server));
        }
        if (server instanceof FieldAliasResolver) {
            Resolver.registerFieldAliasResolver((FieldAliasResolver)((Object)server));
        }
        if (server instanceof MethodAliasResolver) {
            Resolver.registerMethodAliasResolver((MethodAliasResolver)((Object)server));
        }
    }

    private static void initResolvers(CommonServer server) {
        boolean exists;
        String[] starlightNamespaces;
        boolean craftLegacyIsInUtil;
        HashMap<String, String> remappings = new HashMap<String, String>();
        remappings.put("net.minecraft.server.level.EntityTracker", "net.minecraft.server.level.ChunkMap");
        remappings.put("net.minecraft.server.level.EntityTrackerEntry", "net.minecraft.server.level.ChunkMap$TrackedEntity");
        remappings.put("net.minecraft.server.level.EntityTrackerEntryState", "net.minecraft.server.level.ServerEntity");
        remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet", "com.bergerkiller.bukkit.common.internal.proxy.LongHashSet_pre_1_13_2");
        remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet$LongIterator", "com.bergerkiller.bukkit.common.internal.proxy.LongHashSet_pre_1_13_2$LongIterator");
        if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
            String unimi_fastutil_path = "org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.";
            try {
                MPLType.getClassByName(unimi_fastutil_path + "longs.LongSet");
                for (String fastutilClass : new String[]{"ints.Int2ObjectMap", "ints.Int2ObjectOpenHashMap", "ints.IntList", "ints.IntLists", "ints.IntArrayList", "longs.Long2ObjectMap", "longs.Long2ObjectOpenHashMap", "longs.Long2ObjectLinkedOpenHashMap", "longs.Long2IntOpenHashMap", "longs.LongIterator", "longs.LongLinkedOpenHashSet", "longs.LongOpenHashSet", "longs.LongSet", "longs.LongSortedSet", "longs.LongBidirectionalIterator", "objects.Object2IntMap", "objects.ObjectCollection", "objects.ObjectIterator"}) {
                    remappings.put("it.unimi.dsi.fastutil." + fastutilClass, unimi_fastutil_path + fastutilClass);
                }
            }
            catch (ClassNotFoundException ex) {
                unimi_fastutil_path = "it.unimi.dsi.fastutil.";
            }
            remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet", unimi_fastutil_path + "longs.LongSet");
            remappings.put("org.bukkit.craftbukkit.util.LongObjectHashMap", unimi_fastutil_path + "longs.Long2ObjectMap");
            remappings.put("net.minecraft.util.IntHashMap", unimi_fastutil_path + "ints.Int2ObjectMap");
            remappings.put("net.minecraft.util.IntHashMap$IntHashMapEntry", unimi_fastutil_path + "ints.Int2ObjectMap$Entry");
            remappings.put(unimi_fastutil_path + "ints.IntHashMap$IntHashMapEntry", unimi_fastutil_path + "ints.Int2ObjectMap$Entry");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.15.2")) {
            craftLegacyIsInUtil = true;
        } else if (CommonBootstrap.evaluateMCVersion("==", "1.15.2")) {
            try {
                Class.forName(server.getCBRoot() + ".legacy.CraftLegacy", false, CommonBootstrap.class.getClassLoader());
                craftLegacyIsInUtil = false;
            }
            catch (Throwable t) {
                craftLegacyIsInUtil = true;
            }
        } else {
            craftLegacyIsInUtil = false;
        }
        if (craftLegacyIsInUtil) {
            remappings.put("org.bukkit.craftbukkit.legacy.CraftLegacy", "org.bukkit.craftbukkit.util.CraftLegacy");
        }
        String defaultNamespace = "ca.spottedleaf.moonrise.patches.starlight.light.";
        for (String namespace : starlightNamespaces = new String[]{defaultNamespace, "ca.spottedleaf.starlight.common.light.", "ca.spottedleaf.starlight.light.", "com.tuinity.tuinity.chunk.light."}) {
            exists = false;
            try {
                MPLType.getClassByName(namespace + "StarLightEngine");
                exists = true;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
            if (!exists) continue;
            if (namespace.equals(defaultNamespace)) break;
            for (String name : new String[]{"SWMRNibbleArray", "StarLightInterface", "StarLightEngine", "SkyStarLightEngine", "BlockStarLightEngine"}) {
                remappings.put(defaultNamespace + name, namespace + name);
            }
            break;
        }
        defaultNamespace = "org.purpurmc.purpur.";
        String[] purpurNamespaces = new String[]{defaultNamespace, "net.pl3x.purpur."};
        for (String namespace : purpurNamespaces) {
            exists = false;
            try {
                MPLType.getClassByName(namespace + "PurpurConfig");
                exists = true;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
            if (!exists) continue;
            if (namespace.equals(defaultNamespace)) break;
            for (String name : new String[]{"PurpurConfig", "PurpurWorldConfig"}) {
                remappings.put(defaultNamespace + name, namespace + name);
            }
            break;
        }
        if (CommonBootstrap.evaluateMCVersion("<", "26.2")) {
            remappings.put("net.minecraft.world.entity.EntityTypes", "net.minecraft.world.entity.EntityType");
            remappings.put("net.minecraft.world.entity.monster.cubemob.Slime", "net.minecraft.world.entity.monster.Slime");
            remappings.put("net.minecraft.world.entity.monster.cubemob.AbstractCubeMob", "net.minecraft.world.entity.monster.Slime");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "26.1")) {
            remappings.put("net.minecraft.world.inventory.ContainerInput", "net.minecraft.world.inventory.ClickType");
            remappings.put("net.minecraft.network.protocol.game.ServerboundAttackPacket", "net.minecraft.network.protocol.game.ServerboundInteractPacket");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.21.11")) {
            remappings.put("net.minecraft.resources.Identifier", "net.minecraft.resources.ResourceLocation");
            remappings.put("net.minecraft.IdentifierException", "net.minecraft.ResourceLocationException");
            remappings.put("net.minecraft.commands.arguments.IdentifierArgument", "net.minecraft.commands.arguments.ResourceLocationArgument");
            remappings.put("net.minecraft.util.Util", "net.minecraft.Util");
            remappings.put("net.minecraft.world.entity.decoration.painting.PaintingVariants", "net.minecraft.world.entity.decoration.PaintingVariants");
            remappings.put("net.minecraft.world.entity.decoration.painting.PaintingVariant", "net.minecraft.world.entity.decoration.PaintingVariant");
            remappings.put("net.minecraft.world.entity.decoration.painting.Painting", "net.minecraft.world.entity.decoration.Painting");
            remappings.put("net.minecraft.world.entity.projectile.arrow.ThrownTrident", "net.minecraft.world.entity.projectile.ThrownTrident");
            remappings.put("net.minecraft.world.entity.projectile.arrow.SpectralArrow", "net.minecraft.world.entity.projectile.SpectralArrow");
            remappings.put("net.minecraft.world.entity.projectile.arrow.Arrow", "net.minecraft.world.entity.projectile.Arrow");
            remappings.put("net.minecraft.world.entity.projectile.arrow.AbstractArrow", "net.minecraft.world.entity.projectile.AbstractArrow");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull", "net.minecraft.world.entity.projectile.WitherSkull");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball", "net.minecraft.world.entity.projectile.SmallFireball");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball", "net.minecraft.world.entity.projectile.LargeFireball");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.Fireball", "net.minecraft.world.entity.projectile.Fireball");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball", "net.minecraft.world.entity.projectile.DragonFireball");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile", "net.minecraft.world.entity.projectile.AbstractHurtingProjectile");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion", "net.minecraft.world.entity.projectile.ThrownSplashPotion");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle", "net.minecraft.world.entity.projectile.ThrownExperienceBottle");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion", "net.minecraft.world.entity.projectile.AbstractThrownPotion");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile", "net.minecraft.world.entity.projectile.ThrowableItemProjectile");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion", "net.minecraft.world.entity.projectile.ThrownLingeringPotion");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl", "net.minecraft.world.entity.projectile.ThrownEnderpearl");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg", "net.minecraft.world.entity.projectile.ThrownEgg");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball", "net.minecraft.world.entity.projectile.Snowball");
            remappings.put("net.minecraft.world.entity.vehicle.boat.Boat", "net.minecraft.world.entity.vehicle.Boat");
            remappings.put("net.minecraft.world.entity.vehicle.boat.ChestBoat", "net.minecraft.world.entity.vehicle.ChestBoat");
            remappings.put("net.minecraft.world.entity.vehicle.boat.AbstractChestBoat", "net.minecraft.world.entity.vehicle.AbstractChestBoat");
            remappings.put("net.minecraft.world.entity.vehicle.boat.AbstractBoat", "net.minecraft.world.entity.vehicle.AbstractBoat");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartBehavior", "net.minecraft.world.entity.vehicle.MinecartBehavior");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior", "net.minecraft.world.entity.vehicle.NewMinecartBehavior");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior$MinecartStep", "net.minecraft.world.entity.vehicle.NewMinecartBehavior$MinecartStep");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior", "net.minecraft.world.entity.vehicle.OldMinecartBehavior");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.AbstractMinecart", "net.minecraft.world.entity.vehicle.AbstractMinecart");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer", "net.minecraft.world.entity.vehicle.AbstractMinecartContainer");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.Minecart", "net.minecraft.world.entity.vehicle.Minecart");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartTNT", "net.minecraft.world.entity.vehicle.MinecartTNT");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock", "net.minecraft.world.entity.vehicle.MinecartCommandBlock");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartSpawner", "net.minecraft.world.entity.vehicle.MinecartSpawner");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartHopper", "net.minecraft.world.entity.vehicle.MinecartHopper");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartFurnace", "net.minecraft.world.entity.vehicle.MinecartFurnace");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartChest", "net.minecraft.world.entity.vehicle.MinecartChest");
            remappings.put("net.minecraft.world.entity.boss.enderdragon.EnderDragonPart", "net.minecraft.world.entity.boss.EnderDragonPart");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.21.6")) {
            remappings.put("net.minecraft.world.level.storage.ValueOutput", "net.minecraft.nbt.CompoundTag");
            remappings.put("net.minecraft.world.level.storage.ValueInput", "net.minecraft.nbt.CompoundTag");
            remappings.put("net.minecraft.world.level.storage.TagValueOutput", "net.minecraft.nbt.CompoundTag");
            remappings.put("net.minecraft.world.level.storage.TagValueInput", "net.minecraft.nbt.CompoundTag");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.21.5")) {
            remappings.put("net.minecraft.server.level.ServerPlayer$RespawnConfig", "com.bergerkiller.bukkit.common.internal.proxy.PlayerRespawnConfig_pre_1_21_5");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.21.4")) {
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion", "net.minecraft.world.entity.projectile.ThrownPotion");
            remappings.put("net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion", "net.minecraft.world.entity.projectile.ThrownPotion");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.21.2")) {
            remappings.put("net.minecraft.world.entity.Relative", "net.minecraft.world.entity.RelativeMovement");
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket", "net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket");
            remappings.put("net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket", "net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket");
            remappings.put("net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket", "net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.20.5")) {
            remappings.put("net.minecraft.world.level.chunk.status.ChunkStatus", "net.minecraft.world.level.chunk.ChunkStatus");
            remappings.put("org.bukkit.craftbukkit.inventory.SerializableMeta", "org.bukkit.craftbukkit.inventory.CraftMetaItem$SerializableMeta");
            remappings.put("net.minecraft.world.item.component.CustomModelData", "com.bergerkiller.bukkit.common.internal.proxy.CustomModelData_pre_1_20_5");
            remappings.put("net.minecraft.world.level.saveddata.maps.MapDecorationType", "net.minecraft.world.level.saveddata.maps.MapDecoration$Type");
            remappings.put("net.minecraft.world.level.saveddata.maps.MapDecorationTypes", "net.minecraft.world.level.saveddata.maps.MapDecoration$Type");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.20.3")) {
            remappings.put("net.minecraft.network.protocol.game.ClientboundResetScorePacket", "net.minecraft.network.protocol.game.ClientboundSetScorePacket");
            remappings.put("net.minecraft.network.chat.contents.PlainTextContents", "net.minecraft.network.chat.contents.LiteralContents");
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.20.2")) {
            remappings.put("net.minecraft.network.protocol.game.ClientboundAddPlayerPacket", "net.minecraft.network.protocol.game.ClientboundAddEntityPacket");
            if (CommonBootstrap.evaluateMCVersion("<", "1.20.5")) {
                Class<?> customPayloadType = null;
                try {
                    customPayloadType = MPLType.getClassByName("net.minecraft.network.protocol.common.custom.CustomPacketPayload");
                }
                catch (Throwable t) {
                    Logging.LOGGER_REFLECTION.log(Level.WARNING, "Unable to identify the CustomPacketPayload type", t);
                }
                String anonTypeName = null;
                if (customPayloadType != null) {
                    for (int n = 1; n < 1000; ++n) {
                        String name = server.getCBRoot() + ".entity.CraftPlayer$" + n;
                        try {
                            Class<?> type = MPLType.getClassByName(name);
                            if (!customPayloadType.isAssignableFrom(type)) continue;
                            anonTypeName = name;
                        }
                        catch (ClassNotFoundException e) {}
                        break;
                    }
                }
                if (anonTypeName != null) {
                    remappings.put("net.minecraft.network.protocol.common.custom.BukkitCustomPayload", anonTypeName);
                } else {
                    Logging.LOGGER_REFLECTION.log(Level.WARNING, "Unable to identify the Bukkit custom payload type");
                }
            }
        }
        if (CommonBootstrap.evaluateMCVersion("==", "1.20.2")) {
            remappings.put("net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket", "net.minecraft.network.protocol.common.ClientboundResourcePackPacket");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.20.2")) {
            remappings.put("net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket", "net.minecraft.network.protocol.game.ClientboundResourcePackPacket");
            remappings.put("net.minecraft.network.protocol.common.ServerboundKeepAlivePacket", "net.minecraft.network.protocol.game.ServerboundKeepAlivePacket");
            remappings.put("net.minecraft.network.protocol.common.ClientboundKeepAlivePacket", "net.minecraft.network.protocol.game.ClientboundKeepAlivePacket");
            remappings.put("net.minecraft.network.protocol.common.ServerboundResourcePackPacket", "net.minecraft.network.protocol.game.ServerboundResourcePackPacket");
            remappings.put("net.minecraft.network.protocol.common.ServerboundResourcePackPacket$Action", "net.minecraft.network.protocol.game.ServerboundResourcePackPacket$Action");
            remappings.put("net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket", "net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket");
            remappings.put("net.minecraft.network.protocol.common.ClientboundDisconnectPacket", "net.minecraft.network.protocol.game.ClientboundDisconnectPacket");
            remappings.put("net.minecraft.network.protocol.common.ServerboundClientInformationPacket", "net.minecraft.network.protocol.game.ServerboundClientInformationPacket");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.19.4")) {
            remappings.put("net.minecraft.world.entity.Relative", "net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket$RelativeArgument");
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.19.3")) {
            remappings.put("net.minecraft.network.protocol.game.ClientboundCustomSoundPacket", "net.minecraft.network.protocol.game.ClientboundSoundPacket");
        } else {
            remappings.put("net.minecraft.core.registries.BuiltInRegistries", "net.minecraft.core.Registry");
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.19")) {
            remappings.put("net.minecraft.network.protocol.game.ClientboundAddMobPacket", "net.minecraft.network.protocol.game.ClientboundAddEntityPacket");
            remappings.put("net.minecraft.network.protocol.game.ClientboundAddPaintingPacket", "net.minecraft.network.protocol.game.ClientboundAddEntityPacket");
        } else {
            remappings.put("net.minecraft.util.RandomSource", "java.util.Random");
            remappings.put("net.minecraft.world.entity.decoration.painting.PaintingVariant", "net.minecraft.world.entity.decoration.Motive");
            remappings.put("net.minecraft.world.entity.decoration.painting.PaintingVariants", "net.minecraft.world.entity.decoration.Motive");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.18.2")) {
            remappings.put("net.minecraft.core.RegistryAccess$Frozen", "net.minecraft.core.RegistryAccess$RegistryHolder");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.18")) {
            remappings.put("net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket", "net.minecraft.network.protocol.game.ClientboundLevelChunkPacket");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.17.1")) {
            remappings.put("net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket", "net.minecraft.network.protocol.game.ClientboundRemoveEntityPacket");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.16")) {
            remappings.put("net.minecraft.resources.ResourceKey", "com.bergerkiller.bukkit.common.internal.proxy.ResourceKey_1_15_2");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.14")) {
            remappings.put("net.minecraft.server.level.EntityTracker", "net.minecraft.server.level.EntityTracker");
            remappings.put("net.minecraft.server.level.EntityTrackerEntry", "net.minecraft.server.level.ServerEntity");
            remappings.put("net.minecraft.world.level.EmptyBlockGetter", "com.bergerkiller.bukkit.common.internal.logic.EmptyBlockGetter");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.13")) {
            remappings.put("net.minecraft.world.level.levelgen.Heightmap", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2");
            remappings.put("net.minecraft.world.level.levelgen.HeightMap$Types", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2$Type");
            remappings.put("net.minecraft.world.phys.shapes.VoxelShape", "com.bergerkiller.bukkit.common.internal.proxy.VoxelShapeProxy");
            remappings.put("net.minecraft.world.level.block.entity.BlockEntityType", "com.bergerkiller.bukkit.common.internal.proxy.TileEntityTypesProxy_1_8_to_1_12_2");
            remappings.put("net.minecraft.network.protocol.game.ClientboundStopSoundPacket", "net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket");
            remappings.put("net.minecraft.world.level.ItemLike", "net.minecraft.world.item.Item");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.12")) {
            remappings.put("net.minecraft.world.item.crafting.Ingredient", "net.minecraft.world.item.ItemStack");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.11")) {
            remappings.put("net.minecraft.world.level.saveddata.maps.MapDecorationType", "com.bergerkiller.bukkit.common.internal.proxy.MapDecorationType_1_8_to_1_10_2");
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.9")) {
            try {
                Class.forName(server.getNMSRoot() + ".MobEffectList");
            }
            catch (ClassNotFoundException e) {
                remappings.put("net.minecraft.world.effect.MobEffect", "com.bergerkiller.bukkit.common.internal.proxy.MobEffectList");
            }
            remappings.put("net.minecraft.network.protocol.game.ServerboundUseItemOnPacket", "net.minecraft.network.protocol.game.ServerboundUseItemPacket");
            remappings.put("net.minecraft.network.protocol.game.ClientboundCustomSoundPacket", "net.minecraft.network.protocol.game.ClientboundSoundPacket");
            remappings.put("net.minecraft.world.entity.EquipmentSlot", "com.bergerkiller.bukkit.common.internal.proxy.EnumItemSlot");
            remappings.put("net.minecraft.world.level.chunk.PalettedContainer", "com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock");
            remappings.put("net.minecraft.sounds.SoundEvent", "com.bergerkiller.bukkit.common.internal.proxy.SoundEffect_1_8_8");
            remappings.put("net.minecraft.world.level.dimension.DimensionType", "com.bergerkiller.bukkit.common.internal.proxy.DimensionManager_1_8_8");
            remappings.put("net.minecraft.network.syncher.EntityDataAccessor", "com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject");
        }
        if (server instanceof CraftBukkitServer) {
            ((CraftBukkitServer)server).setEarlyRemappings(remappings);
        } else {
            Resolver.registerClassResolver(classPath -> {
                String remapped = (String)remappings.get(classPath);
                return remapped != null ? remapped : classPath;
            });
        }
        NullPacketDataSerializerInit.initialize();
        EmptyBlockGetterInit.initialize();
        ScopedProblemReporterInit.initialize();
        UnsetDataWatcherItemInit.initialize();
        Conversion.registerConverters(WrapperConversion.class);
        Conversion.registerConverters(HandleConversion.class);
        Conversion.registerConverters(NBTConversion.class);
        Conversion.registerConverters(ItemDisplayMode.class);
        Conversion.registerConverters(Brightness.class);
        Conversion.registerConverters(CommonEntityType.class);
        Conversion.registerConverters(MapConversion.class);
        Conversion.registerConverters(RelativeFlags.class);
        boolean hasEquipmentSlotClass = false;
        try {
            Class.forName("org.bukkit.inventory.EquipmentSlot");
            hasEquipmentSlotClass = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        if (hasEquipmentSlotClass) {
            Conversion.registerConverters(ItemSlotConversion.class);
        }
        if (CommonBootstrap.evaluateMCVersion("<", "1.9")) {
            Conversion.registerConverters(MC1_8_8_Conversion.class);
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            Conversion.registerConverters(HumanHandRole.class);
            Conversion.registerConverters(HumanHand.class);
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
            Conversion.registerConverters(EntityPoseConversion.class);
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.16")) {
            Conversion.registerConverters(TextColorChatColorConversion.class);
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            MC1_17_Conversion.init();
            Conversion.registerConverters(MC1_17_Conversion.class);
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.16") && CommonBootstrap.evaluateMCVersion("<=", "1.16.1") || CommonBootstrap.evaluateMCVersion(">=", "1.19")) {
            try {
                DimensionResourceKeyConversion.init();
                Conversion.registerConverters(DimensionResourceKeyConversion.class);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to register ResourceKey<>DimensionManager converter", t);
            }
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.18.2")) {
            MC1_18_2_Conversion.init();
            Conversion.registerConverters(MC1_18_2_Conversion.class);
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "26.2")) {
            Conversion.registerConverters(TeamColorConversion.class);
        }
        try {
            ScoreboardDisplaySlotConversion.init();
            Conversion.registerConverters(ScoreboardDisplaySlotConversion.class);
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to register Scoreboard DisplaySlot converters", t);
        }
        try {
            if (JOMLConversion.available()) {
                JOMLConversion.init();
                Conversion.registerConverters(JOMLConversion.class);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to register JOML converters", t);
        }
        SerializedEnumConversion.registerMinecraftEnumConversion();
        CommonUtil.getClass(Conversion.class.getName(), true);
        CommonUtil.getClass(PropertyConverter.class.getName(), true);
        CommonUtil.getClass(DuplexConversion.class.getName(), true);
    }

    public static void preloadCriticalComponents() {
        TagHandle.ByteArrayTagHandle.T.forceInitialization();
        TagHandle.ByteTagHandle.T.forceInitialization();
        TagHandle.DoubleTagHandle.T.forceInitialization();
        TagHandle.FloatTagHandle.T.forceInitialization();
        TagHandle.IntArrayTagHandle.T.forceInitialization();
        TagHandle.IntTagHandle.T.forceInitialization();
        if (CommonBootstrap.evaluateMCVersion(">=", "1.12")) {
            TagHandle.LongArrayTagHandle.T.forceInitialization();
        }
        TagHandle.LongTagHandle.T.forceInitialization();
        TagHandle.ShortTagHandle.T.forceInitialization();
        TagHandle.StringTagHandle.T.forceInitialization();
        CompoundTagHandle.T.forceInitialization();
        ListTagHandle.T.forceInitialization();
    }

    public static List<String> getGeneratedClassNames() throws IOException {
        boolean isRunFromBuildDirectory;
        try {
            if (Common.IS_TEST_MODE) {
                URL codeSource = CommonBootstrap.class.getProtectionDomain().getCodeSource().getLocation();
                if (codeSource == null) {
                    throw new IOException("Unable to determine code source location for BKCommonLib");
                }
                Path locationPath = Paths.get(codeSource.toURI());
                isRunFromBuildDirectory = Files.isDirectory(locationPath, new LinkOption[0]);
            } else {
                isRunFromBuildDirectory = false;
            }
        }
        catch (URISyntaxException e) {
            throw new IOException("Failed to resolve code source location", e);
        }
        List<Object> tmpClassNames = new ArrayList();
        if (isRunFromBuildDirectory) {
            String bkclClassesDir = System.getProperty("main.classes.dir");
            if (bkclClassesDir == null || bkclClassesDir.isEmpty()) {
                throw new IOException("Run under test without main.classes.dir set (gradle error?)");
            }
            Path bkclClassesPath = new File(bkclClassesDir).toPath();
            try (Stream<Path> bkclClassFiles = Files.walk(bkclClassesPath, new FileVisitOption[0]);){
                tmpClassNames = bkclClassFiles.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).map(bkclClassesPath::relativize).map(Path::toString).collect(Collectors.toList());
            }
        }
        URLClassLoader loader = (URLClassLoader)CommonBootstrap.class.getClassLoader();
        File jarFile = null;
        for (URL url : loader.getURLs()) {
            jarFile = new File(url.getFile());
            if (jarFile.exists()) continue;
            jarFile = null;
        }
        if (jarFile == null) {
            throw new IOException("Unable to determine the jar file of BKCommonLib");
        }
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));){
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory()) {
                    tmpClassNames.add(entry.getName());
                }
                entry = zip.getNextEntry();
            }
        }
        return tmpClassNames.stream().map(className -> {
            if (className.startsWith("/")) {
                className = className.substring(1);
            }
            return className;
        }).filter(name -> name.startsWith("com/bergerkiller/generated") && name.endsWith(".class")).map(name -> name.substring(0, name.length() - 6).replace('/', '.')).sorted().collect(Collectors.toCollection(ArrayList::new));
    }

    public static void preloadTemplateClasses(Random classLoaderOrderRandom) {
        List<String> classNames;
        try {
            classNames = CommonBootstrap.getGeneratedClassNames();
        }
        catch (IOException ex) {
            Logging.LOGGER.log(Level.WARNING, "Failed to pre-load template classes: listing failed", ex);
            return;
        }
        if (classLoaderOrderRandom != null) {
            Collections.shuffle(classNames, classLoaderOrderRandom);
        }
        classNames.parallelStream().map(className -> {
            try {
                Class<?> templateClass = Class.forName(className, true, CommonClasses.class.getClassLoader());
                if (Template.Handle.class.isAssignableFrom(templateClass)) {
                    Field templateClassInstanceField = templateClass.getDeclaredField("T");
                    Template.Class cls = (Template.Class)templateClassInstanceField.get(null);
                    return cls;
                }
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to load class " + className, t);
                return null;
            }
            return null;
        }).filter(Objects::nonNull).forEach(cls -> {
            try {
                cls.forceInitialization();
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to initialize " + cls.getHandleType(), t);
            }
        });
    }

    public static boolean isHeadlessJDK() {
        try {
            Class.forName("java.awt.Color");
            return false;
        }
        catch (ClassNotFoundException ex) {
            return true;
        }
    }

    static {
        _isCompatible = false;
        _incompatibleReason = null;
    }
}

