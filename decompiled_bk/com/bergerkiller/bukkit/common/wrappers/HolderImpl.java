/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

class HolderImpl<T>
implements Holder<T> {
    private static final HolderLogic LOGIC = Template.Class.create(HolderLogic.class, Common.TEMPLATE_RESOLVER);
    private final Object rawHolder;
    private Supplier<T> handleSupplier;

    public static <T extends Template.Handle> HolderImpl<T> direct(T handle) {
        return new HolderImpl<T>(LOGIC.createDirect(handle.getRaw()), handle);
    }

    public static <T extends Template.Handle> HolderImpl<T> directWrap(Object value, Function<Object, T> handleCtor) {
        return new HolderImpl<Function<Object, T>>(LOGIC.createDirect(value), handleCtor);
    }

    public HolderImpl(Object rawHolder, Function<Object, T> handleCtor) {
        this.rawHolder = rawHolder;
        this.handleSupplier = () -> {
            Object result = handleCtor.apply(this.rawValue());
            this.handleSupplier = LogicUtil.constantSupplier(result);
            return result;
        };
    }

    public HolderImpl(Object rawHolder, T handle) {
        this.rawHolder = rawHolder;
        this.handleSupplier = LogicUtil.constantSupplier(handle);
    }

    @Override
    public T value() {
        return this.handleSupplier.get();
    }

    @Override
    public Object rawValue() {
        return LOGIC.getHolderValue(this.rawHolder);
    }

    @Override
    public Optional<ResourceKey<T>> key() {
        return LOGIC.getHolderKey(this.rawHolder).map(ResourceKey::fromResourceKeyHandle);
    }

    @Override
    public Object toRawHolder() {
        return this.rawHolder;
    }

    public String toString() {
        return this.rawHolder.toString();
    }

    public int hashCode() {
        return this.rawHolder.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Holder) {
            return this.rawHolder.equals(((Holder)o).toRawHolder());
        }
        return false;
    }

    @Template.Import(value="java.util.Optional")
    @Template.InstanceType(value="net.minecraft.server.MinecraftServer")
    public static abstract class HolderLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static Object getHolderValue(Object holder) {\n#if version >= 1.18.2\n    return ((net.minecraft.core.Holder) holder).value();\n#else\n               return holder;\n#endif\n           }")
        public abstract Object getHolderValue(Object var1);

        @Template.Generated(value="public static Optional<Object> getHolderKey(Object holder) {\n#if version >= 1.18.2\n    return ((net.minecraft.core.Holder) holder).unwrapKey();\n#else\n               return Optional.empty();\n#endif\n           }")
        public abstract Optional<Object> getHolderKey(Object var1);

        @Template.Generated(value="public static Object createDirect(Object value) {\n#if version >= 1.18.2\n    return net.minecraft.core.Holder.direct(value);\n#else\n               return value;\n#endif\n           }")
        public abstract Object createDirect(Object var1);
    }
}

