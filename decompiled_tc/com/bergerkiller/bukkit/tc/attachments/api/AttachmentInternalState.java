/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationNode;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.config.ObjectPosition;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.helper.ActiveChangeHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public class AttachmentInternalState {
    protected AttachmentManager manager = null;
    protected Plugin plugin = null;
    protected Attachment rootParent = null;
    protected Attachment parent = null;
    protected List<Attachment> children = new ArrayList<Attachment>(1);
    protected ConfigurationNode config = new ConfigurationNode();
    public Set<String> names = Collections.emptySet();
    public Map<String, Animation> animations = new HashMap<String, Animation>();
    public Animation currentAnimation = null;
    public List<Animation> nextAnimationQueue = Collections.emptyList();
    public AnimationNode lastAnimationState = null;
    protected boolean active = true;
    protected boolean focused = false;
    public boolean attached = false;
    public ObjectPosition position = new ObjectPosition();
    private UpdateTask transformUpdateTask;
    public Matrix4x4 last_transform = null;
    public Matrix4x4 curr_transform = null;

    public void onLoad(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType, ConfigurationNode config) {
        List effectNamesList;
        this.resetEffectsAndAnimations();
        try {
            attachmentType.migrateConfiguration(config);
        }
        catch (Throwable t) {
            attachmentType.getPlugin().getLogger().log(Level.SEVERE, "Failed to migrate attachment configuration of " + attachmentType.getName(), t);
        }
        this.plugin = attachmentType.getPlugin();
        this.config = config;
        this.position.load(managerType, attachmentType, config.getNodeIfExists("position"));
        this.names = config.contains("names") && !(effectNamesList = config.getList("names", String.class)).isEmpty() ? new LinkedHashSet<String>(effectNamesList) : Collections.emptySet();
        if (config.isNode("animations")) {
            ConfigurationNode animations = config.getNode("animations");
            for (ConfigurationNode animationConfig : animations.getNodes()) {
                Animation anim = Animation.loadFromConfig(animationConfig);
                if (anim == null) continue;
                this.animations.put(anim.getOptions().getName(), anim);
                if (!anim.getOptions().isAutoPlay()) continue;
                this.currentAnimation = anim;
            }
        }
    }

    public void reset() {
        this.resetEffectsAndAnimations();
        this.last_transform = null;
        this.curr_transform = null;
        this.transformUpdateTask = null;
    }

    private void resetEffectsAndAnimations() {
        this.animations.clear();
        this.currentAnimation = null;
    }

    protected void assignParent(Attachment self, Attachment parent) {
        this.parent = parent;
        this.rootParent = parent.getRootParent();
        AttachmentInternalState.updateRootParentOfChildrenRecurse(self, this.rootParent);
    }

    protected void makeNewSubtree(Attachment self) {
        this.parent = null;
        this.rootParent = self;
        AttachmentInternalState.updateRootParentOfChildrenRecurse(self, self);
    }

    private static void updateRootParentOfChildrenRecurse(Attachment attachment, Attachment rootParent) {
        for (Attachment child : attachment.getChildren()) {
            child.getInternalState().rootParent = rootParent;
            AttachmentInternalState.updateRootParentOfChildrenRecurse(child, rootParent);
        }
    }

    public void updateTransform(Attachment attachment, Matrix4x4 initialTransform, ActiveChangeHandler activeChangeHandler) {
        CartAttachment cart;
        double dt;
        AnimationNode animNode;
        boolean hasLastTransform;
        boolean bl = hasLastTransform = this.last_transform != null;
        if (this.curr_transform != null) {
            if (hasLastTransform) {
                this.last_transform.set(this.curr_transform);
            } else {
                this.last_transform = this.curr_transform.clone();
            }
            hasLastTransform = true;
        }
        if (this.curr_transform == null) {
            this.curr_transform = initialTransform.clone();
        } else {
            this.curr_transform.set(initialTransform);
        }
        if (this.position.anchor.appliedLate()) {
            this.curr_transform.multiply(this.position.transform);
            this.position.anchor.apply(attachment, this.curr_transform);
        } else {
            this.position.anchor.apply(attachment, this.curr_transform);
            this.curr_transform.multiply(this.position.transform);
        }
        if (!this.nextAnimationQueue.isEmpty() && (this.currentAnimation == null || this.currentAnimation.hasReachedEnd())) {
            this.currentAnimation = this.nextAnimationQueue.remove(0);
            this.currentAnimation.start();
        }
        if (this.currentAnimation == null) {
            this.lastAnimationState = null;
        } else if (!this.currentAnimation.hasReachedEnd() && (animNode = this.currentAnimation.update(dt = (cart = (CartAttachment)attachment).hasController() ? cart.getController().getAnimationDeltaTime() : 0.05, initialTransform)) != null) {
            this.lastAnimationState = animNode;
        }
        if (this.lastAnimationState != null) {
            boolean active = this.lastAnimationState.isActive();
            this.lastAnimationState.apply(this.curr_transform);
            if (active != attachment.isActive()) {
                activeChangeHandler.scheduleActiveChange(attachment, active);
            }
        }
        if (!hasLastTransform) {
            this.last_transform = this.curr_transform.clone();
        }
        try {
            attachment.onTransformChanged(this.curr_transform);
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to execute onTransformChanged() on attachment " + attachment.getClass().getName(), t);
        }
        if (!hasLastTransform) {
            this.last_transform = this.curr_transform.clone();
        }
    }

    public ForkJoinTask<Void> updateTransformRecurseAsync(Attachment attachment, Matrix4x4 initialTransform, ActiveChangeHandler activeChangeHandler) {
        if (this.transformUpdateTask instanceof UpdateRootAttachmentTask) {
            ((UpdateRootAttachmentTask)this.transformUpdateTask).initialTransform = initialTransform;
        } else {
            this.transformUpdateTask = new UpdateRootAttachmentTask(attachment, initialTransform, activeChangeHandler);
        }
        this.transformUpdateTask.reinitialize();
        return this.transformUpdateTask;
    }

    private static abstract class UpdateTask
    extends ForkJoinTask<Void> {
        private static final long serialVersionUID = 2077912465035575092L;
        public final Attachment attachment;
        public final ActiveChangeHandler activeChangeHandler;

        public UpdateTask(Attachment attachment, ActiveChangeHandler activeChangeHandler) {
            this.attachment = attachment;
            this.activeChangeHandler = activeChangeHandler;
        }

        @Override
        public final Void getRawResult() {
            return null;
        }

        @Override
        protected final void setRawResult(Void value) {
        }

        @Override
        protected final boolean exec() {
            this.performUpdates();
            return true;
        }

        public abstract void performUpdates();

        protected final void updateChildren(AttachmentInternalState state) {
            AttachmentInternalState childState;
            int nrOfChildren = state.children.size();
            if (nrOfChildren == 0) {
                return;
            }
            int i = nrOfChildren - 1;
            while (true) {
                Attachment child;
                if ((childState = (child = state.children.get(i)).getInternalState()).transformUpdateTask == null) {
                    childState.transformUpdateTask = new UpdateRelativeToParentTask(child, this.activeChangeHandler);
                }
                if (i == 0) break;
                childState.transformUpdateTask.reinitialize();
                childState.transformUpdateTask.fork();
                --i;
            }
            childState.transformUpdateTask.performUpdates();
            for (i = 1; i < nrOfChildren; ++i) {
                state.children.get(i).getInternalState().transformUpdateTask.join();
            }
        }
    }

    private static final class UpdateRootAttachmentTask
    extends UpdateTask {
        private static final long serialVersionUID = -758729101023975440L;
        public Matrix4x4 initialTransform;

        public UpdateRootAttachmentTask(Attachment attachment, Matrix4x4 initialTransform, ActiveChangeHandler activeChangeHandler) {
            super(attachment, activeChangeHandler);
            this.initialTransform = initialTransform;
        }

        @Override
        public void performUpdates() {
            AttachmentInternalState state = this.attachment.getInternalState();
            state.updateTransform(this.attachment, this.initialTransform, this.activeChangeHandler);
            this.updateChildren(state);
        }
    }

    private static final class UpdateRelativeToParentTask
    extends UpdateTask {
        private static final long serialVersionUID = 8242564088493119093L;

        public UpdateRelativeToParentTask(Attachment attachment, ActiveChangeHandler activeChangeHandler) {
            super(attachment, activeChangeHandler);
        }

        @Override
        public void performUpdates() {
            Matrix4x4 initialTransform;
            AttachmentInternalState state = this.attachment.getInternalState();
            try {
                initialTransform = state.parent.getTransform();
            }
            catch (NullPointerException ex) {
                if (state.parent == null) {
                    throw new IllegalStateException("Attachment has no parent");
                }
                throw ex;
            }
            state.updateTransform(this.attachment, initialTransform, this.activeChangeHandler);
            this.updateChildren(state);
        }
    }
}

