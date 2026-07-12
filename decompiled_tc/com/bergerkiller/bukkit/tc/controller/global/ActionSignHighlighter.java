/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.block.BlockRayTrace
 *  com.bergerkiller.bukkit.common.block.BlockRayTrace$HitResult
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.Brightness
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.block.BlockRayTrace;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayBlockEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayTextEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.ListCallbackCollector;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ActionSignHighlighter
implements LibraryComponent {
    private static final Material LEVER_TYPE = MaterialUtil.getFirst((String[])new String[]{"LEVER", "LEGACY_LEVER"});
    private static final ChatColor[] HIGHLIGHT_COLORS = new ChatColor[]{ChatColor.RED, ChatColor.GREEN, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.BLUE, ChatColor.LIGHT_PURPLE, ChatColor.WHITE};
    private final TrainCarts plugin;
    private final Task updateTask;
    private final Listener listener;
    private final Map<Player, PlayerViewedBlockTracker> trackers = new IdentityHashMap<Player, PlayerViewedBlockTracker>();
    private boolean enabled = false;

    public ActionSignHighlighter(TrainCarts plugin) {
        this.plugin = plugin;
        this.updateTask = new Task((JavaPlugin)plugin){
            int stateCtr;
            {
                this.stateCtr = 0;
            }

            public void run() {
                int state = ++this.stateCtr;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerViewedBlockTracker tracker2 = ActionSignHighlighter.this.trackers.computeIfAbsent(player, x$0 -> new PlayerViewedBlockTracker((Player)x$0));
                    tracker2.state = state;
                    tracker2.update();
                }
                ActionSignHighlighter.this.trackers.values().removeIf(tracker -> {
                    if (((PlayerViewedBlockTracker)tracker).state == state) {
                        return false;
                    }
                    tracker.resetAndClearViewedBlock();
                    return true;
                });
            }
        };
        this.listener = new Listener(){

            @EventHandler(priority=EventPriority.MONITOR)
            public void onPlayerInteract(PlayerInteractEvent event) {
                ActionSignHighlighter.this.invalidateBlock(event.getPlayer(), event.getClickedBlock());
            }
        };
    }

    public void updateEnabled() {
        if (TCConfig.debugOutputLevers) {
            if (!this.enabled) {
                this.enabled = true;
                this.updateTask.start(1L, 1L);
                this.plugin.register(this.listener);
            }
        } else {
            this.disable();
        }
    }

    public void enable() {
        this.updateEnabled();
    }

    public void disable() {
        if (this.enabled) {
            this.enabled = false;
            this.trackers.values().forEach(PlayerViewedBlockTracker::resetAndClearViewedBlock);
            this.trackers.clear();
            this.updateTask.stop();
            CommonUtil.unregisterListener((Listener)this.listener);
        }
    }

    private void invalidateBlock(Player player, Block block) {
        if (block == null) {
            return;
        }
        PlayerViewedBlockTracker tracker = this.trackers.get(player);
        if (tracker == null || tracker.lastHighlightedBlock == null) {
            return;
        }
        if (block.equals((Object)tracker.lastHighlightedBlock)) {
            tracker.reset();
            return;
        }
        if (block.equals((Object)tracker.lastHighlightedBlock.getRelative(tracker.lastHighlightedFace))) {
            tracker.reset();
            return;
        }
    }

    private static boolean isRayTraceDifferent(BlockRayTrace a, BlockRayTrace b) {
        if (a.getWorld() != b.getWorld()) {
            return true;
        }
        if (a.getStartPosition().distanceSquared(b.getStartPosition()) > 1.0E-4) {
            return true;
        }
        return a.getEndPosition().distanceSquared(b.getEndPosition()) > 1.0E-4;
    }

    private final class PlayerViewedBlockTracker {
        private final AttachmentViewer viewer;
        private int state = -1;
        private ViewedBlock lastViewedBlock = null;
        private BlockRayTrace lastRayTrace = null;
        private BlockRayTrace.HitResult lastHitResult = null;
        private Block lastHighlightedBlock = null;
        private BlockFace lastHighlightedFace = null;

        public PlayerViewedBlockTracker(Player player) {
            this.viewer = ActionSignHighlighter.this.plugin.getAttachmentViewer(player);
        }

        public void onViewedBlockChanged(ViewedBlock previousViewedBlock, ViewedBlock newViewedBlock) {
            if (previousViewedBlock != null) {
                previousViewedBlock.hide(this.viewer);
            }
            if (newViewedBlock != null) {
                newViewedBlock.show(this.viewer);
            }
        }

        public void reset() {
            this.lastRayTrace = null;
            this.lastHitResult = null;
            this.lastHighlightedBlock = null;
            this.lastHighlightedFace = null;
        }

        public void resetAndClearViewedBlock() {
            this.reset();
            this.clearViewedBlock();
        }

        public void clearViewedBlock() {
            ViewedBlock lastViewedBlockTmp = this.lastViewedBlock;
            if (lastViewedBlockTmp != null) {
                this.lastViewedBlock = null;
                this.onViewedBlockChanged(lastViewedBlockTmp, null);
            }
        }

        public void update() {
            if (!this.viewer.supportsDisplayEntities()) {
                return;
            }
            ItemStack mainHandItem = HumanHand.getItemInMainHand((HumanEntity)this.viewer.getPlayer());
            if (mainHandItem == null || mainHandItem.getType() != LEVER_TYPE) {
                this.resetAndClearViewedBlock();
                return;
            }
            BlockRayTrace rayTrace = BlockRayTrace.fromEyeOf((LivingEntity)this.viewer.getPlayer());
            if (this.lastRayTrace != null && !ActionSignHighlighter.isRayTraceDifferent(this.lastRayTrace, rayTrace)) {
                return;
            }
            this.lastRayTrace = rayTrace;
            BlockRayTrace.HitResult hit = rayTrace.rayTrace();
            if (hit == null) {
                this.lastHitResult = null;
                this.lastHighlightedBlock = null;
                this.lastHighlightedFace = null;
                this.clearViewedBlock();
                return;
            }
            if (this.lastHitResult != null && this.lastHitResult.getHitBlock().equals((Object)hit.getHitBlock()) && this.lastHitResult.getHitFace() == hit.getHitFace()) {
                return;
            }
            this.lastHitResult = hit;
            Block highlightedBlock = hit.getHitBlock();
            BlockFace highlightedFace = hit.getHitFace();
            BlockData blockDataAtFace = WorldUtil.getBlockData((Block)highlightedBlock);
            if (blockDataAtFace.getType() == LEVER_TYPE) {
                highlightedFace = blockDataAtFace.getAttachedFace();
                highlightedBlock = highlightedBlock.getRelative(highlightedFace);
                highlightedFace = highlightedFace.getOppositeFace();
            } else {
                blockDataAtFace = WorldUtil.getBlockData((Block)highlightedBlock.getRelative(highlightedFace));
            }
            if (this.lastHighlightedBlock != null && this.lastHighlightedBlock.equals((Object)highlightedBlock) && this.lastHighlightedFace == highlightedFace) {
                return;
            }
            this.lastHighlightedBlock = highlightedBlock;
            this.lastHighlightedFace = highlightedFace;
            if (blockDataAtFace.getType() == LEVER_TYPE) {
                if (blockDataAtFace.getAttachedFace() != highlightedFace.getOppositeFace()) {
                    this.clearViewedBlock();
                    return;
                }
            } else if (blockDataAtFace.getType() != Material.AIR) {
                this.clearViewedBlock();
                return;
            }
            ListCallbackCollector<HighlightedSign> highlightedSignsTmp = new ListCallbackCollector<HighlightedSign>();
            int colorWheelIdx = 0;
            for (RailLookup.TrackedSign sign : ActionSignHighlighter.this.plugin.getTrackedSignLookup().getOutputtingTrackedSigns(highlightedBlock)) {
                String outputDescription;
                SignAction action = sign.getAction();
                if (action == null || (outputDescription = action.getDescriptiveOutputName(sign.createEvent(SignActionType.NONE))) == null) continue;
                ChatColor color = HIGHLIGHT_COLORS[colorWheelIdx++ % HIGHLIGHT_COLORS.length];
                highlightedSignsTmp.accept(new HighlightedSign(sign, color, outputDescription));
            }
            List<HighlightedSign> highlightedSigns = highlightedSignsTmp.result();
            if (highlightedSigns.isEmpty()) {
                this.clearViewedBlock();
                return;
            }
            ViewedBlock lastViewedBlockTmp = this.lastViewedBlock;
            this.lastViewedBlock = new ViewedBlock(highlightedBlock, highlightedFace, blockDataAtFace, highlightedSigns);
            this.onViewedBlockChanged(lastViewedBlockTmp, this.lastViewedBlock);
        }
    }

    private final class ViewedBlock {
        public final Block block;
        public final BlockFace face;
        public final BlockData blockDataAtFace;
        public final List<HighlightedSign> highlightedSigns;
        VirtualDisplayBlockEntity highlightLeverPos;
        VirtualDisplayTextEntity signDisplay;

        public ViewedBlock(Block block, BlockFace face, BlockData blockDataAtFace, List<HighlightedSign> highlightedSigns) {
            this.block = block;
            this.face = face;
            this.blockDataAtFace = blockDataAtFace;
            this.highlightedSigns = highlightedSigns;
        }

        public void hide(AttachmentViewer viewer) {
            if (this.highlightLeverPos != null) {
                this.highlightLeverPos.destroy(viewer);
            }
            if (this.signDisplay != null) {
                this.signDisplay.destroy(viewer);
            }
            this.highlightedSigns.forEach(HighlightedSign::hideDebug);
        }

        public void show(AttachmentViewer viewer) {
            BlockFace labelFace = BlockFace.SELF;
            if (this.canShowLabel(BlockFace.UP)) {
                labelFace = BlockFace.UP;
            } else if (this.canShowLabel(this.face)) {
                labelFace = this.face;
            } else if (this.canShowLabel(FaceUtil.rotate((BlockFace)this.face, (int)2))) {
                labelFace = FaceUtil.rotate((BlockFace)this.face, (int)2);
            } else if (this.canShowLabel(FaceUtil.rotate((BlockFace)this.face, (int)-2))) {
                labelFace = FaceUtil.rotate((BlockFace)this.face, (int)-2);
            } else if (this.canShowLabel(BlockFace.DOWN)) {
                labelFace = BlockFace.DOWN;
            }
            if (this.blockDataAtFace.getType() == LEVER_TYPE) {
                Matrix4x4 m = new Matrix4x4();
                m.translate(this.block.getRelative(this.face).getLocation().toVector());
                m.translate(0.5, 0.0, 0.5);
                this.highlightLeverPos = new VirtualDisplayBlockEntity(null);
                this.highlightLeverPos.updatePosition(m);
                this.highlightLeverPos.setBlockData(this.blockDataAtFace);
            } else {
                Vector s2 = new Vector(0.5, 0.5, 0.5);
                Vector d = new Vector(0.5, -0.5, -0.01);
                Matrix4x4 m = new Matrix4x4();
                m.translate(this.block.getLocation().toVector());
                m.translate(0.5, 0.5, 0.5);
                m.translate(FaceUtil.faceToVector((BlockFace)this.face).multiply(0.5));
                m.rotate(Quaternion.fromLookDirection((Vector)FaceUtil.faceToVector((BlockFace)this.face.getOppositeFace())));
                d = d.clone().multiply(s2);
                m.translate(d);
                this.highlightLeverPos = new VirtualDisplayBlockEntity(null);
                this.highlightLeverPos.updatePosition(m);
                this.highlightLeverPos.setBlockData(BlockData.fromMaterial((Material)Material.LEVER));
                this.highlightLeverPos.setScale(s2);
            }
            this.highlightLeverPos.setGlowColor(ChatColor.RED);
            this.highlightLeverPos.setBrightness(Brightness.FULL_ALL);
            this.highlightLeverPos.spawn(viewer, new Vector());
            this.signDisplay = new VirtualDisplayTextEntity(null);
            Vector labelPosition = this.block.getLocation().toVector();
            MathUtil.addToVector((Vector)labelPosition, (double)0.5, (double)0.5, (double)0.5);
            labelPosition.add(FaceUtil.faceToVector((BlockFace)this.face));
            labelPosition.add(FaceUtil.faceToVector((BlockFace)labelFace).multiply(0.5));
            Matrix4x4 m = new Matrix4x4();
            m.translate(labelPosition);
            this.signDisplay.updatePosition(m);
            this.signDisplay.getMetadata().set(DisplayHandle.DATA_BILLBOARD_RENDER_CONSTRAINTS, (Object)3);
            this.signDisplay.setScale(new Vector(0.25, 0.25, 0.25));
            this.signDisplay.setText(ChatText.fromMessage((String)this.highlightedSigns.stream().map(s -> s.highlightColor + s.outputDescription).collect(Collectors.joining("\n"))));
            this.signDisplay.setBackgroundColor(Color.fromARGB((int)128, (int)64, (int)64, (int)64));
            this.signDisplay.setBrightness(Brightness.FULL_ALL);
            this.signDisplay.spawn(viewer, new Vector());
            this.highlightedSigns.forEach(s -> s.showDebug(viewer));
        }

        private boolean canShowLabel(BlockFace face) {
            Block block = this.block.getRelative(this.face).getRelative(face);
            return block.getType() == Material.AIR;
        }
    }

    private static final class HighlightedSign {
        public final RailLookup.TrackedSign sign;
        public final ChatColor highlightColor;
        public final String outputDescription;
        public Runnable despawnHighlightCallback = () -> {};

        public HighlightedSign(RailLookup.TrackedSign sign, ChatColor highlightColor, String outputDescription) {
            this.sign = sign;
            this.highlightColor = highlightColor;
            this.outputDescription = outputDescription;
        }

        public void showDebug(final AttachmentViewer viewer) {
            this.despawnHighlightCallback = this.sign.showDebugHighlight(viewer, new RailLookup.TrackedSign.DebugDisplayOptions(){
                final /* synthetic */ HighlightedSign this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public ChatColor getTeamColor() {
                    return this.this$0.highlightColor;
                }

                @Override
                public TrainCarts getTrainCarts() {
                    return viewer.getTrainCarts();
                }
            });
        }

        public void hideDebug() {
            this.despawnHighlightCallback.run();
        }
    }
}

