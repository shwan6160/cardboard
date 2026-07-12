/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkHolderHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.entity.Player;

@Template.InstanceType(value="net.minecraft.server.level.ChunkMap")
public abstract class ChunkMapHandle
extends Template.Handle {
    public static final ChunkMapClass T = Template.Class.create(ChunkMapClass.class, Common.TEMPLATE_RESOLVER);

    public static ChunkMapHandle createHandle(Object handleInstance) {
        return (ChunkMapHandle)T.createHandle(handleInstance);
    }

    public abstract ChunkHolderHandle getVisibleChunk(int var1, int var2);

    public abstract ChunkHolderHandle getUpdatingChunk(int var1, int var2);

    public abstract boolean isChunkEntered(ServerPlayerHandle var1, int var2, int var3);

    public Collection<Player> getChunkEnteredPlayers(int chunkX, int chunkZ) {
        ChunkHolderHandle playerChunk = this.getVisibleChunk(chunkX, chunkZ);
        if (playerChunk == null || playerChunk.getChunkIfLoaded() == null) {
            return Collections.emptyList();
        }
        return playerChunk.getPlayers();
    }

    public static final class ChunkMapClass
    extends Template.Class<ChunkMapHandle> {
        public final Template.Method.Converted<ChunkHolderHandle> getVisibleChunk = new Template.Method.Converted();
        public final Template.Method.Converted<ChunkHolderHandle> getUpdatingChunk = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> isChunkEntered = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method.Converted<Void> trackEntity = new Template.Method.Converted();
    }
}

