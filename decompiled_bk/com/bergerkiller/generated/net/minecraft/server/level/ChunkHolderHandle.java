/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkMapHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

@Template.InstanceType(value="net.minecraft.server.level.ChunkHolder")
public abstract class ChunkHolderHandle
extends Template.Handle {
    public static final ChunkHolderClass T = Template.Class.create(ChunkHolderClass.class, Common.TEMPLATE_RESOLVER);

    public static ChunkHolderHandle createHandle(Object handleInstance) {
        return (ChunkHolderHandle)T.createHandle(handleInstance);
    }

    public abstract ChunkMapHandle getPlayerChunkMap();

    public abstract boolean resendChunk();

    public abstract boolean resendAllLighting();

    public abstract Collection<Player> getPlayers();

    public abstract IntVector2 getLocation();

    public abstract Chunk getChunkIfLoaded();

    public static final class ChunkHolderClass
    extends Template.Class<ChunkHolderHandle> {
        public final Template.Method.Converted<ChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted();
        public final Template.Method<Boolean> resendChunk = new Template.Method();
        public final Template.Method<Boolean> resendAllLighting = new Template.Method();
        public final Template.Method.Converted<Collection<Player>> getPlayers = new Template.Method.Converted();
        public final Template.Method.Converted<IntVector2> getLocation = new Template.Method.Converted();
        public final Template.Method.Converted<Chunk> getChunkIfLoaded = new Template.Method.Converted();
    }
}

