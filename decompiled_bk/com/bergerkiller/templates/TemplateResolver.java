/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.templates;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateResolver
implements ClassDeclarationResolver {
    private HashMap<Class<?>, List<ClassDeclaration>> classes = new HashMap();
    private boolean classes_loaded = false;
    private String version = "UNKNOWN";
    private ClassResolver globalSourceResolver = ClassResolver.DEFAULT;
    private final Map<String, String> variables = new HashMap<String, String>();
    private final String[] supported_mc_versions = new String[]{"1.8", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9", "1.9", "1.9.2", "1.9.4", "1.10.2", "1.11", "1.11.2", "1.12", "1.12.1", "1.12.2", "1.13", "1.13.1", "1.13.2", "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4", "1.15", "1.15.1", "1.15.2", "1.16.1", "1.16.2", "1.16.3", "1.16.4", "1.16.5", "1.17", "1.17.1", "1.18", "1.18.1", "1.18.2", "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11", "26.1", "26.1.1", "26.1.2", "26.2"};

    @Override
    public ClassDeclaration resolveClassDeclaration(String classPath, Class<?> type) {
        List<ClassDeclaration> allByType = this.classes.get(type);
        if (allByType == null || allByType.isEmpty()) {
            return null;
        }
        if (allByType.size() == 1) {
            return allByType.get(0);
        }
        for (ClassDeclaration dec : allByType) {
            if (!classPath.endsWith(dec.type.typeName)) continue;
            return dec;
        }
        return allByType.get(0);
    }

    @Override
    public void resolveClassVariables(String classPath, Class<?> classType, Map<String, String> variables) {
        variables.putAll(this.variables);
    }

    @Override
    public ClassResolver getRootClassResolver(String classPath, Class<?> classType) {
        return this.globalSourceResolver;
    }

    public Collection<ClassDeclaration> all() {
        ArrayList<ClassDeclaration> all = new ArrayList<ClassDeclaration>(this.classes.size() + 10);
        for (List<ClassDeclaration> ls : this.classes.values()) {
            all.addAll(ls);
        }
        return all;
    }

    public void unload() {
        this.classes_loaded = false;
        this.classes = new HashMap(0);
    }

    public void load() {
        if (!this.classes_loaded) {
            this.classes_loaded = true;
            this.version = Common.SERVER.getMinecraftVersionMajor();
            String templatePath = "com/bergerkiller/templates/init.txt";
            this.variables.clear();
            Common.SERVER.addVariables(this.variables);
            ClassLoader classLoader = TemplateResolver.class.getClassLoader();
            SourceDeclaration sourceDec = SourceDeclaration.parseFromResources(classLoader, templatePath, this.variables);
            this.globalSourceResolver = sourceDec.getResolver();
            for (ClassDeclaration cdec : sourceDec.classes) {
                this.register(cdec);
            }
        }
    }

    private final void register(ClassDeclaration cdec) {
        List<ClassDeclaration> old_list = this.classes.get(cdec.type.type);
        if (old_list == null) {
            this.classes.put(cdec.type.type, Collections.singletonList(cdec));
        } else {
            ArrayList<ClassDeclaration> new_list = new ArrayList<ClassDeclaration>(old_list);
            new_list.add(cdec);
            new_list.trimToSize();
            this.classes.put(cdec.type.type, new_list);
        }
        for (ClassDeclaration subcdec : cdec.subclasses) {
            this.register(subcdec);
        }
    }

    public boolean isSupported(String mc_version) {
        return LogicUtil.contains(CommonServer.cleanVersion(mc_version), this.supported_mc_versions);
    }

    public String[] getSupportedVersions() {
        return this.supported_mc_versions;
    }

    public String getDebugSupportedVersionsString() {
        String ver;
        int lastMajorVersionsStart;
        int secondDot;
        String lastMayorVersion = this.supported_mc_versions[this.supported_mc_versions.length - 1];
        int firstDot = lastMayorVersion.indexOf(46);
        if (firstDot != -1 && (secondDot = lastMayorVersion.indexOf(46, firstDot + 1)) != -1) {
            lastMayorVersion = lastMayorVersion.substring(0, secondDot);
        }
        String lastMayorVersionWithDot = lastMayorVersion + ".";
        for (lastMajorVersionsStart = this.supported_mc_versions.length - 1; lastMajorVersionsStart >= 1 && ((ver = this.supported_mc_versions[lastMajorVersionsStart - 1]).equals(lastMayorVersion) || ver.startsWith(lastMayorVersionWithDot)); --lastMajorVersionsStart) {
        }
        StringBuilder str = new StringBuilder();
        str.append(this.supported_mc_versions[0]);
        str.append(" to ");
        str.append(this.supported_mc_versions[lastMajorVersionsStart - 1]);
        for (int i = lastMajorVersionsStart; i < this.supported_mc_versions.length; ++i) {
            if (i == this.supported_mc_versions.length - 1) {
                str.append(" and ");
            } else {
                str.append(", ");
            }
            str.append(this.supported_mc_versions[i]);
        }
        return str.toString();
    }

    public String getVersion() {
        return this.version;
    }
}

