/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.commands.argument;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.commands.parsers.AttachmentByNameParser;
import java.util.List;
import org.bukkit.command.CommandSender;

public final class AttachmentsByName<T extends Attachment> {
    private final String name;
    private final AttachmentByNameParser<T> parser;
    private final CommandContext<CommandSender> context;
    private List<T> attachments = null;

    public AttachmentsByName(String name, AttachmentByNameParser<T> parser, CommandContext<CommandSender> context) {
        this.name = name;
        this.parser = parser;
        this.context = context;
    }

    public String name() {
        return this.name;
    }

    public void validate() {
        this.attachments();
    }

    public List<T> attachments() {
        if (this.attachments == null) {
            this.attachments = this.parser.parse(this.context, this.name);
        }
        return this.attachments;
    }
}

