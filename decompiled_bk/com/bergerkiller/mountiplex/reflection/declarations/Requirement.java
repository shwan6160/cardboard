/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Requirement {
    public final String name;
    public final Declaration declaration;
    private Map<String, Object> metadata;

    public Requirement(String name, Declaration declaration) {
        this.name = name;
        this.declaration = declaration;
        this.metadata = Collections.emptyMap();
    }

    public boolean hasProperty(String name) {
        return this.metadata.containsKey(name);
    }

    public <T> T property(String name) {
        return (T)this.metadata.get(name);
    }

    public void setProperty(String name) {
        this.setProperty(name, Boolean.TRUE);
    }

    public void setProperty(String name, Object value) {
        if (this.metadata.isEmpty()) {
            this.metadata = Collections.singletonMap(name, value);
        } else {
            if (this.metadata.size() == 1) {
                this.metadata = new HashMap<String, Object>(this.metadata);
            }
            this.metadata.put(name, value);
        }
    }
}

