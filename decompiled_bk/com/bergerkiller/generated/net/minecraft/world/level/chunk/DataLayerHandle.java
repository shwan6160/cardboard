/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.chunk;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Arrays;

@Template.InstanceType(value="net.minecraft.world.level.chunk.DataLayer")
public abstract class DataLayerHandle
extends Template.Handle {
    public static final DataLayerClass T = Template.Class.create(DataLayerClass.class, Common.TEMPLATE_RESOLVER);

    public static DataLayerHandle createHandle(Object handleInstance) {
        return (DataLayerHandle)T.createHandle(handleInstance);
    }

    public static final DataLayerHandle createNew() {
        return DataLayerHandle.T.constr.newInstance();
    }

    public static final DataLayerHandle createNew(byte[] data) {
        return DataLayerHandle.T.constr_data.newInstance(data);
    }

    public abstract int get(int var1, int var2, int var3);

    public abstract void set(int var1, int var2, int var3, int var4);

    public abstract byte[] getData();

    public void fill(int nibbleValue) {
        Arrays.fill(this.getData(), (byte)(nibbleValue & 0xF));
    }

    public boolean dataEquals(DataLayerHandle other) {
        return Arrays.equals(this.getData(), other.getData());
    }

    public abstract byte[] getDataField();

    public abstract void setDataField(byte[] var1);

    public static final class DataLayerClass
    extends Template.Class<DataLayerHandle> {
        public final Template.Constructor.Converted<DataLayerHandle> constr = new Template.Constructor.Converted();
        public final Template.Constructor.Converted<DataLayerHandle> constr_data = new Template.Constructor.Converted();
        public final Template.Field<byte[]> dataField = new Template.Field();
        public final Template.Method<Integer> get = new Template.Method();
        public final Template.Method<Void> set = new Template.Method();
        public final Template.Method<byte[]> getData = new Template.Method();
    }
}

