/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.entity.Display")
public abstract class DisplayHandle
extends EntityHandle {
    public static final DisplayClass T = Template.Class.create(DisplayClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Integer> DATA_INTERPOLATION_START_DELTA_TICKS = DataWatcher.Key.Type.INTEGER.createKey(DisplayHandle.T.DATA_INTERPOLATION_START_DELTA_TICKS_ID, -1);
    public static final DataWatcher.Key<Integer> DATA_INTERPOLATION_DURATION = DataWatcher.Key.Type.INTEGER.createKey(DisplayHandle.T.DATA_INTERPOLATION_DURATION_ID, -1);
    public static final DataWatcher.Key<Integer> DATA_POS_ROT_INTERPOLATION_DURATION = DataWatcher.Key.Type.INTEGER.createKey(DisplayHandle.T.DATA_POS_ROT_INTERPOLATION_DURATION_ID, -1);
    public static final DataWatcher.Key<Vector> DATA_TRANSLATION = DataWatcher.Key.Type.JOML_VECTOR3F.createKey(DisplayHandle.T.DATA_TRANSLATION_ID, -1);
    public static final DataWatcher.Key<Vector> DATA_SCALE = DataWatcher.Key.Type.JOML_VECTOR3F.createKey(DisplayHandle.T.DATA_SCALE_ID, -1);
    public static final DataWatcher.Key<Quaternion> DATA_LEFT_ROTATION = DataWatcher.Key.Type.JOML_QUATERNIONF.createKey(DisplayHandle.T.DATA_LEFT_ROTATION_ID, -1);
    public static final DataWatcher.Key<Quaternion> DATA_RIGHT_ROTATION = DataWatcher.Key.Type.JOML_QUATERNIONF.createKey(DisplayHandle.T.DATA_RIGHT_ROTATION_ID, -1);
    public static final DataWatcher.Key<Byte> DATA_BILLBOARD_RENDER_CONSTRAINTS = DataWatcher.Key.Type.BYTE.createKey(DisplayHandle.T.DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, -1);
    public static final DataWatcher.Key<Brightness> DATA_BRIGHTNESS_OVERRIDE = DataWatcher.Key.Type.DISPLAY_BRIGHTNESS.createKey(DisplayHandle.T.DATA_BRIGHTNESS_OVERRIDE_ID, -1);
    public static final DataWatcher.Key<Float> DATA_VIEW_RANGE = DataWatcher.Key.Type.FLOAT.createKey(DisplayHandle.T.DATA_VIEW_RANGE_ID, -1);
    public static final DataWatcher.Key<Float> DATA_SHADOW_RADIUS = DataWatcher.Key.Type.FLOAT.createKey(DisplayHandle.T.DATA_SHADOW_RADIUS_ID, -1);
    public static final DataWatcher.Key<Float> DATA_SHADOW_STRENGTH = DataWatcher.Key.Type.FLOAT.createKey(DisplayHandle.T.DATA_SHADOW_STRENGTH_ID, -1);
    public static final DataWatcher.Key<Float> DATA_WIDTH = DataWatcher.Key.Type.FLOAT.createKey(DisplayHandle.T.DATA_WIDTH_ID, -1);
    public static final DataWatcher.Key<Float> DATA_HEIGHT = DataWatcher.Key.Type.FLOAT.createKey(DisplayHandle.T.DATA_HEIGHT_ID, -1);
    public static final DataWatcher.Key<Integer> DATA_GLOW_COLOR_OVERRIDE = DataWatcher.Key.Type.INTEGER.createKey(DisplayHandle.T.DATA_GLOW_COLOR_OVERRIDE_ID, -1);
    public static final byte BILLBOARD_RENDER_FIXED = 0;
    public static final byte BILLBOARD_RENDER_VERTICAL = 1;
    public static final byte BILLBOARD_RENDER_HORIZONTAL = 2;
    public static final byte BILLBOARD_RENDER_CENTER = 3;

    public static DisplayHandle createHandle(Object handleInstance) {
        return (DisplayHandle)T.createHandle(handleInstance);
    }

    public static final class DisplayClass
    extends Template.Class<DisplayHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_INTERPOLATION_START_DELTA_TICKS_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_INTERPOLATION_DURATION_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_POS_ROT_INTERPOLATION_DURATION_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_TRANSLATION_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_SCALE_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_LEFT_ROTATION_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_RIGHT_ROTATION_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_BILLBOARD_RENDER_CONSTRAINTS_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_BRIGHTNESS_OVERRIDE_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_VIEW_RANGE_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_SHADOW_RADIUS_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_SHADOW_STRENGTH_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_WIDTH_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_HEIGHT_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_GLOW_COLOR_OVERRIDE_ID = new Template.StaticField.Converted();
    }

    @Template.InstanceType(value="net.minecraft.world.entity.Display.ItemDisplay")
    public static abstract class ItemDisplayHandle
    extends DisplayHandle {
        public static final ItemDisplayClass T = Template.Class.create(ItemDisplayClass.class, Common.TEMPLATE_RESOLVER);
        public static final DataWatcher.Key<ItemStack> DATA_ITEM_STACK = DataWatcher.Key.Type.ITEMSTACK.createKey(ItemDisplayHandle.T.DATA_ITEM_STACK_ID, -1);
        public static final DataWatcher.Key<ItemDisplayMode> DATA_ITEM_DISPLAY_MODE = DataWatcher.Key.Type.ITEM_DISPLAY_MODE.createKey(ItemDisplayHandle.T.DATA_ITEM_DISPLAY_ID, -1);

        public static ItemDisplayHandle createHandle(Object handleInstance) {
            return (ItemDisplayHandle)T.createHandle(handleInstance);
        }

        public static final class ItemDisplayClass
        extends Template.Class<ItemDisplayHandle> {
            @Template.Optional
            public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_ITEM_STACK_ID = new Template.StaticField.Converted();
            @Template.Optional
            public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_ITEM_DISPLAY_ID = new Template.StaticField.Converted();
        }
    }

    @Template.InstanceType(value="net.minecraft.world.entity.Display.BlockDisplay")
    public static abstract class BlockDisplayHandle
    extends DisplayHandle {
        public static final BlockDisplayClass T = Template.Class.create(BlockDisplayClass.class, Common.TEMPLATE_RESOLVER);
        public static final DataWatcher.Key<BlockData> DATA_BLOCK_STATE = DataWatcher.Key.Type.BLOCK_DATA.createKey(BlockDisplayHandle.T.DATA_BLOCK_STATE_ID, -1);

        public static BlockDisplayHandle createHandle(Object handleInstance) {
            return (BlockDisplayHandle)T.createHandle(handleInstance);
        }

        public static final class BlockDisplayClass
        extends Template.Class<BlockDisplayHandle> {
            @Template.Optional
            public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_BLOCK_STATE_ID = new Template.StaticField.Converted();
        }
    }

    @Template.InstanceType(value="net.minecraft.world.entity.Display.TextDisplay")
    public static abstract class TextDisplayHandle
    extends DisplayHandle {
        public static final TextDisplayClass T = Template.Class.create(TextDisplayClass.class, Common.TEMPLATE_RESOLVER);
        public static final byte STYLE_FLAG_SHADOW = 1;
        public static final byte STYLE_FLAG_SEE_THROUGH = 2;
        public static final byte STYLE_FLAG_USE_DEFAULT_BACKGROUND = 4;
        public static final byte STYLE_FLAG_ALIGN_LEFT = 8;
        public static final byte STYLE_FLAG_ALIGN_RIGHT = 16;
        public static final DataWatcher.Key<ChatText> DATA_TEXT = DataWatcher.Key.Type.CHAT_TEXT.createKey(TextDisplayHandle.T.DATA_TEXT_ID, -1);
        public static final DataWatcher.Key<Integer> DATA_LINE_WIDTH = DataWatcher.Key.Type.INTEGER.createKey(TextDisplayHandle.T.DATA_LINE_WIDTH_ID, -1);
        public static final DataWatcher.Key<Integer> DATA_BACKGROUND_COLOR = DataWatcher.Key.Type.INTEGER.createKey(TextDisplayHandle.T.DATA_BACKGROUND_COLOR_ID, -1);
        public static final DataWatcher.Key<Byte> DATA_TEXT_OPACITY = DataWatcher.Key.Type.BYTE.createKey(TextDisplayHandle.T.DATA_TEXT_OPACITY_ID, -1);
        public static final DataWatcher.Key<Byte> DATA_STYLE_FLAGS = DataWatcher.Key.Type.BYTE.createKey(TextDisplayHandle.T.DATA_STYLE_FLAGS_ID, -1);

        public static TextDisplayHandle createHandle(Object handleInstance) {
            return (TextDisplayHandle)T.createHandle(handleInstance);
        }

        public static final class TextDisplayClass
        extends Template.Class<TextDisplayHandle> {
            @Template.Optional
            public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_TEXT_ID = new Template.StaticField.Converted();
            @Template.Optional
            public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_LINE_WIDTH_ID = new Template.StaticField.Converted();
            @Template.Optional
            public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_BACKGROUND_COLOR_ID = new Template.StaticField.Converted();
            @Template.Optional
            public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_TEXT_OPACITY_ID = new Template.StaticField.Converted();
            @Template.Optional
            public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_STYLE_FLAGS_ID = new Template.StaticField.Converted();
        }
    }
}

