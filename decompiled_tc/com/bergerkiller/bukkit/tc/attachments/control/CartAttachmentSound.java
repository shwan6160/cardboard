/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundCustomSoundPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundStopSoundPacketHandle
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundAutoResumeToggle;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundPerspectiveMode;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundPlayStop;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundPositionMode;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundSelector;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundVolumePitch;
import com.bergerkiller.bukkit.tc.attachments.control.sound.SoundPerspectiveMode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundCustomSoundPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundStopSoundPacketHandle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CartAttachmentSound
extends CartAttachment
implements Attachment.EffectAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "SOUND";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/sound.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentSound();
        }

        @Override
        public void createAppearanceTab(final MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            final MapWidgetSoundSelector soundSelector = (MapWidgetSoundSelector)tab.addWidget((MapWidget)new MapWidgetSoundSelector(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    this.setMode(((SoundPerspectiveMode)((Object)attachment.getConfig().getOrDefault("perspectiveMode", (Object)SoundPerspectiveMode.SAME))).getSoundMode());
                    this.setSoundPath((String)attachment.getConfig().getOrDefault("sound.key", String.class, null));
                    this.setCategory((String)attachment.getConfig().getOrDefault("sound.category", (Object)"master"));
                    super.onAttached();
                }

                @Override
                public void onSoundChanged(ResourceKey<SoundEffect> sound) {
                    attachment.getConfig().set("sound.key", (Object)(sound == null ? null : sound.getPath()));
                }

                @Override
                public void onCategoryChanged(String categoryName) {
                    attachment.getConfig().set("sound.category", (Object)categoryName);
                }
            }.setMode(MapWidgetSoundSelector.Mode.FIRST_PERSPECTIVE));
            soundSelector.setBounds(2, 18, 102, 11);
            final MapWidgetSoundSelector soundSelectorAlt = (MapWidgetSoundSelector)tab.addWidget((MapWidget)new MapWidgetSoundSelector(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    this.setMode(((SoundPerspectiveMode)((Object)attachment.getConfig().getOrDefault("perspectiveMode", (Object)SoundPerspectiveMode.SAME))).getSoundAltMode());
                    this.setSoundPath((String)attachment.getConfig().getOrDefault("soundAlt.key", String.class, null));
                    this.setCategory((String)attachment.getConfig().getOrDefault("soundAlt.category", (Object)"master"));
                    super.onAttached();
                }

                @Override
                public void onSoundChanged(ResourceKey<SoundEffect> sound) {
                    attachment.getConfig().set("soundAlt.key", (Object)(sound == null ? null : sound.getPath()));
                }

                @Override
                public void onCategoryChanged(String categoryName) {
                    attachment.getConfig().set("soundAlt.category", (Object)categoryName);
                }
            }.setMode(MapWidgetSoundSelector.Mode.THIRD_PERSPECTIVE));
            soundSelectorAlt.setBounds(2, 32, 102, 11);
            tab.addWidget(new MapWidgetSoundPerspectiveMode(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.setMode((SoundPerspectiveMode)((Object)attachment.getConfig().getOrDefault("perspectiveMode", (Object)SoundPerspectiveMode.SAME)));
                    super.onAttached();
                }

                @Override
                public void onModeChanged(SoundPerspectiveMode newMode) {
                    attachment.getConfig().set("perspectiveMode", (Object)newMode);
                    soundSelector.setMode(newMode.getSoundMode());
                    soundSelectorAlt.setMode(newMode.getSoundAltMode());
                    for (MapWidget widget : tab.getWidgets()) {
                        if (!(widget instanceof MapWidgetSoundPositionMode)) continue;
                        ((MapWidgetSoundPositionMode)widget).setIsSamePerspective(newMode == SoundPerspectiveMode.SAME);
                    }
                }
            }.setPosition(9, 3));
            tab.addWidget(new MapWidgetSoundAutoResumeToggle(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.setAutoResume((Boolean)attachment.getConfig().getOrDefault("autoResume", (Object)false));
                    super.onAttached();
                }

                @Override
                public void onAutoResumeChanged(boolean autoResume) {
                    attachment.getConfig().set("autoResume", (Object)autoResume);
                }
            }.setPosition(21, 3));
            tab.addWidget(new MapWidgetSoundPositionMode(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    SoundPerspectiveMode perspective = (SoundPerspectiveMode)((Object)attachment.getConfig().getOrDefault("perspectiveMode", (Object)SoundPerspectiveMode.SAME));
                    this.setIsSamePerspective(perspective == SoundPerspectiveMode.SAME);
                    this.setMode((Boolean)attachment.getConfig().getOrDefault("sound.atPlayer", (Object)false), (Boolean)attachment.getConfig().getOrDefault("soundAlt.atPlayer", (Object)false));
                    super.onAttached();
                }

                @Override
                public void onModeChanged(MapWidgetSoundPositionMode.SoundPositionMode newMode) {
                    attachment.getConfig().set("sound.atPlayer", (Object)newMode.isAtPlayer1P());
                    attachment.getConfig().set("soundAlt.atPlayer", (Object)newMode.isAtPlayer3P());
                }
            }.setPosition(33, 3));
            tab.addWidget(new MapWidgetSoundPlayStop(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onPlay() {
                    attachment.getAttachmentsOfType(CartAttachmentSound.class).forEach(a -> a.playEffect(Attachment.EffectAttachment.EffectOptions.DEFAULT));
                }

                @Override
                public void onStop() {
                    attachment.getAttachmentsOfType(CartAttachmentSound.class).forEach(CartAttachmentSound::stopEffect);
                }
            }.setBounds(81, 3, 24, 11));
            (tab.addWidget((MapWidget)new MapWidgetSoundVolumePitch(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    this.setInitialBaseVolume(((Float)attachment.getConfig().getOrDefault("volume.base", (Object)Float.valueOf(1.0f))).floatValue());
                    this.setInitialRandomVolume(((Float)attachment.getConfig().getOrDefault("volume.random", (Object)Float.valueOf(0.0f))).floatValue());
                    this.setInitialBaseSpeed(((Float)attachment.getConfig().getOrDefault("pitch.base", (Object)Float.valueOf(1.0f))).floatValue());
                    this.setInitialRandomSpeed(((Float)attachment.getConfig().getOrDefault("pitch.random", (Object)Float.valueOf(0.0f))).floatValue());
                    super.onAttached();
                }

                @Override
                public void onChanged() {
                    if (this.getBaseVolume() == 1.0 && this.getRandomVolume() == 0.0) {
                        attachment.getConfig().remove("volume");
                    } else {
                        attachment.getConfig().set("volume.base", (Object)this.getBaseVolume());
                        attachment.getConfig().set("volume.random", (Object)this.getRandomVolume());
                    }
                    if (this.getBaseSpeed() == 1.0 && this.getRandomSpeed() == 0.0) {
                        attachment.getConfig().remove("pitch");
                    } else {
                        attachment.getConfig().set("pitch.base", (Object)this.getBaseSpeed());
                        attachment.getConfig().set("pitch.random", (Object)this.getRandomSpeed());
                    }
                }
            })).setBounds(-3, 47, 107, 30);
        }
    };
    private final SoundListeners listeners = new SoundListeners();
    private SoundConfiguration sound = SoundConfiguration.NO_CONFIG;

    @Override
    public void onLoad(ConfigurationNode config) {
        SoundConfiguration newSound = new SoundConfiguration(config);
        boolean refreshSounds = !SoundConfiguration.isSameSounds(this.sound, newSound);
        this.sound = newSound;
        this.listeners.updateListeners(this, this.sound, refreshSounds);
    }

    @Override
    public void makeVisible(Player viewer) {
        this.makeVisible(this.getManager().asAttachmentViewer(viewer));
    }

    @Override
    public void makeHidden(Player viewer) {
        this.makeHidden(this.getManager().asAttachmentViewer(viewer));
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        this.listeners.addListener(this, this.sound, viewer);
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        this.listeners.removeListener(this, this.sound, viewer);
    }

    @Override
    public void playEffect(Attachment.EffectAttachment.EffectOptions effectOptions) {
        this.listeners.play(this.sound.createVolumePitch(effectOptions));
    }

    @Override
    public void stopEffect() {
        this.listeners.stop();
    }

    @Override
    public void onTick() {
        this.listeners.updateListeners(this, this.sound, false);
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        this.listeners.updateLoc(transform, this.getManager().getWorld());
    }

    @Override
    public void onMove(boolean absolute) {
    }

    private static class SoundListeners {
        private final List<SoundListener> listeners = new ArrayList<SoundListener>();
        private VolumePitch lastVolumePitch = VolumePitch.SILENT;
        private Location loc = null;

        private SoundListeners() {
        }

        public synchronized void play(VolumePitch volumePitch) {
            this.lastVolumePitch = volumePitch;
            if (volumePitch.silent) {
                return;
            }
            Location loc = this.loc;
            if (loc != null) {
                for (SoundListener listener : this.listeners) {
                    listener.play(loc, volumePitch);
                }
            }
        }

        public synchronized void stop() {
            this.lastVolumePitch = VolumePitch.SILENT;
            this.listeners.forEach(SoundListener::stop);
        }

        public synchronized void addListener(CartAttachmentSound sound, SoundConfiguration config, AttachmentViewer viewer) {
            for (SoundListener listener : this.listeners) {
                if (!listener.viewer.equals(viewer)) continue;
                return;
            }
            boolean isAlt = this.detectIsAlt(sound, config, viewer);
            this.listeners.add(new SoundListener(viewer, isAlt, config.sound(isAlt)));
        }

        public synchronized void removeListener(CartAttachmentSound sound, SoundConfiguration config, AttachmentViewer viewer) {
            Iterator<SoundListener> iter = this.listeners.iterator();
            while (iter.hasNext()) {
                SoundListener listener = iter.next();
                if (!viewer.equals(listener.viewer)) continue;
                iter.remove();
                if (config.autoResume) {
                    listener.stop();
                }
                return;
            }
        }

        public void updateLoc(Matrix4x4 transform, World world) {
            this.loc = transform.toLocation(world);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void updateListeners(CartAttachmentSound sound, SoundConfiguration config, boolean forceRefreshSounds) {
            for (SoundListener listener : this.listeners) {
                boolean isAlt = this.detectIsAlt(sound, config, listener.viewer);
                if (!forceRefreshSounds && isAlt == listener.isAlt) continue;
                SoundListeners soundListeners = this;
                synchronized (soundListeners) {
                    Location loc;
                    if (config.autoResume) {
                        listener.stop();
                    }
                    listener.isAlt = isAlt;
                    listener.sound = config.sound(isAlt);
                    if (config.autoResume && !this.lastVolumePitch.silent && (loc = this.loc) != null) {
                        listener.playResume(loc, this.lastVolumePitch);
                    }
                }
            }
        }

        private boolean detectIsAlt(CartAttachmentSound sound, SoundConfiguration config, AttachmentViewer viewer) {
            if (config.perspectiveMode == SoundPerspectiveMode.SAME) {
                return false;
            }
            MinecartMember<?> member = MinecartMemberStore.getFromEntity(viewer.getPlayer().getVehicle());
            if (member == null || member.isUnloaded()) {
                return true;
            }
            MinecartMember<?> soundMember = sound.getMember();
            if (soundMember == null || soundMember.isUnloaded()) {
                return true;
            }
            switch (config.perspectiveMode) {
                case CART: {
                    return member != soundMember;
                }
                case TRAIN: {
                    return member.getGroup() != soundMember.getGroup();
                }
                case SEAT: {
                    if (member == soundMember) {
                        for (Attachment a = sound.getParent(); a != null; a = a.getParent()) {
                            if (!(a instanceof CartAttachmentSeat) || ((CartAttachmentSeat)a).getEntity() != viewer.getPlayer()) continue;
                            return false;
                        }
                    }
                    return true;
                }
            }
            return true;
        }
    }

    private static class SoundConfiguration {
        public static final SoundConfiguration NO_CONFIG = new SoundConfiguration(new ConfigurationNode());
        public final SoundType sound;
        public final SoundType soundAlt;
        public final SoundPerspectiveMode perspectiveMode;
        public final boolean autoResume;
        public final VariableFloatRange volume;
        public final VariableFloatRange pitch;

        public SoundConfiguration(ConfigurationNode config) {
            this.sound = new SoundType(config.getNodeIfExists("sound"));
            this.soundAlt = new SoundType(config.getNodeIfExists("soundAlt"));
            this.perspectiveMode = SoundType.isSameSound(this.sound, this.soundAlt) ? SoundPerspectiveMode.SAME : (SoundPerspectiveMode)((Object)config.getOrDefault("perspectiveMode", (Object)SoundPerspectiveMode.SAME));
            this.autoResume = (Boolean)config.getOrDefault("autoResume", (Object)false);
            this.volume = VariableFloatRange.decode(config.getNodeIfExists("volume"));
            this.pitch = VariableFloatRange.decode(config.getNodeIfExists("pitch"));
        }

        public SoundType sound(boolean isAlt) {
            return isAlt ? this.soundAlt : this.sound;
        }

        public VolumePitch createVolumePitch(Attachment.EffectAttachment.EffectOptions effectOptions) {
            return new VolumePitch((float)(effectOptions.volume() * (double)this.volume.next()), (float)(effectOptions.speed() * (double)this.pitch.next()));
        }

        public static boolean isSameSounds(SoundConfiguration a, SoundConfiguration b) {
            return SoundType.isSameSound(a.sound, b.sound) && SoundType.isSameSound(a.soundAlt, b.soundAlt);
        }
    }

    private static class VolumePitch {
        public static final VolumePitch SILENT = new VolumePitch(0.0f, 1.0f);
        public final float volume;
        public final float pitch;
        public final boolean silent;

        public VolumePitch(float volume, float pitch) {
            this.volume = volume;
            this.silent = volume < 1.0E-4f;
            this.pitch = pitch;
        }
    }

    private static class RandomFloat
    implements VariableFloatRange {
        private final Random random = new Random();
        private final float base;
        private final float mult;

        public RandomFloat(float base, float mult) {
            this.base = base - mult;
            this.mult = 2.0f * mult;
        }

        @Override
        public float next() {
            return this.base + this.random.nextFloat(this.mult);
        }
    }

    @FunctionalInterface
    private static interface VariableFloatRange {
        public static final VariableFloatRange DEFAULT = () -> 1.0f;

        public float next();

        public static VariableFloatRange decode(ConfigurationNode node) {
            return node == null ? DEFAULT : VariableFloatRange.get(((Float)node.getOrDefault("base", (Object)Float.valueOf(1.0f))).floatValue(), ((Float)node.getOrDefault("random", (Object)Float.valueOf(0.0f))).floatValue());
        }

        public static VariableFloatRange get(float base, float random) {
            if (random < 1.0E-4f) {
                return Math.abs(base - 1.0f) < 1.0E-4f ? DEFAULT : () -> base;
            }
            return new RandomFloat(base, random);
        }
    }

    private static class SoundListener {
        public final AttachmentViewer viewer;
        public final AtomicBoolean isResumingPlay;
        public boolean isAlt;
        public SoundType sound;

        public SoundListener(AttachmentViewer viewer, boolean isAlt, SoundType sound) {
            this.viewer = viewer;
            this.isResumingPlay = new AtomicBoolean(false);
            this.isAlt = isAlt;
            this.sound = sound;
        }

        public void stop() {
            this.isResumingPlay.set(false);
            this.sound.stop(this.viewer);
        }

        public void play(Location location, VolumePitch volumePitch) {
            SoundType sound = this.sound;
            if (this.isResumingPlay.compareAndSet(true, false)) {
                sound.stop(this.viewer);
            }
            sound.play(this.viewer, location, volumePitch);
        }

        public void playResume(Location location, VolumePitch volumePitch) {
            this.isResumingPlay.set(true);
            this.sound.play(this.viewer, location, volumePitch);
        }
    }

    private static class SoundType {
        private static final Random RANDOM_SEED_SOURCE = new Random();
        private static final boolean CAN_STOP_SOUND = Common.hasCapability((String)"Common:Sound:StopSoundPacket");
        public final ResourceKey<SoundEffect> key;
        public final String category;
        public final boolean atPlayer;

        public SoundType(ConfigurationNode config) {
            if (config != null) {
                String keyPath = (String)config.getOrDefault("key", String.class, null);
                this.key = keyPath == null ? null : SoundEffect.fromName((String)keyPath);
                this.category = (String)config.getOrDefault("category", (Object)"master");
                this.atPlayer = (Boolean)config.getOrDefault("atPlayer", (Object)false);
            } else {
                this.key = null;
                this.category = "master";
                this.atPlayer = false;
            }
        }

        public boolean exists() {
            return this.key != null;
        }

        public void play(AttachmentViewer viewer, Location location, VolumePitch volumePitch) {
            if (this.key != null) {
                Location at = this.atPlayer ? viewer.getPlayer().getLocation() : location;
                viewer.send((PacketHandle)ClientboundCustomSoundPacketHandle.createNew(this.key, (String)this.category, (double)at.getX(), (double)at.getY(), (double)at.getZ(), (float)volumePitch.volume, (float)volumePitch.pitch, (long)RANDOM_SEED_SOURCE.nextLong()));
            }
        }

        public void stop(AttachmentViewer viewer) {
            if (this.key != null && CAN_STOP_SOUND) {
                this.stopImpl(viewer);
            }
        }

        private void stopImpl(AttachmentViewer viewer) {
            viewer.send((PacketHandle)ClientboundStopSoundPacketHandle.createNew(this.key, (String)this.category));
        }

        public static boolean isSameSound(SoundType a, SoundType b) {
            return LogicUtil.bothNullOrEqual(a.key, b.key) && a.category.equals(b.category) && a.atPlayer == b.atPlayer;
        }
    }
}

