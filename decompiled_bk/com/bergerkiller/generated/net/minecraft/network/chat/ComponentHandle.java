/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.bergerkiller.generated.net.minecraft.network.chat;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import org.bukkit.ChatColor;

@Template.InstanceType(value="net.minecraft.network.chat.Component")
public abstract class ComponentHandle
extends Template.Handle {
    public static final ComponentClass T = Template.Class.create(ComponentClass.class, Common.TEMPLATE_RESOLVER);

    public static ComponentHandle createHandle(Object handleInstance) {
        return (ComponentHandle)T.createHandle(handleInstance);
    }

    public static String chatComponentToJson(ComponentHandle chatComponent) {
        return ComponentHandle.T.chatComponentToJson.invoke(chatComponent);
    }

    public static ComponentHandle jsonToChatComponent(String jsonString) {
        return ComponentHandle.T.jsonToChatComponent.invoke(jsonString);
    }

    public static ComponentHandle nbtToChatComponent(CommonTag nbt) {
        return ComponentHandle.T.nbtToChatComponent.invoke(nbt);
    }

    public static CommonTag chatComponentToNBT(ComponentHandle chatComponent) {
        return ComponentHandle.T.chatComponentToNBT.invoke(chatComponent);
    }

    public static ComponentHandle empty() {
        return ComponentHandle.T.empty.invoke();
    }

    public static ComponentHandle newLine() {
        return ComponentHandle.T.newLine.invoke();
    }

    public static ComponentHandle modifiersToComponent(Collection<ChatColor> colors) {
        return ComponentHandle.T.modifiersToComponent.invoke(colors);
    }

    public abstract String getText();

    public abstract boolean isEmpty();

    public abstract ComponentHandle addSibling(ComponentHandle var1);

    public abstract boolean isMutable();

    public abstract ComponentHandle createCopy();

    public abstract ComponentHandle setClickableURL(String var1);

    public abstract ComponentHandle setClickableContent(String var1);

    public abstract ComponentHandle setClickableSuggestedCommand(String var1);

    public abstract ComponentHandle setClickableRunCommand(String var1);

    public abstract ComponentHandle setHoverText(ComponentHandle var1);

    public static final class ComponentClass
    extends Template.Class<ComponentHandle> {
        public final Template.StaticMethod.Converted<String> chatComponentToJson = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ComponentHandle> jsonToChatComponent = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ComponentHandle> nbtToChatComponent = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<CommonTag> chatComponentToNBT = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ComponentHandle> empty = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ComponentHandle> newLine = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ComponentHandle> modifiersToComponent = new Template.StaticMethod.Converted();
        public final Template.Method<String> getText = new Template.Method();
        public final Template.Method<Boolean> isEmpty = new Template.Method();
        public final Template.Method.Converted<ComponentHandle> addSibling = new Template.Method.Converted();
        public final Template.Method<Boolean> isMutable = new Template.Method();
        public final Template.Method.Converted<ComponentHandle> createCopy = new Template.Method.Converted();
        public final Template.Method.Converted<ComponentHandle> setClickableURL = new Template.Method.Converted();
        public final Template.Method.Converted<ComponentHandle> setClickableContent = new Template.Method.Converted();
        public final Template.Method.Converted<ComponentHandle> setClickableSuggestedCommand = new Template.Method.Converted();
        public final Template.Method.Converted<ComponentHandle> setClickableRunCommand = new Template.Method.Converted();
        public final Template.Method.Converted<ComponentHandle> setHoverText = new Template.Method.Converted();
    }
}

