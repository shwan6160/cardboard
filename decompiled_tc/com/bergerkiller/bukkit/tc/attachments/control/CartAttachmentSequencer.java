/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.controller.Tickable
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.controller.Tickable;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentNameLookup;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelection;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.control.effect.DelayedEffectTask;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.effect.ScheduledEffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.MapWidgetSequencerConfigurationMenu;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.SequencerMode;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.SequencerPlayStatus;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.SequencerType;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionBoolean;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionConstant;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionRegistry;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CartAttachmentSequencer
extends CartAttachment
implements Attachment.EffectAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "SEQUENCER";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/sequencer.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentSequencer();
        }

        @Override
        public void createAppearanceTab(MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            final TransferFunctionHost host = new TransferFunctionHost(){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public TransferFunctionRegistry getRegistry() {
                    return TransferFunction.getRegistry();
                }

                @Override
                public TransferFunctionInput.ReferencedSource registerInputSource(TransferFunctionInput.ReferencedSource source) {
                    return source;
                }

                @Override
                public boolean isSequencer() {
                    return true;
                }

                @Override
                public boolean isAttachment() {
                    return true;
                }

                @Override
                public MinecartMember<?> getMember() {
                    return null;
                }

                @Override
                public Attachment getAttachment() {
                    List<Attachment> attachments = attachment.getAttachments();
                    return attachments.isEmpty() ? null : attachments.get(0);
                }

                @Override
                public TrainCarts getTrainCarts() {
                    return TrainCarts.plugin;
                }
            };
            (tab.addWidget((MapWidget)new MapWidgetSequencerConfigurationMenu(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public ConfigurationNode getConfig() {
                    return attachment.getConfig();
                }

                @Override
                public List<String> getEffectNames(AttachmentSelector<Attachment.EffectAttachment> allSelector) {
                    return AttachmentNameLookup.Supplier.getSelection(allSelector, () -> attachment.getAttachmentConfig().liveAttachmentsOfType(CartAttachmentSequencer.class)).names();
                }

                @Override
                public TransferFunctionHost getTransferFunctionHost() {
                    return host;
                }

                @Override
                public Attachment.EffectSink createEffectSink(AttachmentSelector<Attachment.EffectAttachment> effectSelector) {
                    return Attachment.EffectSink.combineEffects(AttachmentNameLookup.Supplier.getSelection(effectSelector, () -> attachment.getAttachmentsOfType(CartAttachmentSequencer.class)));
                }

                @Override
                public SequencerPlayStatus getPlayStatus() {
                    List<CartAttachmentSequencer> sequencers = attachment.getAttachmentsOfType(CartAttachmentSequencer.class);
                    if (sequencers.isEmpty()) {
                        return SequencerPlayStatus.STOPPED_AUTOMATIC;
                    }
                    if (sequencers.size() == 1) {
                        return sequencers.get(0).getPlayStatus();
                    }
                    for (CartAttachmentSequencer sequencer : sequencers) {
                        SequencerPlayStatus playStatus = sequencer.getPlayStatus();
                        if (!playStatus.isPlaying()) continue;
                        return playStatus;
                    }
                    return sequencers.get(0).getPlayStatus();
                }

                @Override
                public void startPlaying() {
                    attachment.getAttachmentsOfType(CartAttachmentSequencer.class).forEach(a -> a.playEffect(Attachment.EffectAttachment.EffectOptions.DEFAULT));
                }

                @Override
                public void stopPlaying() {
                    attachment.getAttachmentsOfType(CartAttachmentSequencer.class).forEach(CartAttachmentSequencer::stopEffect);
                }
            })).setBounds(-5, 1, 110, 81);
        }
    };
    private static final int STATE_NOT_PLAYING = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_STOP_REQUESTED = 2;
    private static final int STATE_IMMEDIATE_STOP_REQUESTED = 3;
    private final EffectLoop.Player player = TrainCarts.plugin.getEffectLoopPlayerController().createPlayer(20);
    private final SequencerTransferFunctionHost functionHost = new SequencerTransferFunctionHost();
    private final EnumMap<SequencerMode, SequencerGroup> sequencerGroups;
    private SequencerGroup currentGroup;
    private EffectLoop.RunMode runMode = EffectLoop.RunMode.ASYNCHRONOUS;
    private final ConfigLoadedValue<TransferFunction> autoplayFunction = new ConfigLoadedValue<TransferFunctionBoolean>(TransferFunctionBoolean.FALSE);
    private final AtomicInteger playState = new AtomicInteger(0);
    private Attachment.EffectAttachment.EffectOptions playOptions = Attachment.EffectAttachment.EffectOptions.DEFAULT;
    private SequencerPlayStatus autoPlayStatus = SequencerPlayStatus.STOPPED_AUTOMATIC;
    private SequencerPlayStatus playStatus = SequencerPlayStatus.STOPPED_AUTOMATIC;

    public CartAttachmentSequencer() {
        this.sequencerGroups = new EnumMap(SequencerMode.class);
        for (SequencerMode mode : SequencerMode.values()) {
            this.sequencerGroups.put(mode, new SequencerGroup(this, mode));
        }
        this.currentGroup = this.sequencerGroups.get((Object)SequencerMode.START);
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        this.runMode = (EffectLoop.RunMode)((Object)config.getOrDefault("runMode", (Object)EffectLoop.RunMode.ASYNCHRONOUS));
        this.autoplayFunction.load(config.getNodeIfExists("autoplay"), this.functionHost::loadFunction);
        for (SequencerMode mode : SequencerMode.values()) {
            this.sequencerGroups.get((Object)mode).load(config.getNodeIfExists(mode.configKey()));
        }
    }

    @Override
    public void onDetached() {
        this.immediateStop();
        for (SequencerGroup group : this.sequencerGroups.values()) {
            group.onDetached();
        }
    }

    public Attachment.EffectAttachment.EffectOptions getCurrentPlayOptions() {
        return this.playOptions;
    }

    public double getProgression() {
        return Math.min(1.0, (double)this.currentGroup.nanosElapsed / (double)((SequencerGroup)this.currentGroup).duration.nanos);
    }

    public SequencerPlayStatus getPlayStatus() {
        return this.playStatus;
    }

    @Override
    public void playEffect(Attachment.EffectAttachment.EffectOptions options) {
        this.playOptions = options;
        this.updatePlayStatus(SequencerPlayStatus.PLAYING_MANUAL);
    }

    @Override
    public void stopEffect() {
        this.updatePlayStatus(SequencerPlayStatus.STOPPED_MANUAL);
    }

    private void updatePlayStatus(SequencerPlayStatus status) {
        this.playStatus = status;
        if (status.isPlaying()) {
            int prevState = this.playState.getAndSet(1);
            if (prevState == 0) {
                new ActiveEffectLoop(this.player, this.runMode).play();
            }
        } else {
            this.playState.compareAndSet(1, 2);
        }
    }

    private void immediateStop() {
        this.autoPlayStatus = SequencerPlayStatus.STOPPED_AUTOMATIC;
        this.playStatus = SequencerPlayStatus.STOPPED_AUTOMATIC;
        this.playState.compareAndSet(1, 3);
        this.playState.compareAndSet(2, 3);
    }

    @Override
    public void makeVisible(Player viewer) {
    }

    @Override
    public void makeHidden(Player viewer) {
    }

    @Override
    public void onTick() {
        SequencerPlayStatus currAutoPlayStatus;
        MinecartMember<?> member = this.getMember();
        if (member == null || member.isUnloaded()) {
            if (this.playStatus.isPlaying()) {
                this.immediateStop();
            }
            return;
        }
        SequencerPlayStatus sequencerPlayStatus = currAutoPlayStatus = this.autoplayFunction.get().map(0.0) != 0.0 ? SequencerPlayStatus.PLAYING_AUTOMATIC : SequencerPlayStatus.STOPPED_AUTOMATIC;
        if (this.autoPlayStatus != currAutoPlayStatus) {
            this.autoPlayStatus = currAutoPlayStatus;
            this.updatePlayStatus(currAutoPlayStatus);
        }
        this.sequencerGroups.values().forEach(SequencerGroup::onTick);
        this.functionHost.sources.removeIf(s -> {
            if (s.hasRecipients()) {
                if (!s.isTickedDuringPlay()) {
                    s.onTick();
                }
                return false;
            }
            this.functionHost.onSourceRemoved((TransferFunctionInput.ReferencedSource)s);
            return true;
        });
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        this.functionHost.sources.forEach(s -> s.onTransform(transform));
    }

    @Override
    public void onMove(boolean absolute) {
    }

    public class SequencerTransferFunctionHost
    implements TransferFunctionHost {
        private final List<TransferFunctionInput.ReferencedSource> sources = new ArrayList<TransferFunctionInput.ReferencedSource>();
        private List<TransferFunctionInput.ReferencedSource> sourcesTickedDuringPlay = Collections.emptyList();

        @Override
        public TrainCarts getTrainCarts() {
            return TrainCarts.plugin;
        }

        @Override
        public TransferFunctionRegistry getRegistry() {
            return TransferFunction.getRegistry();
        }

        public void tickPlaySources() {
            this.sourcesTickedDuringPlay.forEach(TransferFunctionInput.ReferencedSource::onTick);
        }

        public void onSourceRemoved(TransferFunctionInput.ReferencedSource source) {
            int idx = this.sourcesTickedDuringPlay.indexOf(source);
            if (idx != -1) {
                ArrayList<TransferFunctionInput.ReferencedSource> newList = new ArrayList<TransferFunctionInput.ReferencedSource>(this.sourcesTickedDuringPlay);
                newList.remove(idx);
                this.sourcesTickedDuringPlay = newList;
            }
        }

        @Override
        public TransferFunctionInput.ReferencedSource registerInputSource(TransferFunctionInput.ReferencedSource source) {
            int index = this.sources.indexOf(source);
            if (index == -1) {
                this.sources.add(source);
                if (source.isTickedDuringPlay()) {
                    ArrayList<TransferFunctionInput.ReferencedSource> newList = new ArrayList<TransferFunctionInput.ReferencedSource>(this.sourcesTickedDuringPlay);
                    newList.add(source);
                    this.sourcesTickedDuringPlay = newList;
                }
                return source;
            }
            return this.sources.get(index);
        }

        @Override
        public boolean isSequencer() {
            return true;
        }

        @Override
        public boolean isAttachment() {
            return true;
        }

        @Override
        public Attachment getAttachment() {
            return CartAttachmentSequencer.this;
        }

        @Override
        public MinecartMember<?> getMember() {
            return CartAttachmentSequencer.this.getMember();
        }
    }

    public static class SequencerGroup
    implements Tickable {
        private final ConfigLoadedValue<TransferFunction> speedFunction = new ConfigLoadedValue<TransferFunctionConstant>(TransferFunctionConstant.of(1.0));
        private final CartAttachmentSequencer sequencer;
        private final SequencerMode mode;
        private EffectLoop.Time duration = EffectLoop.Time.ZERO;
        private long nanosElapsed = 0L;
        private boolean interruptPlay = false;
        private final Map<ConfigurationNode, SequencerEffect> effectsByConfig = new IdentityHashMap<ConfigurationNode, SequencerEffect>();
        private List<SequencerEffect> effects = Collections.emptyList();

        public SequencerGroup(CartAttachmentSequencer sequencer, SequencerMode mode) {
            this.sequencer = sequencer;
            this.mode = mode;
        }

        public SequencerMode mode() {
            return this.mode;
        }

        public void onDetached() {
            this.effects.forEach(SequencerEffect::onRemoved);
        }

        public void load(ConfigurationNode config) {
            List effectConfigs;
            if (config == null || config.isEmpty()) {
                this.speedFunction.reset();
                this.duration = EffectLoop.Time.ZERO;
                this.interruptPlay = false;
                this.nanosElapsed = 0L;
                this.effects.forEach(SequencerEffect::onRemoved);
                this.effects = Collections.emptyList();
                this.effectsByConfig.clear();
                return;
            }
            this.speedFunction.load(config.getNodeIfExists("speed"), this.sequencer.functionHost::loadFunction);
            this.duration = EffectLoop.Time.seconds(Math.max(0.0, (Double)config.getOrDefault("duration", (Object)0.0)));
            this.interruptPlay = (Boolean)config.getOrDefault("interrupt", (Object)false);
            if (this.duration.isZero() || (effectConfigs = config.getNodeList("effects")).isEmpty()) {
                this.effects = Collections.emptyList();
            } else {
                ArrayList<SequencerEffect> newEffects = new ArrayList<SequencerEffect>(effectConfigs.size());
                for (ConfigurationNode effectConfig : effectConfigs) {
                    SequencerEffect effect = this.effectsByConfig.remove(effectConfig);
                    if (effect == null) {
                        effect = new SequencerEffect(this);
                    }
                    effect.load(this.sequencer, effectConfig);
                    newEffects.add(effect);
                }
                this.effectsByConfig.values().forEach(SequencerEffect::onRemoved);
                this.effectsByConfig.clear();
                for (int i = 0; i < newEffects.size(); ++i) {
                    this.effectsByConfig.put((ConfigurationNode)effectConfigs.get(i), (SequencerEffect)newEffects.get(i));
                }
                this.effects = newEffects;
            }
        }

        public void onTick() {
            this.effects.forEach(SequencerEffect::onTick);
        }

        public EffectLoop.Time advance(EffectLoop.Time dt, boolean stopRequested) {
            long durationNanos = this.duration.nanos;
            if (stopRequested && this.interruptPlay) {
                return dt;
            }
            if (durationNanos == 0L) {
                return stopRequested || this.mode != SequencerMode.LOOP ? dt : EffectLoop.Time.ZERO;
            }
            this.sequencer.functionHost.tickPlaySources();
            double speed = this.speedFunction.get().map(0.0);
            this.effects.forEach(SequencerEffect::updateEffectLoop);
            if (speed <= 1.0E-6) {
                return EffectLoop.Time.ZERO;
            }
            EffectLoop.Time dt_adjusted = speed == 1.0 ? dt : dt.multiply(speed);
            long prev_time_nanos = this.nanosElapsed;
            long curr_time_nanos = prev_time_nanos + dt_adjusted.nanos;
            if (curr_time_nanos <= durationNanos) {
                this.advanceAllEffects(prev_time_nanos, curr_time_nanos);
                return EffectLoop.Time.ZERO;
            }
            if (this.mode == SequencerMode.LOOP && !stopRequested) {
                long remainder = curr_time_nanos - durationNanos;
                if (remainder >= durationNanos) {
                    remainder %= durationNanos;
                }
                this.advanceAllEffects(prev_time_nanos, durationNanos);
                this.advanceAllEffects(0L, remainder);
                return EffectLoop.Time.ZERO;
            }
            this.advanceAllEffects(prev_time_nanos, durationNanos);
            return EffectLoop.Time.nanos(Math.max(1L, (long)((double)(curr_time_nanos - durationNanos) / speed)));
        }

        private void advanceAllEffects(long prevNanos, long currNanos) {
            this.effects.forEach(e -> ((ScheduledEffectLoop)((SequencerEffect)e).effectLoop.get()).advance(prevNanos, currNanos));
            this.nanosElapsed = currNanos;
        }

        public void resetToBeginning() {
            this.nanosElapsed = 0L;
        }
    }

    private static class ConfigLoadedValue<T> {
        private final T defaultValue;
        private ConfigurationNode previousConfig = null;
        private T value;

        public ConfigLoadedValue(T defaultValue) {
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public T get() {
            return this.value;
        }

        public void reset() {
            this.previousConfig = null;
            this.value = this.defaultValue;
        }

        public void forceLoad(ConfigurationNode config, Function<ConfigurationNode, T> loader) {
            if (config != null) {
                this.previousConfig = config.clone();
                this.value = loader.apply(config);
            } else {
                this.reset();
            }
        }

        public void load(ConfigurationNode config, Function<ConfigurationNode, T> loader) {
            if (this.previousConfig == null) {
                if (config != null) {
                    this.forceLoad(config, loader);
                }
            } else if (config == null || !this.previousConfig.equals((Object)config)) {
                this.forceLoad(config, loader);
            }
        }
    }

    private class ActiveEffectLoop
    implements EffectLoop {
        private final EffectLoop.Player player;
        private final EffectLoop.RunMode runMode;
        private boolean stopped;

        public ActiveEffectLoop(EffectLoop.Player player, EffectLoop.RunMode runMode) {
            this.player = player;
            this.runMode = runMode;
            this.stopped = false;
        }

        public void play() {
            this.player.play(this, this.runMode);
        }

        @Override
        public boolean advance(EffectLoop.Time dt, EffectLoop.Time duration, boolean loop) {
            if (this.stopped) {
                return false;
            }
            if (!this.advanceGroups(dt)) {
                this.stopped = true;
                return false;
            }
            EffectLoop.RunMode currentMode = CartAttachmentSequencer.this.runMode;
            if (currentMode != this.runMode) {
                this.stopped = true;
                new ActiveEffectLoop(this.player, currentMode).play();
                return false;
            }
            return true;
        }

        public boolean advanceGroups(EffectLoop.Time dt) {
            int currState = CartAttachmentSequencer.this.playState.get();
            if (currState == 0) {
                return false;
            }
            if (currState == 3) {
                CartAttachmentSequencer.this.currentGroup = (SequencerGroup)CartAttachmentSequencer.this.sequencerGroups.get((Object)SequencerMode.START);
                CartAttachmentSequencer.this.currentGroup.resetToBeginning();
                return !CartAttachmentSequencer.this.playState.compareAndSet(3, 0) && !CartAttachmentSequencer.this.playState.compareAndSet(2, 0);
            }
            if (currState == 2) {
                while (true) {
                    if ((dt = CartAttachmentSequencer.this.currentGroup.advance(dt, CartAttachmentSequencer.this.currentGroup.mode() != SequencerMode.STOP)).isZero()) {
                        return true;
                    }
                    if (CartAttachmentSequencer.this.currentGroup.mode() == SequencerMode.STOP) {
                        return !CartAttachmentSequencer.this.playState.compareAndSet(2, 0) && !CartAttachmentSequencer.this.playState.compareAndSet(3, 0);
                    }
                    CartAttachmentSequencer.this.currentGroup = (SequencerGroup)CartAttachmentSequencer.this.sequencerGroups.get((Object)SequencerMode.STOP);
                    CartAttachmentSequencer.this.currentGroup.resetToBeginning();
                }
            }
            while (!(dt = CartAttachmentSequencer.this.currentGroup.advance(dt, CartAttachmentSequencer.this.currentGroup.mode() == SequencerMode.STOP)).isZero()) {
                if (CartAttachmentSequencer.this.currentGroup.mode() == SequencerMode.STOP) {
                    CartAttachmentSequencer.this.currentGroup = (SequencerGroup)CartAttachmentSequencer.this.sequencerGroups.get((Object)SequencerMode.START);
                } else {
                    CartAttachmentSequencer.this.currentGroup = (SequencerGroup)CartAttachmentSequencer.this.sequencerGroups.get((Object)SequencerMode.LOOP);
                }
                CartAttachmentSequencer.this.currentGroup.resetToBeginning();
            }
            return true;
        }
    }

    public static class SequencerEffect
    implements Attachment.EffectSink,
    Tickable {
        private final SequencerGroup group;
        private AttachmentSelection<Attachment.EffectAttachment> effectAttachments = AttachmentSelection.none(Attachment.EffectAttachment.class);
        private final ConfigLoadedValue<TransferFunction> activeFunction = new ConfigLoadedValue<TransferFunctionBoolean>(TransferFunctionBoolean.TRUE);
        private final ConfigLoadedValue<TransferFunction> volumeFunction = new ConfigLoadedValue<TransferFunctionConstant>(TransferFunctionConstant.of(1.0));
        private final ConfigLoadedValue<TransferFunction> pitchFunction = new ConfigLoadedValue<TransferFunctionConstant>(TransferFunctionConstant.of(1.0));
        private final ConfigLoadedValue<ScheduledEffectLoop> effectLoop = new ConfigLoadedValue<ScheduledEffectLoop>(ScheduledEffectLoop.NONE);
        private SequencerType sequencerType = null;
        private boolean active;
        private double volume;
        private double pitch;
        private EffectLoop.Time stopAfterTime = EffectLoop.Time.NEVER;
        private final AtomicReference<DelayedEffectTask> pendingStop = new AtomicReference<Object>(null);

        public SequencerEffect(SequencerGroup group) {
            this.group = group;
        }

        public void onRemoved() {
            DelayedEffectTask task = this.pendingStop.getAndSet(null);
            if (task != null) {
                task.runNow();
            }
        }

        public void onTick() {
            this.effectAttachments.sync();
        }

        public void updateEffectLoop() {
            this.active = this.activeFunction.get().map(0.0) != 0.0;
            this.volume = this.volumeFunction.get().map(0.0);
            this.pitch = this.pitchFunction.get().map(0.0);
        }

        @Override
        public void playEffect(Attachment.EffectAttachment.EffectOptions options) {
            if (this.active) {
                EffectLoop.Time stopAfterTime = this.stopAfterTime;
                if (stopAfterTime.isZero()) {
                    this.effectAttachments.forEach(Attachment.EffectAttachment::stopEffect);
                } else {
                    DelayedEffectTask newTask;
                    DelayedEffectTask prevTask;
                    Attachment.EffectAttachment.EffectOptions adjusted = this.volume != 1.0 || this.pitch != 1.0 ? options.multiply(this.volume, this.pitch) : options;
                    this.effectAttachments.forEach(e -> e.playEffect(adjusted));
                    if (!stopAfterTime.isNever() && (prevTask = this.pendingStop.getAndSet(newTask = this.group.sequencer.player.scheduleTask(stopAfterTime, () -> this.effectAttachments.forEach(Attachment.EffectAttachment::stopEffect), this.group.sequencer.runMode))) != null) {
                        prevTask.cancel();
                    }
                }
            }
        }

        @Override
        public void stopEffect() {
            if (this.active) {
                this.effectAttachments.forEach(Attachment.EffectAttachment::stopEffect);
            }
        }

        public void load(CartAttachmentSequencer sequencer, ConfigurationNode config) {
            this.effectAttachments = sequencer.getSelection(AttachmentSelector.readFromConfig(config, "effect").withType(Attachment.EffectAttachment.class).excludingSelf());
            this.activeFunction.load(config.getNodeIfExists("active"), sequencer.functionHost::loadFunction);
            this.volumeFunction.load(config.getNodeIfExists("volume"), sequencer.functionHost::loadFunction);
            this.pitchFunction.load(config.getNodeIfExists("pitch"), sequencer.functionHost::loadFunction);
            Double stopAfterTimeSeconds = (Double)config.getOrDefault("stopAfter", Double.class, null);
            this.stopAfterTime = stopAfterTimeSeconds != null ? EffectLoop.Time.seconds(stopAfterTimeSeconds) : EffectLoop.Time.NEVER;
            SequencerType newType = SequencerType.byName((String)config.getOrDefault("type", (Object)""));
            if (this.sequencerType != newType) {
                this.sequencerType = newType;
                this.effectLoop.forceLoad(config.getNodeIfExists("config"), c -> this.sequencerType.createEffectLoop((ConfigurationNode)c, this));
            } else {
                this.effectLoop.load(config.getNodeIfExists("config"), (ConfigurationNode c) -> this.sequencerType.createEffectLoop((ConfigurationNode)c, this));
            }
        }
    }
}

