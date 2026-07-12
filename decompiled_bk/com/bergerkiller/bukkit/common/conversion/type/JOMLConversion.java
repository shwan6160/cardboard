/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

public class JOMLConversion {
    public static final Class<?> JOML_VECTOR3F_TYPE = LogicUtil.tryMake(() -> Class.forName("org.joml.Vector3f"), null);
    public static final Class<?> JOML_VECTOR3F_CONSTANT_TYPE = LogicUtil.tryMake(() -> Class.forName("org.joml.Vector3fc"), null);
    public static final Class<?> JOML_QUATERNIONF_TYPE = LogicUtil.tryMake(() -> Class.forName("org.joml.Quaternionf"), null);
    public static final Class<?> JOML_QUATERNIONF_CONSTANT_TYPE = LogicUtil.tryMake(() -> Class.forName("org.joml.Quaternionfc"), null);
    private static final ConversionLogic LOGIC = JOML_VECTOR3F_TYPE != null && JOML_QUATERNIONF_TYPE != null ? Template.Class.create(ConversionLogic.class) : null;

    public static boolean available() {
        return LOGIC != null;
    }

    public static void init() {
        if (LOGIC != null) {
            LOGIC.forceInitialization();
        }
    }

    @ConverterMethod(input="org.joml.Vector3fc")
    public static Vector toVector(Object vector3f) {
        return LOGIC.decodeVector3f(vector3f);
    }

    @ConverterMethod(output="org.joml.Vector3f")
    public static Object fromVector(Vector vector) {
        return LOGIC.encodeVector3f(vector);
    }

    @ConverterMethod(input="org.joml.Quaternionfc")
    public static Quaternion toQuaternion(Object quaternionf) {
        return LOGIC.decodeQuaternionf(quaternionf);
    }

    @ConverterMethod(output="org.joml.Quaternionf")
    public static Object fromQuaternion(Quaternion quaternion) {
        return LOGIC.encodeQuaternionf(quaternion);
    }

    @Template.ImportList(value={@Template.Import(value="org.joml.Vector3f"), @Template.Import(value="org.joml.Vector3fc"), @Template.Import(value="org.joml.Quaternionf"), @Template.Import(value="org.joml.Quaternionfc"), @Template.Import(value="org.bukkit.util.Vector"), @Template.Import(value="com.bergerkiller.bukkit.common.math.Quaternion")})
    public static abstract class ConversionLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static Vector3f encode(float x, float y, float z) {\n    return new Vector3f(x, y, z);\n}")
        public abstract Object encodeVector3f(float var1, float var2, float var3);

        @Template.Generated(value="public static Vector3f encode(Vector v) {\n    return new Vector3f((float) v.getX(), (float) v.getY(), (float) v.getZ());\n}")
        public abstract Object encodeVector3f(Vector var1);

        @Template.Generated(value="public static Vector decode(Vector3fc v) {\n    return new Vector((double) v.x(), (double) v.y(), (double) v.z());\n}")
        public abstract Vector decodeVector3f(Object var1);

        @Template.Generated(value="public static Quaternionf encode(Quaternion q) {\n    return new Quaternionf(q.getX(), q.getY(), q.getZ(), q.getW());\n}")
        public abstract Object encodeQuaternionf(Quaternion var1);

        @Template.Generated(value="public static Quaternion decode(Quaternionfc q) {\n    return new Quaternion((double) q.x(), (double) q.y(), (double) q.z(), (double) q.w());\n}")
        public abstract Quaternion decodeQuaternionf(Object var1);
    }
}

