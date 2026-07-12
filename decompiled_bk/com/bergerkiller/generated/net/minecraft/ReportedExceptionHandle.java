/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.CrashReportHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.ReportedException")
public abstract class ReportedExceptionHandle
extends Template.Handle {
    public static final ReportedExceptionClass T = Template.Class.create(ReportedExceptionClass.class, Common.TEMPLATE_RESOLVER);

    public static ReportedExceptionHandle createHandle(Object handleInstance) {
        return (ReportedExceptionHandle)T.createHandle(handleInstance);
    }

    public static final ReportedExceptionHandle createNew(CrashReportHandle paramCrashReport) {
        return ReportedExceptionHandle.T.constr_paramCrashReport.newInstance(paramCrashReport);
    }

    public static final class ReportedExceptionClass
    extends Template.Class<ReportedExceptionHandle> {
        public final Template.Constructor.Converted<ReportedExceptionHandle> constr_paramCrashReport = new Template.Constructor.Converted();
    }
}

