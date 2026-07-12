/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapBlendMode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.util.Model
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Vector3
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.ui.item;

import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MapWidgetItemPreview
extends MapWidget {
    private final Object renderLock = new Object();
    private Thread renderThread = null;
    private volatile boolean renderThreadStopping = false;
    private volatile RenderOptions lastRenderOptions = new RenderOptions();
    private volatile MapTexture lastRenderResult = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setItem(ItemStack item) {
        this.lastRenderOptions = item != null ? new RenderOptions(this.lastRenderOptions, item) : new RenderOptions();
        Object object = this.renderLock;
        synchronized (object) {
            this.renderLock.notifyAll();
        }
    }

    public void onAttached() {
        super.onAttached();
        this.updateRenderRotation();
        this.renderThreadStopping = false;
        if (this.renderThread == null) {
            this.renderThread = new Thread(this::asyncRender);
            this.renderThread.setDaemon(true);
            this.renderThread.start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onDetached() {
        super.onDetached();
        this.renderThreadStopping = true;
        this.renderThread = null;
        Object object = this.renderLock;
        synchronized (object) {
            this.renderLock.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void asyncRender() {
        RenderOptions opt = null;
        while (!this.renderThreadStopping && this.getDisplay() != null) {
            Object object = this.renderLock;
            synchronized (object) {
                if (opt == this.lastRenderOptions) {
                    try {
                        this.renderLock.wait(1000L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    continue;
                }
                opt = this.lastRenderOptions;
            }
            this.lastRenderResult = opt.render(this.view.getWidth(), this.view.getHeight());
            this.invalidate();
        }
    }

    public void onTick() {
        this.updateRenderRotation();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateRenderRotation() {
        Location loc;
        RenderOptions opt;
        List viewers = this.display.getViewers();
        if (!viewers.isEmpty() && RenderOptions.isDifferent(this.lastRenderOptions, opt = new RenderOptions(this.lastRenderOptions, loc = Util.getRealEyeLocation((Player)viewers.get(0))))) {
            Object object = this.renderLock;
            synchronized (object) {
                this.lastRenderOptions = opt;
                this.renderLock.notifyAll();
            }
        }
    }

    public void onDraw() {
        MapTexture result = this.lastRenderResult;
        if (result != null) {
            this.view.setBlendMode(MapBlendMode.NONE);
            this.view.draw((MapCanvas)result, 0, 0);
        }
    }

    private static class RenderOptions {
        public final ItemStack item;
        public final Model model;
        public final float yaw;
        public final float pitch;

        public RenderOptions() {
            this.item = null;
            this.model = null;
            this.yaw = 0.0f;
            this.pitch = 0.0f;
        }

        public RenderOptions(RenderOptions orig, ItemStack item) {
            if (item == null) {
                throw new IllegalArgumentException("Null item");
            }
            this.item = item;
            this.model = TCConfig.resourcePack.getItemModel(item);
            this.yaw = orig.yaw;
            this.pitch = orig.pitch;
        }

        public RenderOptions(RenderOptions orig, Location eyeLocation) {
            this.item = orig.item;
            this.model = orig.model;
            this.yaw = eyeLocation.getYaw();
            this.pitch = eyeLocation.getPitch() - 90.0f;
        }

        public MapTexture render(int width, int height) {
            MapTexture texture = MapTexture.createEmpty((int)width, (int)height);
            if (this.model != null) {
                double scale = (double)width / 64.0;
                Matrix4x4 transform = new Matrix4x4();
                transform.translate((double)width / 2.0, 0.0, (double)height / 2.0);
                transform.scale(scale);
                transform.rotateX((double)this.pitch);
                transform.rotateY((double)this.yaw);
                transform.translate(-8.0 / scale, -8.0 / scale, -8.0 / scale);
                texture.setLightOptions(0.0f, 1.0f, new Vector3(-1.0, 1.0, -1.0));
                texture.drawModel(this.model, transform);
            }
            return texture;
        }

        public static boolean isDifferent(RenderOptions opt1, RenderOptions opt2) {
            return opt1.model != opt2.model || MathUtil.getAngleDifference((float)opt1.yaw, (float)opt2.yaw) > 2.0f || MathUtil.getAngleDifference((float)opt1.pitch, (float)opt2.pitch) > 2.0f;
        }
    }
}

