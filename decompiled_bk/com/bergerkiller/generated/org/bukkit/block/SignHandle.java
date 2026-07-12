/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.block.SignChangeEvent
 */
package com.bergerkiller.generated.org.bukkit.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.org.bukkit.block.BlockStateHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.event.block.SignChangeEvent;

@Template.InstanceType(value="org.bukkit.block.Sign")
public abstract class SignHandle
extends BlockStateHandle {
    public static final SignClass T = Template.Class.create(SignClass.class, Common.TEMPLATE_RESOLVER);

    public static SignHandle createHandle(Object handleInstance) {
        return (SignHandle)T.createHandle(handleInstance);
    }

    public static boolean isChangingFrontLines(SignChangeEvent event) {
        return (Boolean)SignHandle.T.isChangingFrontLines.invoker.invoke(null, event);
    }

    public abstract String getFrontLine(int var1);

    public abstract void setFrontLine(int var1, String var2);

    public abstract String[] getFrontLines();

    public abstract String getBackLine(int var1);

    public abstract void setBackLine(int var1, String var2);

    public abstract String[] getBackLines();

    public static final class SignClass
    extends Template.Class<SignHandle> {
        public final Template.StaticMethod<Boolean> isChangingFrontLines = new Template.StaticMethod();
        public final Template.Method<String> getFrontLine = new Template.Method();
        public final Template.Method<Void> setFrontLine = new Template.Method();
        public final Template.Method<String[]> getFrontLines = new Template.Method();
        public final Template.Method<String> getBackLine = new Template.Method();
        public final Template.Method<Void> setBackLine = new Template.Method();
        public final Template.Method<String[]> getBackLines = new Template.Method();
    }
}

