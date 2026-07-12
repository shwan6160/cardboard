/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.ModuleLogger
 *  com.bergerkiller.bukkit.common.bases.IntVector2
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.offline.OfflineWorldMap
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.mountiplex.reflection.ReflectionUtil
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.offline.sign;

import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.offline.OfflineWorldMap;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSign;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignLegacyImporter;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignMetadataHandler;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStoreListener;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStoreUpgradeV1ToV2;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;

public class OfflineSignStore
implements LibraryComponent {
    private final TrainCarts plugin;
    private final ModuleLogger logger;
    private final OfflineWorldMap<OfflineSignWorldStore> byWorld = new OfflineWorldMap();
    private final Map<String, MetadataHandlerEntry<?>> handlers = new HashMap();
    private final Map<Class<?>, MetadataHandlerEntry<?>> handlersByMetadataType = new HashMap();
    private final Map<String, List<OfflineMetadataEntry<Object>>> pendingByMetadataType = new HashMap<String, List<OfflineMetadataEntry<Object>>>();
    private final ImplicitlySharedSet<OfflineMetadataEntry<?>> allEntries = new ImplicitlySharedSet(new LinkedHashSet());
    private final OfflineSignLegacyImporter legacyImporter;
    private final BackgroundWriter writer;
    private final OfflineSignStoreListener listener;

    public OfflineSignStore(TrainCarts plugin) {
        this.plugin = plugin;
        this.logger = new ModuleLogger((Plugin)plugin, new String[]{"OfflineSignStore"});
        this.legacyImporter = new OfflineSignLegacyImporter(this, plugin);
        this.writer = new BackgroundWriter(plugin.getDataFile(new String[]{"SignMetadata.dat"}));
        this.listener = new OfflineSignStoreListener(this);
    }

    public TrainCarts getPlugin() {
        return this.plugin;
    }

    public void load() {
        this.clearAllEntries();
        this.writer.load();
    }

    public void enable() {
        this.legacyImporter.enable();
        this.writer.start();
        this.plugin.register(this.listener);
        for (World world : Bukkit.getWorlds()) {
            this.loadSignsOnWorld(world);
        }
    }

    public void disable() {
        for (MetadataHandlerEntry<?> metadataHandlerEntry : this.handlers.values()) {
            Plugin plugin = CommonUtil.getPluginByClass(metadataHandlerEntry.metadataType);
            String pluginNamePart = plugin == null ? "" : " [Plugin " + plugin.getName() + "] ";
            this.logger.log(Level.WARNING, "[Developer] " + pluginNamePart + "Sign metadata handler for " + metadataHandlerEntry.metadataTypeName + " is still registered! Please call unregisterHandler() in onDisable() to fix this warning!");
        }
        for (OfflineMetadataEntry offlineMetadataEntry : this.allEntries.cloneAsIterable()) {
            if (offlineMetadataEntry.clearHandler()) continue;
            this.removeEntry(offlineMetadataEntry);
        }
        this.handlers.clear();
        this.handlersByMetadataType.clear();
        this.writer.stop();
    }

    public <H extends OfflineSignMetadataHandler<T>, T> H registerHandler(Class<T> metadataType, H handler) {
        MetadataHandlerEntry<T> newHandlerEntry = new MetadataHandlerEntry<T>(metadataType, handler);
        MetadataHandlerEntry<T> existing = this.handlers.put(newHandlerEntry.metadataTypeName, newHandlerEntry);
        if (existing != null) {
            this.handlers.put(newHandlerEntry.metadataTypeName, existing);
            if (existing.handler == handler) {
                return handler;
            }
            throw new IllegalStateException("A handler for " + existing.metadataTypeName + " is already registered: " + existing.handler.getClass().getName());
        }
        this.handlersByMetadataType.clear();
        this.handlers.values().forEach(h -> this.handlersByMetadataType.put(h.metadataType, (MetadataHandlerEntry<?>)h));
        List entries = (List)CommonUtil.unsafeCast(this.pendingByMetadataType.remove(newHandlerEntry.metadataTypeName));
        if (entries != null) {
            for (OfflineMetadataEntry entry : entries) {
                if (entry.isRemoved()) continue;
                this.initHandler(entry, newHandlerEntry);
            }
        }
        return handler;
    }

    public void unregisterHandler(Class<?> metadataType) {
        MetadataHandlerEntry<?> handlerEntry = this.handlersByMetadataType.get(metadataType);
        if (handlerEntry == null) {
            throw new IllegalArgumentException("Handler for type " + metadataType + " is not registered");
        }
        this.unregisterHandlerEntry(handlerEntry);
    }

    public void unregisterHandler(OfflineSignMetadataHandler<?> handler) {
        List<MetadataHandlerEntry> handlerEntries = this.handlers.values().stream().filter(e -> e.handler == handler).collect(Collectors.toList());
        if (handlerEntries.isEmpty()) {
            throw new IllegalArgumentException("Handler is not registered");
        }
        handlerEntries.forEach(e -> this.unregisterHandlerEntry((MetadataHandlerEntry<?>)e));
    }

    private void unregisterHandlerEntry(MetadataHandlerEntry<?> handlerEntry) {
        this.handlers.remove(handlerEntry.metadataTypeName);
        this.handlersByMetadataType.clear();
        this.handlers.values().forEach(h -> this.handlersByMetadataType.put(h.metadataType, (MetadataHandlerEntry<?>)h));
        List handlerEntries = this.byWorld.values().stream().flatMap(v -> v.values().stream()).filter(e -> ((OfflineMetadataEntry)e).handlerEntry == handlerEntry).collect(Collectors.toCollection(ArrayList::new));
        if (!handlerEntries.isEmpty()) {
            handlerEntries.forEach(e -> {
                if (!e.clearHandler()) {
                    this.removeEntry((OfflineMetadataEntry<?>)e);
                }
            });
            this.pendingByMetadataType.put(handlerEntry.metadataTypeName, handlerEntries);
        }
    }

    @Deprecated
    public <T> T computeIfAbsent(Block signBlock, Class<T> metadataType, Function<OfflineSign, ? extends T> factory) {
        return this.computeIfAbsent(signBlock, true, metadataType, factory);
    }

    @Deprecated
    public <T> T computeIfAbsent(Sign sign, Class<T> metadataType, Function<OfflineSign, ? extends T> factory) {
        return this.computeIfAbsent(sign, true, metadataType, factory);
    }

    @Deprecated
    public <T> T putIfPresent(OfflineBlock signBlock, T metadata) {
        return this.putIfPresent(signBlock, true, metadata);
    }

    @Deprecated
    public <T> T put(Block signBlock, T metadata) {
        return this.put(signBlock, true, metadata);
    }

    @Deprecated
    public <T> T put(Sign sign, T metadata) {
        return this.put(sign, true, metadata);
    }

    @Deprecated
    public <T> T remove(Block signBlock, Class<T> metadataType) {
        return this.remove(signBlock, true, metadataType);
    }

    @Deprecated
    public <T> T remove(OfflineBlock signBlock, Class<T> metadataType) {
        return this.remove(signBlock, true, metadataType);
    }

    public <T> T computeIfAbsent(RailLookup.TrackedSign sign, Class<T> metadataType, Function<OfflineSign, ? extends T> factory) {
        if (sign instanceof RailLookup.TrackedRealSign) {
            return this.computeIfAbsent(sign.sign, ((RailLookup.TrackedRealSign)sign).isFrontText(), metadataType, factory);
        }
        throw new IllegalArgumentException("Sign is not a real physical sign and cannot store metadata");
    }

    public <T> T computeIfAbsent(Sign sign, boolean frontText, Class<T> metadataType, Function<OfflineSign, ? extends T> factory) {
        return this.computeIfAbsentImpl(OfflineWorld.of((World)sign.getWorld()), new IntVector3(sign.getX(), sign.getY(), sign.getZ()), frontText, () -> sign, metadataType, factory);
    }

    public <T> T computeIfAbsent(Block signBlock, boolean frontText, Class<T> metadataType, Function<OfflineSign, ? extends T> factory) {
        return this.computeIfAbsentImpl(OfflineWorld.of((World)signBlock.getWorld()), new IntVector3(signBlock), frontText, OfflineSignStore.signFromBlockSupplier(signBlock), metadataType, factory);
    }

    private <T> T computeIfAbsentImpl(OfflineWorld world, IntVector3 position, boolean frontText, Supplier<Sign> signGetter, Class<T> metadataType, Function<OfflineSign, ? extends T> factory) {
        T metadata;
        MetadataHandlerEntry<T> handlerEntry = this.findHandlerByType(metadataType);
        OfflineSign sign = null;
        OfflineSignWorldStore atWorld = this.forWorld(world);
        List<OfflineMetadataEntry<Object>> entries = atWorld.at(position);
        for (OfflineMetadataEntry<Object> entry : entries) {
            if (entry.sign.isFrontText() != frontText) continue;
            sign = entry.sign;
            if (((OfflineMetadataEntry)entry).handlerEntry != handlerEntry) continue;
            return (T)((OfflineMetadataEntry)entry).metadata;
        }
        if (sign == null) {
            sign = OfflineSign.fromSign(signGetter.get(), frontText);
        }
        if ((metadata = factory.apply(sign)) != null) {
            OfflineMetadataEntry<T> newEntry = new OfflineMetadataEntry<T>(sign, handlerEntry, metadata);
            entries.add(newEntry);
            atWorld.atChunk(position.toChunkCoordinates()).add(newEntry);
            this.onEntryAdded(newEntry);
        }
        return metadata;
    }

    public <T> T putIfPresent(OfflineSign sign, T metadata) {
        return this.putIfPresent(sign.getBlock(), sign.isFrontText(), metadata);
    }

    public <T> T putIfPresent(RailLookup.TrackedSign sign, T metadata) {
        if (sign instanceof RailLookup.TrackedRealSign) {
            return this.putIfPresent(OfflineBlock.of((Block)sign.signBlock), ((RailLookup.TrackedRealSign)sign).isFrontText(), metadata);
        }
        return null;
    }

    public <T> T putIfPresent(OfflineBlock signBlock, boolean frontText, T metadata) {
        OfflineSignWorldStore atWorld = this.forWorld(signBlock.getWorld());
        MetadataHandlerEntry<T> handlerEntry = this.findHandler(metadata);
        List<OfflineMetadataEntry<Object>> entries = atWorld.at(signBlock.getPosition());
        for (OfflineMetadataEntry<Object> entry : entries) {
            if (entry.sign.isFrontText() != frontText || ((OfflineMetadataEntry)entry).handlerEntry != handlerEntry) continue;
            Object oldValue = ((OfflineMetadataEntry)entry).metadata;
            entry.setMetadata(metadata);
            return (T)oldValue;
        }
        return null;
    }

    public <T> T put(RailLookup.TrackedSign sign, T metadata) {
        if (sign instanceof RailLookup.TrackedRealSign) {
            return this.put(sign.sign, ((RailLookup.TrackedRealSign)sign).isFrontText(), metadata);
        }
        throw new IllegalArgumentException("Sign is not a real physical sign and cannot store metadata");
    }

    public <T> T put(Sign sign, boolean frontText, T metadata) {
        return this.putImpl(OfflineWorld.of((World)sign.getWorld()), new IntVector3(sign.getX(), sign.getY(), sign.getZ()), frontText, () -> sign, metadata);
    }

    public <T> T put(Block signBlock, boolean frontText, T metadata) {
        return this.putImpl(OfflineWorld.of((World)signBlock.getWorld()), new IntVector3(signBlock), frontText, OfflineSignStore.signFromBlockSupplier(signBlock), metadata);
    }

    private <T> T putImpl(OfflineWorld world, IntVector3 position, boolean frontText, Supplier<Sign> signGetter, T metadata) {
        OfflineSignWorldStore atWorld = this.forWorld(world);
        MetadataHandlerEntry<T> handlerEntry = this.findHandler(metadata);
        OfflineSign offlineSign = null;
        List<OfflineMetadataEntry<Object>> entries = atWorld.at(position);
        for (OfflineMetadataEntry<Object> entry : entries) {
            if (entry.sign.isFrontText() != frontText) continue;
            offlineSign = entry.sign;
            if (((OfflineMetadataEntry)entry).handlerEntry != handlerEntry) continue;
            Object oldValue = ((OfflineMetadataEntry)entry).metadata;
            entry.setMetadata(metadata);
            return (T)oldValue;
        }
        if (offlineSign == null) {
            offlineSign = OfflineSign.fromSign(signGetter.get(), frontText);
        }
        OfflineMetadataEntry<T> newEntry = new OfflineMetadataEntry<T>(offlineSign, handlerEntry, metadata);
        entries.add(newEntry);
        atWorld.atChunk(position.toChunkCoordinates()).add(newEntry);
        this.onEntryAdded(newEntry);
        return null;
    }

    public <T> Collection<Entry<T>> getAllEntries(Class<T> metadataType) {
        MetadataHandlerEntry<T> handler = this.tryFindHandlerByType(metadataType);
        if (handler == null) {
            return Collections.emptyList();
        }
        if (handler.metadataType == metadataType) {
            return Collections.unmodifiableCollection(handler.entries);
        }
        ArrayList<Entry<T>> entriesFiltered = new ArrayList<Entry<T>>(handler.entries.size());
        for (Entry entry : handler.entries) {
            if (!metadataType.isInstance(entry.getMetadata())) continue;
            entriesFiltered.add(entry);
        }
        return entriesFiltered;
    }

    public <T> T get(OfflineSign sign, Class<T> metadataType) {
        return this.get(sign.getBlock(), sign.isFrontText(), metadataType);
    }

    public <T> T get(RailLookup.TrackedSign sign, Class<T> metadataType) {
        if (sign instanceof RailLookup.TrackedRealSign) {
            return this.get(sign.sign, ((RailLookup.TrackedRealSign)sign).isFrontText(), metadataType);
        }
        return null;
    }

    public <T> T get(Block signBlock, boolean frontText, Class<T> metadataType) {
        return this.get(OfflineWorld.of((World)signBlock.getWorld()), new IntVector3(signBlock), frontText, metadataType);
    }

    public <T> T get(Sign sign, boolean frontText, Class<T> metadataType) {
        return this.get(OfflineWorld.of((World)sign.getWorld()), new IntVector3(sign.getX(), sign.getY(), sign.getZ()), frontText, metadataType);
    }

    public <T> T get(OfflineBlock signBlock, boolean frontText, Class<T> metadataType) {
        return this.get(signBlock.getWorld(), signBlock.getPosition(), frontText, metadataType);
    }

    public <T> T get(OfflineWorld world, IntVector3 position, boolean frontText, Class<T> metadataType) {
        for (OfflineMetadataEntry<Object> entry : this.forWorld(world).at(position)) {
            Object metadata;
            if (entry.sign.isFrontText() != frontText || !metadataType.isInstance(metadata = entry.getMetadata())) continue;
            return (T)metadata;
        }
        return null;
    }

    public void removeAll(Block signBlock) {
        this.removeAll(OfflineBlock.of((Block)signBlock));
    }

    public void removeAll(Block signBlock, boolean frontText) {
        this.removeAll(OfflineBlock.of((Block)signBlock), frontText);
    }

    public void removeAll(OfflineBlock signBlock) {
        boolean hasMoreMetadata;
        OfflineSignWorldStore atWorld = this.forWorld(signBlock.getWorld());
        do {
            Iterator<OfflineMetadataEntry<Object>> iter;
            if (!(iter = atWorld.at(signBlock.getPosition()).iterator()).hasNext()) {
                return;
            }
            OfflineMetadataEntry<Object> entry = iter.next();
            hasMoreMetadata = iter.hasNext();
            iter.remove();
            atWorld.atChunk(signBlock.getPosition().toChunkCoordinates()).remove(entry);
            this.onEntryRemoved(entry);
        } while (hasMoreMetadata);
    }

    public void removeAll(OfflineBlock signBlock, boolean frontText) {
        boolean hasMoreMetadata;
        OfflineSignWorldStore atWorld = this.forWorld(signBlock.getWorld());
        do {
            OfflineMetadataEntry<Object> entry;
            Iterator<OfflineMetadataEntry<Object>> iter = atWorld.at(signBlock.getPosition()).iterator();
            do {
                if (!iter.hasNext()) {
                    return;
                }
                entry = iter.next();
            } while (entry.sign.isFrontText() != frontText);
            hasMoreMetadata = iter.hasNext();
            iter.remove();
            atWorld.atChunk(signBlock.getPosition().toChunkCoordinates()).remove(entry);
            this.onEntryRemoved(entry);
        } while (hasMoreMetadata);
    }

    public <T> T remove(OfflineSign sign, Class<T> metadataType) {
        return this.remove(sign.getBlock(), sign.isFrontText(), metadataType);
    }

    public <T> T remove(RailLookup.TrackedSign sign, Class<T> metadataType) {
        if (sign instanceof RailLookup.TrackedRealSign) {
            return this.remove(sign.signBlock, ((RailLookup.TrackedRealSign)sign).isFrontText(), metadataType);
        }
        return null;
    }

    public <T> T remove(Block signBlock, boolean frontText, Class<T> metadataType) {
        return this.remove(OfflineBlock.of((Block)signBlock), frontText, metadataType);
    }

    public <T> T remove(OfflineBlock signBlock, boolean frontText, Class<T> metadataType) {
        OfflineSignWorldStore atWorld = this.forWorld(signBlock.getWorld());
        Iterator<OfflineMetadataEntry<Object>> iter = atWorld.at(signBlock.getPosition()).iterator();
        while (iter.hasNext()) {
            Object metadata;
            OfflineMetadataEntry<Object> entry = iter.next();
            if (entry.sign.isFrontText() != frontText || !metadataType.isInstance(metadata = entry.getMetadata())) continue;
            iter.remove();
            atWorld.atChunk(signBlock.getPosition().toChunkCoordinates()).remove(entry);
            this.onEntryRemoved(entry);
            return (T)metadata;
        }
        return null;
    }

    public void verifySign(Sign sign) {
        IntVector3 position = new IntVector3(sign.getX(), sign.getY(), sign.getZ());
        OfflineSignWorldStore atWorld = this.forWorld(sign.getWorld());
        Iterator<OfflineMetadataEntry<Object>> iter = atWorld.at(position).iterator();
        while (iter.hasNext()) {
            OfflineSign newSign;
            OfflineMetadataEntry<Object> entry = iter.next();
            if (entry.sign.verify(sign) || entry.callOnSignChanged(newSign = OfflineSign.fromSign(sign, entry.sign.isFrontText()))) continue;
            iter.remove();
            atWorld.atChunk(position.toChunkCoordinates()).remove(entry);
            this.onEntryRemoved(entry);
        }
    }

    public <T> T verifySign(Sign sign, boolean frontText, Class<T> metadataType) {
        IntVector3 position = new IntVector3(sign.getX(), sign.getY(), sign.getZ());
        OfflineSignWorldStore atWorld = this.forWorld(sign.getWorld());
        Iterator<OfflineMetadataEntry<Object>> iter = atWorld.at(position).iterator();
        Object result = null;
        while (iter.hasNext()) {
            OfflineSign newSign;
            OfflineMetadataEntry<Object> entry = iter.next();
            if (entry.sign.isFrontText() != frontText) continue;
            if (!entry.sign.verify(sign) && !entry.callOnSignChanged(newSign = OfflineSign.fromSign(sign, frontText))) {
                iter.remove();
                atWorld.atChunk(position.toChunkCoordinates()).remove(entry);
                this.onEntryRemoved(entry);
                continue;
            }
            Object metadata = entry.getMetadata();
            if (metadataType == null || !metadataType.isInstance(metadata)) continue;
            result = metadata;
        }
        return (T)result;
    }

    private void removeEntry(OfflineMetadataEntry<?> entryToRemove) {
        OfflineSignWorldStore atWorld = this.forWorld(entryToRemove.sign.getWorld());
        Iterator<OfflineMetadataEntry<Object>> iter = atWorld.at(entryToRemove.sign.getPosition()).iterator();
        while (iter.hasNext()) {
            OfflineMetadataEntry<Object> entry = iter.next();
            if (entry != entryToRemove) continue;
            iter.remove();
            atWorld.atChunk(entry.sign.getPosition().toChunkCoordinates()).remove(entry);
            this.onEntryRemoved(entry);
            break;
        }
    }

    private static Supplier<Sign> signFromBlockSupplier(Block block) {
        return () -> {
            Sign bukkitSign = BlockUtil.getSign((Block)block);
            if (bukkitSign == null) {
                throw new IllegalArgumentException(String.format("Block on world %s at [x=%d y=%d z=%d] is not a sign", block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
            }
            return bukkitSign;
        };
    }

    private OfflineSignWorldStore forWorld(OfflineWorld world) {
        return (OfflineSignWorldStore)this.byWorld.computeIfAbsent(world, OfflineSignWorldStore::new);
    }

    private OfflineSignWorldStore forWorld(World world) {
        return (OfflineSignWorldStore)this.byWorld.computeIfAbsent(world, OfflineSignWorldStore::new);
    }

    private void clearAllEntries() {
        this.byWorld.clear();
        this.allEntries.clear();
        this.pendingByMetadataType.clear();
    }

    private void loadEntry(String metadataTypeName, OfflineMetadataEntry<Object> newEntry) {
        OfflineSignWorldStore forWorld = this.forWorld(newEntry.sign.getWorld());
        forWorld.at(newEntry.sign.getPosition()).add(newEntry);
        forWorld.atChunk(newEntry.sign.getPosition().toChunkCoordinates()).add(newEntry);
        this.allEntries.add((Object)((OfflineMetadataEntry)CommonUtil.unsafeCast(newEntry)));
        MetadataHandlerEntry<?> handler = this.handlers.get(metadataTypeName);
        if (handler != null) {
            this.initHandler(newEntry, (MetadataHandlerEntry)CommonUtil.unsafeCast(handler));
        } else {
            List pending = this.pendingByMetadataType.computeIfAbsent(metadataTypeName, k -> new ArrayList());
            pending.add(newEntry);
        }
    }

    private <T> void initHandler(OfflineMetadataEntry<T> entry, MetadataHandlerEntry<T> handler) {
        OfflineSignWorldStore atWorld = this.forWorld(entry.sign.getWorld());
        List<OfflineMetadataEntry<Object>> entriesAtBlock = atWorld.at(entry.sign.getPosition());
        boolean hasDuplicateEntry = false;
        for (OfflineMetadataEntry<Object> existingEntry : entriesAtBlock) {
            if (existingEntry == entry || existingEntry.sign.isFrontText() != entry.sign.isFrontText() || ((OfflineMetadataEntry)existingEntry).handlerEntry != handler) continue;
            hasDuplicateEntry = true;
            break;
        }
        if (hasDuplicateEntry || !entry.setHandler(handler)) {
            Iterator<OfflineMetadataEntry<Object>> iter = entriesAtBlock.iterator();
            while (iter.hasNext()) {
                if (iter.next() != entry) continue;
                iter.remove();
                atWorld.atChunk(entry.sign.getPosition().toChunkCoordinates()).remove(entry);
                break;
            }
            this.onEntryRemoved(entry);
        }
    }

    protected void unloadSignsOnWorld(World world) {
        for (OfflineMetadataEntry<Object> entry : new ArrayList<OfflineMetadataEntry<Object>>(this.forWorld(world).values())) {
            if (((OfflineMetadataEntry)entry).handlerEntry == null || !((OfflineMetadataEntry)entry).handlerEntry.handler.isUnloadedWorldsIgnored() || entry.unload()) continue;
            this.removeEntry(entry);
        }
    }

    protected void loadSignsOnWorld(World world) {
        for (OfflineMetadataEntry<Object> entry : new ArrayList<OfflineMetadataEntry<Object>>(this.forWorld(world).values())) {
            if (((OfflineMetadataEntry)entry).addedToHandler || ((OfflineMetadataEntry)entry).handlerEntry == null) continue;
            if (entry.decodeMetadata()) {
                entry.callOnLoaded();
                continue;
            }
            this.removeEntry(entry);
        }
        for (Chunk chunk : world.getLoadedChunks()) {
            this.verifySignsInChunk(chunk);
        }
    }

    protected void verifySignsInChunk(Chunk chunk) {
        HashMap<IntVector3, Sign> signsByBlock;
        OfflineSignWorldStore atWorld = this.forWorld(OfflineWorld.of((World)chunk.getWorld()));
        Iterator<OfflineMetadataEntry<Object>> entriesAtChunk = atWorld.atChunk(new IntVector2(chunk)).iterator();
        if (!entriesAtChunk.hasNext()) {
            return;
        }
        try {
            Collection blockStates = WorldUtil.getBlockStates((Chunk)chunk);
            signsByBlock = new HashMap<IntVector3, Sign>(blockStates.size());
            for (BlockState state : blockStates) {
                if (!(state instanceof Sign)) continue;
                Sign s = (Sign)state;
                signsByBlock.put(new IntVector3(s.getX(), s.getY(), s.getZ()), s);
            }
        }
        catch (Throwable t) {
            this.logger.log(Level.SEVERE, String.format("Failed to read BlockStates in chunk {world=%s, x=%d, z=%d}, verify failed", chunk.getWorld().getName(), chunk.getX(), chunk.getZ()), t);
            return;
        }
        do {
            OfflineSign newSign;
            OfflineMetadataEntry<Object> entry = entriesAtChunk.next();
            Sign sign = (Sign)signsByBlock.get(entry.sign.getPosition());
            if (sign != null && (entry.sign.verify(sign) || entry.callOnSignChanged(newSign = OfflineSign.fromSign(sign, entry.sign.isFrontText())))) continue;
            entriesAtChunk.remove();
            atWorld.at(entry.sign.getPosition()).remove(entry);
            this.onEntryRemoved(entry);
        } while (entriesAtChunk.hasNext());
    }

    private void onEntryAdded(OfflineMetadataEntry<?> entry) {
        this.allEntries.add((Object)((OfflineMetadataEntry)CommonUtil.unsafeCast(entry)));
        ((OfflineMetadataEntry)entry).handlerEntry.entries.add((OfflineMetadataEntry)CommonUtil.unsafeCast(entry));
        this.writer.changed();
        entry.callOnAdded();
    }

    private void onEntryRemoved(OfflineMetadataEntry<?> entry) {
        if (this.allEntries.remove(entry)) {
            this.writer.changed();
        }
        ((OfflineMetadataEntry)entry).removed = true;
        entry.callOnRemoved();
    }

    private <T> MetadataHandlerEntry<T> findHandler(T metadataValue) {
        if (metadataValue == null) {
            throw new IllegalArgumentException("Metadata value type is null");
        }
        return this.findHandlerByType(metadataValue.getClass());
    }

    private <T> MetadataHandlerEntry<T> findHandlerByType(Class<?> metadataType) {
        MetadataHandlerEntry<T> handler = this.tryFindHandlerByType(metadataType);
        if (handler != null) {
            return handler;
        }
        throw new IllegalArgumentException("No handler is registered for metadata type " + metadataType);
    }

    private <T> MetadataHandlerEntry<T> tryFindHandlerByType(Class<?> metadataType) {
        MetadataHandlerEntry<?> handler = this.handlersByMetadataType.get(metadataType);
        if (handler != null) {
            return (MetadataHandlerEntry)CommonUtil.unsafeCast(handler);
        }
        for (Class superType : ReflectionUtil.getAllClassesAndInterfaces(metadataType).collect(Collectors.toList())) {
            handler = this.handlersByMetadataType.get(superType);
            if (handler == null) continue;
            this.handlersByMetadataType.put(metadataType, handler);
            return (MetadataHandlerEntry)CommonUtil.unsafeCast(handler);
        }
        return null;
    }

    private static void atomicMove(File fromFile, File toFile) throws Throwable {
        try {
            Files.move(fromFile.toPath(), toFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        catch (UnsupportedOperationException | AtomicMoveNotSupportedException exception) {
            if (!fromFile.exists()) {
                throw new IOException("File " + fromFile + " does not exist");
            }
            if (toFile.delete() && fromFile.renameTo(toFile)) {
                return;
            }
            if (StreamUtil.tryCopyFile((File)fromFile, (File)toFile)) {
                fromFile.delete();
                return;
            }
            throw new IOException("Atomic move from " + fromFile + " to " + toFile + " failed");
        }
    }

    private final class OfflineMetadataEntry<T>
    implements Entry<T> {
        public OfflineSign sign;
        private MetadataHandlerEntry<T> handlerEntry;
        private byte[] encodedData;
        private T metadata;
        private boolean removed;
        private boolean addedToHandler;

        public OfflineMetadataEntry(OfflineSign sign, MetadataHandlerEntry<T> handlerEntry, T metadata) {
            this.sign = sign;
            this.handlerEntry = handlerEntry;
            this.encodedData = null;
            this.metadata = metadata;
            this.removed = false;
            this.addedToHandler = false;
        }

        public OfflineMetadataEntry(OfflineSign sign, byte[] encodedData) {
            this.sign = sign;
            this.handlerEntry = null;
            this.encodedData = encodedData;
            this.metadata = null;
            this.removed = false;
            this.addedToHandler = false;
        }

        @Override
        public OfflineSign getSign() {
            return this.sign;
        }

        @Override
        public T getMetadata() {
            return this.metadata;
        }

        @Override
        public boolean isRemoved() {
            return this.removed;
        }

        public boolean clearHandler() {
            if (this.handlerEntry == null) {
                return true;
            }
            if (this.encodeMetadata() == null) {
                return false;
            }
            this.handlerEntry.entries.remove(this);
            this.callOnUnloaded();
            this.handlerEntry = null;
            return true;
        }

        public boolean setHandler(MetadataHandlerEntry<T> handlerEntry) {
            if (this.handlerEntry == handlerEntry) {
                return true;
            }
            if (this.handlerEntry != null) {
                OfflineSignStore.this.logger.log(Level.SEVERE, "Attempted to register handler " + handlerEntry.handler.getClass().getName() + " for sign " + this.sign + " but another handler was already registered");
                OfflineSignStore.this.logger.log(Level.SEVERE, "Handler currently registered: " + this.handlerEntry.handler.getClass().getName());
                return false;
            }
            if (this.encodedData == null) {
                OfflineSignStore.this.logger.log(Level.SEVERE, "Attempted to decode metadata for sign " + this.sign + " but no encoded data is available to decode");
                return false;
            }
            this.handlerEntry = handlerEntry;
            this.handlerEntry.entries.add(this);
            if (!this.sign.getWorld().isLoaded() && handlerEntry.handler.isUnloadedWorldsIgnored()) {
                return true;
            }
            if (!this.decodeMetadata()) {
                return false;
            }
            this.callOnLoaded();
            return true;
        }

        public boolean unload() {
            if (this.encodeMetadata() == null) {
                return false;
            }
            this.callOnUnloaded();
            this.metadata = null;
            return true;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public boolean decodeMetadata() {
            try {
                ByteArrayInputStream b_stream = new ByteArrayInputStream(this.encodedData);
                try {
                    InflaterInputStream d_stream = new InflaterInputStream(b_stream);
                    try (DataInputStream stream = new DataInputStream(d_stream);){
                        OfflineSignMetadataHandler.DataMigrationDecoder decoder;
                        OfflineSign.readFrom(stream);
                        stream.readUTF();
                        int metadataVersion = Util.readVariableLengthInt(stream);
                        if (metadataVersion == this.handlerEntry.handler.getMetadataVersion()) {
                            this.metadata = this.handlerEntry.handler.onDecode(stream, this.sign);
                            if (this.metadata != null) return true;
                            throw new IllegalStateException("Decoded metadata is null");
                        }
                        try {
                            decoder = this.handlerEntry.handler.getMigrationDecoder(this.sign, metadataVersion);
                            if (decoder == null) {
                                throw new UnsupportedOperationException("Not supported");
                            }
                        }
                        catch (UnsupportedOperationException ex) {
                            OfflineSignStore.this.logger.log(Level.WARNING, "Failed to decode metadata for sign " + this.sign + ": Unsupported data version (type=" + this.handlerEntry.metadataTypeName + ")");
                            boolean bl = false;
                            stream.close();
                            d_stream.close();
                            b_stream.close();
                            return bl;
                        }
                        this.metadata = decoder.onDecode(stream, this.sign, metadataVersion);
                        if (this.metadata != null) return true;
                        throw new IllegalStateException("Failed to migrate metadata: decoded metadata is null");
                    }
                    finally {
                        try {
                            d_stream.close();
                        }
                        catch (Throwable throwable) {
                            Throwable throwable2;
                            throwable2.addSuppressed(throwable);
                        }
                    }
                }
                finally {
                    try {
                        b_stream.close();
                    }
                    catch (Throwable throwable) {
                        Throwable throwable3;
                        throwable3.addSuppressed(throwable);
                    }
                }
            }
            catch (OfflineSignMetadataHandler.InvalidMetadataException ex) {
                return false;
            }
            catch (Throwable t) {
                OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to decode metadata for sign " + this.sign, t);
                return false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public byte[] encodeMetadata() {
            byte[] encodedData = this.encodedData;
            if (encodedData == null) {
                OfflineMetadataEntry offlineMetadataEntry = this;
                synchronized (offlineMetadataEntry) {
                    encodedData = this.encodedData;
                    if (encodedData == null) {
                        try (ByteArrayOutputStream b_stream = new ByteArrayOutputStream();){
                            try (DeflaterOutputStream d_stream = new DeflaterOutputStream(b_stream);
                                 DataOutputStream stream = new DataOutputStream(d_stream);){
                                OfflineSign.writeTo(stream, this.sign);
                                stream.writeUTF(this.handlerEntry.metadataTypeName);
                                Util.writeVariableLengthInt(stream, this.handlerEntry.handler.getMetadataVersion());
                                this.handlerEntry.handler.onEncode(stream, this.sign, this.metadata);
                            }
                            this.encodedData = encodedData = b_stream.toByteArray();
                        }
                        catch (OfflineSignMetadataHandler.InvalidMetadataException ex) {
                            return null;
                        }
                        catch (Throwable t) {
                            OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to encode metadata for sign " + this.sign, t);
                            return null;
                        }
                    }
                }
            }
            return encodedData;
        }

        public boolean callOnSignChanged(OfflineSign newSign) {
            Object newMetadata;
            T oldMetadata = this.metadata;
            if (this.handlerEntry == null || oldMetadata == null) {
                return false;
            }
            try {
                newMetadata = this.handlerEntry.handler.onSignChanged(OfflineSignStore.this, this.sign, newSign, oldMetadata);
                if (newMetadata == null) {
                    return false;
                }
            }
            catch (Throwable t) {
                OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to handle onSignChanged for sign " + newSign, t);
                return false;
            }
            this.sign = newSign;
            this.setMetadataFireEvent(newMetadata);
            return true;
        }

        public void callOnAdded() {
            if (!this.addedToHandler && this.handlerEntry != null) {
                try {
                    this.handlerEntry.handler.onAdded(OfflineSignStore.this, this.sign, this.metadata);
                }
                catch (Throwable t) {
                    OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to handle onAdded for sign " + this.sign, t);
                }
                this.addedToHandler = true;
            }
        }

        public void callOnRemoved() {
            if (this.addedToHandler && this.handlerEntry != null && this.metadata != null) {
                try {
                    this.handlerEntry.handler.onRemoved(OfflineSignStore.this, this.sign, this.metadata);
                }
                catch (Throwable t) {
                    OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to handle onRemoved for sign " + this.sign, t);
                }
                this.addedToHandler = false;
            }
        }

        public void callOnLoaded() {
            if (!this.addedToHandler && this.handlerEntry != null) {
                try {
                    this.handlerEntry.handler.onLoaded(OfflineSignStore.this, this.sign, this.metadata);
                }
                catch (Throwable t) {
                    OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to handle onLoaded for sign " + this.sign, t);
                }
                this.addedToHandler = true;
            }
        }

        public void callOnUnloaded() {
            if (this.addedToHandler && this.handlerEntry != null && this.metadata != null) {
                try {
                    this.handlerEntry.handler.onUnloaded(OfflineSignStore.this, this.sign, this.metadata);
                }
                catch (Throwable t) {
                    OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to handle onUnloaded for sign " + this.sign, t);
                }
                this.addedToHandler = false;
            }
        }

        @Override
        public void setMetadata(T metadata) {
            if (metadata == null) {
                throw new IllegalArgumentException("New metadata is null");
            }
            if (metadata.equals(this.metadata)) {
                return;
            }
            this.setMetadataFireEvent(metadata);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void setMetadataFireEvent(T metadata) {
            T oldMetadata = this.metadata;
            OfflineMetadataEntry offlineMetadataEntry = this;
            synchronized (offlineMetadataEntry) {
                this.metadata = metadata;
                this.encodedData = null;
            }
            if (this.handlerEntry != null) {
                try {
                    this.handlerEntry.handler.onUpdated(OfflineSignStore.this, this.sign, oldMetadata, metadata);
                }
                catch (Throwable t) {
                    OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to handle onUpdated for sign " + this.sign, t);
                }
            }
            OfflineSignStore.this.writer.changed();
        }

        @Override
        public void remove() {
            OfflineSignStore.this.removeEntry(this);
        }
    }

    private class BackgroundWriter {
        private Thread thread;
        private final Object lock = new Object();
        private final File saveFile;
        private volatile boolean savingNeeded = false;
        private volatile boolean shuttingDown = false;

        public BackgroundWriter(File saveFile) {
            this.saveFile = saveFile;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void changed() {
            Object object = this.lock;
            synchronized (object) {
                this.savingNeeded = true;
                this.lock.notifyAll();
            }
        }

        public void start() {
            this.shuttingDown = false;
            if (this.thread == null) {
                this.thread = new Thread(this::runWorker, "TrainCarts:SignMetadataWriterThread");
                this.thread.setDaemon(true);
                this.thread.start();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void stop() {
            Object object = this.lock;
            synchronized (object) {
                this.shuttingDown = true;
                this.lock.notifyAll();
            }
            if (this.thread != null) {
                try {
                    this.thread.join(10000L);
                    if (this.thread.isAlive()) {
                        OfflineSignStore.this.logger.log(Level.WARNING, "Saving sign metadata is taking longer than 10s");
                        this.thread.join();
                    }
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                this.thread = null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void runWorker() {
            long MIN_SAVE_INTERVAL = 5000L;
            long lastSaveTS = System.currentTimeMillis() - 5000L;
            do {
                boolean doSave = false;
                Object object = this.lock;
                synchronized (object) {
                    try {
                        long remaining;
                        while (!this.savingNeeded && !this.shuttingDown) {
                            this.lock.wait();
                        }
                        while (!this.shuttingDown && (remaining = lastSaveTS + 5000L - System.currentTimeMillis()) > 0L) {
                            this.lock.wait(remaining);
                        }
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    doSave = this.savingNeeded;
                    this.savingNeeded = false;
                }
                if (!doSave) continue;
                lastSaveTS = System.currentTimeMillis();
                this.save();
            } while (!this.shuttingDown);
        }

        public void load() {
            if (this.saveFile.exists()) {
                try (FileInputStream f_stream = new FileInputStream(this.saveFile);
                     DataInputStream stream = new DataInputStream(f_stream);){
                    this.load(stream);
                }
                catch (EOFException ex) {
                    OfflineSignStore.this.logger.log(Level.SEVERE, "Reached unexpected end-of-file while reading sign metadata (corrupted file?)");
                }
                catch (IOException ex) {
                    OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to read sign metadata", (Throwable)ex);
                }
            }
        }

        private void load(DataInputStream stream) throws IOException {
            int versionCode = Util.readVariableLengthInt(stream);
            if (versionCode == 1) {
                OfflineSignStore.this.logger.log(Level.WARNING, "Upgrading offline sign metadata format from V1 to V2");
                try (DataInputStream upgraded = OfflineSignStoreUpgradeV1ToV2.upgrade(stream);){
                    this.load(upgraded);
                }
                return;
            }
            if (versionCode != 2) {
                OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to read sign metadata: unsupported version " + versionCode);
                return;
            }
            while (stream.available() > 0) {
                String metadataTypeName;
                OfflineSign sign;
                byte[] encodedData = Util.readByteArray(stream);
                try (ByteArrayInputStream m_b_stream = new ByteArrayInputStream(encodedData);
                     InflaterInputStream m_d_stream = new InflaterInputStream(m_b_stream);
                     DataInputStream m_stream = new DataInputStream(m_d_stream);){
                    sign = OfflineSign.readFrom(m_stream);
                    metadataTypeName = m_stream.readUTF();
                }
                OfflineMetadataEntry newEntry = new OfflineMetadataEntry(sign, encodedData);
                OfflineSignStore.this.loadEntry(metadataTypeName, newEntry);
            }
        }

        public void save() {
            ArrayList<OfflineMetadataEntry> encodeFailures = new ArrayList<OfflineMetadataEntry>();
            File tmpFile = new File(this.saveFile.getParentFile(), this.saveFile.getName() + "." + System.currentTimeMillis() + ".tmp");
            boolean saveSuccessful = false;
            try {
                try (FileOutputStream f_stream = new FileOutputStream(tmpFile);
                     DataOutputStream stream = new DataOutputStream(f_stream);){
                    Util.writeVariableLengthInt(stream, 2);
                    for (OfflineMetadataEntry entry : OfflineSignStore.this.allEntries.cloneAsIterable()) {
                        byte[] encodedData = entry.encodeMetadata();
                        if (encodedData != null) {
                            Util.writeByteArray(stream, encodedData);
                            continue;
                        }
                        encodeFailures.add(entry);
                    }
                }
                saveSuccessful = true;
            }
            catch (IOException ex) {
                OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to write sign metadata", (Throwable)ex);
            }
            if (saveSuccessful) {
                try {
                    OfflineSignStore.atomicMove(tmpFile, this.saveFile);
                }
                catch (Throwable t) {
                    OfflineSignStore.this.logger.log(Level.SEVERE, "Failed to finalize writing sign metadata", t);
                }
            }
            if (!encodeFailures.isEmpty()) {
                CommonUtil.getPluginExecutor((Plugin)OfflineSignStore.this.plugin).execute(() -> {
                    OfflineSignStore rec$ = OfflineSignStore.this;
                    encodeFailures.forEach(x$0 -> rec$.removeEntry(x$0));
                });
            }
        }
    }

    private static class MetadataHandlerEntry<T> {
        public final Class<T> metadataType;
        public final String metadataTypeName;
        public final OfflineSignMetadataHandler<T> handler;
        public final Set<OfflineMetadataEntry<T>> entries;

        public MetadataHandlerEntry(Class<T> metadataType, OfflineSignMetadataHandler<T> handler) {
            this.metadataType = metadataType;
            this.metadataTypeName = metadataType.getName();
            this.handler = handler;
            this.entries = new LinkedHashSet<OfflineMetadataEntry<T>>();
        }
    }

    private static final class OfflineSignWorldStore {
        private final OfflineWorld world;
        private final ListMultimap<IntVector3, OfflineMetadataEntry<Object>> byBlockCoordinates;
        private final ListMultimap<IntVector2, OfflineMetadataEntry<Object>> byChunkCoordinates;

        public OfflineSignWorldStore(World world) {
            this(OfflineWorld.of((World)world));
        }

        public OfflineSignWorldStore(OfflineWorld world) {
            this.world = world;
            this.byBlockCoordinates = ArrayListMultimap.create((int)1000, (int)1);
            this.byChunkCoordinates = ArrayListMultimap.create((int)500, (int)1);
        }

        public Collection<OfflineMetadataEntry<Object>> values() {
            return this.byBlockCoordinates.values();
        }

        public List<OfflineMetadataEntry<Object>> at(IntVector3 coordinate) {
            return this.byBlockCoordinates.get((Object)coordinate);
        }

        public List<OfflineMetadataEntry<Object>> atChunk(IntVector2 chunkCoordinates) {
            return this.byChunkCoordinates.get((Object)chunkCoordinates);
        }
    }

    public static interface Entry<T> {
        public OfflineSign getSign();

        public boolean isRemoved();

        public T getMetadata();

        public void setMetadata(T var1);

        public void remove();
    }
}

