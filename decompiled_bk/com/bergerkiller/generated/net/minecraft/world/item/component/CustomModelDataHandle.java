/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.item.component;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

@Template.InstanceType(value="net.minecraft.world.item.component.CustomModelData")
public abstract class CustomModelDataHandle
extends Template.Handle {
    public static final CustomModelDataClass T = Template.Class.create(CustomModelDataClass.class, Common.TEMPLATE_RESOLVER);

    public static CustomModelDataHandle createHandle(Object handleInstance) {
        return (CustomModelDataHandle)T.createHandle(handleInstance);
    }

    public static CustomModelDataHandle empty() {
        return CustomModelDataHandle.T.empty.invoke();
    }

    public static CustomModelDataHandle createNew(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors) {
        return CustomModelDataHandle.T.createNew.invoke(floats, flags, strings, colors);
    }

    public static CustomModelDataHandle createNewLegacy(int value) {
        return CustomModelDataHandle.T.createNewLegacy.invoke(value);
    }

    public abstract List<Float> floats();

    public abstract List<Boolean> flags();

    public abstract List<String> strings();

    public abstract List<Integer> colors();

    public static final class CustomModelDataClass
    extends Template.Class<CustomModelDataHandle> {
        public final Template.StaticMethod.Converted<CustomModelDataHandle> empty = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<CustomModelDataHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<CustomModelDataHandle> createNewLegacy = new Template.StaticMethod.Converted();
        public final Template.Method<List<Float>> floats = new Template.Method();
        public final Template.Method<List<Boolean>> flags = new Template.Method();
        public final Template.Method<List<String>> strings = new Template.Method();
        public final Template.Method<List<Integer>> colors = new Template.Method();
    }
}

