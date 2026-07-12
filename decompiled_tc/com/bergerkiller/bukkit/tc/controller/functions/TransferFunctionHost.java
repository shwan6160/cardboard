/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionRegistry;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInput;
import com.bergerkiller.bukkit.tc.properties.CartProperties;

public interface TransferFunctionHost
extends TrainCarts.Provider {
    public TransferFunctionRegistry getRegistry();

    public TransferFunctionInput.ReferencedSource registerInputSource(TransferFunctionInput.ReferencedSource var1);

    default public CartProperties getCartProperties() {
        MinecartMember<?> member = this.getMember();
        return member == null ? null : member.getProperties();
    }

    public MinecartMember<?> getMember();

    public Attachment getAttachment();

    public boolean isSequencer();

    public boolean isAttachment();

    default public TransferFunction loadFunction(ConfigurationNode config) {
        return this.getRegistry().load(this, config);
    }

    default public ConfigurationNode saveFunction(TransferFunction function) {
        return this.getRegistry().save(this, function);
    }
}

