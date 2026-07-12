/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.PluginDescriptionFile
 */
package com.bergerkiller.reflection.org.bukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import java.util.Map;
import org.bukkit.plugin.PluginDescriptionFile;

public class BPluginDescriptionFile {
    public static final ClassTemplate<PluginDescriptionFile> T = ClassTemplate.create(PluginDescriptionFile.class);
    public static final FieldAccessor<Map<String, Map<String, Object>>> commands = T.selectField("private Map<String, Map<String, Object>> commands");
}

