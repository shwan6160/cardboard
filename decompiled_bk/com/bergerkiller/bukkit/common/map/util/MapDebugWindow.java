/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MapDebugWindow {
    private double _x_dirty = 0.0;
    private double _y_dirty = 0.0;
    private int _z_dirty = 0;
    private int _xint = 0;
    private int _yint = 0;
    private int _zint = 0;
    private double _x = 0.0;
    private double _y = 0.0;
    private boolean _event = true;
    private boolean _closing = false;
    private final JLabel label;
    private final MapCanvas map;
    private final int scale;

    private MapDebugWindow(final JLabel label, MapCanvas map, int scale) {
        this.map = map;
        this.scale = scale;
        this.label = label;
        this.label.addMouseMotionListener(new MouseMotionListener(){
            final /* synthetic */ MapDebugWindow this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                this.this$0._x_dirty = (double)e.getX() / (double)label.getWidth();
                this.this$0._y_dirty = (double)e.getY() / (double)label.getHeight();
                this.this$0.signal();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        this.label.addMouseWheelListener(e -> {
            if (e.getWheelRotation() > 0) {
                ++this._z_dirty;
                this.signal();
            } else if (e.getWheelRotation() < 0) {
                --this._z_dirty;
                this.signal();
            }
        });
        this.updateImage();
    }

    private void updateImage() {
        this.label.setIcon(new ImageIcon(this.map.toJavaImage().getScaledInstance(this.map.getWidth() * this.scale, this.map.getHeight() * this.scale, 0)));
    }

    public synchronized boolean waitNext() {
        this.updateImage();
        this._event = false;
        while (!this._event) {
            try {
                this.wait();
            }
            catch (InterruptedException interruptedException) {}
        }
        this._x = this._x_dirty;
        this._y = this._y_dirty;
        this._xint = (int)(this._x * (double)this.map.getWidth());
        this._yint = (int)(this._y * (double)this.map.getHeight());
        this._zint = this._z_dirty;
        return !this._closing;
    }

    public int x() {
        return this._xint;
    }

    public int y() {
        return this._yint;
    }

    public int z() {
        return this._zint;
    }

    public int x(int min, int max) {
        return MathUtil.clamp(this._xint, min, max);
    }

    public int y(int min, int max) {
        return MathUtil.clamp(this._yint, min, max);
    }

    public int z(int min, int max) {
        return MathUtil.clamp(this._zint, min, max);
    }

    public double fx() {
        return this._x;
    }

    public double fy() {
        return this._y;
    }

    private synchronized void signal() {
        this._event = true;
        this.notifyAll();
    }

    public void waitForever() {
        while (this.waitNext()) {
        }
    }

    public static void showMapForeverAutoScale(MapCanvas mapContents) {
        MapDebugWindow.showMapAutoScale(mapContents).waitForever();
    }

    public static void showMapForever(MapCanvas mapContents) {
        MapDebugWindow.showMap(mapContents).waitForever();
    }

    public static void showMapForever(MapCanvas mapContents, int scale) {
        MapDebugWindow.showMap(mapContents, scale).waitForever();
    }

    public static MapDebugWindow showMapAutoScale(MapCanvas mapContents) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double scale = Math.min(width / (double)(mapContents.getWidth() + 32), height / (double)(mapContents.getHeight() + 64));
        return MapDebugWindow.showMap(mapContents, Math.max(1, MathUtil.floor(scale)));
    }

    public static MapDebugWindow showMap(MapCanvas mapContents) {
        return MapDebugWindow.showMap(mapContents, 1);
    }

    public static MapDebugWindow showMap(MapCanvas mapContents, int scale) {
        final MapDebugWindow window = new MapDebugWindow(new JLabel(), mapContents, scale);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(0);
        f.getContentPane().setLayout(new GridLayout(1, 2));
        f.getContentPane().add(window.label);
        f.pack();
        f.setLocationRelativeTo(null);
        f.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                window._closing = true;
                window.signal();
            }
        });
        f.setVisible(true);
        return window;
    }
}

