/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.CrashReportCategoryHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.CrashReport")
public abstract class CrashReportHandle
extends Template.Handle {
    public static final CrashReportClass T = Template.Class.create(CrashReportClass.class, Common.TEMPLATE_RESOLVER);

    public static CrashReportHandle createHandle(Object handleInstance) {
        return (CrashReportHandle)T.createHandle(handleInstance);
    }

    public static CrashReportHandle create(Throwable throwable, String message) {
        return CrashReportHandle.T.create.invoke(throwable, message);
    }

    public abstract CrashReportCategoryHandle getSystemDetails(String var1);

    public static final class CrashReportClass
    extends Template.Class<CrashReportHandle> {
        public final Template.StaticMethod.Converted<CrashReportHandle> create = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<CrashReportCategoryHandle> getSystemDetails = new Template.Method.Converted();
    }
}

