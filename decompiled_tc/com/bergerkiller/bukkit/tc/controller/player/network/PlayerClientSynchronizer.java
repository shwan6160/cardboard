/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.controller.player.network;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerClientSynchronizerProviderLegacyImpl;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerClientSynchronizerProviderModernImpl;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import org.bukkit.entity.Player;

public interface PlayerClientSynchronizer {
    public Player getPlayer();

    public void synchronize(IntFunction<ClientboundPlayerPositionPacketHandle> var1, Consumer<ClientboundPlayerPositionPacketHandle> var2);

    public void synchronizeBundle(List<? extends PacketHandle> var1, Runnable var2, Runnable var3);

    public void synchronize(Runnable var1);

    public static interface Provider
    extends LibraryComponent {
        public PlayerClientSynchronizer forViewer(AttachmentViewer var1);

        default public PlayerClientSynchronizer forPlayer(Player player) {
            return this.forViewer(AttachmentViewer.forPlayer(player));
        }

        default public PlayerClientSynchronizer createNoOp(final Player player) {
            return new PlayerClientSynchronizer(){
                final /* synthetic */ Provider this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void synchronize(IntFunction<ClientboundPlayerPositionPacketHandle> positionPacketMaker, Consumer<ClientboundPlayerPositionPacketHandle> callback) {
                }

                @Override
                public void synchronizeBundle(List<? extends PacketHandle> packets, Runnable startCallback, Runnable endCallback) {
                }

                @Override
                public void synchronize(Runnable callback) {
                }
            };
        }

        public static Provider create(TrainCarts traincarts) {
            if (Common.evaluateMCVersion((String)">=", (String)"1.9")) {
                return new PlayerClientSynchronizerProviderModernImpl(traincarts);
            }
            return new PlayerClientSynchronizerProviderLegacyImpl(traincarts);
        }

        public void enable();

        public void disable();
    }
}

