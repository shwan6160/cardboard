/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.bukkit.Chunk;

@Template.InstanceType(value="net.minecraft.server.level.ServerChunkCache")
public abstract class ServerChunkCacheHandle
extends Template.Handle {
    public static final ServerChunkCacheClass T = Template.Class.create(ServerChunkCacheClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerChunkCacheHandle createHandle(Object handleInstance) {
        return (ServerChunkCacheHandle)T.createHandle(handleInstance);
    }

    public static Chunk unpackGetChunkAsyncResult(Object result) {
        return ServerChunkCacheHandle.T.unpackGetChunkAsyncResult.invoke(result);
    }

    public abstract LevelChunkHandle getChunkAt(int var1, int var2);

    public abstract Executor getAsyncExecutor();

    public abstract void getChunkAtAsync(int var1, int var2, Consumer<?> var3);

    public abstract void saveLoadedChunk(LevelChunkHandle var1);

    public abstract void markBlockDirty(BlockPosHandle var1);

    public abstract ServerLevelHandle getWorld();

    public abstract void setWorld(ServerLevelHandle var1);

    public static final class ServerChunkCacheClass
    extends Template.Class<ServerChunkCacheHandle> {
        public final Template.Field.Converted<ServerLevelHandle> world = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<Chunk> unpackGetChunkAsyncResult = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<LevelChunkHandle> getChunkAt = new Template.Method.Converted();
        public final Template.Method<Executor> getAsyncExecutor = new Template.Method();
        public final Template.Method<Void> getChunkAtAsync = new Template.Method();
        public final Template.Method.Converted<Void> saveLoadedChunk = new Template.Method.Converted();
        public final Template.Method.Converted<Void> markBlockDirty = new Template.Method.Converted();
    }
}

