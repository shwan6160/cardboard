/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.InventoryView
 */
package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.inventory.AbstractContainerMenuHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.InventoryView;

@Template.InstanceType(value="net.minecraft.world.inventory.AnvilMenu")
public abstract class AnvilMenuHandle
extends AbstractContainerMenuHandle {
    public static final AnvilMenuClass T = Template.Class.create(AnvilMenuClass.class, Common.TEMPLATE_RESOLVER);

    public static AnvilMenuHandle createHandle(Object handleInstance) {
        return (AnvilMenuHandle)T.createHandle(handleInstance);
    }

    public static AnvilMenuHandle fromBukkit(InventoryView bukkitView) {
        return (AnvilMenuHandle)AnvilMenuHandle.T.fromBukkit.invoker.invoke(null, bukkitView);
    }

    public abstract String getRenameText();

    public abstract void setRenameText(String var1);

    public static final class AnvilMenuClass
    extends Template.Class<AnvilMenuHandle> {
        public final Template.Field<String> renameText = new Template.Field();
        public final Template.StaticMethod<AnvilMenuHandle> fromBukkit = new Template.StaticMethod();
    }
}

