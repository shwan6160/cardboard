/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSign;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignMetadataHandler;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.signactions.detector.DetectorSign;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SignActionDetector
extends TrainCartsSignAction {
    public static final SignActionDetector INSTANCE = new SignActionDetector();

    public SignActionDetector() {
        super("detector");
    }

    public void enable(TrainCarts plugin) {
        plugin.getOfflineSigns().registerHandler(DetectorSign.Metadata.class, new OfflineSignMetadataHandler<DetectorSign.Metadata>(){

            @Override
            public int getMetadataVersion() {
                return 1;
            }

            @Override
            public void onUpdated(OfflineSignStore store, OfflineSign sign, DetectorSign.Metadata oldValue, DetectorSign.Metadata newValue) {
                if (oldValue.owner != newValue.owner) {
                    this.onUnloaded(store, sign, oldValue);
                    if (newValue.owner == null) {
                        this.onAdded(store, sign, newValue);
                    }
                }
            }

            @Override
            public void onAdded(OfflineSignStore store, OfflineSign sign, DetectorSign.Metadata metadata) {
                metadata.owner = new DetectorSign(store, sign, metadata);
                metadata.region.register(metadata.owner);
            }

            @Override
            public void onUnloaded(OfflineSignStore store, OfflineSign sign, DetectorSign.Metadata metadata) {
                DetectorSign prevOwner = metadata.owner;
                if (prevOwner != null) {
                    metadata.owner = null;
                    metadata.region.unregister(prevOwner);
                }
            }

            @Override
            public void onRemoved(OfflineSignStore store, OfflineSign sign, DetectorSign.Metadata metadata) {
                DetectorSign prevOwner = metadata.owner;
                if (prevOwner != null) {
                    DetectorSign.Metadata otherMeta;
                    metadata.owner = null;
                    metadata.region.unregister(prevOwner);
                    if (!metadata.region.isRegistered()) {
                        metadata.region.remove();
                    }
                    if ((otherMeta = store.get(metadata.otherSign, metadata.otherSignFront, DetectorSign.Metadata.class)) != null && metadata.region == otherMeta.region) {
                        store.remove(metadata.otherSign, metadata.otherSignFront, DetectorSign.Metadata.class);
                    }
                }
            }

            @Override
            public void onEncode(DataOutputStream stream, OfflineSign sign, DetectorSign.Metadata value) throws IOException {
                value.otherSign.getPosition().write(stream);
                stream.writeBoolean(value.otherSignFront);
                StreamUtil.writeUUID((DataOutputStream)stream, (UUID)value.region.getUniqueId());
                stream.writeBoolean(value.isLeverDown);
            }

            @Override
            public DetectorSign.Metadata onDecode(DataInputStream stream, OfflineSign sign) throws IOException {
                OfflineBlock otherSign = sign.getWorld().getBlockAt(IntVector3.read((DataInputStream)stream));
                boolean otherSignFront = stream.readBoolean();
                DetectorRegion region = DetectorRegion.getRegion(StreamUtil.readUUID((DataInputStream)stream));
                boolean isLeverDown = stream.readBoolean();
                if (region == null) {
                    throw new OfflineSignMetadataHandler.InvalidMetadataException();
                }
                return new DetectorSign.Metadata(otherSign, otherSignFront, region, isLeverDown);
            }

            @Override
            public OfflineSignMetadataHandler.DataMigrationDecoder<DetectorSign.Metadata> getMigrationDecoder(OfflineSign gsign, int gdataVersion) {
                if (gdataVersion == 0) {
                    return (stream, sign, dataVersion) -> {
                        OfflineBlock otherSign = sign.getWorld().getBlockAt(IntVector3.read((DataInputStream)stream));
                        DetectorRegion region = DetectorRegion.getRegion(StreamUtil.readUUID((DataInputStream)stream));
                        boolean isLeverDown = stream.readBoolean();
                        if (region == null) {
                            throw new OfflineSignMetadataHandler.InvalidMetadataException();
                        }
                        return new DetectorSign.Metadata(otherSign, true, region, isLeverDown);
                    };
                }
                return OfflineSignMetadataHandler.super.getMigrationDecoder(gsign, gdataVersion);
            }
        });
    }

    public void disable(TrainCarts plugin) {
        plugin.getOfflineSigns().unregisterHandler(DetectorSign.Metadata.class);
    }

    @Override
    public boolean canSupportFakeSign(SignActionEvent info) {
        return false;
    }

    public boolean matchLabel(SignActionEvent info, String label) {
        if (!this.match(info)) {
            return false;
        }
        String otherLabel = this.getLabel(info);
        return label == null ? otherLabel == null : label.equalsIgnoreCase(otherLabel);
    }

    public String getLabel(SignActionEvent info) {
        String data = info.getLine(1);
        int index = Util.minStringIndex(data.indexOf(32), data.indexOf(58));
        return index == -1 ? null : data.substring(index + 1).trim();
    }

    @Override
    public void execute(SignActionEvent info) {
        if ((info.getAction().isRedstone() || info.isAction(SignActionType.GROUP_ENTER)) && info.getTrainCarts().getOfflineSigns().get(info.getTrackedSign(), DetectorSign.Metadata.class) == null) {
            this.handlePlacement(info);
        }
    }

    @Override
    public String getDescriptiveOutputName(SignActionEvent event) {
        return "Train activates detector";
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (!SignBuildOptions.create().setPermission(Permission.BUILD_DETECTOR).setName("train detector").setDescription("detect trains between this detector sign and another").setTraincartsWIKIHelp("TrainCarts/Signs/Detector").handle(event)) {
            return false;
        }
        if (!event.getTrackedSign().isRealSign()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Detector signs must be placed using real signs");
            return false;
        }
        if (!event.hasRails()) {
            event.getPlayer().sendMessage(ChatColor.RED + "No rails are nearby: This detector sign has not been activated!");
            return true;
        }
        if (!this.handlePlacement(event)) {
            event.getPlayer().sendMessage(ChatColor.RED + "Failed to find a second detector sign: No region set.");
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Place a second connected detector sign to finish this region!");
            return true;
        }
        event.getPlayer().sendMessage(ChatColor.GREEN + "A second detector sign was found: Region set.");
        return true;
    }

    private boolean handlePlacement(SignActionEvent event) {
        if (!event.hasRails() || !event.getTrackedSign().isRealSign()) {
            return false;
        }
        RailLookup.TrackedRealSign startSign = (RailLookup.TrackedRealSign)event.getTrackedSign();
        Block startrails = event.getRails();
        BlockFace dir = event.getFacing();
        String label = this.getLabel(event);
        return this.tryBuild(event.getTrainCarts(), label, startrails, startSign, dir) || this.tryBuild(event.getTrainCarts(), label, startrails, startSign, FaceUtil.rotate((BlockFace)dir, (int)2)) || this.tryBuild(event.getTrainCarts(), label, startrails, startSign, FaceUtil.rotate((BlockFace)dir, (int)-2));
    }

    public boolean tryBuild(TrainCarts traincarts, String label, Block startrails, RailLookup.TrackedRealSign startSign, BlockFace direction) {
        TrackWalkingPoint walker = new TrackWalkingPoint(startrails, direction);
        HashSet<IntVector3> coords = new HashSet<IntVector3>();
        while (walker.moveFull() && walker.movedTotal <= (double)TCConfig.maxDetectorLength) {
            if (!coords.add(walker.state.railPiece().blockPosition())) continue;
            for (RailLookup.TrackedSign sign : walker.state.railPiece().signs()) {
                SignActionEvent info;
                if (!sign.isRealSign() || sign.isRemoved() || sign.signBlock.equals((Object)startSign.signBlock) && ((RailLookup.TrackedRealSign)sign).isFrontText() == startSign.isFrontText() || !this.matchLabel(info = new SignActionEvent(sign), label)) continue;
                RailLookup.TrackedSign endsign = sign;
                DetectorRegion region = DetectorRegion.create(walker.state.railWorld(), coords);
                OfflineSignStore store = traincarts.getOfflineSigns();
                store.put(startSign, new DetectorSign.Metadata(endsign, region, false));
                store.put(endsign, new DetectorSign.Metadata(startSign, region, false));
                CommonUtil.nextTick(() -> region.detectMinecarts());
                return true;
            }
        }
        return false;
    }
}

