/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.markers;

import com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkerTile;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

@Template.Package(value="net.minecraft.world.level.saveddata.maps")
@Template.ImportList(value={@Template.Import(value="net.minecraft.network.chat.Component"), @Template.Import(value="com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMapItemDataPacketHandle.Builder"), @Template.Import(value="com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkerTile"), @Template.Import(value="com.bergerkiller.bukkit.common.map.MapMarker"), @Template.Import(value="com.bergerkiller.bukkit.common.wrappers.ChatText")})
@Template.InstanceType(value="net.minecraft.world.level.saveddata.maps.MapDecoration")
public abstract class MapDisplayMarkerConverter
extends Template.Class<Template.Handle> {
    @Template.Generated(value="public static List<Object> getMapIcons(MapDisplayMarkerTile tile) {\n    int numMarkers = tile.getMarkerCount();\n\n    List cursors = new ArrayList(numMarkers);\n\n    for (int i = 0; i < numMarkers; i++) {\n        // Prepare arguments\n        com.bergerkiller.bukkit.common.map.MapMarker marker = tile.getMarker(i);\n\n#if version >= 1.11\n        MapDecorationType type = MapDecorationType.a(marker.getType().id());\n#endif\n\n               byte x = tile.encodeX(marker.getPositionX());\n        byte y = tile.encodeY(marker.getPositionY());\n        byte rot = tile.encodeRotation(marker.getRotation());\n\n#if version >= 1.13\n        ChatText caption_ct = marker.getFormattedCaption();\n        Component caption = (caption_ct==null)?null:((Component) caption_ct.clone().getRawHandle());\n#endif\n\n               // Create MapDecoration and assign to list\n#if version >= 1.13\n        cursors.add(new MapDecoration(type, x, y, rot, caption));\n#elseif version >= 1.11\n        cursors.add(new MapDecoration(type, x, y, rot));\n#else\n               cursors.add(new MapDecoration(marker.getType().id(), x, y, rot));\n#endif\n           }\n\n    return cursors;\n}")
    public abstract List<Object> getMapIcons(MapDisplayMarkerTile var1);
}

