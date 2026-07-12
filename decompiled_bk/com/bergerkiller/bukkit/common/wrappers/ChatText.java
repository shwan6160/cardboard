/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.network.chat.ComponentHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftChatMessageHandle;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ChatText
extends BasicWrapper<ComponentHandle>
implements Cloneable {
    private ChatText() {
    }

    public final void sendTo(Player player) {
        PlayerUtil.sendMessage(player, this);
    }

    public final void sendTo(CommandSender sender) {
        if (sender instanceof Player) {
            PlayerUtil.sendMessage((Player)sender, this);
        } else {
            sender.sendMessage(this.getMessage());
        }
    }

    public final CommonTag getNBT() {
        if (this.handle == null) {
            return CommonTagCompound.EMPTY;
        }
        return ComponentHandle.chatComponentToNBT((ComponentHandle)this.handle);
    }

    public final void setNBT(CommonTag nbt) {
        this.handle = ComponentHandle.nbtToChatComponent(nbt);
    }

    public final String getJson() {
        if (this.handle == null) {
            return "{}";
        }
        return ComponentHandle.chatComponentToJson((ComponentHandle)this.handle);
    }

    public final void setJson(String jsonText) {
        ComponentHandle jsonHandle = ComponentHandle.jsonToChatComponent(jsonText);
        this.handle = jsonHandle != null ? jsonHandle : ComponentHandle.empty();
    }

    public final String getMessage() {
        if (this.handle == null) {
            return "";
        }
        return CraftChatMessageHandle.fromComponent((ComponentHandle)this.handle);
    }

    public final boolean isEmpty() {
        return this.handle == null || ((ComponentHandle)this.handle).isEmpty();
    }

    public static boolean isAllEmpty(ChatText[] lines) {
        for (ChatText line : lines) {
            if (line == null || line.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public final void setMessage(String messageText) {
        ChatColor color;
        if (messageText.isEmpty()) {
            this.handle = ComponentHandle.empty();
            return;
        }
        ComponentHandle[] parts = CraftChatMessageHandle.fromString(messageText, true);
        this.handle = parts[0];
        for (int i = 1; i < parts.length; ++i) {
            this.handle = ((ComponentHandle)this.handle).addSibling(parts[i]);
        }
        ArrayList<ChatColor> trailing_formatting_chars = new ArrayList<ChatColor>(0);
        for (int i = messageText.length() - 2; i >= 0 && messageText.charAt(i) == '\u00a7' && (color = ChatColor.getByChar((char)messageText.charAt(i + 1))) != null; i -= 2) {
            trailing_formatting_chars.add(0, color);
        }
        if (!trailing_formatting_chars.isEmpty()) {
            this.handle = ((ComponentHandle)this.handle).addSibling(ComponentHandle.modifiersToComponent(trailing_formatting_chars));
        }
    }

    public final ChatText append(ChatText text) {
        return this.append((ComponentHandle)text.handle);
    }

    private final ChatText append(ComponentHandle handle) {
        this.handle = ((ComponentHandle)this.handle).addSibling(handle);
        return this;
    }

    public final ChatText append(String text) {
        return this.append(ChatText.fromMessage(text));
    }

    public final ChatText appendClickableURL(String text, String url) {
        return this.append(ChatText.fromClickableURL(text, url));
    }

    public final ChatText appendClickableURL(String text, String url, String altText) {
        return this.append(ChatText.fromClickableURL(text, url).setHoverText(altText));
    }

    public final ChatText appendClickableContent(String text, String content) {
        return this.append(ChatText.fromClickableContent(text, content));
    }

    public final ChatText appendNewLine() {
        return this.append(ComponentHandle.newLine());
    }

    public final ChatText setClickableURL(String url) {
        this.handle = ((ComponentHandle)this.handle).setClickableURL(url);
        return this;
    }

    public final ChatText setClickableContent(String content) {
        this.handle = ((ComponentHandle)this.handle).setClickableContent(content);
        return this;
    }

    public final ChatText setClickableSuggestedCommand(String command) {
        this.handle = ((ComponentHandle)this.handle).setClickableSuggestedCommand(command);
        return this;
    }

    public final ChatText setClickableRunCommand(String command) {
        this.handle = ((ComponentHandle)this.handle).setClickableRunCommand(command);
        return this;
    }

    public final ChatText setHoverText(ChatText hoverText) {
        if (hoverText != null) {
            this.handle = ((ComponentHandle)this.handle).setHoverText((ComponentHandle)hoverText.getBackingHandle());
        }
        return this;
    }

    public final ChatText setHoverText(String hoverText) {
        if (hoverText != null) {
            this.handle = ((ComponentHandle)this.handle).setHoverText((ComponentHandle)ChatText.fromMessage(hoverText).getBackingHandle());
        }
        return this;
    }

    public ChatText copy(ChatText from) {
        this.setHandle(((ComponentHandle)from.handle).isMutable() ? ((ComponentHandle)from.handle).createCopy() : (ComponentHandle)from.handle);
        return this;
    }

    public final ChatText clone() {
        ChatText clone = new ChatText();
        clone.setHandle(((ComponentHandle)this.handle).isMutable() ? ((ComponentHandle)this.handle).createCopy() : (ComponentHandle)this.handle);
        return clone;
    }

    public static ChatText empty() {
        return ChatText.fromMessage("");
    }

    public static ChatText fromClickableURL(String text, String url) {
        return ChatText.fromMessage(text).setClickableURL(url);
    }

    public static ChatText fromClickableContent(String text, String content) {
        return ChatText.fromMessage(text).setClickableContent(content);
    }

    public static ChatText fromClickableSuggestedCommand(String text, String command) {
        return ChatText.fromMessage(text).setClickableSuggestedCommand(command);
    }

    public static ChatText fromClickableRunCommand(String text, String command) {
        return ChatText.fromMessage(text).setClickableRunCommand(command);
    }

    public static ChatText fromJson(String jsonText) {
        if (jsonText == null) {
            return null;
        }
        ComponentHandle handle = ComponentHandle.jsonToChatComponent(jsonText);
        if (handle == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setHandle(handle);
        return text;
    }

    public static ChatText fromNBT(CommonTag nbt) {
        if (nbt == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setNBT(nbt);
        return text;
    }

    public static ChatText fromMessage(String message) {
        if (message == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setMessage(message);
        return text;
    }

    public static ChatText fromComponent(Object iChatBaseComponentHandle) {
        if (iChatBaseComponentHandle == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setHandle(ComponentHandle.createHandle(iChatBaseComponentHandle));
        return text;
    }

    public static ChatText fromChatColor(ChatColor color) {
        if (color == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setHandle(ComponentHandle.modifiersToComponent(Collections.singleton(color)));
        return text;
    }

    protected static String convertTextToJson_old(String text) {
        if (text == null || text.length() == 0) {
            return "\"\"";
        }
        int len = text.length();
        StringBuilder sb = new StringBuilder(len + 4);
        sb.append('\"');
        block9: for (int i = 0; i < len; ++i) {
            char c = text.charAt(i);
            switch (c) {
                case '\"': 
                case '\\': {
                    sb.append('\\');
                    sb.append(c);
                    continue block9;
                }
                case '/': {
                    sb.append('\\');
                    sb.append(c);
                    continue block9;
                }
                case '\b': {
                    sb.append("\\b");
                    continue block9;
                }
                case '\t': {
                    sb.append("\\t");
                    continue block9;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block9;
                }
                case '\f': {
                    sb.append("\\f");
                    continue block9;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block9;
                }
                default: {
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                        continue block9;
                    }
                    sb.append(c);
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
}

