/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.gson.MapResourcePackDeserializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

public interface MapResourcePackArchive {
    public String name();

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    default public MapResourcePack.Metadata tryLoadMetadata(MapResourcePackDeserializer deserializer) {
        try (InputStream mcMetaInputStream = this.openFileStream("pack.mcmeta");){
            if (mcMetaInputStream == null) {
                Logging.LOGGER_MAPDISPLAY.warning("Resource pack " + this.name() + " has no pack.mcmeta");
                MapResourcePack.Metadata metadata = MapResourcePack.Metadata.fallback("missing pack.mcmeta");
                return metadata;
            }
            MapResourcePack.Metadata.PackWrapper wrap = deserializer.readGsonObject(MapResourcePack.Metadata.PackWrapper.class, mcMetaInputStream, "pack.mcmeta");
            if (wrap == null || wrap.pack == null) {
                Logging.LOGGER_MAPDISPLAY.warning("Resource pack " + this.name() + " pack.mcmeta could not be loaded (format error)");
                MapResourcePack.Metadata metadata = MapResourcePack.Metadata.fallback("corrupt pack.mcmeta");
                return metadata;
            }
            wrap.postLoad();
            MapResourcePack.Metadata metadata = wrap.pack;
            return metadata;
        }
        catch (Throwable t) {
            Logging.LOGGER_MAPDISPLAY.log(Level.WARNING, "Resource pack " + this.name() + " pack.mcmeta could not be loaded", t);
            return MapResourcePack.Metadata.fallback("error reading pack.mcmeta");
        }
    }

    public void load(boolean var1);

    default public void configure(MapResourcePack.Metadata metadata) {
    }

    default public InputStream openFileStream(String path) throws IOException {
        ArchiveResource resource = this.openResource(path);
        if (resource != null) {
            return resource.openStream();
        }
        return null;
    }

    public ArchiveResource openResource(String var1);

    default public List<String> listFiles(String folder) throws IOException {
        return this.listFiles(folder, false);
    }

    public List<String> listFiles(String var1, boolean var2) throws IOException;

    @FunctionalInterface
    public static interface ArchiveResource {
        public InputStream openStream() throws IOException;

        default public MapResourcePack.Resource toPackResource(final MapResourcePack pack, final MapResourcePack.ResourceType type, final String path) {
            return new MapResourcePack.Resource(){
                final /* synthetic */ ArchiveResource this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public MapResourcePack getPack() {
                    return pack;
                }

                @Override
                public MapResourcePack.ResourceType getType() {
                    return type;
                }

                @Override
                public String getPath() {
                    return path;
                }

                @Override
                public InputStream openStream() throws IOException {
                    return this.this$0.openStream();
                }
            };
        }
    }
}

