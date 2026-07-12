/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation;

import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.AnnotationMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.ArrayMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.BooleanMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.ByteMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.CharMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.ClassMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.DoubleMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.EnumMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.FloatMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.IntegerMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.LongMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.ShortMemberValue;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.annotation.StringMemberValue;

public interface MemberValueVisitor {
    public void visitAnnotationMemberValue(AnnotationMemberValue var1);

    public void visitArrayMemberValue(ArrayMemberValue var1);

    public void visitBooleanMemberValue(BooleanMemberValue var1);

    public void visitByteMemberValue(ByteMemberValue var1);

    public void visitCharMemberValue(CharMemberValue var1);

    public void visitDoubleMemberValue(DoubleMemberValue var1);

    public void visitEnumMemberValue(EnumMemberValue var1);

    public void visitFloatMemberValue(FloatMemberValue var1);

    public void visitIntegerMemberValue(IntegerMemberValue var1);

    public void visitLongMemberValue(LongMemberValue var1);

    public void visitShortMemberValue(ShortMemberValue var1);

    public void visitStringMemberValue(StringMemberValue var1);

    public void visitClassMemberValue(ClassMemberValue var1);
}

