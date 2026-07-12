/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import java.util.Set;

@Template.InstanceType(value="net.minecraft.nbt.CompoundTag")
public abstract class CompoundTagHandle
extends TagHandle {
    public static final CompoundTagClass T = Template.Class.create(CompoundTagClass.class, Common.TEMPLATE_RESOLVER);

    public static CompoundTagHandle createHandle(Object handleInstance) {
        return (CompoundTagHandle)T.createHandle(handleInstance);
    }

    public static CompoundTagHandle createEmpty() {
        return CompoundTagHandle.T.createEmpty.invoke();
    }

    public static CompoundTagHandle create(Map<String, ?> map) {
        return CompoundTagHandle.T.create.invoke(map);
    }

    public abstract boolean isEmpty();

    public abstract int size();

    public abstract Set<String> getKeys();

    public abstract void remove(String var1);

    public abstract TagHandle put(String var1, TagHandle var2);

    public abstract TagHandle get(String var1);

    public abstract boolean containsKey(String var1);

    @Override
    public CompoundTagHandle clone() {
        return CompoundTagHandle.createHandle(this.raw_clone());
    }

    @Override
    public CommonTagCompound toCommonTag() {
        return new CommonTagCompound(this);
    }

    @Override
    public void toPrettyString(StringBuilder str, int indent) {
        for (int i = 0; i < indent; ++i) {
            str.append("  ");
        }
        Object values = this.getData();
        str.append("TagCompound: ").append(values.size()).append(" entries {");
        for (Map.Entry entry : values.entrySet()) {
            str.append('\n');
            for (int i = 0; i <= indent; ++i) {
                str.append("  ");
            }
            str.append((String)entry.getKey()).append(" = ");
            int startOffset = str.length();
            ((TagHandle)entry.getValue()).toPrettyString(str, indent + 1);
            str.delete(startOffset, startOffset + 2 * (indent + 1));
        }
        if (!values.isEmpty()) {
            str.append('\n');
            for (int i = 0; i < indent; ++i) {
                str.append("  ");
            }
        }
        str.append('}');
    }

    @Override
    @Template.Readonly
    public abstract Map<String, TagHandle> getData();

    public static final class CompoundTagClass
    extends Template.Class<CompoundTagHandle> {
        @Template.Readonly
        public final Template.Field.Converted<Map<String, TagHandle>> data = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<CompoundTagHandle> createEmpty = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<CompoundTagHandle> create = new Template.StaticMethod.Converted();
        public final Template.Method<Boolean> isEmpty = new Template.Method();
        public final Template.Method<Integer> size = new Template.Method();
        public final Template.Method<Set<String>> getKeys = new Template.Method();
        public final Template.Method.Converted<Void> remove = new Template.Method.Converted();
        public final Template.Method.Converted<TagHandle> put = new Template.Method.Converted();
        public final Template.Method.Converted<TagHandle> get = new Template.Method.Converted();
        public final Template.Method<Boolean> containsKey = new Template.Method();
    }
}

