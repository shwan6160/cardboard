/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;

public enum InteractionResult {
    SUCCESS(true),
    CONSUME(true),
    PASS(false),
    FAIL(false);

    private final Object _handle;
    private final boolean _truthy;

    private InteractionResult(boolean truthy) {
        this._truthy = truthy;
        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.2")) {
            String name = this.name();
            try {
                this._handle = Resolver.resolveAndGetDeclaredField(CommonUtil.getClass("net.minecraft.world.InteractionResult"), name).get(null);
            }
            catch (Throwable t) {
                throw new IllegalStateException("InteractionResult missing constant: " + name, t);
            }
        } else {
            if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
                String name = this.name();
                if (name.equals("CONSUME") && !CommonBootstrap.evaluateMCVersion(">=", "1.15")) {
                    name = "SUCCESS";
                }
                for (Enum enumConstant : (Enum[])CommonUtil.getClass("net.minecraft.world.InteractionResult").getEnumConstants()) {
                    if (!enumConstant.name().equals(name)) continue;
                    this._handle = enumConstant;
                    return;
                }
                throw new IllegalStateException("InteractionResult missing constant: " + name);
            }
            this._handle = null;
        }
    }

    public Object getRawHandle() {
        return this._handle;
    }

    public boolean isTruthy() {
        return this._truthy;
    }

    public static InteractionResult fromHandle(Object nmsEnumInteractionResult) {
        if (nmsEnumInteractionResult == null) {
            return null;
        }
        for (InteractionResult result : InteractionResult.values()) {
            if (result._handle != nmsEnumInteractionResult) continue;
            return result;
        }
        throw new IllegalArgumentException("Unsupported enum value: " + nmsEnumInteractionResult);
    }

    public static InteractionResult fromTruthy(boolean truth) {
        return truth ? SUCCESS : PASS;
    }
}

