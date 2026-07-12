/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import org.bukkit.entity.Player;

@Template.InstanceType(value="net.minecraft.server.level.EntityTrackerEntry")
public abstract class EntityTrackerEntryHandle
extends Template.Handle {
    public static final EntityTrackerEntryClass T = Template.Class.create(EntityTrackerEntryClass.class, Common.TEMPLATE_RESOLVER);

    public static EntityTrackerEntryHandle createHandle(Object handleInstance) {
        return (EntityTrackerEntryHandle)T.createHandle(handleInstance);
    }

    public static Player convertRawViewer(Object viewer) {
        return (Player)EntityTrackerEntryHandle.T.convertRawViewer.invoker.invoke(null, viewer);
    }

    public abstract int getPlayerViewDistance();

    public abstract Collection<Player> getViewers();

    public abstract Collection<Object> getRawViewers();

    public abstract void clearViewers();

    public abstract boolean addViewerToSet(Player var1);

    public abstract boolean removeViewerFromSet(Player var1);

    public abstract EntityTrackerEntryStateHandle getState();

    public abstract EntityHandle getEntity();

    public abstract void setEntity(EntityHandle var1);

    public abstract void updateViewers();

    public abstract void removeViewer(Player var1);

    public abstract void updatePlayer(Player var1);

    public abstract void hideForAll();

    public abstract void broadcastRawPacket(Object var1);

    @Deprecated
    public static final boolean hasProtocolRotationChanged(float angle1, float angle2) {
        return EntityTrackerEntryStateHandle.hasProtocolRotationChanged(angle1, angle2);
    }

    @Deprecated
    public static final int getProtocolRotation(float angle) {
        return EntityTrackerEntryStateHandle.getProtocolRotation(angle);
    }

    @Deprecated
    public static final float getRotationFromProtocol(int protocol) {
        return EntityTrackerEntryStateHandle.getRotationFromProtocol(protocol);
    }

    @Deprecated
    public void setTimeSinceLocationSync(int time) {
        this.getState().setTimeSinceLocationSync(time);
    }

    public abstract int getTrackingDistance();

    public abstract void setTrackingDistance(int var1);

    public static final class EntityTrackerEntryClass
    extends Template.Class<EntityTrackerEntryHandle> {
        public final Template.Field.Integer trackingDistance = new Template.Field.Integer();
        public final Template.StaticMethod<Player> convertRawViewer = new Template.StaticMethod();
        public final Template.Method<Integer> getPlayerViewDistance = new Template.Method();
        public final Template.Method.Converted<Collection<Player>> getViewers = new Template.Method.Converted();
        public final Template.Method<Collection<Object>> getRawViewers = new Template.Method();
        public final Template.Method<Void> clearViewers = new Template.Method();
        public final Template.Method.Converted<Boolean> addViewerToSet = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> removeViewerFromSet = new Template.Method.Converted();
        public final Template.Method.Converted<EntityTrackerEntryStateHandle> getState = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method.Converted<Void> setState = new Template.Method.Converted();
        public final Template.Method.Converted<EntityHandle> getEntity = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setEntity = new Template.Method.Converted();
        public final Template.Method<Void> updateViewers = new Template.Method();
        public final Template.Method.Converted<Void> removeViewer = new Template.Method.Converted();
        public final Template.Method.Converted<Void> updatePlayer = new Template.Method.Converted();
        public final Template.Method<Void> hideForAll = new Template.Method();
        public final Template.Method.Converted<Void> broadcastRawPacket = new Template.Method.Converted();
    }
}

