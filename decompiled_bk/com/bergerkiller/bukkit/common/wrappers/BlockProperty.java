/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.properties.PropertyHandle;
import java.util.Collection;

public class BlockProperty<T extends Comparable<?>>
extends BasicWrapper<PropertyHandle> {
    public BlockProperty(PropertyHandle handle) {
        this.setHandle(handle);
    }

    public String name() {
        return ((PropertyHandle)this.handle).getKeyToken();
    }

    public Collection<T> values() {
        return (Collection)LogicUtil.unsafeCast(((PropertyHandle)this.handle).getValues());
    }

    public String valueName(Comparable<?> value) {
        return ((PropertyHandle)this.handle).getValueToken(value);
    }
}

