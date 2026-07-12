/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.ChatFormatting")
public abstract class ChatFormattingHandle
extends Template.Handle {
    public static final ChatFormattingClass T = Template.Class.create(ChatFormattingClass.class, Common.TEMPLATE_RESOLVER);
    public static final ChatFormattingHandle RESET = ChatFormattingHandle.T.RESET.getSafe();
    public static final Object[] RAW_VALUES = T.getType().getEnumConstants();
    public static final ChatFormattingHandle[] VALUES = new ChatFormattingHandle[RAW_VALUES.length];

    public static ChatFormattingHandle createHandle(Object handleInstance) {
        return (ChatFormattingHandle)T.createHandle(handleInstance);
    }

    public abstract int getId();

    public static ChatFormattingHandle byChar(char c) {
        for (ChatFormattingHandle format : VALUES) {
            String s = format.toString();
            if (s.length() < 2 || s.charAt(1) != c) continue;
            return format;
        }
        return RESET;
    }

    public static ChatFormattingHandle byId(int id) {
        if (id >= 0) {
            for (ChatFormattingHandle format : VALUES) {
                if (format.getId() != id) continue;
                return format;
            }
        }
        return RESET;
    }

    static {
        for (int i = 0; i < VALUES.length; ++i) {
            ChatFormattingHandle.VALUES[i] = ChatFormattingHandle.createHandle(RAW_VALUES[i]);
        }
    }

    public static final class ChatFormattingClass
    extends Template.Class<ChatFormattingHandle> {
        public final Template.EnumConstant.Converted<ChatFormattingHandle> RESET = new Template.EnumConstant.Converted();
        public final Template.Method<Integer> getId = new Template.Method();
    }
}

