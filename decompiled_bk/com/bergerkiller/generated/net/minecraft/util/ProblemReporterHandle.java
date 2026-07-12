/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.util.ProblemReporter")
public abstract class ProblemReporterHandle
extends Template.Handle {
    public static final ProblemReporterClass T = Template.Class.create(ProblemReporterClass.class, Common.TEMPLATE_RESOLVER);

    public static ProblemReporterHandle createHandle(Object handleInstance) {
        return (ProblemReporterHandle)T.createHandle(handleInstance);
    }

    public static ProblemReporterHandle createScoped() {
        return ProblemReporterHandle.T.createScoped.invoke();
    }

    public abstract void close();

    public static final class ProblemReporterClass
    extends Template.Class<ProblemReporterHandle> {
        public final Template.StaticMethod.Converted<ProblemReporterHandle> createScoped = new Template.StaticMethod.Converted();
        public final Template.Method<Void> close = new Template.Method();
    }
}

