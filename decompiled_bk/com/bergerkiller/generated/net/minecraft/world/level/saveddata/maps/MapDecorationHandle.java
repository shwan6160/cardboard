/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapCursor
 */
package com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.map.MapMarker;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.map.MapCursor;

@Template.InstanceType(value="net.minecraft.world.level.saveddata.maps.MapDecoration")
public abstract class MapDecorationHandle
extends Template.Handle {
    public static final MapDecorationClass T = Template.Class.create(MapDecorationClass.class, Common.TEMPLATE_RESOLVER);

    public static MapDecorationHandle createHandle(Object handleInstance) {
        return (MapDecorationHandle)T.createHandle(handleInstance);
    }

    public static MapDecorationHandle createNew(MapMarker.Type type, byte x, byte y, byte direction, ChatText title) {
        return MapDecorationHandle.T.createNew.invoke(type, x, y, direction, title);
    }

    public static MapDecorationHandle fromCursor(MapCursor cursor) {
        return MapDecorationHandle.T.fromCursor.invoke(cursor);
    }

    public abstract MapCursor toCursor();

    public abstract MapMarker.Type getType();

    public abstract byte getX();

    public abstract byte getY();

    public abstract byte getDirection();

    public static MapDecorationHandle createNew(MapMarker.Type type, byte x, byte y, byte direction) {
        return MapDecorationHandle.createNew(type, x, y, direction, null);
    }

    public static final class MapDecorationClass
    extends Template.Class<MapDecorationHandle> {
        public final Template.StaticMethod.Converted<MapDecorationHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<MapDecorationHandle> fromCursor = new Template.StaticMethod.Converted();
        public final Template.Method<MapCursor> toCursor = new Template.Method();
        public final Template.Method.Converted<MapMarker.Type> getType = new Template.Method.Converted();
        public final Template.Method<Byte> getX = new Template.Method();
        public final Template.Method<Byte> getY = new Template.Method();
        public final Template.Method<Byte> getDirection = new Template.Method();
    }
}

