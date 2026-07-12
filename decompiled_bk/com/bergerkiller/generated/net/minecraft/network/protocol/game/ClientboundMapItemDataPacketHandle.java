/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapCursor
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapDecorationHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.map.MapCursor;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundMapItemDataPacket")
public abstract class ClientboundMapItemDataPacketHandle
extends PacketHandle {
    public static final ClientboundMapItemDataPacketClass T = Template.Class.create(ClientboundMapItemDataPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundMapItemDataPacketHandle createHandle(Object handleInstance) {
        return (ClientboundMapItemDataPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundMapItemDataPacketHandle createNew(Builder builder) {
        return ClientboundMapItemDataPacketHandle.T.createNew.invoke(builder);
    }

    public abstract int getMapId();

    public abstract byte getScale();

    public abstract boolean isLocked();

    public abstract boolean hasCursors();

    public abstract List<MapCursor> getCursors();

    public abstract int getStartX();

    public abstract int getStartY();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract boolean hasPixels();

    public abstract byte[] getPixels();

    public static Builder build() {
        return new Builder();
    }

    public Builder mutable() {
        Builder b = ClientboundMapItemDataPacketHandle.build().mapId(this.getMapId()).scale(this.getScale()).locked(this.isLocked());
        if (this.hasCursors()) {
            b.cursors(this.getCursors());
        }
        if (this.hasPixels()) {
            b.data(this.getStartX(), this.getStartY(), this.getWidth(), this.getHeight(), this.getPixels());
        }
        return b;
    }

    public static final class ClientboundMapItemDataPacketClass
    extends Template.Class<ClientboundMapItemDataPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundMapItemDataPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getMapId = new Template.Method();
        public final Template.Method<Byte> getScale = new Template.Method();
        public final Template.Method<Boolean> isLocked = new Template.Method();
        public final Template.Method<Boolean> hasCursors = new Template.Method();
        public final Template.Method.Converted<List<MapCursor>> getCursors = new Template.Method.Converted();
        public final Template.Method<Integer> getStartX = new Template.Method();
        public final Template.Method<Integer> getStartY = new Template.Method();
        public final Template.Method<Integer> getWidth = new Template.Method();
        public final Template.Method<Integer> getHeight = new Template.Method();
        public final Template.Method<Boolean> hasPixels = new Template.Method();
        public final Template.Method<byte[]> getPixels = new Template.Method();
    }

    public static class Builder
    implements Cloneable {
        private int mapId = 0;
        private byte scale = 1;
        private boolean locked = false;
        private Optional<List<MapDecorationHandle>> mapIcons = Optional.empty();
        private boolean hasData = false;
        private int startX;
        private int startY;
        private int width;
        private int height;
        private byte[] colors = null;
        private static final DuplexConverter<MapDecorationHandle, MapCursor> mapCursorHandleConversion = new DuplexConverter<MapDecorationHandle, MapCursor>(MapDecorationHandle.class, MapCursor.class){

            @Override
            public MapCursor convertInput(MapDecorationHandle mapIconHandle) {
                return mapIconHandle.toCursor();
            }

            @Override
            public MapDecorationHandle convertOutput(MapCursor mapCursor) {
                return MapDecorationHandle.fromCursor(mapCursor);
            }
        };

        private Builder() {
        }

        public int get_mapId() {
            return this.mapId;
        }

        public byte get_scale() {
            return this.scale;
        }

        public boolean get_locked() {
            return this.locked;
        }

        public Optional<List<MapCursor>> get_cursors() {
            return this.mapIcons.map(list -> new ConvertingList<MapCursor>((List<?>)list, (DuplexConverter<?, MapCursor>)mapCursorHandleConversion));
        }

        public Optional<List<MapDecorationHandle>> get_cursors_nms() {
            return this.mapIcons;
        }

        public boolean has_data() {
            return this.hasData;
        }

        public int get_data_startX() {
            return this.startX;
        }

        public int get_data_startY() {
            return this.startY;
        }

        public int get_data_width() {
            return this.width;
        }

        public int get_data_height() {
            return this.height;
        }

        public byte[] get_data_colors() {
            return this.colors;
        }

        public Builder mapId(int mapId) {
            this.mapId = mapId;
            return this;
        }

        public Builder scale(byte scale) {
            this.scale = scale;
            return this;
        }

        public Builder locked(boolean locked) {
            this.locked = locked;
            return this;
        }

        private List<MapDecorationHandle> convertCursors(List<MapCursor> cursors) {
            return new ConvertingList<MapDecorationHandle>(cursors, mapCursorHandleConversion.reverse());
        }

        public Builder no_cursors() {
            return this.cursors_nms(Collections.emptyList());
        }

        public Builder cursors(List<MapCursor> cursors) {
            return this.cursors_nms(this.convertCursors(cursors));
        }

        public Builder cursors_nms(List<MapDecorationHandle> rawCursors) {
            this.mapIcons = Optional.ofNullable(rawCursors);
            return this;
        }

        public Builder add_cursors(List<MapCursor> cursors) {
            return this.add_cursors_nms(this.convertCursors(cursors));
        }

        public Builder add_cursors_nms(List<MapDecorationHandle> rawCursors) {
            if (!this.mapIcons.isPresent()) {
                this.cursors_nms(rawCursors);
            } else if (rawCursors != null && !rawCursors.isEmpty()) {
                List<MapDecorationHandle> existing = this.mapIcons.get();
                ArrayList<MapDecorationHandle> newList = new ArrayList<MapDecorationHandle>(existing.size() + rawCursors.size());
                newList.addAll(existing);
                newList.addAll(rawCursors);
                this.mapIcons = Optional.of(newList);
            }
            return this;
        }

        public Builder data(int startX, int startY, int width, int height, byte[] colors) {
            this.startX = startX;
            this.startY = startY;
            this.width = width;
            this.height = height;
            this.colors = colors;
            this.hasData = true;
            return this;
        }

        public Builder clone() {
            Builder copy = new Builder();
            copy.mapId = this.mapId;
            copy.scale = this.scale;
            copy.locked = this.locked;
            copy.mapIcons = this.mapIcons;
            copy.startX = this.startX;
            copy.startY = this.startY;
            copy.width = this.width;
            copy.height = this.height;
            copy.colors = this.colors;
            copy.hasData = this.hasData;
            return copy;
        }

        public ClientboundMapItemDataPacketHandle create() {
            return ClientboundMapItemDataPacketHandle.createNew(this);
        }
    }
}

