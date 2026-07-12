/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentInternalState;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentNameLookup;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.ObjectPosition;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface Attachment
extends AttachmentNameLookup.Supplier {
    public AttachmentInternalState getInternalState();

    default public AttachmentManager getManager() {
        return this.getInternalState().manager;
    }

    default public ConfigurationNode getConfig() {
        return this.getInternalState().config;
    }

    default public Set<String> getNames() {
        return this.getInternalState().names;
    }

    default public Plugin getPlugin() {
        return this.getInternalState().plugin;
    }

    public void onAttached();

    public void onDetached();

    public void onLoad(ConfigurationNode var1);

    default public boolean checkCanReload(ConfigurationNode config) {
        return true;
    }

    public void onTransformChanged(Matrix4x4 var1);

    public void onTick();

    public void onMove(boolean var1);

    default public boolean isAttached() {
        return this.getInternalState().attached;
    }

    default public boolean isFocused() {
        return this.getInternalState().focused;
    }

    default public void setFocused(boolean focused) {
        AttachmentInternalState state = this.getInternalState();
        if (state.focused != focused) {
            state.focused = focused;
            if (state.attached) {
                if (focused) {
                    this.onFocus();
                } else {
                    this.onBlur();
                }
            }
        }
    }

    default public void onFocus() {
    }

    default public void onBlur() {
    }

    default public void onActiveChanged(boolean active) {
    }

    public void makeVisible(Player var1);

    default public void makeVisible(AttachmentViewer viewer) {
        this.makeVisible(viewer.getPlayer());
    }

    public void makeHidden(Player var1);

    default public void makeHidden(AttachmentViewer viewer) {
        this.makeHidden(viewer.getPlayer());
    }

    default public ObjectPosition getConfiguredPosition() {
        return this.getInternalState().position;
    }

    default public Matrix4x4 getPreviousTransform() {
        AttachmentInternalState state = this.getInternalState();
        return state.last_transform == null ? state.curr_transform : state.last_transform;
    }

    default public Matrix4x4 getTransform() {
        return this.getInternalState().curr_transform;
    }

    default public void addChild(Attachment child) {
        this.getInternalState().children.add(child);
        child.getInternalState().assignParent(child, this);
    }

    default public void addChild(int index, Attachment child) {
        this.getInternalState().children.add(index, child);
        child.getInternalState().assignParent(child, this);
    }

    default public boolean removeChild(Attachment child) {
        if (this.getInternalState().children.remove(child)) {
            child.getInternalState().makeNewSubtree(child);
            return true;
        }
        return false;
    }

    default public List<Attachment> getChildren() {
        return this.getInternalState().children;
    }

    @Override
    default public AttachmentNameLookup getNameLookup() {
        return this.getManager().getNameLookup(this);
    }

    @Override
    default public AttachmentNameLookup getNameLookup(AttachmentSelector.SearchStrategy strategy) {
        return strategy == AttachmentSelector.SearchStrategy.ROOT_CHILDREN ? this.getRootParent().getNameLookup() : this.getNameLookup();
    }

    @Override
    default public Set<Attachment> getSelfFilterOfNameLookup() {
        return Collections.singleton(this);
    }

    default public Attachment getParent() {
        return this.getInternalState().parent;
    }

    default public Attachment getRootParent() {
        return this.getInternalState().rootParent;
    }

    public Collection<Player> getViewers();

    public Collection<AttachmentViewer> getAttachmentViewers();

    default public boolean isHiddenWhenInactive() {
        return true;
    }

    default public boolean isActive() {
        return this.getInternalState().active;
    }

    default public void setActive(boolean active) {
        AttachmentInternalState state = this.getInternalState();
        if (state.active != active) {
            state.active = active;
            if (!HelperMethods.hasInactiveParent(this)) {
                HelperMethods.updateActiveRecursive(this, active, this.getViewers());
            }
        }
    }

    default public void applyPassengerSeatTransform(Matrix4x4 transform) {
    }

    default public void addAnimation(Animation animation) {
        this.getInternalState().animations.put(animation.getOptions().getName(), animation);
    }

    default public List<String> getAnimationNames() {
        return Collections.unmodifiableList(new ArrayList<String>(this.getInternalState().animations.keySet()));
    }

    default public Set<String> getAnimationScenes(String animationName) {
        Animation animation = this.getInternalState().animations.get(animationName);
        return animation == null ? Collections.emptySet() : animation.getSceneNames();
    }

    default public List<String> getAnimationNamesRecursive() {
        LinkedHashSet<String> tmp = new LinkedHashSet<String>();
        HelperMethods.addAnimationNamesToSetRecursive(tmp, this);
        return Collections.unmodifiableList(new ArrayList<String>(tmp));
    }

    default public Set<String> getAnimationScenesRecursive(String animationName) {
        LinkedHashSet<String> tmp = new LinkedHashSet<String>();
        HelperMethods.addAnimationScenesToSetRecursive(tmp, animationName, this);
        return Collections.unmodifiableSet(tmp);
    }

    default public Collection<Animation> getAnimations() {
        return this.getInternalState().animations.values();
    }

    default public void clearAnimations() {
        this.getInternalState().animations.clear();
    }

    default public Animation getCurrentAnimation() {
        return this.getInternalState().currentAnimation;
    }

    default public void stopAnimation() {
        AttachmentInternalState state = this.getInternalState();
        state.currentAnimation = null;
        state.nextAnimationQueue = Collections.emptyList();
    }

    default public void startAnimation(Animation animation) {
        if (animation == null) {
            this.stopAnimation();
            return;
        }
        AttachmentInternalState state = this.getInternalState();
        if (state.currentAnimation == null || animation.getOptions().getReset()) {
            state.currentAnimation = animation;
            state.currentAnimation.start();
            state.nextAnimationQueue = Collections.emptyList();
        } else if (animation.getOptions().getQueue()) {
            if (state.nextAnimationQueue.isEmpty()) {
                state.nextAnimationQueue = new ArrayList<Animation>(1);
            }
            state.nextAnimationQueue.add(animation);
        } else if (state.currentAnimation.isSame(animation)) {
            state.currentAnimation.setOptions(animation.getOptions().clone());
            state.nextAnimationQueue = Collections.emptyList();
        } else {
            state.currentAnimation = animation;
            state.currentAnimation.start();
            state.nextAnimationQueue = Collections.emptyList();
        }
    }

    default public boolean containsEntityId(int entityId) {
        return false;
    }

    default public boolean playNamedAnimationRecursive(String name) {
        return this.playNamedAnimationRecursive(new AnimationOptions(name));
    }

    default public boolean playNamedAnimationRecursive(AnimationOptions options) {
        return HelperMethods.playAnimationRecursive(this, options);
    }

    default public boolean playNamedAnimation(String name) {
        return this.playNamedAnimation(new AnimationOptions(name));
    }

    default public boolean playNamedAnimation(AnimationOptions options) {
        return HelperMethods.playAnimation(this, options);
    }

    default public Attachment findChild(int[] targetPath) {
        Attachment target = this;
        for (int index : targetPath) {
            List<Attachment> children = target.getChildren();
            if (index < 0 || index >= children.size()) {
                return null;
            }
            target = children.get(index);
        }
        return target;
    }

    default public int[] getPath() {
        Attachment parent = this.getParent();
        if (parent == null) {
            return new int[0];
        }
        int[] result = parent.getPath();
        int len = result.length;
        result = Arrays.copyOf(result, len + 1);
        result[len] = parent.getChildren().indexOf(this);
        return result;
    }

    public static interface TextDisplayAttachment
    extends Attachment {
        public ChatText getDisplayedText();

        public void setDisplayedText(ChatText var1);
    }

    public static interface ItemDisplayAttachment
    extends Attachment {
        public ItemStack getDisplayedItem();

        public void setDisplayedItem(ItemStack var1);
    }

    public static interface EffectAttachment
    extends Attachment,
    EffectSink {
        @Override
        public void playEffect(EffectOptions var1);

        @Override
        public void stopEffect();

        public static class EffectOptions {
            public static final EffectOptions DEFAULT = new EffectOptions(1.0, 1.0);
            private final double volume;
            private final double speed;

            protected EffectOptions(double volume, double speed) {
                this.volume = volume;
                this.speed = speed;
            }

            public double volume() {
                return this.volume;
            }

            public double speed() {
                return this.speed;
            }

            public EffectOptions withVolume(double newVolume) {
                return new EffectOptions(newVolume, this.speed);
            }

            public EffectOptions withSpeed(double newSpeed) {
                return new EffectOptions(this.volume, newSpeed);
            }

            public EffectOptions multiply(double multVolume, double multSpeed) {
                return new EffectOptions(this.volume * multVolume, this.speed * multSpeed);
            }

            public static EffectOptions of(double volume, double speed) {
                return new EffectOptions(volume, speed);
            }
        }
    }

    public static interface EffectSink {
        public static final EffectSink DISABLED_EFFECT_SINK = new EffectSink(){

            @Override
            public void playEffect(EffectAttachment.EffectOptions options) {
            }

            @Override
            public void stopEffect() {
            }
        };

        public void playEffect(EffectAttachment.EffectOptions var1);

        public void stopEffect();

        public static EffectSink combineEffects(final Iterable<? extends EffectAttachment> effectAttachments) {
            return new EffectSink(){

                @Override
                public void playEffect(EffectAttachment.EffectOptions options) {
                    effectAttachments.forEach(e -> e.playEffect(options));
                }

                @Override
                public void stopEffect() {
                    effectAttachments.forEach(EffectAttachment::stopEffect);
                }
            };
        }

        public static EffectSink combineEffects(final Collection<Iterable<? extends EffectAttachment>> effectAttachments) {
            return new EffectSink(){

                @Override
                public void playEffect(EffectAttachment.EffectOptions options) {
                    effectAttachments.forEach(n -> n.forEach(e -> e.playEffect(options)));
                }

                @Override
                public void stopEffect() {
                    effectAttachments.forEach(n -> n.forEach(EffectAttachment::stopEffect));
                }
            };
        }
    }
}

