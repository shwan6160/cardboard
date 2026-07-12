/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.generated.net.minecraft.server.network;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.network.ConnectionHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Template.InstanceType(value="net.minecraft.server.network.ServerGamePacketListenerImpl")
public abstract class ServerGamePacketListenerImplHandle
extends Template.Handle {
    public static final ServerGamePacketListenerImplClass T = Template.Class.create(ServerGamePacketListenerImplClass.class, Common.TEMPLATE_RESOLVER);
    private static final QueuePacketMethod defaultQueuePacketMethod = ConnectionHandle::queuePacketUnsafe;
    private static final Map<Class<?>, QueuePacketMethod> queuePacketMethods = new ConcurrentHashMap(5, 0.75f, 2);

    public static ServerGamePacketListenerImplHandle createHandle(Object handleInstance) {
        return (ServerGamePacketListenerImplHandle)T.createHandle(handleInstance);
    }

    public abstract Object getNetworkManager();

    public abstract void sendPacket(Object var1);

    public abstract void sendPos(double var1, double var3, double var5);

    public abstract int getAwaitingTeleportId();

    public abstract void resetAwaitTeleport();

    private static QueuePacketMethod findPacketMethod(Class<?> networkManagerType) throws Throwable {
        String typeName = networkManagerType.getName();
        if (typeName.startsWith("com.denizenscript.denizen.nms.") && typeName.endsWith("DenizenNetworkManagerImpl")) {
            FastField oldManagerField = new FastField();
            oldManagerField.init(networkManagerType.getDeclaredField("oldManager"));
            oldManagerField.forceInitialization();
            return (networkManager, packet) -> {
                Object oldManager = oldManagerField.get(networkManager);
                return ServerGamePacketListenerImplHandle.queuePacket(oldManager, packet);
            };
        }
        if (typeName.startsWith("com.denizenscript.denizen.nms.") && typeName.endsWith("FakeNetworkManagerImpl")) {
            return defaultQueuePacketMethod;
        }
        return null;
    }

    private static boolean queuePacket(Object networkManager, Object packet) {
        if (networkManager != null) {
            QueuePacketMethod method = queuePacketMethods.get(networkManager.getClass());
            if (method == null) {
                try {
                    method = ServerGamePacketListenerImplHandle.findPacketMethod(networkManager.getClass());
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (method != null) {
                    queuePacketMethods.put(networkManager.getClass(), method);
                } else {
                    queuePacketMethods.put(networkManager.getClass(), (n, p) -> false);
                    Logging.LOGGER_NETWORK.warning("Unsupported Connection detected: " + networkManager.getClass().getName());
                    return false;
                }
            }
            if (method.queuePacket(networkManager, packet)) {
                return true;
            }
        }
        return false;
    }

    public void queuePacket(Object packet) {
        if (!ServerGamePacketListenerImplHandle.queuePacket(this.getNetworkManager(), packet)) {
            CommonUtil.nextTick(() -> this.sendPacket(packet));
        }
    }

    public boolean isConnected() {
        return ConnectionHandle.T.isConnected.invoke(this.getNetworkManager());
    }

    public static ServerGamePacketListenerImplHandle forPlayer(Player player) {
        Object handle = HandleConversion.toEntityHandle((Entity)player);
        ServerGamePacketListenerImplHandle connection = ServerPlayerHandle.T.playerConnection.get(handle);
        if (connection == null || !connection.isConnected()) {
            return null;
        }
        return connection;
    }

    static {
        queuePacketMethods.put(ConnectionHandle.T.getType(), defaultQueuePacketMethod);
    }

    public static final class ServerGamePacketListenerImplClass
    extends Template.Class<ServerGamePacketListenerImplHandle> {
        public final Template.Method<Object> getNetworkManager = new Template.Method();
        public final Template.Method.Converted<Void> sendPacket = new Template.Method.Converted();
        public final Template.Method<Void> sendPos = new Template.Method();
        public final Template.Method<Integer> getAwaitingTeleportId = new Template.Method();
        public final Template.Method<Void> resetAwaitTeleport = new Template.Method();
    }

    private static interface QueuePacketMethod {
        public boolean queuePacket(Object var1, Object var2);
    }
}

