/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.ToggledState
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.controller.EntityNetworkController
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.ToggledState;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TCSeatChangeListener;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentNameLookup;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentWorldFeatures;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigListener;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigModelTracker;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigTracker;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModelStore;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.helper.AttachmentUpdateTransformHelper;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberNetwork;
import com.bergerkiller.bukkit.tc.events.MemberEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberBeforeSeatChangeEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberBeforeSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberBeforeSeatExitEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatChangeEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import com.bergerkiller.bukkit.tc.utils.SetCallbackCollector;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class AttachmentControllerMember
implements AttachmentConfigListener,
AttachmentManager,
SavedAttachmentModelStore.ModelUsing,
AttachmentNameLookup.Supplier {
    private final MinecartMember<?> member;
    private final TrainCarts plugin;
    private AttachmentModel model;
    private AttachmentConfigModelTracker modelTracker;
    private Attachment rootAttachment;
    private List<CartAttachmentSeat> seatAttachments = Collections.emptyList();
    private final Map<Entity, CartAttachmentSeat> cachedSeatAttachmentsByPassenger = new HashMap<Entity, CartAttachmentSeat>();
    private List<Attachment> flattenedAttachments = Collections.emptyList();
    private final Map<Attachment, AttachmentNameLookup> cachedNameLookups = new IdentityHashMap<Attachment, AttachmentNameLookup>();
    private Map<Entity, SeatHint> seatHints = new HashMap<Entity, SeatHint>();
    private final Map<Player, AttachmentViewer> viewers = new IdentityHashMap<Player, AttachmentViewer>();
    private final Map<Entity, Vector> previousSeatPositions = new IdentityHashMap<Entity, Vector>();
    private final AttachmentWorldFeatures.Tracker worldFeaturesTracker = new AttachmentWorldFeatures.Tracker();
    private boolean changeListenerSeatsAddedOrRemoved = false;
    protected final ToggledState networkInvalid = new ToggledState();
    private boolean attached = false;
    private boolean hidden = false;
    private long animationCurrentTime = 0L;
    private double animationDeltaTime = 0.0;
    private boolean teleporting = false;
    private boolean recreateAfterTeleport = false;
    private Set<Player> viewersAddedWhileTeleporting = Collections.emptySet();

    public AttachmentControllerMember(MinecartMember<?> member) {
        this.member = member;
        this.plugin = member.getTrainCarts();
    }

    public boolean isAttached() {
        return this.attached;
    }

    public synchronized void onAttached() {
        if (this.teleporting) {
            return;
        }
        this.animationCurrentTime = System.currentTimeMillis();
        this.animationDeltaTime = 0.0;
        this.attached = true;
        this.createRootAttachmentAndStartTracking();
    }

    public synchronized void onDetached() {
        if (this.teleporting) {
            this.recreateAfterTeleport = true;
            this.makeHiddenForAll();
            return;
        }
        this.attached = false;
        try {
            this.destroyRootAttachmentAndStopTracking();
        }
        finally {
            this.viewers.clear();
        }
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public synchronized void setHidden(boolean hidden) {
        if (this.hidden == hidden) {
            return;
        }
        this.hidden = hidden;
        if (hidden) {
            this.destroyRootAttachmentAndStopTracking();
        } else {
            this.createRootAttachmentAndStartTracking();
        }
    }

    public void startTeleport() {
        this.teleporting = true;
        this.makeHiddenForAll();
        this.viewersAddedWhileTeleporting = Collections.emptySet();
    }

    public void finishTeleport() {
        if (this.teleporting) {
            this.teleporting = false;
            try {
                if (this.recreateAfterTeleport) {
                    this.onDetached();
                    this.onAttached();
                }
                for (Player viewer : this.viewersAddedWhileTeleporting) {
                    this.makeVisible(viewer);
                }
            }
            finally {
                this.viewersAddedWhileTeleporting = Collections.emptySet();
                this.recreateAfterTeleport = false;
            }
        }
    }

    public MinecartMember<?> getMember() {
        return this.member;
    }

    public void fixNetworkController() {
        EntityNetworkController controller;
        if (this.networkInvalid.clear() && !((controller = ((CommonMinecart)this.member.getEntity()).getNetworkController()) instanceof MinecartMemberNetwork)) {
            ((CommonMinecart)this.member.getEntity()).setNetworkController((EntityNetworkController)new MinecartMemberNetwork(this.plugin));
        }
    }

    public Attachment getRootAttachment() {
        if (!this.attached) {
            throw new IllegalStateException("This member has no network presence and was probably unloaded");
        }
        if (this.hidden) {
            throw new IllegalStateException("This member's attachments are temporarily hidden");
        }
        if (this.rootAttachment == null) {
            AttachmentModel model = AttachmentModel.getDefaultModel(((CommonMinecart)this.member.getEntity()).getType());
            this.onAttachmentAdded(model.getRoot().get());
        }
        return this.rootAttachment;
    }

    public List<Attachment> getAllAttachments() {
        return this.flattenedAttachments;
    }

    @Override
    public synchronized AttachmentNameLookup getNameLookup() {
        return this.getNameLookup(this.getRootAttachment());
    }

    @Override
    public synchronized AttachmentNameLookup getNameLookup(Attachment root) {
        return this.cachedNameLookups.compute(root, (r, prev) -> prev != null && prev.isValid() ? prev : AttachmentNameLookup.create(root));
    }

    public double getAnimationDeltaTime() {
        return this.animationDeltaTime;
    }

    public synchronized void onPassengersChanged(List<Entity> oldPassengers, List<Entity> newPassengers) {
        for (CartAttachmentSeat seat : this.seatAttachments) {
            Entity oldPassenger = seat.getEntity();
            if (newPassengers.contains(oldPassenger)) continue;
            seat.setEntity(null);
        }
        for (Entity newPassenger : newPassengers) {
            CartAttachmentSeat newSeat;
            boolean isInSeat = false;
            for (CartAttachmentSeat seat : this.seatAttachments) {
                if (seat.getEntity() != newPassenger) continue;
                isInSeat = true;
                break;
            }
            if (isInSeat || (newSeat = this.findNewSeatForEntity(newPassenger)) == null) continue;
            newSeat.setEntity(newPassenger);
        }
    }

    public synchronized boolean changeSeatsLookingAt(Entity passenger) {
        return this.changeSeats(passenger, this.findNewSeatForEntity(passenger), true);
    }

    public synchronized boolean changeSeats(Entity passenger, CartAttachmentSeat new_seat, boolean playerInitiated) {
        if (new_seat != null && new_seat.getController() != this) {
            throw new IllegalArgumentException("Cannot change seats to a seat of another member");
        }
        CartAttachmentSeat old_seat = this.findSeatOfExistingPassenger(passenger);
        if (old_seat == null || new_seat == null || old_seat == new_seat) {
            return false;
        }
        return AttachmentControllerMember.handleSeatChange(passenger, old_seat, new_seat, playerInitiated);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean handleSeatChange(Entity passenger, CartAttachmentSeat old_seat, CartAttachmentSeat new_seat, boolean isPlayerInitiated) {
        MemberEvent event;
        if (old_seat == new_seat) {
            return false;
        }
        AttachmentControllerMember.resetCachedSeatsByPassenger(old_seat);
        AttachmentControllerMember.resetCachedSeatsByPassenger(new_seat);
        Location seatPosition = null;
        Location exitPosition = null;
        boolean exitPreserveRotation = true;
        if (old_seat != null && new_seat != null) {
            event = new MemberBeforeSeatChangeEvent(old_seat, new_seat, passenger, isPlayerInitiated);
            seatPosition = ((MemberBeforeSeatExitEvent)event).getSeatPosition();
            if (((MemberBeforeSeatChangeEvent)CommonUtil.callEvent((Event)event)).isCancelled()) {
                return false;
            }
            new_seat = ((MemberBeforeSeatChangeEvent)event).getEnteredSeat();
            exitPosition = ((MemberBeforeSeatExitEvent)event).getExitPosition();
            if (old_seat == new_seat) {
                return false;
            }
        } else if (old_seat != null) {
            seatPosition = old_seat.getPosition(passenger);
            event = new MemberBeforeSeatExitEvent(old_seat, passenger, seatPosition, exitPosition = old_seat.getEjectPosition(passenger), exitPreserveRotation = old_seat.isEjectRotationPreserved(), isPlayerInitiated);
            if (((MemberBeforeSeatExitEvent)CommonUtil.callEvent((Event)event)).isCancelled()) {
                return false;
            }
            exitPosition = ((MemberBeforeSeatExitEvent)event).getExitPosition();
            exitPreserveRotation = ((MemberBeforeSeatExitEvent)event).isExitPlayerRotationPreserved();
        } else if (new_seat != null) {
            event = new MemberBeforeSeatEnterEvent(new_seat, passenger, isPlayerInitiated, false, true);
            if (((MemberBeforeSeatEnterEvent)CommonUtil.callEvent((Event)event)).isCancelled()) {
                return false;
            }
            new_seat = ((MemberBeforeSeatEnterEvent)event).getSeat();
        } else {
            return false;
        }
        if (old_seat != null && new_seat != null && old_seat.getMember() == new_seat.getMember()) {
            old_seat.setEntity(null);
            new_seat.setEntity(passenger);
            CommonUtil.callEvent((Event)new MemberSeatChangeEvent(old_seat, new_seat, passenger, seatPosition, exitPosition, isPlayerInitiated));
            CommonUtil.callEvent((Event)new MemberSeatEnterEvent(new_seat, passenger, isPlayerInitiated, true, false));
            return true;
        }
        try {
            TCSeatChangeListener.suppressSeatChangeEvents = true;
            if (old_seat != null) {
                if (!((CommonMinecart)old_seat.getMember().getEntity()).removePassenger(passenger) && ((CommonMinecart)old_seat.getMember().getEntity()).isPassenger(passenger)) {
                    boolean event2 = false;
                    return event2;
                }
                if (old_seat.getEntity() == passenger) {
                    old_seat.setEntity(null);
                }
            }
            boolean enteredNewSeat = false;
            if (new_seat != null) {
                if (new_seat.getEntity() == null) {
                    new_seat.getController().storeSeatHint(passenger, new_seat);
                    enteredNewSeat = ((CommonMinecart)new_seat.getMember().getEntity()).addPassenger(passenger);
                    new_seat.getController().storeSeatHint(passenger, null);
                    if (enteredNewSeat &= new_seat.getEntity() == null) {
                        new_seat.setEntity(passenger);
                    }
                } else {
                    enteredNewSeat = new_seat.getEntity() == passenger;
                }
            }
            if (old_seat != null) {
                if (enteredNewSeat) {
                    CommonUtil.callEvent((Event)new MemberSeatChangeEvent(old_seat, new_seat, passenger, seatPosition, exitPosition, isPlayerInitiated));
                } else {
                    CommonUtil.callEvent((Event)new MemberSeatExitEvent(old_seat, passenger, seatPosition, exitPosition, exitPreserveRotation, isPlayerInitiated));
                }
            }
            if (enteredNewSeat) {
                CommonUtil.callEvent((Event)new MemberSeatEnterEvent(new_seat, passenger, isPlayerInitiated, old_seat != null, old_seat == null || old_seat.getMember() != new_seat.getMember()));
            }
            boolean bl = true;
            return bl;
        }
        finally {
            TCSeatChangeListener.suppressSeatChangeEvents = false;
            AttachmentControllerMember.resetCachedSeatsByPassenger(old_seat);
            AttachmentControllerMember.resetCachedSeatsByPassenger(new_seat);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void resetCachedSeatsByPassenger(CartAttachmentSeat seat) {
        if (seat != null && seat.getManager() instanceof AttachmentControllerMember) {
            AttachmentControllerMember controller;
            AttachmentControllerMember attachmentControllerMember = controller = (AttachmentControllerMember)seat.getManager();
            synchronized (attachmentControllerMember) {
                controller.cachedSeatAttachmentsByPassenger.clear();
            }
        }
    }

    public boolean hasSeatHint(Entity passenger) {
        return this.seatHints.containsKey(passenger);
    }

    public synchronized CartAttachmentSeat findNewSeatForEntity(Entity passenger) {
        List<CartAttachmentSeat> sortedSeats;
        if (this.seatAttachments.isEmpty()) {
            return null;
        }
        SeatHint seatHint = this.seatHints.get(passenger);
        if (seatHint != null && !seatHint.isExpired()) {
            sortedSeats = seatHint.seats;
        } else {
            Vector position = new Vector();
            EntityHandle handle = EntityHandle.fromBukkit((Entity)passenger);
            position.setX(handle.getLastX());
            position.setY(handle.getLastY());
            position.setZ(handle.getLastZ());
            sortedSeats = this.getSeatsClosestToPosition(position);
        }
        for (CartAttachmentSeat seat : sortedSeats) {
            if (!seat.canEnter(passenger)) continue;
            return seat;
        }
        return null;
    }

    private List<CartAttachmentSeat> getSeatsClosestToPosition(Vector position) {
        if (this.seatAttachments.size() <= 1) {
            return this.seatAttachments;
        }
        ArrayList<CartAttachmentSeat> result = new ArrayList<CartAttachmentSeat>(this.seatAttachments);
        Collections.sort(result, (o1, o2) -> {
            double d1 = o1.getTransform().toVector().distanceSquared(position);
            double d2 = o2.getTransform().toVector().distanceSquared(position);
            return Double.compare(d1, d2);
        });
        return Collections.unmodifiableList(result);
    }

    private List<CartAttachmentSeat> getSeatsClosestToHitTest(Location eyeLocation) {
        if (this.seatAttachments.size() <= 1) {
            return this.seatAttachments;
        }
        Matrix4x4 cameraTransform = new Matrix4x4();
        cameraTransform.translateRotate(eyeLocation);
        cameraTransform.invert();
        ArrayList<CartAttachmentSeat> result = new ArrayList<CartAttachmentSeat>(this.seatAttachments);
        Collections.sort(result, (o1, o2) -> {
            double d1 = AttachmentControllerMember.getViewDistance(cameraTransform, o1.getTransform().toVector());
            double d2 = AttachmentControllerMember.getViewDistance(cameraTransform, o2.getTransform().toVector());
            return Double.compare(d1, d2);
        });
        return Collections.unmodifiableList(result);
    }

    private static double getViewDistance(Matrix4x4 cameraTransform, Vector pos) {
        pos = pos.clone();
        cameraTransform.transformPoint(pos);
        if (pos.getZ() >= 1.0E-6 && pos.getZ() <= 5.0) {
            return Math.sqrt(pos.getX() * pos.getX() + pos.getY() * pos.getY());
        }
        return 5.0 + pos.length();
    }

    public synchronized CartAttachmentSeat findSeat(Entity passenger) {
        CartAttachmentSeat seat = this.findSeatOfExistingPassenger(passenger);
        if (seat != null) {
            return seat;
        }
        return this.findNewSeatForEntity(passenger);
    }

    public synchronized CartAttachmentSeat findSeatOfExistingPassenger(Entity passenger) {
        if (this.seatAttachments.isEmpty()) {
            return null;
        }
        CartAttachmentSeat seat = this.cachedSeatAttachmentsByPassenger.get(passenger);
        if (seat != null && seat.getEntity() == passenger) {
            return seat;
        }
        for (CartAttachmentSeat seat2 : this.seatAttachments) {
            if (seat2.getEntity() != passenger) continue;
            this.cachedSeatAttachmentsByPassenger.put(passenger, seat2);
            return seat2;
        }
        return null;
    }

    public synchronized void makeVisible(Player viewer) {
        if (this.teleporting) {
            if (this.viewersAddedWhileTeleporting.isEmpty()) {
                this.viewersAddedWhileTeleporting = new LinkedHashSet<Player>();
            }
            this.viewersAddedWhileTeleporting.add(viewer);
            return;
        }
        AttachmentViewer attachmentViewer = this.asAttachmentViewer(viewer);
        this.viewers.put(viewer, attachmentViewer);
        if (!this.hidden) {
            HelperMethods.makeVisibleRecursive(this.getRootAttachment(), true, attachmentViewer);
        }
    }

    public synchronized void makeHidden(Player viewer) {
        if (this.teleporting) {
            if (!this.viewersAddedWhileTeleporting.isEmpty()) {
                this.viewersAddedWhileTeleporting.remove(viewer);
            }
            return;
        }
        AttachmentViewer attachmentViewer = this.viewers.remove(viewer);
        if (attachmentViewer == null) {
            attachmentViewer = this.asAttachmentViewer(viewer);
        }
        if (!this.hidden && this.rootAttachment != null) {
            HelperMethods.makeHiddenRecursive(this.rootAttachment, true, attachmentViewer);
        }
    }

    public Set<Player> getViewers() {
        return this.viewers.keySet();
    }

    @Override
    public Collection<AttachmentViewer> getAttachmentViewers() {
        return this.viewers.values();
    }

    @Override
    public AttachmentViewer asAttachmentViewer(Player player) {
        return this.plugin.getAttachmentViewer(player);
    }

    public synchronized boolean isViewer(Player player) {
        return this.viewers.containsKey(player);
    }

    public synchronized boolean isAttachment(int entityId) {
        for (Attachment attachment : this.flattenedAttachments) {
            if (!attachment.containsEntityId(entityId)) continue;
            return true;
        }
        return false;
    }

    public void storeSeatHint(Player player) {
        this.seatHints.put((Entity)player, new SeatHint(this.getSeatsClosestToHitTest(Util.getRealEyeLocation(player))));
    }

    public void storeSeatHint(Entity entity, CartAttachmentSeat seat) {
        if (seat == null) {
            this.seatHints.remove(entity);
        } else {
            this.seatHints.put(entity, new SeatHint(Collections.singletonList(seat)));
        }
    }

    public void syncUnloaded() {
        this.syncMovement(false);
    }

    public synchronized void syncPrePositionUpdate(AttachmentUpdateTransformHelper updater) {
        if (this.isAttached()) {
            this.syncPrePositionUpdate();
            updater.start(this.getRootAttachment(), this.getLiveTransform());
        }
    }

    public synchronized void syncRespawn() {
        ArrayList oldViewers = new ArrayList(this.getViewers());
        this.makeHiddenForAll();
        this.plugin.getTrainUpdateController().syncPositions(this.member);
        for (Player viewer : oldViewers) {
            this.makeVisible(viewer);
        }
    }

    public synchronized void makeHiddenForAll() {
        Iterator<AttachmentViewer> iter = this.viewers.values().iterator();
        while (iter.hasNext()) {
            AttachmentViewer attachmentViewer = iter.next();
            iter.remove();
            HelperMethods.makeHiddenRecursive(this.rootAttachment, true, attachmentViewer);
        }
    }

    public boolean isUnloadedOrDead() {
        return this.member.isUnloaded() || ((CommonMinecart)this.member.getEntity()).isRemoved();
    }

    public void syncPrePositionUpdate() {
        if (!this.seatHints.isEmpty()) {
            Iterator<SeatHint> iter = this.seatHints.values().iterator();
            while (iter.hasNext()) {
                if (!iter.next().isExpired()) continue;
                iter.remove();
            }
        }
        if (this.isUnloadedOrDead()) {
            return;
        }
        this.getRootAttachment();
        if (TCConfig.animationsUseTickTime) {
            this.animationDeltaTime = 0.05;
        } else {
            long time_now = System.currentTimeMillis();
            this.animationDeltaTime = 0.001 * (double)(time_now - this.animationCurrentTime);
            this.animationCurrentTime = time_now;
        }
    }

    public void syncPostPositionUpdate() {
        if (this.rootAttachment == null || this.isUnloadedOrDead()) {
            return;
        }
        this.flattenedAttachments.forEach(Attachment::onTick);
    }

    public void syncMovement(boolean absolute) {
        if (this.isUnloadedOrDead()) {
            return;
        }
        if (absolute && !(((CommonMinecart)this.member.getEntity()).getNetworkController() instanceof MinecartMemberNetwork)) {
            this.networkInvalid.set();
        }
        ((CommonMinecart)this.member.getEntity()).setPositionChanged(false);
        ((CommonMinecart)this.member.getEntity()).setVelocityChanged(false);
        if (this.rootAttachment != null) {
            this.flattenedAttachments.forEach(a -> a.onMove(absolute));
        }
    }

    public Matrix4x4 getLiveTransform() {
        Matrix4x4 transform = new Matrix4x4();
        transform.translate(this.member.getWheels().getPosition());
        transform.rotate(this.member.getOrientation());
        transform.rotateZ(this.member.getRoll());
        return transform;
    }

    public int getAvailableSeatCount(Entity passenger) {
        int count = 0;
        for (CartAttachmentSeat seat : this.seatAttachments) {
            if (!seat.canEnter(passenger)) continue;
            ++count;
        }
        return count;
    }

    @Override
    public World getWorld() {
        return this.member.getWorld();
    }

    @Override
    public AttachmentWorldFeatures getWorldFeatures() {
        return this.worldFeaturesTracker.get(this.getWorld());
    }

    private void createRootAttachmentAndStartTracking() {
        if (!this.attached || this.hidden) {
            return;
        }
        this.model = this.member.getProperties().getModel();
        this.modelTracker = new AttachmentConfigModelTracker(this.model.getConfigTracker(), (Plugin)this.plugin){

            @Override
            public AttachmentConfigTracker findModelConfig(String name) {
                return AttachmentControllerMember.this.plugin.getSavedAttachmentModels().getModelOrNone(name).getConfigTracker();
            }
        };
        AttachmentConfig rootConfig = this.modelTracker.startTracking(this);
        this.destroyRootAttachment();
        this.onAttachmentAdded(rootConfig);
        this.onSynchronized(rootConfig);
    }

    private void destroyRootAttachmentAndStopTracking() {
        try {
            this.destroyRootAttachment();
        }
        finally {
            if (this.modelTracker != null) {
                this.modelTracker.stopTracking(this);
                this.modelTracker = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void destroyRootAttachment() {
        if (this.rootAttachment == null) {
            return;
        }
        try {
            for (CartAttachmentSeat seat : this.seatAttachments) {
                Entity oldEntity = seat.getEntity();
                if (oldEntity == null) continue;
                this.previousSeatPositions.put(oldEntity, seat.getTransform().toVector());
            }
            for (AttachmentViewer viewer : this.viewers.values()) {
                HelperMethods.makeHiddenRecursive(this.rootAttachment, true, viewer);
            }
            AttachmentControllerMember.detachAttachments(this.flattenedAttachments);
        }
        finally {
            this.rootAttachment = null;
            this.changeListenerSeatsAddedOrRemoved = false;
            this.flattenedAttachments = Collections.emptyList();
            this.seatAttachments = Collections.emptyList();
            this.cachedSeatAttachmentsByPassenger.clear();
            this.invalidateCachedNameLookups();
        }
    }

    private void invalidateCachedNameLookups() {
        this.cachedNameLookups.values().forEach(AttachmentNameLookup::invalidate);
        this.cachedNameLookups.clear();
    }

    private static void detachAttachments(List<Attachment> flattenedAttachments) {
        ListIterator<Attachment> iter = flattenedAttachments.listIterator(flattenedAttachments.size());
        while (iter.hasPrevious()) {
            HelperMethods.perform_onDetached_single(iter.previous());
        }
    }

    private void updateFlattenedLists() {
        this.flattenedAttachments = HelperMethods.listAllAttachments(this.rootAttachment);
        this.seatAttachments = (List)this.flattenedAttachments.stream().filter(attachment -> attachment instanceof CartAttachmentSeat).map(attachment -> (CartAttachmentSeat)attachment).collect(StreamUtil.toUnmodifiableList());
        this.cachedSeatAttachmentsByPassenger.clear();
        this.invalidateCachedNameLookups();
    }

    @Override
    public synchronized void onAttachmentRemoved(AttachmentConfig attachmentConfig) {
        if (attachmentConfig.isRoot()) {
            this.destroyRootAttachment();
        } else if (this.rootAttachment != null) {
            Attachment curr = this.rootAttachment;
            for (int p : attachmentConfig.childPath()) {
                curr = curr.getChildren().get(p);
            }
            Attachment curr_parent = curr.getParent();
            List<Attachment> removedAttachments = HelperMethods.listAllAttachments(curr);
            for (Attachment removedAttachment : removedAttachments) {
                if (!(removedAttachment instanceof CartAttachmentSeat)) continue;
                CartAttachmentSeat seat = (CartAttachmentSeat)removedAttachment;
                Entity oldEntity = seat.getEntity();
                if (oldEntity != null) {
                    this.previousSeatPositions.put(oldEntity, seat.getTransform().toVector());
                }
                this.changeListenerSeatsAddedOrRemoved = true;
            }
            boolean active = !HelperMethods.hasInactiveParent(curr);
            for (AttachmentViewer viewer : this.viewers.values()) {
                HelperMethods.makeHiddenRecursive(curr, active, viewer);
            }
            AttachmentControllerMember.detachAttachments(removedAttachments);
            curr_parent.removeChild(curr);
            this.updateFlattenedLists();
        }
    }

    @Override
    public synchronized void onAttachmentAdded(AttachmentConfig attachmentConfig) {
        if (attachmentConfig.isRoot()) {
            this.destroyRootAttachment();
            this.rootAttachment = this.createAttachment(attachmentConfig);
            this.updateFlattenedLists();
            this.changeListenerSeatsAddedOrRemoved |= !this.seatAttachments.isEmpty();
            this.flattenedAttachments.forEach(HelperMethods::perform_onAttached_single);
            this.plugin.getTrainUpdateController().computeAttachmentTransform(this.rootAttachment, this.getLiveTransform());
            for (AttachmentViewer viewer : this.viewers.values()) {
                HelperMethods.makeVisibleRecursive(this.rootAttachment, true, viewer);
            }
        } else {
            Attachment curr_parent = this.rootAttachment;
            int[] path = attachmentConfig.childPath();
            int limit = path.length - 1;
            for (int i = 0; i < limit; ++i) {
                curr_parent = curr_parent.getChildren().get(path[i]);
            }
            Attachment curr = this.createAttachment(attachmentConfig);
            curr_parent.addChild(attachmentConfig.childIndex(), curr);
            List<Attachment> addedAttachments = HelperMethods.listAllAttachments(curr);
            int prevSeatCount = this.seatAttachments.size();
            this.updateFlattenedLists();
            if (this.seatAttachments.size() > prevSeatCount) {
                this.changeListenerSeatsAddedOrRemoved = true;
            }
            addedAttachments.forEach(HelperMethods::perform_onAttached_single);
            this.plugin.getTrainUpdateController().computeAttachmentTransform(this.rootAttachment, this.getLiveTransform());
            boolean active = !HelperMethods.hasInactiveParent(curr);
            for (AttachmentViewer viewer : this.viewers.values()) {
                HelperMethods.makeVisibleRecursive(curr, active, viewer);
            }
        }
    }

    @Override
    public synchronized void onAttachmentChanged(AttachmentConfig attachmentConfig) {
        Attachment curr = this.rootAttachment;
        if (curr != null) {
            curr = curr.findChild(attachmentConfig.childPath());
            AttachmentType type = this.getTypeRegistry().findOrEmpty(attachmentConfig.typeId());
            ConfigurationNode config = attachmentConfig.config();
            try {
                type.migrateConfiguration(config);
            }
            catch (Throwable t) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to migrate attachment configuration of " + type.getName(), t);
            }
            if (!curr.checkCanReload(config)) {
                this.onAttachmentRemoved(attachmentConfig);
                this.onAttachmentAdded(attachmentConfig);
                return;
            }
            Set<String> oldNames = curr.getNames();
            curr.getInternalState().onLoad(this.getClass(), type, config);
            if (!oldNames.equals(curr.getNames())) {
                this.invalidateCachedNameLookups();
            }
            curr.onLoad(config);
        }
    }

    @Override
    public void onAttachmentAction(AttachmentConfig attachmentConfig, Consumer<Attachment> action) {
        Attachment curr = this.rootAttachment;
        if (curr != null && (curr = curr.findChild(attachmentConfig.childPath())) != null) {
            action.accept(curr);
        }
    }

    @Override
    public void onSynchronized(AttachmentConfig rootAttachmentConfig) {
        if (!this.member.onModelChanged(this.model)) {
            if (this.modelTracker != null) {
                this.modelTracker.stopTracking(this);
                this.modelTracker = null;
            }
            return;
        }
        this.putPassengersIntoSeats();
    }

    private void putPassengersIntoSeats() {
        if (this.changeListenerSeatsAddedOrRemoved) {
            this.changeListenerSeatsAddedOrRemoved = false;
            List allPassengers = ((CommonMinecart)this.member.getEntity()).getPassengers();
            ArrayList<Entity> remainingPassengers = new ArrayList<Entity>(allPassengers.size());
            for (Entity entity : allPassengers) {
                if (this.findSeatOfExistingPassenger(entity) != null) continue;
                remainingPassengers.add(entity);
            }
            while (!remainingPassengers.isEmpty()) {
                Entity entity = (Entity)remainingPassengers.get(0);
                Vector position = this.previousSeatPositions.get(entity);
                if (position == null) {
                    position = entity.getLocation().toVector();
                }
                boolean foundSeat = false;
                List<CartAttachmentSeat> seats = this.getSeatsClosestToPosition(position);
                for (CartAttachmentSeat seat : seats) {
                    if (seat.getEntity() != null) continue;
                    seat.setEntity(entity);
                    remainingPassengers.remove(0);
                    foundSeat = true;
                    break;
                }
                if (foundSeat) continue;
                break;
            }
            for (Entity entity : remainingPassengers) {
                ((CommonMinecart)this.member.getEntity()).removePassenger(entity);
            }
        }
        this.previousSeatPositions.clear();
        this.cachedSeatAttachmentsByPassenger.clear();
    }

    @Override
    public void getUsedModels(SetCallbackCollector<SavedAttachmentModel> collector) {
        if (this.model != null) {
            this.model.getUsedModels(collector);
        }
    }

    private static class SeatHint {
        public final List<CartAttachmentSeat> seats;
        public final int expire;

        public SeatHint(List<CartAttachmentSeat> seats) {
            this.seats = seats;
            this.expire = CommonUtil.getServerTicks() + 2;
        }

        public boolean isExpired() {
            return CommonUtil.getServerTicks() >= this.expire;
        }
    }
}

