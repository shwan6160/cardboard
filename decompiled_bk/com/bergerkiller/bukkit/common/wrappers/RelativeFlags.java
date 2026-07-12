/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.util.Collections;
import java.util.Set;

public final class RelativeFlags {
    private static final int FLAG_X = 1;
    private static final int FLAG_Y = 2;
    private static final int FLAG_Z = 4;
    private static final int FLAG_YAW = 8;
    private static final int FLAG_PITCH = 16;
    private static final int FLAG_DELTA_X = 32;
    private static final int FLAG_DELTA_Y = 64;
    private static final int FLAG_DELTA_Z = 128;
    private static final int FLAG_DELTA_ROTATION = 256;
    private static final RelativeFlags[] cache = new RelativeFlags[512];
    public static final FastMethod<Set<?>> unpackMethod;
    public static final FastMethod<Integer> packMethod;
    public static final RelativeFlags ABSOLUTE_POSITION;
    public static final RelativeFlags RELATIVE_ROTATION;
    public static final RelativeFlags RELATIVE_POSITION_ROTATION;
    private final int flags;
    private final Set<?> relativeFlags;

    private RelativeFlags(int flags) {
        this.flags = flags;
        this.relativeFlags = Collections.unmodifiableSet(unpackMethod.invoke(null, flags));
    }

    public boolean isRelativeX() {
        return (this.flags & 1) != 0;
    }

    public boolean isRelativeY() {
        return (this.flags & 2) != 0;
    }

    public boolean isRelativeZ() {
        return (this.flags & 4) != 0;
    }

    public boolean isRelativeYaw() {
        return (this.flags & 8) != 0;
    }

    public boolean isRelativePitch() {
        return (this.flags & 0x10) != 0;
    }

    public boolean isRelativeDeltaX() {
        return (this.flags & 0x20) != 0;
    }

    public boolean isRelativeDeltaY() {
        return (this.flags & 0x40) != 0;
    }

    public boolean isRelativeDeltaZ() {
        return (this.flags & 0x80) != 0;
    }

    public boolean isRelativeDeltaRotation() {
        return (this.flags & 0x100) != 0;
    }

    public RelativeFlags withRelativeX() {
        return RelativeFlags.fromFlags(this.flags | 1);
    }

    public RelativeFlags withAbsoluteX() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFFE);
    }

    public RelativeFlags withRelativeY() {
        return RelativeFlags.fromFlags(this.flags | 2);
    }

    public RelativeFlags withAbsoluteY() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFFD);
    }

    public RelativeFlags withRelativeZ() {
        return RelativeFlags.fromFlags(this.flags | 4);
    }

    public RelativeFlags withAbsoluteZ() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFFB);
    }

    public RelativeFlags withRelativeDeltaX() {
        return RelativeFlags.fromFlags(this.flags | 0x20);
    }

    public RelativeFlags withAbsoluteDeltaX() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFDF);
    }

    public RelativeFlags withRelativeDeltaY() {
        return RelativeFlags.fromFlags(this.flags | 0x40);
    }

    public RelativeFlags withAbsoluteDeltaY() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFBF);
    }

    public RelativeFlags withRelativeDeltaZ() {
        return RelativeFlags.fromFlags(this.flags | 0x80);
    }

    public RelativeFlags withAbsoluteDeltaZ() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFF7F);
    }

    public RelativeFlags withRelativeDeltaRotation() {
        return RelativeFlags.fromFlags(this.flags | 0x100);
    }

    public RelativeFlags withAbsoluteDeltaRotation() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFEFF);
    }

    public RelativeFlags withRelativePosition() {
        return RelativeFlags.fromFlags(this.flags | 7);
    }

    public RelativeFlags withAbsolutePosition() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFF8);
    }

    public RelativeFlags withRelativeRotation() {
        return RelativeFlags.fromFlags(this.flags | 0x18);
    }

    public RelativeFlags withAbsoluteRotation() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFE7);
    }

    public RelativeFlags withRelativeYaw() {
        return RelativeFlags.fromFlags(this.flags | 8);
    }

    public RelativeFlags withAbsoluteYaw() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFF7);
    }

    public RelativeFlags withRelativePitch() {
        return RelativeFlags.fromFlags(this.flags | 0x10);
    }

    public RelativeFlags withAbsolutePitch() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFFEF);
    }

    public RelativeFlags withRelativeDelta() {
        return RelativeFlags.fromFlags(this.flags | 0x1E0);
    }

    public RelativeFlags withAbsoluteDelta() {
        return RelativeFlags.fromFlags(this.flags & 0xFFFFFE1F);
    }

    public int hashCode() {
        return this.flags;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("RelativeFlags{flags=").append(this.flags).append(", relative=[");
        if (this.isRelativeX()) {
            str.append(" X");
        }
        if (this.isRelativeY()) {
            str.append(" Y");
        }
        if (this.isRelativeZ()) {
            str.append(" Z");
        }
        if (this.isRelativeYaw()) {
            str.append(" Yaw");
        }
        if (this.isRelativePitch()) {
            str.append(" Pitch");
        }
        if (this.isRelativeDeltaX()) {
            str.append(" Delta-X");
        }
        if (this.isRelativeDeltaY()) {
            str.append(" Delta-Y");
        }
        if (this.isRelativeDeltaZ()) {
            str.append(" Delta-Z");
        }
        if (this.isRelativeDeltaRotation()) {
            str.append(" Delta-Rotation");
        }
        str.append(" ]}");
        return str.toString();
    }

    @ConverterMethod(input="java.util.Set<net.minecraft.world.entity.Relative>")
    public static RelativeFlags fromRawRelativeFlags(Set<?> rawRelativeFlags) {
        return RelativeFlags.fromFlags(packMethod.invoke(null, rawRelativeFlags));
    }

    @ConverterMethod(output="java.util.Set<net.minecraft.world.entity.Relative>")
    public static Set<?> toRawRelativeFlags(RelativeFlags flags) {
        return flags.relativeFlags;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static RelativeFlags fromFlags(int flags) {
        RelativeFlags cachedFlags = cache[flags];
        if (cachedFlags != null) return cachedFlags;
        RelativeFlags[] relativeFlagsArray = cache;
        synchronized (cache) {
            cachedFlags = cache[flags];
            if (cachedFlags != null) return cachedFlags;
            RelativeFlags.cache[flags] = cachedFlags = new RelativeFlags(flags);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return cachedFlags;
        }
    }

    static {
        CommonBootstrap.initCommonServerAssertCompatibility();
        unpackMethod = new FastMethod(m -> {
            Class<?> relativeType = CommonUtil.getClass("net.minecraft.world.entity.Relative");
            if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
                m.init(Resolver.resolveAndGetDeclaredMethod(relativeType, "unpack", Integer.TYPE));
            } else {
                m.init(Resolver.resolveAndGetDeclaredMethod(relativeType, "a", Integer.TYPE));
            }
        });
        packMethod = new FastMethod(m -> {
            Class<?> relativeType = CommonUtil.getClass("net.minecraft.world.entity.Relative");
            if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
                m.init(Resolver.resolveAndGetDeclaredMethod(relativeType, "pack", Set.class));
            } else {
                m.init(Resolver.resolveAndGetDeclaredMethod(relativeType, "a", Set.class));
            }
        });
        ABSOLUTE_POSITION = RelativeFlags.fromFlags(0);
        RELATIVE_ROTATION = RelativeFlags.fromFlags(24);
        RELATIVE_POSITION_ROTATION = RelativeFlags.fromFlags(31);
    }
}

