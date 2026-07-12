/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation;

import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.ConstPool;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.AnnotationsWriter;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.MemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.MemberValueVisitor;
import java.io.IOException;
import java.lang.reflect.Method;

public class FloatMemberValue
extends MemberValue {
    int valueIndex;

    public FloatMemberValue(int index, ConstPool cp) {
        super('F', cp);
        this.valueIndex = index;
    }

    public FloatMemberValue(float f, ConstPool cp) {
        super('F', cp);
        this.setValue(f);
    }

    public FloatMemberValue(ConstPool cp) {
        super('F', cp);
        this.setValue(0.0f);
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m) {
        return Float.valueOf(this.getValue());
    }

    @Override
    Class<?> getType(ClassLoader cl) {
        return Float.TYPE;
    }

    public float getValue() {
        return this.cp.getFloatInfo(this.valueIndex);
    }

    public void setValue(float newValue) {
        this.valueIndex = this.cp.addFloatInfo(newValue);
    }

    public String toString() {
        return Float.toString(this.getValue());
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitFloatMemberValue(this);
    }
}

