/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItemInit;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="net.minecraft.network.syncher.SynchedEntityData")
public abstract class SynchedEntityDataHandle
extends Template.Handle {
    public static final SynchedEntityDataClass T = Template.Class.create(SynchedEntityDataClass.class, Common.TEMPLATE_RESOLVER);
    public static final Object UNSET_MARKER_VALUE = UnsetDataWatcherItemInit.UNSET_MARKER_VALUE;

    public static SynchedEntityDataHandle createHandle(Object handleInstance) {
        return (SynchedEntityDataHandle)T.createHandle(handleInstance);
    }

    public static SynchedEntityDataHandle createNew(EntityHandle owner) {
        return SynchedEntityDataHandle.T.createNew.invoke(owner);
    }

    public abstract EntityHandle getOwner();

    public abstract void setOwner(EntityHandle var1);

    public abstract SynchedEntityDataHandle cloneWithOwner(EntityHandle var1);

    public abstract List<DataWatcher.PackedItem<?>> packChanges();

    public abstract List<DataWatcher.PackedItem<?>> packNonDefaults();

    public abstract List<DataWatcher.PackedItem<?>> packAll();

    public abstract List<DataWatcher.Item<?>> getCopyOfAllItems();

    public abstract DataWatcher.Item<Object> read(DataWatcher.Key<?> var1);

    public abstract void setRawDefault(DataWatcher.Key<?> var1, Object var2);

    public abstract void setRaw(DataWatcher.Key<?> var1, Object var2, boolean var3);

    public abstract Object get(DataWatcher.Key<?> var1);

    public abstract boolean isChanged();

    public abstract boolean isEmpty();

    public static SynchedEntityDataHandle createNew(Entity owner) {
        return SynchedEntityDataHandle.createHandle(((Template.StaticMethod)SynchedEntityDataHandle.T.createNew.raw).invoke(HandleConversion.toEntityHandle(owner)));
    }

    public static final class SynchedEntityDataClass
    extends Template.Class<SynchedEntityDataHandle> {
        public final Template.StaticMethod.Converted<SynchedEntityDataHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<EntityHandle> getOwner = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setOwner = new Template.Method.Converted();
        public final Template.Method.Converted<SynchedEntityDataHandle> cloneWithOwner = new Template.Method.Converted();
        public final Template.Method.Converted<List<DataWatcher.PackedItem<?>>> packChanges = new Template.Method.Converted();
        public final Template.Method.Converted<List<DataWatcher.PackedItem<?>>> packNonDefaults = new Template.Method.Converted();
        public final Template.Method.Converted<List<DataWatcher.PackedItem<?>>> packAll = new Template.Method.Converted();
        public final Template.Method.Converted<List<DataWatcher.Item<?>>> getCopyOfAllItems = new Template.Method.Converted();
        public final Template.Method.Converted<DataWatcher.Item<Object>> read = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setRawDefault = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setRaw = new Template.Method.Converted();
        public final Template.Method<Object> get = new Template.Method();
        public final Template.Method<Boolean> isChanged = new Template.Method();
        public final Template.Method<Boolean> isEmpty = new Template.Method();
    }

    @Template.InstanceType(value="net.minecraft.network.syncher.SynchedEntityData.DataValue")
    public static abstract class DataValueHandle
    extends Template.Handle {
        public static final DataValueClass T = Template.Class.create(DataValueClass.class, Common.TEMPLATE_RESOLVER);

        public static DataValueHandle createHandle(Object handleInstance) {
            return (DataValueHandle)T.createHandle(handleInstance);
        }

        public abstract Object value();

        public abstract DataValueHandle cloneWithValue(Object var1);

        public abstract boolean isForKey(DataWatcher.Key<?> var1);

        public static final class DataValueClass
        extends Template.Class<DataValueHandle> {
            public final Template.Method<Object> value = new Template.Method();
            public final Template.Method.Converted<DataValueHandle> cloneWithValue = new Template.Method.Converted();
            public final Template.Method.Converted<Boolean> isForKey = new Template.Method.Converted();
        }
    }

    @Template.InstanceType(value="net.minecraft.network.syncher.SynchedEntityData.DataItem")
    public static abstract class DataItemHandle
    extends Template.Handle {
        public static final DataItemClass T = Template.Class.create(DataItemClass.class, Common.TEMPLATE_RESOLVER);

        public static DataItemHandle createHandle(Object handleInstance) {
            return (DataItemHandle)T.createHandle(handleInstance);
        }

        public abstract void setChanged(boolean var1);

        public abstract boolean isChanged();

        public abstract Object getValue();

        public abstract void setValue(Object var1);

        public abstract DataValueHandle pack();

        public static final class DataItemClass
        extends Template.Class<DataItemHandle> {
            @Template.Optional
            public final Template.Field.Integer typeId = new Template.Field.Integer();
            @Template.Optional
            public final Template.Field.Integer keyId = new Template.Field.Integer();
            @Template.Optional
            public final Template.Field.Converted<DataWatcher.Key<?>> key = new Template.Field.Converted();
            public final Template.Method<Void> setChanged = new Template.Method();
            public final Template.Method<Boolean> isChanged = new Template.Method();
            public final Template.Method<Object> getValue = new Template.Method();
            public final Template.Method<Void> setValue = new Template.Method();
            public final Template.Method.Converted<DataValueHandle> pack = new Template.Method.Converted();
        }
    }
}

