/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.commands.selector;

import com.bergerkiller.bukkit.tc.commands.selector.SelectorCondition;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorException;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandlerConditionOption;
import java.util.Collection;
import java.util.List;
import org.bukkit.command.CommandSender;

public interface SelectorHandler {
    public Collection<String> handle(CommandSender var1, String var2, List<SelectorCondition> var3) throws SelectorException;

    public List<SelectorHandlerConditionOption> options(CommandSender var1, String var2, List<SelectorCondition> var3);

    default public boolean isCommandHandled(String command) {
        return true;
    }
}

