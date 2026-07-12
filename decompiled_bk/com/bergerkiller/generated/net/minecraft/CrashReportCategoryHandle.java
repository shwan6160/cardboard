/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.CrashReportCategory")
public abstract class CrashReportCategoryHandle
extends Template.Handle {
    public static final CrashReportCategoryClass T = Template.Class.create(CrashReportCategoryClass.class, Common.TEMPLATE_RESOLVER);

    public static CrashReportCategoryHandle createHandle(Object handleInstance) {
        return (CrashReportCategoryHandle)T.createHandle(handleInstance);
    }

    public static final class CrashReportCategoryClass
    extends Template.Class<CrashReportCategoryHandle> {
    }
}

