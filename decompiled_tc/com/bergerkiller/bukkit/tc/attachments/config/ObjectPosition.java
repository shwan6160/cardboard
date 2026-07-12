/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Vector3
 */
package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentAnchor;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;

public class ObjectPosition {
    public AttachmentAnchor anchor = AttachmentAnchor.DEFAULT;
    public Vector3 position = new Vector3();
    public Vector3 rotation = new Vector3();
    public Vector3 size = new Vector3();
    public Matrix4x4 transform = new Matrix4x4();
    private boolean _isDefault = true;
    private boolean _isIdentity = true;

    public void reset() {
        this._isDefault = true;
        this._isIdentity = true;
        this.position.x = 0.0;
        this.position.y = 0.0;
        this.position.z = 0.0;
        this.rotation.x = 0.0;
        this.rotation.y = 0.0;
        this.rotation.z = 0.0;
        this.size.x = 1.0;
        this.size.y = 1.0;
        this.size.z = 1.0;
        this.transform.setIdentity();
        this.anchor = AttachmentAnchor.DEFAULT;
    }

    public static boolean isDefaultSeatParent(ConfigurationNode config) {
        if (!config.isEmpty()) {
            String name;
            return config.contains("anchor") && (name = (String)config.get("anchor", (Object)AttachmentAnchor.DEFAULT.getName())).equals(AttachmentAnchor.SEAT_PARENT.getName());
        }
        return true;
    }

    public void load(ObjectPosition source) {
        this.anchor = source.anchor;
        this.position = source.position;
        this.rotation = source.rotation;
        this.size = source.size;
        this.transform.set(source.transform);
        this._isDefault = source._isDefault;
        this._isIdentity = source._isIdentity;
    }

    public void load(Class<? extends AttachmentManager> managerType, AttachmentType attachmentType, ConfigurationNode config) {
        if (config != null && !config.isEmpty()) {
            this.anchor = config.contains("anchor") ? AttachmentAnchor.find(managerType, attachmentType, (String)config.get("anchor", (Object)AttachmentAnchor.DEFAULT.getName())) : AttachmentAnchor.DEFAULT;
            this._isDefault = false;
            this.position.x = (Double)config.get("posX", (Object)0.0);
            this.position.y = (Double)config.get("posY", (Object)0.0);
            this.position.z = (Double)config.get("posZ", (Object)0.0);
            this.rotation.x = (Double)config.get("rotX", (Object)0.0);
            this.rotation.y = (Double)config.get("rotY", (Object)0.0);
            this.rotation.z = (Double)config.get("rotZ", (Object)0.0);
            this.size.x = (Double)config.getOrDefault("sizeX", (Object)1.0);
            this.size.y = (Double)config.getOrDefault("sizeY", (Object)1.0);
            this.size.z = (Double)config.getOrDefault("sizeZ", (Object)1.0);
            this._isIdentity = this.position.x == 0.0 && this.position.y == 0.0 && this.position.z == 0.0 && this.rotation.x == 0.0 && this.rotation.y == 0.0 && this.rotation.z == 0.0;
            this.initTransform();
            return;
        }
        this.reset();
    }

    public void initTransform() {
        this.transform.setIdentity();
        this.transform.translate(this.position);
        this.transform.rotateYawPitchRoll(this.rotation);
    }

    public boolean isDefault() {
        return this._isDefault;
    }

    public boolean isIdentity() {
        return this._isIdentity;
    }
}

