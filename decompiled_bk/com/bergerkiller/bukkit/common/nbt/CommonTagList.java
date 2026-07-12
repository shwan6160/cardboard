/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.nbt.ListTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingIterator;
import com.bergerkiller.mountiplex.conversion.util.ConvertingListIterator;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CommonTagList
extends CommonTag
implements List<CommonTag> {
    public static final CommonTagList EMPTY = CommonTagList.makeReadOnly(new CommonTagList());

    public CommonTagList() {
        this(new ArrayList());
    }

    public CommonTagList(Object ... values) {
        this(new ArrayList<Object>(Arrays.asList(values)));
    }

    public CommonTagList(List<?> data) {
        this((Object)data);
    }

    public CommonTagList(Object value) {
        super(value);
    }

    public CommonTagList(ListTagHandle handle) {
        super(handle);
    }

    @Override
    public ListTagHandle getBackingHandle() {
        return (ListTagHandle)this.handle;
    }

    @Override
    public CommonTagList clone() {
        return new CommonTagList((ListTagHandle)((TagHandle)this.handle).clone());
    }

    @Override
    public List<CommonTag> getData() {
        return (List)super.getData();
    }

    @Override
    protected List<Object> getRawData() {
        return (List)super.getRawData();
    }

    @Override
    public int size() {
        return this.getBackingHandle().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getBackingHandle().isEmpty();
    }

    @Override
    public void clear() {
        this.assertWritable();
        this.getBackingHandle().clear();
    }

    public Object getValue(int index) {
        return CommonTagList.wrapRawData(TagHandle.getDataForHandle(((Template.Method)ListTagHandle.T.get_at.raw).invoke(this.getRawHandle(), index)), this.readOnly);
    }

    public <T> T getValue(int index, Class<T> type) {
        return Conversion.convert(this.getValue(index), type, null);
    }

    public <T> T getValue(int index, T def) {
        return Conversion.convert(this.getValue(index), def);
    }

    public <T> T getValue(int index, Class<T> type, T def) {
        return Conversion.convert(this.getValue(index), type, def);
    }

    public void setValue(int index, Object element) {
        this.assertWritable();
        ((Template.Method)ListTagHandle.T.set_at.raw).invoke(this.getRawHandle(), index, TagHandle.createRawHandleForData(element));
    }

    public void addValue(int index, Object element) {
        this.assertWritable();
        ((Template.Method)ListTagHandle.T.add_at.raw).invoke(this.getRawHandle(), index, TagHandle.createRawHandleForData(element));
    }

    public void addValue(Object element) {
        this.assertWritable();
        ((Template.Method)ListTagHandle.T.add.raw).invoke(this.getRawHandle(), TagHandle.createRawHandleForData(element));
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof CommonTag) {
            return this.getRawData().indexOf(((CommonTag)o).getRawHandle());
        }
        if (TagHandle.isDataSupportedNatively(o)) {
            return this.getRawData().indexOf(TagHandle.createRawHandleForData(o));
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof CommonTag) {
            return this.getRawData().lastIndexOf(((CommonTag)o).getRawHandle());
        }
        if (TagHandle.isDataSupportedNatively(o)) {
            return this.getRawData().lastIndexOf(TagHandle.createRawHandleForData(o));
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return this.indexOf(o) != -1;
    }

    @Override
    public boolean remove(Object o) {
        this.assertWritable();
        int index = this.indexOf(o);
        if (index == -1) {
            return false;
        }
        this.remove(index);
        return true;
    }

    @Override
    public CommonTag get(int index) {
        CommonTag tag = this.getBackingHandle().get_at(index).toCommonTag();
        tag.readOnly = this.readOnly;
        return tag;
    }

    @Override
    public CommonTag set(int index, CommonTag element) {
        this.assertWritable();
        return this.getBackingHandle().set_at(index, (TagHandle)element.getBackingHandle()).toCommonTag();
    }

    @Override
    public CommonTag remove(int index) {
        this.assertWritable();
        return this.getBackingHandle().remove_at(index).toCommonTag();
    }

    @Override
    public void add(int index, CommonTag element) {
        this.assertWritable();
        this.getBackingHandle().add_at(index, (TagHandle)element.getBackingHandle());
    }

    @Override
    public boolean add(CommonTag element) {
        this.assertWritable();
        return this.getBackingHandle().add((TagHandle)element.getBackingHandle());
    }

    public <T> T getAllValues(Class<T> type) {
        T values = Conversion.convert(this, type, null);
        if (values == null) {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        }
        return values;
    }

    public <T> void setAllValues(T ... values) {
        this.clear();
        this.addAllValues(values);
    }

    public <T> void addAllValues(T ... values) {
        for (T data : values) {
            if (data == null) continue;
            Class<?> dataType = data.getClass();
            if (data instanceof Collection) {
                for (Object o : (Collection)data) {
                    this.addValue(o);
                }
                continue;
            }
            if (data instanceof Map) {
                for (Map.Entry entry : ((Map)data).entrySet()) {
                    this.addValue(entry.getValue());
                }
                continue;
            }
            if (dataType.isArray()) {
                if (dataType.isPrimitive()) {
                    int len = Array.getLength(data);
                    for (int i = 0; i < len; ++i) {
                        this.addValue(Array.get(data, i));
                    }
                    continue;
                }
                for (Object elem : (Object[])data) {
                    this.addValue(elem);
                }
                continue;
            }
            this.addValue(data);
        }
    }

    @Override
    public Object[] toArray() {
        Object[] values = new Object[this.size()];
        Iterator<CommonTag> iter = this.iterator();
        for (int i = 0; i < values.length; ++i) {
            values[i] = iter.next();
        }
        return values;
    }

    @Override
    public <K> K[] toArray(K[] array) {
        if (this.size() > array.length) {
            array = LogicUtil.createArray(array.getClass().getComponentType(), this.size());
        }
        Iterator<CommonTag> iter = this.iterator();
        int i = 0;
        while (iter.hasNext()) {
            array[i] = iter.next();
            ++i;
        }
        return array;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return CollectionBasics.containsAll(this, c);
    }

    @Override
    public boolean addAll(Collection<? extends CommonTag> c) {
        return CollectionBasics.addAll(this, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends CommonTag> c) {
        return CollectionBasics.addAll(this, index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return CollectionBasics.removeAll(this, c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll(this, c);
    }

    @Override
    public Iterator<CommonTag> iterator() {
        return new ConvertingIterator<CommonTag>(this.getRawData().iterator(), CommonTagList.nbtBaseToCommonTag(this.readOnly));
    }

    @Override
    public ListIterator<CommonTag> listIterator() {
        return new ConvertingListIterator<CommonTag>(this.getRawData().listIterator(), CommonTagList.nbtBaseToCommonTag(this.readOnly));
    }

    @Override
    public ListIterator<CommonTag> listIterator(int index) {
        return new ConvertingListIterator<CommonTag>(this.getRawData().listIterator(index), CommonTagList.nbtBaseToCommonTag(this.readOnly));
    }

    @Override
    public List<CommonTag> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("No sublist can be made from tag data");
    }

    public static CommonTagList readFromStream(InputStream in) throws IOException {
        CommonTag tag = CommonTag.readFromStream(in);
        if (!(tag instanceof CommonTagList)) {
            throw new IOException("Tag read is not a list!");
        }
        return (CommonTagList)tag;
    }

    public static CommonTagList create(Object handle) {
        return LogicUtil.tryCast(CommonTag.create(handle), CommonTagList.class);
    }

    public static CommonTagList createReadOnly(Object handle) {
        return CommonTagList.makeReadOnly(CommonTagList.create(handle));
    }
}

