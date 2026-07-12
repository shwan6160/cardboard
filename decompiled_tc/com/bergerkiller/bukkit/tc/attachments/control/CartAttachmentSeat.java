/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentAnchor;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentInternalState;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.ObjectPosition;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ItemTransformType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentEntity;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonEyePositionDialog;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonView;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewDefault;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewLockMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewNone;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewSpectator;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewStanding;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatDebugUI;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatDisplayedItemDialog;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatExitPositionMenu;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntity;
import com.bergerkiller.bukkit.tc.attachments.control.seat.ThirdPersonDefault;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetBlinkyButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetToggleButton;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerMember;
import com.bergerkiller.bukkit.tc.properties.standard.type.ExitOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CartAttachmentSeat
extends CartAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "SEAT";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/seat.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentSeat();
        }

        @Override
        public void migrateConfiguration(ConfigurationNode config) {
            if ("FLOATING".equals(config.get("firstPersonViewMode", String.class))) {
                config.set("firstPersonViewMode", (Object)"DEFAULT");
                config.remove("firstPersonViewPosition");
                ConfigurationNode eye = config.getNode("firstPersonViewPosition");
                eye.set("posX", (Object)0.0);
                eye.set("posY", (Object)1.0);
                eye.set("posZ", (Object)0.0);
                eye.set("rotX", (Object)0.0);
                eye.set("rotY", (Object)0.0);
                eye.set("rotZ", (Object)0.0);
            }
        }

        @Override
        public void createAppearanceTab(final MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            ((MapWidgetText)tab.addWidget((MapWidget)new MapWidgetText())).setText("FIRST PERSON VIEW").setFont(MapFont.TINY).setColor((byte)18).setPosition(17, 2);
            (tab.addWidget((MapWidget)new MapWidgetToggleButton<FirstPersonViewMode>(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onSelectionChanged() {
                    attachment.getConfig().set("firstPersonViewMode", this.getSelectedOption());
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    attachment.resetIcon();
                    this.display.playSound(SoundEffect.CLICK);
                }
            })).addOptions(Enum::name, (FirstPersonViewMode[])Stream.of(FirstPersonViewMode.values()).filter(FirstPersonViewMode::isSelectable).toArray(FirstPersonViewMode[]::new)).setSelectedOption((FirstPersonViewMode)((Object)attachment.getConfig().get("firstPersonViewMode", (Object)FirstPersonViewMode.DYNAMIC))).setBounds(0, 9, 68, 14);
            (tab.addWidget((MapWidget)new MapWidgetBlinkyButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.updateIcon();
                }

                @Override
                public void onClick() {
                    tab.addWidget((MapWidget)new FirstPersonEyePositionDialog(attachment){

                        @Override
                        public void close() {
                            super.close();
                            this.updateIcon();
                        }
                    });
                }

                public void updateIcon() {
                    boolean configured = attachment.getConfig().isNode("firstPersonViewPosition");
                    this.setIcon(configured ? "attachments/view_camera_configured.png" : "attachments/view_camera_auto.png");
                    this.setTooltip(configured ? "Set eye position\n  (Configured)" : "Set eye position\n   (Automatic)");
                }
            })).setPosition(70, 9);
            (tab.addWidget((MapWidget)new MapWidgetBlinkyButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.updateIcon();
                }

                @Override
                public void onClick() {
                    FirstPersonViewLockMode lockMode = (FirstPersonViewLockMode)((Object)attachment.getConfig().get("firstPersonViewLockMode", (Object)FirstPersonViewLockMode.OFF));
                    lockMode = FirstPersonViewLockMode.values()[(lockMode.ordinal() + 1) % FirstPersonViewLockMode.values().length];
                    attachment.getConfig().set("firstPersonViewLockMode", (Object)lockMode);
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    this.updateIcon();
                    this.display.playSound(SoundEffect.CLICK);
                }

                public void updateIcon() {
                    FirstPersonViewLockMode lockMode = (FirstPersonViewLockMode)((Object)attachment.getConfig().get("firstPersonViewLockMode", (Object)FirstPersonViewLockMode.OFF));
                    this.setIcon(lockMode.getIconPath());
                    this.setTooltip(lockMode.getTooltip());
                }
            })).setPosition(86, 9);
            ((MapWidgetText)tab.addWidget((MapWidget)new MapWidgetText())).setText("PASSENGER DISPLAY").setFont(MapFont.TINY).setColor((byte)18).setPosition(17, 26);
            (tab.addWidget((MapWidget)new MapWidgetToggleButton<SeatedEntity.DisplayMode>(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onSelectionChanged() {
                    attachment.getConfig().set("displayMode", this.getSelectedOption());
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    attachment.resetIcon();
                    this.display.playSound(SoundEffect.CLICK);
                }
            })).addOptions(Enum::name, SeatedEntity.DisplayMode.class).setSelectedOption((SeatedEntity.DisplayMode)((Object)attachment.getConfig().get("displayMode", (Object)SeatedEntity.DisplayMode.DEFAULT))).setBounds(0, 33, 68, 14);
            (tab.addWidget((MapWidget)new MapWidgetBlinkyButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.updateIcon();
                }

                @Override
                public void onClick() {
                    (tab.getParent().getParent().addWidget((MapWidget)new SeatDisplayedItemDialog(){

                        public void onDetached() {
                            super.onDetached();
                            this.updateIcon();
                        }
                    })).setAttachment(attachment);
                }

                public void updateIcon() {
                    boolean hasItemSet = false;
                    if (attachment.getConfig().isNode("displayItem")) {
                        ConfigurationNode displayItem = attachment.getConfig().getNode("displayItem");
                        hasItemSet = (Boolean)displayItem.get("enabled", (Object)false);
                    }
                    if (hasItemSet) {
                        this.setIcon("attachments/seat_item_on.png");
                        this.setTooltip("Display an item\n   (Enabled)");
                    } else {
                        this.setIcon("attachments/seat_item_off.png");
                        this.setTooltip("Display an item\n   (Disabled)");
                    }
                }
            })).setPosition(70, 33);
            (tab.addWidget((MapWidget)new MapWidgetBlinkyButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.updateIcon();
                }

                @Override
                public void onClick() {
                    attachment.getConfig().set("lockRotation", (Object)((Boolean)attachment.getConfig().get("lockRotation", (Object)false) == false ? 1 : 0));
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    this.updateIcon();
                    this.display.playSound(SoundEffect.CLICK);
                }

                public void updateIcon() {
                    boolean isPassengerLocked = (Boolean)attachment.getConfig().get("lockRotation", (Object)false);
                    if (isPassengerLocked) {
                        this.setIcon("attachments/seat_body_locked.png");
                        this.setTooltip("No body rotation");
                    } else {
                        this.setIcon("attachments/seat_body_unlocked.png");
                        this.setTooltip("Body can rotate");
                    }
                }
            })).setPosition(86, 33);
            (tab.addWidget((MapWidget)new MapWidgetButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onActivate() {
                    ((SeatExitPositionMenu)tab.getParent().getParent().addWidget((MapWidget)new SeatExitPositionMenu())).setAttachment(attachment);
                }
            })).setText("Change Exit").setBounds(0, 52, 100, 14);
            final MapWidgetSubmitText permissionTextBox = (MapWidgetSubmitText)tab.addWidget((MapWidget)new MapWidgetSubmitText(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.setDescription("Enter permission node");
                }

                public void onAccept(String text) {
                    attachment.getConfig().set("enterPermission", (Object)text);
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                }
            });
            (tab.addWidget((MapWidget)new MapWidgetButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onActivate() {
                    permissionTextBox.activate();
                }
            })).setText("Permission").setBounds(0, 68, 100, 14);
        }
    };
    public FirstPersonView firstPerson = new FirstPersonViewNone(this);
    public ThirdPersonDefault thirdPerson = new ThirdPersonDefault(this);
    public SeatedEntity seated = null;
    private AttachmentViewer _makeVisibleCurrent = null;
    private ObjectPosition _ejectPosition = new ObjectPosition();
    private boolean _ejectLockRotation = false;
    private String _enterPermission = null;
    private boolean _locked = false;
    private FirstPersonViewMode fpvViewMode;
    private FirstPersonViewLockMode fpvViewLockMode;
    private final ObjectPosition fpvEyePosition = new ObjectPosition();
    private static final int FOCUS_DEBOUNCE_TICKS = 40;
    private int _focusDebounceTimer = 0;
    private VirtualSpawnableObject _displayedItemEntity = null;
    private ObjectPosition _displayedItemPosition = null;
    private boolean _displayedItemShowFirstPersonEnabled = false;
    private boolean _displayedItemShownInFirstPerson = false;
    public final SeatDebugUI debug = new SeatDebugUI(this);
    private boolean _useSmoothCoasters = false;

    public Collection<AttachmentViewer> getAttachmentViewersSynced() {
        if (this._makeVisibleCurrent == null) {
            return this.getAttachmentViewers();
        }
        ArrayList<AttachmentViewer> tmp = new ArrayList<AttachmentViewer>(this.getAttachmentViewers());
        tmp.remove(this._makeVisibleCurrent);
        return tmp;
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.seated = ((SeatedEntity.DisplayMode)((Object)this.getConfig().getOrDefault("displayMode", (Object)SeatedEntity.DisplayMode.DEFAULT))).create(this);
        this._locked = (Boolean)this.getConfig().getOrDefault("lockRotation", (Object)false);
        this.fpvViewMode = (FirstPersonViewMode)((Object)this.getConfig().getOrDefault("firstPersonViewMode", (Object)FirstPersonViewMode.DYNAMIC));
        this.fpvViewLockMode = (FirstPersonViewLockMode)((Object)this.getConfig().getOrDefault("firstPersonViewLockMode", (Object)FirstPersonViewLockMode.OFF));
        this._displayedItemPosition = null;
        this._displayedItemEntity = null;
        this._displayedItemShowFirstPersonEnabled = false;
        if (((Boolean)this.getConfig().getOrDefault("displayItem.enabled", (Object)false)).booleanValue()) {
            ItemTransformType type = ItemTransformType.deserialize(this.getConfig(), "displayItem.position.transform");
            this._displayedItemPosition = new ObjectPosition();
            this._displayedItemEntity = type.create(this.getManager(), null);
            this._displayedItemShowFirstPersonEnabled = (Boolean)this.getConfig().getOrDefault("displayItem.showFirstPerson", (Object)false);
        }
    }

    @Override
    public boolean checkCanReload(ConfigurationNode config) {
        boolean newSeatParent;
        if ((Boolean)config.get("displayItem.enabled", (Object)false) != (this._displayedItemEntity != null)) {
            return false;
        }
        if (this._displayedItemEntity != null) {
            if ((Boolean)config.getOrDefault("displayItem.showFirstPerson", (Object)false) != this._displayedItemShowFirstPersonEnabled) {
                return false;
            }
            ItemTransformType type = ItemTransformType.deserialize(config, "displayItem.position.transform");
            if (!type.canUpdate(this._displayedItemEntity)) {
                return false;
            }
        }
        if (this.seated.getDisplayMode() != config.getOrDefault("displayMode", (Object)SeatedEntity.DisplayMode.DEFAULT)) {
            return false;
        }
        if (this._locked != (Boolean)config.getOrDefault("lockRotation", (Object)false)) {
            return false;
        }
        if (this.fpvViewMode != config.getOrDefault("firstPersonViewMode", (Object)FirstPersonViewMode.DYNAMIC)) {
            return false;
        }
        if (this.fpvViewLockMode != config.getOrDefault("firstPersonViewLockMode", (Object)FirstPersonViewLockMode.OFF)) {
            return false;
        }
        ObjectPosition newPos = this.parsePosition(config);
        boolean oldSeatParent = this.getConfiguredPosition().anchor == AttachmentAnchor.SEAT_PARENT && this.getConfiguredPosition().isIdentity();
        boolean bl = newSeatParent = (newPos.anchor == AttachmentAnchor.SEAT_PARENT || newPos.anchor == AttachmentAnchor.DEFAULT) && newPos.isIdentity();
        return oldSeatParent == newSeatParent;
    }

    private ObjectPosition parsePosition(ConfigurationNode config) {
        ObjectPosition pos = new ObjectPosition();
        pos.load(this.getManager().getClass(), TYPE, config.getNode("position"));
        return pos;
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        super.onLoad(config);
        this._enterPermission = (String)this.getConfig().getOrDefault("enterPermission", String.class, null);
        AttachmentInternalState state = this.getInternalState();
        if (state.position.isDefault() && state.position.anchor == AttachmentAnchor.DEFAULT) {
            state.position.anchor = AttachmentAnchor.SEAT_PARENT;
        }
        if (config.contains("firstPersonViewPosition")) {
            this.fpvEyePosition.load(this.getManager().getClass(), TYPE, config.getNode("firstPersonViewPosition"));
        } else {
            this.fpvEyePosition.reset();
        }
        this.firstPerson.getEyePosition().load(this.fpvEyePosition);
        ConfigurationNode ejectPosition = this.getConfig().getNodeIfExists("ejectPosition");
        this._ejectPosition.load(this.getManager().getClass(), TYPE, ejectPosition);
        boolean bl = this._ejectLockRotation = ejectPosition != null && (Boolean)ejectPosition.getOrDefault("lockRotation", (Object)false) != false;
        if (this._displayedItemEntity != null) {
            ConfigurationNode displayItemConfig = config.getNode("displayItem");
            this._displayedItemPosition.load(this.getManager().getClass(), CartAttachmentItem.TYPE, displayItemConfig.getNode("position"));
            ItemTransformType.deserialize(displayItemConfig, "position.transform").load(this._displayedItemEntity, displayItemConfig, this._displayedItemPosition);
        }
        if (this._focusDebounceTimer > 0) {
            this._focusDebounceTimer = 40;
        }
    }

    @Override
    public void onDetached() {
        super.onDetached();
        this.debug.stopEyePreviews();
        this.setEntityImpl(null, true);
        this._displayedItemEntity = null;
        this._displayedItemPosition = null;
    }

    @Override
    @Deprecated
    public void makeVisible(Player player) {
        this.makeVisible(this.getManager().asAttachmentViewer(player));
    }

    @Override
    @Deprecated
    public void makeHidden(Player player) {
        this.makeHidden(this.getManager().asAttachmentViewer(player));
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        try {
            this._makeVisibleCurrent = viewer;
            this.seated.updateMode(false);
            this.makeVisibleImpl(viewer, false);
        }
        finally {
            this._makeVisibleCurrent = null;
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        this.makeHiddenImpl(viewer, false);
    }

    public void makeVisibleImpl(AttachmentViewer viewer, boolean isReload) {
        if (!this.seated.isDisplayed()) {
            return;
        }
        if (viewer.getPlayer() == this.seated.getEntity()) {
            if (!isReload || !viewer.equals(this.firstPerson.getViewer())) {
                FirstPersonViewMode liveMode = this.firstPerson.getLiveMode();
                this.firstPerson = this.fpvViewLockMode.isSpectator() && !this.useSmoothCoasters() ? new FirstPersonViewSpectator(this, viewer) : (this.fpvViewMode == FirstPersonViewMode.STANDING ? new FirstPersonViewStanding(this, viewer) : new FirstPersonViewDefault(this, viewer));
                this.firstPerson.setMode(this.fpvViewMode);
                this.firstPerson.setLiveMode(liveMode);
                this.firstPerson.setLockMode(this.fpvViewLockMode);
                this.firstPerson.getEyePosition().load(this.fpvEyePosition);
                this.seated.updateMode(true);
            }
            this.firstPerson.makeVisible(viewer, isReload);
            if (this._displayedItemEntity != null && !this._displayedItemShownInFirstPerson && this.showDisplayedItemInFirstPerson()) {
                this._displayedItemShownInFirstPerson = true;
                this.makeDisplayedItemVisible(viewer);
            }
        } else {
            this.thirdPerson.makeVisible(viewer);
            if (!isReload && this._displayedItemEntity != null) {
                this.makeDisplayedItemVisible(viewer);
            }
        }
    }

    private void makeDisplayedItemVisible(AttachmentViewer viewer) {
        if (!this._displayedItemEntity.hasViewers()) {
            this._displayedItemEntity.setUseMinecartInterpolation(this.isMinecartInterpolation());
            this.updateDisplayedItemPosition(this.getTransform());
            this._displayedItemEntity.syncPosition(true);
        }
        this._displayedItemEntity.spawn(viewer, this.calcMotion());
    }

    private boolean showDisplayedItemInFirstPerson() {
        return this._displayedItemShowFirstPersonEnabled || this.firstPerson.getLiveMode() == FirstPersonViewMode.THIRD_P;
    }

    private void updateDisplayedItemPosition(Matrix4x4 transform) {
        transform = transform.clone();
        transform.multiply(this._displayedItemPosition.transform);
        this._displayedItemEntity.updatePosition(transform);
        this._displayedItemEntity.syncPosition(false);
    }

    public void makeHiddenImpl(AttachmentViewer viewer, boolean isReload) {
        if (this.seated.getEntity() == viewer.getPlayer()) {
            if (!isReload && this._displayedItemEntity != null && this._displayedItemShownInFirstPerson) {
                this._displayedItemShownInFirstPerson = false;
                this._displayedItemEntity.destroy(viewer);
            }
            this.firstPerson.makeHidden(viewer, isReload);
            if (!isReload) {
                this.firstPerson = new FirstPersonViewNone(this);
            }
        } else {
            this.thirdPerson.makeHidden(viewer);
            if (!isReload && this._displayedItemEntity != null) {
                this._displayedItemEntity.destroy(viewer);
            }
        }
    }

    public Vector calcMotion() {
        AttachmentInternalState state = this.getInternalState();
        Vector pos_old = state.last_transform.toVector();
        Vector pos_new = state.curr_transform.toVector();
        return pos_new.subtract(pos_old);
    }

    public boolean isMinecartInterpolation() {
        return this.getConfiguredPosition().anchor == AttachmentAnchor.SEAT_PARENT && this.getParent() instanceof CartAttachmentEntity && ((CartAttachmentEntity)this.getParent()).isMinecartInterpolation();
    }

    public boolean useSmoothCoasters() {
        return this._useSmoothCoasters && this.getPlugin().isEnabled();
    }

    public void sendSmoothCoastersRelativeRotation(Quaternion orientation, boolean instant) {
        if (this._useSmoothCoasters) {
            this.getPlugin().getSmoothCoastersAPI().setRotation(this.firstPerson.getViewer().getSmoothCoastersNetwork(), this.firstPerson.getViewer().getPlayer(), (float)orientation.getX(), (float)orientation.getY(), (float)orientation.getZ(), (float)orientation.getW(), (byte)(instant ? 0 : (this.isMinecartInterpolation() ? 5 : 3)));
        }
    }

    public void transformToEyes(Matrix4x4 transform) {
        if (!this.firstPerson.getEyePosition().isDefault()) {
            Matrix4x4 tmp = this.firstPerson.getEyePosition().transform.clone();
            tmp.invert();
            transform.multiply(tmp);
            return;
        }
        FirstPersonViewMode mode = this.firstPerson.getMode();
        if (mode == FirstPersonViewMode.DYNAMIC) {
            mode = FirstPersonViewMode.THIRD_P;
        }
        if (mode == FirstPersonViewMode.THIRD_P) {
            transform.translate(this.seated.getThirdPersonCameraOffset().clone().multiply(-1.0));
        } else {
            transform.translate(0.0, -1.0, 0.0);
        }
    }

    @Override
    public void onMove(boolean absolute) {
        if (this.seated.isPlayer() && this.getViewers().contains(this.seated.getEntity())) {
            this.firstPerson.onMove(absolute);
        }
        this.debug.syncEyePreviews(absolute);
        this.seated.syncPosition(absolute);
    }

    public Entity getEntity() {
        return this.seated.getEntity();
    }

    public int getTicksInSeat() {
        return this.seated.getTicksInSeat();
    }

    public void setEntity(Entity entity) {
        this.setEntityImpl(entity, false);
    }

    private void setEntityImpl(Entity entity, boolean isDetaching) {
        if (this.seated.getEntity() == entity) {
            return;
        }
        if (this.seated.isDisplayed()) {
            for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                this.makeHiddenImpl(viewer, false);
            }
            this._useSmoothCoasters = false;
        }
        if (!this.seated.isEmpty()) {
            TrainCarts.plugin.getSeatAttachmentMap().remove(this.seated.getEntity().getEntityId(), this);
        }
        this._useSmoothCoasters = entity instanceof Player && this.getPlugin().getSmoothCoastersAPI().isEnabled((Player)entity);
        this.seated.setEntity(entity);
        if (!this.seated.isEmpty()) {
            TrainCarts.plugin.getSeatAttachmentMap().set(this.seated.getEntity().getEntityId(), this);
        }
        if (!isDetaching && this.seated.isDisplayed()) {
            for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                this.makeVisibleImpl(viewer, false);
            }
        }
    }

    public TrainCarts getPlugin() {
        return (TrainCarts)super.getPlugin();
    }

    public boolean eject() {
        return this.eject(false);
    }

    public boolean eject(boolean isPlayerInitiated) {
        if (this.getEntity() == null) {
            return false;
        }
        return AttachmentControllerMember.handleSeatChange(this.getEntity(), this, null, isPlayerInitiated);
    }

    public boolean enter(Entity entity) {
        boolean mustTeleport;
        if (this.getEntity() == entity) {
            return true;
        }
        if (this.getEntity() != null) {
            return false;
        }
        if (entity.getVehicle() != null) {
            CartAttachmentSeat previousSeat = this.getController().findSeatOfExistingPassenger(entity);
            if (previousSeat != null) {
                return AttachmentControllerMember.handleSeatChange(entity, previousSeat, this, false);
            }
            if (!CommonEntity.get((Entity)entity.getVehicle()).removePassenger(entity)) {
                return false;
            }
        }
        Location seatLoc = this.getPosition(entity);
        boolean bl = mustTeleport = entity.getWorld() != seatLoc.getWorld() || entity.getLocation().distance(seatLoc) > 64.0;
        if (mustTeleport && !entity.teleport(seatLoc)) {
            return false;
        }
        this.getController().storeSeatHint(entity, this);
        return ((CommonMinecart)this.getMember().getEntity()).addPassenger(entity);
    }

    @Override
    public void onTick() {
        if (this._focusDebounceTimer > 0 && --this._focusDebounceTimer == 0) {
            this.hideDummyPlayer();
        }
        this.seated.updatePosition(this.getTransform());
        if (this._displayedItemEntity != null) {
            this.updateDisplayedItemPosition(this.getTransform());
        }
        this.seated.updateMode(false);
        this.firstPerson.onTick();
        this.debug.updateEyePreview();
        if (this.seated.isPlayer()) {
            Player player = (Player)this.seated.getEntity();
            boolean enabled = this.getPlugin().getSmoothCoastersAPI().isEnabled(player);
            if (enabled != this._useSmoothCoasters) {
                AttachmentViewer viewer = this.getManager().asAttachmentViewer(player);
                this.makeHiddenImpl(viewer, false);
                this._useSmoothCoasters = enabled;
                this.seated.updateMode(false);
                this.makeVisibleImpl(viewer, false);
            }
        }
    }

    @Override
    public void onFocus() {
        if (this._focusDebounceTimer == 0) {
            this.showDummyPlayer();
        }
        this._focusDebounceTimer = 40;
        this.seated.updateFocus(true);
    }

    @Override
    public void onBlur() {
        if (this._focusDebounceTimer > 0) {
            this._focusDebounceTimer = 40;
        }
        this.seated.updateFocus(false);
    }

    private void showDummyPlayer() {
        if (!this.seated.isDummyPlayer()) {
            this.seated.setShowDummyPlayer(true);
            if (this.seated.isEmpty()) {
                for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                    this.makeVisibleImpl(viewer, false);
                }
            }
        }
    }

    private void hideDummyPlayer() {
        if (this.seated.isDummyPlayer()) {
            if (this.seated.isEmpty()) {
                for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                    this.makeHiddenImpl(viewer, false);
                }
            }
            this.seated.setShowDummyPlayer(false);
        }
    }

    public boolean isRotationLocked() {
        return this._locked;
    }

    public float getPassengerYaw() {
        return this.seated.orientation.getPassengerYaw();
    }

    public float getPassengerPitch() {
        return this.seated.orientation.getPassengerPitch();
    }

    public float getPassengerHeadYaw() {
        return this.seated.orientation.getPassengerHeadYaw();
    }

    public Location getPosition(Entity passenger) {
        World w = this.getManager().getWorld();
        Matrix4x4 transform = this.getTransform();
        Vector pyr = transform.getYawPitchRoll();
        return transform.toVector().toLocation(w, (float)pyr.getY(), (float)pyr.getX());
    }

    public Location getEjectPosition(Entity passenger) {
        Matrix4x4 tmp = this.getTransform().clone();
        this._ejectPosition.anchor.apply(this, tmp);
        tmp.multiply(this._ejectPosition.transform);
        World w = this.getManager().getWorld();
        Vector pos = tmp.toVector();
        Vector ypr = tmp.getYawPitchRoll();
        float yaw = (float)ypr.getY();
        float pitch = (float)ypr.getX();
        if (!this._ejectLockRotation && passenger != null) {
            Location curr_loc = passenger instanceof LivingEntity ? ((LivingEntity)passenger).getEyeLocation() : passenger.getLocation();
            yaw = curr_loc.getYaw();
            pitch = curr_loc.getPitch();
        }
        if (this.getManager() instanceof AttachmentControllerMember) {
            MinecartMember<?> member = ((AttachmentControllerMember)this.getManager()).getMember();
            ExitOffset cprop_offset = member.getProperties().getExitOffset();
            if (cprop_offset.isAbsolute()) {
                MathUtil.setVector((Vector)pos, (Vector)cprop_offset.getPosition());
                if (cprop_offset.hasLockedYaw()) {
                    yaw = cprop_offset.getYaw();
                }
                if (cprop_offset.hasLockedPitch()) {
                    pitch = cprop_offset.getPitch();
                }
            } else {
                Quaternion orientation = member.getOrientation();
                if (member.isOrientationInverted()) {
                    orientation.rotateY(180.0);
                }
                Vector exitpos = cprop_offset.getPosition();
                exitpos.setX(-exitpos.getX());
                orientation.transformPoint(exitpos);
                pos.add(exitpos);
                if (cprop_offset.hasLockedYaw()) {
                    yaw = (float)(orientation.getYaw() + (double)cprop_offset.getYaw());
                }
                if (cprop_offset.hasLockedPitch()) {
                    pitch = (float)(orientation.getPitch() + (double)cprop_offset.getPitch());
                }
            }
        }
        return new Location(w, pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
    }

    public boolean isEjectRotationPreserved() {
        MinecartMember<?> member;
        ExitOffset cprop_offset;
        if (this._ejectLockRotation) {
            return false;
        }
        return !(this.getManager() instanceof AttachmentControllerMember) || !(cprop_offset = (member = ((AttachmentControllerMember)this.getManager()).getMember()).getProperties().getExitOffset()).hasLockedYaw() && !cprop_offset.hasLockedPitch();
    }

    public Location getFirstPersonEyeLocation() {
        return this.firstPerson.getPlayerEyeLocation();
    }

    @Override
    public boolean isHiddenWhenInactive() {
        return false;
    }

    @Override
    public boolean containsEntityId(int entityId) {
        if (this._displayedItemEntity != null && this._displayedItemEntity.containsEntityId(entityId)) {
            return true;
        }
        return this.seated.containsEntityId(entityId);
    }

    public boolean canEnter(Entity passenger) {
        Player p;
        if (!this.seated.isEmpty()) {
            return false;
        }
        return !(passenger instanceof Player) || this._enterPermission == null || this._enterPermission.isEmpty() || (p = (Player)passenger).hasPermission(this._enterPermission);
    }
}

