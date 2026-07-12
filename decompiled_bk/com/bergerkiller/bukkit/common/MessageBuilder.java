/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageBuilder {
    private final List<StringBuilder> lines = new ArrayList<StringBuilder>();
    private StringBuilder builder;
    private int currentWidth;
    private String separator;
    private int sepwidth;
    private boolean isFirstSeparatorCall;
    private int indent;
    private String errorStr;
    private String resultStr;
    public static final int CHAT_WINDOW_WIDTH = 240;

    public MessageBuilder() {
        this.reset();
    }

    public MessageBuilder(String firstLine) {
        this.reset(new StringBuilder(firstLine));
    }

    public MessageBuilder(int capacity) {
        this.reset(new StringBuilder(capacity));
    }

    public MessageBuilder(StringBuilder builder) {
        this.reset(builder);
    }

    public MessageBuilder reset() {
        return this.reset(new StringBuilder());
    }

    public MessageBuilder reset(StringBuilder builder) {
        this.currentWidth = 0;
        this.separator = null;
        this.sepwidth = 0;
        this.isFirstSeparatorCall = true;
        this.indent = 0;
        this.errorStr = null;
        this.resultStr = null;
        this.lines.clear();
        this.builder = builder;
        this.lines.add(this.builder);
        this.builder.setLength(0);
        return this;
    }

    public MessageBuilder setSeparator(ChatColor color, String separator) {
        return this.setSeparator(color + separator);
    }

    public MessageBuilder setSeparator(String separator) {
        if (separator == null) {
            return this.clearSeparator();
        }
        this.separator = separator;
        this.sepwidth = StringUtil.getWidth(separator);
        this.isFirstSeparatorCall = true;
        return this;
    }

    public MessageBuilder clearSeparator() {
        this.separator = null;
        this.sepwidth = 0;
        return this;
    }

    public int getIndent() {
        return this.indent;
    }

    public MessageBuilder indent(int indent) {
        this.indent += indent;
        return this;
    }

    public MessageBuilder setIndent(int indent) {
        this.indent = indent;
        return this;
    }

    public MessageBuilder black(Object ... text) {
        return this.append(ChatColor.BLACK, text);
    }

    public MessageBuilder dark_blue(Object ... text) {
        return this.append(ChatColor.DARK_BLUE, text);
    }

    public MessageBuilder dark_green(Object ... text) {
        return this.append(ChatColor.DARK_GREEN, text);
    }

    public MessageBuilder dark_aqua(Object ... text) {
        return this.append(ChatColor.DARK_AQUA, text);
    }

    public MessageBuilder dark_red(Object ... text) {
        return this.append(ChatColor.DARK_RED, text);
    }

    public MessageBuilder dark_purple(Object ... text) {
        return this.append(ChatColor.DARK_PURPLE, text);
    }

    public MessageBuilder gold(Object ... text) {
        return this.append(ChatColor.GOLD, text);
    }

    public MessageBuilder gray(Object ... text) {
        return this.append(ChatColor.GRAY, text);
    }

    public MessageBuilder dark_gray(Object ... text) {
        return this.append(ChatColor.DARK_GRAY, text);
    }

    public MessageBuilder blue(Object ... text) {
        return this.append(ChatColor.BLUE, text);
    }

    public MessageBuilder green(Object ... text) {
        return this.append(ChatColor.GREEN, text);
    }

    public MessageBuilder aqua(Object ... text) {
        return this.append(ChatColor.AQUA, text);
    }

    public MessageBuilder red(Object ... text) {
        return this.append(ChatColor.RED, text);
    }

    public MessageBuilder light_purple(Object ... text) {
        return this.append(ChatColor.LIGHT_PURPLE, text);
    }

    public MessageBuilder yellow(Object ... text) {
        return this.append(ChatColor.YELLOW, text);
    }

    public MessageBuilder white(Object ... text) {
        return this.append(ChatColor.WHITE, text);
    }

    public MessageBuilder magic(Object ... text) {
        return this.append(ChatColor.MAGIC, text);
    }

    public MessageBuilder append(ChatColor color, Object ... text) {
        String[] newtext = new String[text.length];
        for (int i = 0; i < text.length; ++i) {
            newtext[i] = text[i].toString();
        }
        return this.append(color, newtext);
    }

    public MessageBuilder append(Object ... text) {
        String[] newtext = new String[text.length];
        for (int i = 0; i < text.length; ++i) {
            newtext[i] = text[i].toString();
        }
        return this.append(newtext);
    }

    public MessageBuilder append(ChatColor color, String ... text) {
        if (text != null && text.length > 0) {
            this.prepareAppend(StringUtil.getTotalWidth(text));
            this.builder.append(color.toString());
            for (String part : text) {
                this.builder.append(part);
            }
        }
        return this;
    }

    public MessageBuilder append(char character) {
        if (character == '\n') {
            return this.newLine();
        }
        this.prepareAppend(StringUtil.getWidth(character));
        this.builder.append(character);
        return this;
    }

    public MessageBuilder append(String ... text) {
        if (text != null && text.length > 0) {
            this.prepareAppend(StringUtil.getTotalWidth(text));
            for (String part : text) {
                this.builder.append(part);
            }
        }
        return this;
    }

    private void prepareAppend(int widthToAppend) {
        if (this.currentWidth + widthToAppend + this.sepwidth > 240) {
            this.newLine();
        } else if (this.separator != null) {
            if (!this.isFirstSeparatorCall) {
                this.currentWidth += this.sepwidth;
                this.builder.append(this.separator);
            }
            this.isFirstSeparatorCall = false;
        }
        this.currentWidth += widthToAppend;
    }

    public MessageBuilder newLine() {
        this.builder = new StringBuilder(30);
        for (int i = 0; i < this.indent; ++i) {
            this.builder.append(' ');
        }
        this.currentWidth = this.indent * StringUtil.SPACE_WIDTH;
        this.lines.add(this.builder);
        return this;
    }

    public int length() {
        int length = this.lines.size() - 1;
        for (StringBuilder line : this.lines) {
            length += line.length();
        }
        return length;
    }

    public boolean isEmpty() {
        return this.lines.size() == 1 && this.builder.length() == 0;
    }

    public String lastLine() {
        return this.builder.toString();
    }

    public String toString() {
        StringBuilder total = new StringBuilder(this.length());
        for (int i = 0; i < this.lines.size(); ++i) {
            if (i != 0) {
                total.append('\n');
            }
            total.append((CharSequence)this.lines.get(i));
        }
        return total.toString();
    }

    public String[] lines() {
        if (this.isEmpty()) {
            return new String[0];
        }
        String[] lines = new String[this.lines.size()];
        int i = 0;
        for (StringBuilder line : this.lines) {
            lines[i++] = line.toString();
        }
        return lines;
    }

    public MessageBuilder clear() {
        this.lines.clear();
        this.builder = new StringBuilder();
        this.lines.add(this.builder);
        this.currentWidth = 0;
        return this;
    }

    public MessageBuilder flush(CommandSender sender) {
        return this.send(sender).clear();
    }

    public MessageBuilder send(CommandSender sender) {
        for (StringBuilder line : this.lines) {
            sender.sendMessage(line.toString());
        }
        return this;
    }

    public MessageBuilder log(Level level) {
        for (StringBuilder line : this.lines) {
            Common.LOGGER.log(level, line.toString());
        }
        return this;
    }

    public String getLastError() {
        return this.errorStr;
    }

    public String getLastResult() {
        return this.resultStr;
    }

    public MessageBuilder error(Object error_message) {
        this.errorStr = error_message == null ? null : error_message.toString();
        return this;
    }

    public MessageBuilder result(Object result) {
        this.resultStr = result == null ? null : result.toString();
        return this;
    }
}

