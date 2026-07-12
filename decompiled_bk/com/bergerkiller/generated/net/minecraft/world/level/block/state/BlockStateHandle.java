/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block.state;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.generated.net.minecraft.world.level.BlockGetterHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.SoundTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.properties.PropertyHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

@Template.InstanceType(value="net.minecraft.world.level.block.state.BlockState")
public abstract class BlockStateHandle
extends Template.Handle {
    public static final BlockStateClass T = Template.Class.create(BlockStateClass.class, Common.TEMPLATE_RESOLVER);

    public static BlockStateHandle createHandle(Object handleInstance) {
        return (BlockStateHandle)T.createHandle(handleInstance);
    }

    public abstract BlockHandle getBlock();

    public abstract BlockFaceSet getCachedOpaqueFaces();

    public abstract int getCachedOpacity();

    public abstract boolean isPowerSource();

    public abstract SoundTypeHandle getSoundType();

    public abstract boolean isSolid();

    public abstract AABBHandle getInteractableBox(BlockGetterHandle var1, IntVector3 var2);

    public abstract AABBHandle getBoundingBox(BlockGetterHandle var1, IntVector3 var2);

    public abstract Object get(PropertyHandle var1);

    public abstract BlockStateHandle set(PropertyHandle var1, Object var2);

    public abstract Collection<PropertyHandle> getProperties();

    public void logProperties() {
        for (PropertyHandle property : this.getProperties()) {
            Logging.LOGGER.info(property + " = " + this.get(property));
        }
    }

    public PropertyHandle findProperty(String key) {
        for (PropertyHandle property : this.getProperties()) {
            if (!property.getKeyToken().equals(key)) continue;
            return property;
        }
        return null;
    }

    public BlockStateHandle set(String key, Object value) {
        return this.set(this.findProperty(key), value);
    }

    public <T> T get(String key, Class<T> type) {
        return this.get(this.findProperty(key), type);
    }

    public <T> T get(PropertyHandle state, Class<T> type) {
        return Conversion.convert(this.get(state), type, null);
    }

    public static final class BlockStateClass
    extends Template.Class<BlockStateHandle> {
        public final Template.Method.Converted<BlockHandle> getBlock = new Template.Method.Converted();
        public final Template.Method<BlockFaceSet> getCachedOpaqueFaces = new Template.Method();
        public final Template.Method<Integer> getCachedOpacity = new Template.Method();
        public final Template.Method<Boolean> isPowerSource = new Template.Method();
        public final Template.Method.Converted<SoundTypeHandle> getSoundType = new Template.Method.Converted();
        public final Template.Method<Boolean> isSolid = new Template.Method();
        public final Template.Method.Converted<AABBHandle> getInteractableBox = new Template.Method.Converted();
        public final Template.Method.Converted<AABBHandle> getBoundingBox = new Template.Method.Converted();
        public final Template.Method.Converted<Object> get = new Template.Method.Converted();
        public final Template.Method.Converted<BlockStateHandle> set = new Template.Method.Converted();
        public final Template.Method.Converted<Collection<PropertyHandle>> getProperties = new Template.Method.Converted();
    }
}

