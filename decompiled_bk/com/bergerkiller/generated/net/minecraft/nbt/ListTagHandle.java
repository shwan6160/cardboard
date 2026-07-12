/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Template.InstanceType(value="net.minecraft.nbt.ListTag")
public abstract class ListTagHandle
extends TagHandle {
    public static final ListTagClass T = Template.Class.create(ListTagClass.class, Common.TEMPLATE_RESOLVER);

    public static ListTagHandle createHandle(Object handleInstance) {
        return (ListTagHandle)T.createHandle(handleInstance);
    }

    public static ListTagHandle createEmpty() {
        return ListTagHandle.T.createEmpty.invoke();
    }

    public static ListTagHandle create(Collection<?> data) {
        return ListTagHandle.T.create.invoke(data);
    }

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract byte getElementTypeId();

    public abstract TagHandle get_at(int var1);

    public abstract void clear();

    public abstract TagHandle set_at(int var1, TagHandle var2);

    public abstract TagHandle remove_at(int var1);

    public abstract void add_at(int var1, TagHandle var2);

    public abstract boolean add(TagHandle var1);

    @Override
    public ListTagHandle clone() {
        return ListTagHandle.createHandle(this.raw_clone());
    }

    @Override
    public CommonTagList toCommonTag() {
        return new CommonTagList(this);
    }

    @Override
    public void toPrettyString(StringBuilder str, int indent) {
        for (int i = 0; i < indent; ++i) {
            str.append("  ");
        }
        Object values = this.getData();
        str.append("TagList: ").append(values.size()).append(" entries [");
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            TagHandle value = (TagHandle)iterator.next();
            str.append('\n');
            value.toPrettyString(str, indent + 1);
        }
        if (!values.isEmpty()) {
            str.append('\n');
            for (int i = 0; i < indent; ++i) {
                str.append("  ");
            }
        }
        str.append(']');
    }

    @Override
    @Template.Readonly
    public abstract List<TagHandle> getData();

    public static final class ListTagClass
    extends Template.Class<ListTagHandle> {
        @Template.Readonly
        public final Template.Field.Converted<List<TagHandle>> data = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ListTagHandle> createEmpty = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ListTagHandle> create = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> size = new Template.Method();
        public final Template.Method<Boolean> isEmpty = new Template.Method();
        public final Template.Method.Converted<Byte> getElementTypeId = new Template.Method.Converted();
        public final Template.Method.Converted<TagHandle> get_at = new Template.Method.Converted();
        public final Template.Method<Void> clear = new Template.Method();
        public final Template.Method.Converted<TagHandle> set_at = new Template.Method.Converted();
        public final Template.Method.Converted<TagHandle> remove_at = new Template.Method.Converted();
        public final Template.Method.Converted<Void> add_at = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> add = new Template.Method.Converted();
    }
}

