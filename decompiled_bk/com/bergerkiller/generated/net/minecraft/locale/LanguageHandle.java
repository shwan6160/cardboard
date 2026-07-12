/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.locale;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.locale.Language")
public abstract class LanguageHandle
extends Template.Handle {
    public static final LanguageClass T = Template.Class.create(LanguageClass.class, Common.TEMPLATE_RESOLVER);

    public static LanguageHandle createHandle(Object handleInstance) {
        return (LanguageHandle)T.createHandle(handleInstance);
    }

    public static LanguageHandle INSTANCE() {
        return LanguageHandle.T.INSTANCE.get();
    }

    public static void INSTANCE_set(LanguageHandle value) {
        LanguageHandle.T.INSTANCE.set(value);
    }

    public abstract String get(String var1);

    public static final class LanguageClass
    extends Template.Class<LanguageHandle> {
        public final Template.StaticField.Converted<LanguageHandle> INSTANCE = new Template.StaticField.Converted();
        public final Template.Method<String> get = new Template.Method();
    }
}

