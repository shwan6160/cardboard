/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.actions.Action;
import com.bergerkiller.bukkit.tc.actions.registry.ActionRegistry;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import java.io.DataInputStream;
import java.io.IOException;

public class TrackedSignActionSetOutput
extends Action {
    private final TrainCarts traincarts;
    private final RailLookup.TrackedSign sign;
    private final boolean output;

    public TrackedSignActionSetOutput(TrainCarts traincarts, RailLookup.TrackedSign sign, boolean output) {
        this.traincarts = traincarts;
        this.sign = sign;
        this.output = output;
    }

    public RailLookup.TrackedSign getSign() {
        return this.sign;
    }

    public boolean getOutput() {
        return this.output;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.traincarts;
    }

    @Override
    public void start() {
        if (!this.sign.isRemoved()) {
            this.sign.setOutput(this.output);
        }
    }

    public static class Serializer
    implements ActionRegistry.Serializer<TrackedSignActionSetOutput> {
        private final TrainCarts plugin;

        public Serializer(TrainCarts plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean save(TrackedSignActionSetOutput action, OfflineDataBlock data, ActionTracker tracker) throws IOException {
            byte[] signData = this.plugin.getTrackedSignLookup().serializeUniqueKey(action.getSign().getUniqueKey());
            if (signData == null) {
                return false;
            }
            data.addChild("sign-output", stream -> {
                Util.writeByteArray(stream, signData);
                stream.writeBoolean(action.getOutput());
            });
            return true;
        }

        @Override
        public TrackedSignActionSetOutput load(OfflineDataBlock data, ActionTracker tracker) throws IOException {
            boolean output;
            RailLookup.TrackedSign sign;
            try (DataInputStream stream = data.findChildOrThrow("sign-output").readData();){
                Object uniqueKey = this.plugin.getTrackedSignLookup().deserializeUniqueKey(Util.readByteArray(stream));
                if (uniqueKey == null) {
                    throw new IllegalStateException("Sign unique key is not understood");
                }
                sign = this.plugin.getTrackedSignLookup().getTrackedSign(uniqueKey);
                if (sign == null) {
                    throw new IllegalStateException("Sign [" + uniqueKey + "] is missing");
                }
                output = stream.readBoolean();
            }
            return new TrackedSignActionSetOutput(this.plugin, sign, output);
        }
    }
}

