/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.math.Vector3
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerMember;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.util.Vector;

public abstract class AttachmentAnchor {
    private static final Map<String, AttachmentAnchor> registry = new LinkedHashMap<String, AttachmentAnchor>();
    private static final List<AttachmentAnchor> values = new ArrayList<AttachmentAnchor>();
    public static AttachmentAnchor DEFAULT = AttachmentAnchor.register(new AttachmentAnchor("default"){

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
        }
    });
    public static AttachmentAnchor SEAT_PARENT = AttachmentAnchor.register(new AttachmentAnchor("seat parent"){

        @Override
        public boolean supports(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType) {
            return attachmentType == CartAttachmentSeat.TYPE;
        }

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
            if (attachment.getParent() != null) {
                attachment.getParent().applyPassengerSeatTransform(transform);
            }
        }
    });
    public static AttachmentAnchor NO_ROTATION = AttachmentAnchor.register(new AttachmentAnchor("no rotation"){

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
            Vector3 absolutePosition = transform.toVector3();
            transform.setIdentity();
            transform.translate(absolutePosition);
        }
    });
    public static AttachmentAnchor ALIGN_UP = AttachmentAnchor.register(new AttachmentAnchor("align up"){

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
            Vector3 absolutePosition = transform.toVector3();
            Quaternion rotation = transform.getRotation();
            Vector forward = rotation.forwardVector();
            forward.setY(0.0);
            transform.setIdentity();
            transform.translate(absolutePosition);
            if (forward.lengthSquared() > 1.0E-9) {
                transform.rotate(Quaternion.fromLookDirection((Vector)forward));
            }
        }
    });
    public static AttachmentAnchor ALIGN_UP_PITCH = AttachmentAnchor.register(new AttachmentAnchor("align up [P]"){

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
            Vector3 absolutePosition = transform.toVector3();
            Quaternion rotation = transform.getRotation();
            Vector right = rotation.rightVector();
            Vector forward = new Vector(-right.getZ(), 0.0, right.getX());
            transform.setIdentity();
            transform.translate(absolutePosition);
            if (forward.lengthSquared() > 1.0E-9) {
                transform.rotate(Quaternion.fromLookDirection((Vector)forward));
            }
        }
    });
    public static AttachmentAnchor SEAT_EYES = AttachmentAnchor.register(new AttachmentAnchor("eyes"){

        @Override
        public boolean supports(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType) {
            return attachmentType == CartAttachmentSeat.TYPE;
        }

        @Override
        public boolean appliedLate() {
            return true;
        }

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
            if (attachment instanceof CartAttachmentSeat) {
                ((CartAttachmentSeat)attachment).transformToEyes(transform);
            }
        }
    });
    public static AttachmentAnchor FRONT_WHEEL = AttachmentAnchor.register(new AttachmentAnchor("front wheel"){

        @Override
        public boolean supports(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType) {
            return managerType.isAssignableFrom(AttachmentControllerMember.class);
        }

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
            if (attachment.getManager() instanceof AttachmentControllerMember) {
                AttachmentControllerMember controller = (AttachmentControllerMember)attachment.getManager();
                controller.getMember().getWheels().front().getAbsoluteTransform(transform);
            }
        }
    });
    public static AttachmentAnchor BACK_WHEEL = AttachmentAnchor.register(new AttachmentAnchor("back wheel"){

        @Override
        public boolean supports(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType) {
            return managerType.isAssignableFrom(AttachmentControllerMember.class);
        }

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
            if (attachment.getManager() instanceof AttachmentControllerMember) {
                AttachmentControllerMember controller = (AttachmentControllerMember)attachment.getManager();
                controller.getMember().getWheels().back().getAbsoluteTransform(transform);
            }
        }
    });
    public static AttachmentAnchor CART = AttachmentAnchor.register(new AttachmentAnchor("cart"){

        @Override
        public boolean supports(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType) {
            return managerType.isAssignableFrom(AttachmentControllerMember.class);
        }

        @Override
        public void apply(Attachment attachment, Matrix4x4 transform) {
            if (attachment.getManager() instanceof AttachmentControllerMember) {
                AttachmentControllerMember controller = (AttachmentControllerMember)attachment.getManager();
                transform.set(controller.getLiveTransform());
            }
        }
    });
    private final String _name;

    public AttachmentAnchor(String name) {
        this._name = name;
    }

    public final String getName() {
        return this._name;
    }

    public boolean supports(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType) {
        return true;
    }

    public boolean appliedLate() {
        return false;
    }

    public abstract void apply(Attachment var1, Matrix4x4 var2);

    public static <T extends AttachmentAnchor> T register(T anchor) {
        registry.put(anchor.getName(), anchor);
        registry.put(anchor.getName().toLowerCase(Locale.ENGLISH), anchor);
        registry.put(anchor.getName().toUpperCase(Locale.ENGLISH), anchor);
        values.add(anchor);
        return anchor;
    }

    public static void unregister(AttachmentAnchor anchor) {
        registry.remove(anchor.getName(), anchor);
        registry.remove(anchor.getName().toLowerCase(Locale.ENGLISH), anchor);
        registry.remove(anchor.getName().toUpperCase(Locale.ENGLISH), anchor);
        values.remove(anchor);
    }

    public static Collection<AttachmentAnchor> values() {
        return values;
    }

    public static AttachmentAnchor find(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType, String name) {
        AttachmentAnchor anchor = registry.get(name);
        if (anchor != null && anchor.supports(managerType, attachmentType)) {
            return anchor;
        }
        return new AttachmentAnchor(name){

            @Override
            public void apply(Attachment attachment, Matrix4x4 transform) {
            }
        };
    }

    public String toString() {
        return this.getName();
    }
}

