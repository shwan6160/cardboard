/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapCursor$Type
 */
package com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.map.MapCursor;

@Template.InstanceType(value="net.minecraft.world.level.saveddata.maps.MapDecorationType")
public abstract class MapDecorationTypeHandle
extends Template.Handle {
    public static final MapDecorationTypeClass T = Template.Class.create(MapDecorationTypeClass.class, Common.TEMPLATE_RESOLVER);

    public static MapDecorationTypeHandle createHandle(Object handleInstance) {
        return (MapDecorationTypeHandle)T.createHandle(handleInstance);
    }

    public static List<Holder<MapDecorationTypeHandle>> getValues() {
        return MapDecorationTypeHandle.T.getValues.invoke();
    }

    public abstract IdentifierHandle getName();

    public abstract boolean isShownOnItemFrame();

    public abstract MapCursor.Type toBukkit();

    public abstract byte getId();

    public static final class MapDecorationTypeClass
    extends Template.Class<MapDecorationTypeHandle> {
        public final Template.StaticMethod.Converted<List<Holder<MapDecorationTypeHandle>>> getValues = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<IdentifierHandle> getName = new Template.Method.Converted();
        public final Template.Method<Boolean> isShownOnItemFrame = new Template.Method();
        public final Template.Method<MapCursor.Type> toBukkit = new Template.Method();
        public final Template.Method<Byte> getId = new Template.Method();
    }
}

