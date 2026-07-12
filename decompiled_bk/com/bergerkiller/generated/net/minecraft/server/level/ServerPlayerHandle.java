/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.InventoryView
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

@Template.InstanceType(value="net.minecraft.server.level.ServerPlayer")
public abstract class ServerPlayerHandle
extends PlayerHandle {
    public static final ServerPlayerClass T = Template.Class.create(ServerPlayerClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerPlayerHandle createHandle(Object handleInstance) {
        return (ServerPlayerHandle)T.createHandle(handleInstance);
    }

    public abstract boolean hasDisconnected();

    public abstract RespawnConfigHandle getRespawnConfig();

    public abstract void setRespawnConfigSilent(RespawnConfigHandle var1);

    public abstract int getPing();

    public abstract boolean hasSeenCredits();

    public abstract void setHasSeenCredits(boolean var1);

    public abstract void sendMessage(ChatText var1);

    public abstract int getCurrentWindowId();

    public abstract InventoryView openAnvilWindow(ChatText var1);

    public abstract void openSignEditWindow(IntVector3 var1, boolean var2);

    @Deprecated
    public void setSpawnForced(boolean forced) {
    }

    public void closeSignEditWindow() {
        this.openSignEditWindow(IntVector3.of(Integer.MAX_VALUE, 0, Integer.MAX_VALUE));
    }

    public void openSignEditWindow(IntVector3 signPosition) {
        this.openSignEditWindow(signPosition, true);
    }

    public static ServerPlayerHandle fromBukkit(Player player) {
        return ServerPlayerHandle.createHandle(HandleConversion.toEntityHandle((Entity)player));
    }

    public abstract ServerGamePacketListenerImplHandle getPlayerConnection();

    public abstract void setPlayerConnection(ServerGamePacketListenerImplHandle var1);

    @Template.Readonly
    public abstract boolean isViewingCredits();

    public static final class ServerPlayerClass
    extends Template.Class<ServerPlayerHandle> {
        public final Template.Field.Converted<ServerGamePacketListenerImplHandle> playerConnection = new Template.Field.Converted();
        @Template.Readonly
        public final Template.Field.Boolean viewingCredits = new Template.Field.Boolean();
        public final Template.Method<Boolean> hasDisconnected = new Template.Method();
        public final Template.Method.Converted<RespawnConfigHandle> getRespawnConfig = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setRespawnConfigSilent = new Template.Method.Converted();
        public final Template.Method<Integer> getPing = new Template.Method();
        public final Template.Method<Boolean> hasSeenCredits = new Template.Method();
        public final Template.Method<Void> setHasSeenCredits = new Template.Method();
        public final Template.Method.Converted<Void> sendMessage = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method<Collection<Integer>> getRemoveQueue = new Template.Method();
        public final Template.Method<Integer> getCurrentWindowId = new Template.Method();
        public final Template.Method<InventoryView> openAnvilWindow = new Template.Method();
        public final Template.Method.Converted<Void> openSignEditWindow = new Template.Method.Converted();
    }

    @Template.InstanceType(value="net.minecraft.server.level.ServerPlayer.RespawnConfig")
    public static abstract class RespawnConfigHandle
    extends Template.Handle {
        public static final RespawnConfigClass T = Template.Class.create(RespawnConfigClass.class, Common.TEMPLATE_RESOLVER);

        public static RespawnConfigHandle createHandle(Object handleInstance) {
            return (RespawnConfigHandle)T.createHandle(handleInstance);
        }

        public static RespawnConfigHandle of(ResourceKey<World> dimension, String worldName, IntVector3 position, float yaw, float pitch, boolean forced) {
            return RespawnConfigHandle.T.of.invokeVA(dimension, worldName, position, Float.valueOf(yaw), Float.valueOf(pitch), forced);
        }

        public static RespawnConfigHandle codecFromNBT(CommonTagCompound nbt) {
            return RespawnConfigHandle.T.codecFromNBT.invoke(nbt);
        }

        public static void codecToNBT(RespawnConfigHandle respawnConfig, CommonTagCompound nbt) {
            RespawnConfigHandle.T.codecToNBT.invoke(respawnConfig, nbt);
        }

        public abstract ResourceKey<World> dimension();

        public abstract IntVector3 position();

        public abstract String worldName();

        public abstract float yaw();

        public abstract float pitch();

        public abstract boolean forced();

        @Deprecated
        public static RespawnConfigHandle of(ResourceKey<World> dimension, String worldName, IntVector3 position, float yaw, boolean forced) {
            return RespawnConfigHandle.of(dimension, worldName, position, yaw, 0.0f, forced);
        }

        public World world() {
            return WorldUtil.getWorldByDimensionKey(this.dimension());
        }

        public static RespawnConfigHandle of(World world, IntVector3 position, float angle, boolean forced) {
            return RespawnConfigHandle.of(WorldUtil.getDimensionKey(world), world.getName(), position, angle, forced);
        }

        public static final class RespawnConfigClass
        extends Template.Class<RespawnConfigHandle> {
            public final Template.StaticMethod.Converted<RespawnConfigHandle> of = new Template.StaticMethod.Converted();
            public final Template.StaticMethod.Converted<RespawnConfigHandle> codecFromNBT = new Template.StaticMethod.Converted();
            public final Template.StaticMethod.Converted<Void> codecToNBT = new Template.StaticMethod.Converted();
            public final Template.Method.Converted<ResourceKey<World>> dimension = new Template.Method.Converted();
            public final Template.Method.Converted<IntVector3> position = new Template.Method.Converted();
            public final Template.Method<String> worldName = new Template.Method();
            public final Template.Method<Float> yaw = new Template.Method();
            public final Template.Method<Float> pitch = new Template.Method();
            public final Template.Method<Boolean> forced = new Template.Method();
        }
    }
}

