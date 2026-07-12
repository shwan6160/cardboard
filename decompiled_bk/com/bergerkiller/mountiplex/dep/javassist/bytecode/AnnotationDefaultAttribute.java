/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.bytecode;

import com.bergerkiller.mountiplex.dep.javassist.bytecode.AnnotationsAttribute;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.AttributeInfo;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.ConstPool;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.AnnotationsWriter;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.MemberValue;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class AnnotationDefaultAttribute
extends AttributeInfo {
    public static final String tag = "AnnotationDefault";

    public AnnotationDefaultAttribute(ConstPool cp, byte[] info) {
        super(cp, tag, info);
    }

    public AnnotationDefaultAttribute(ConstPool cp) {
        this(cp, new byte[]{0, 0});
    }

    AnnotationDefaultAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
        super(cp, n, in);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        AnnotationsAttribute.Copier copier = new AnnotationsAttribute.Copier(this.info, this.constPool, newCp, classnames);
        try {
            copier.memberValue(0);
            return new AnnotationDefaultAttribute(newCp, copier.close());
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    @Override
    void renameClass(String oldname, String newname) {
        try {
            MemberValue defaultValue = this.getDefaultValue();
            defaultValue.renameClass(oldname, newname);
            this.setDefaultValue(defaultValue);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    void renameClass(Map<String, String> classnames) {
        try {
            MemberValue defaultValue = this.getDefaultValue();
            defaultValue.renameClass(classnames);
            this.setDefaultValue(defaultValue);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public MemberValue getDefaultValue() {
        try {
            return new AnnotationsAttribute.Parser(this.info, this.constPool).parseMemberValue();
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    public void setDefaultValue(MemberValue value) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);
        try {
            value.write(writer);
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.set(output.toByteArray());
    }

    public String toString() {
        return this.getDefaultValue().toString();
    }
}

