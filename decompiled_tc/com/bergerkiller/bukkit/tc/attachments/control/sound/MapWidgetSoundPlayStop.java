/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundButton;
import java.util.ArrayList;
import java.util.List;

public abstract class MapWidgetSoundPlayStop
extends MapWidget {
    private final PlayButton play = new PlayButton();
    private final StopButton stop = new StopButton();
    private boolean sentPlay = false;

    public abstract void onPlay();

    public abstract void onStop();

    public void onAttached() {
        this.addWidget(this.play.setBounds(0, 0, 11, 11));
        this.addWidget(this.stop.setBounds(this.play.getWidth() + 1, 0, 11, 11));
    }

    public void onDetached() {
        if (this.sentPlay) {
            this.sentPlay = false;
            this.onStop();
        }
        super.onDetached();
    }

    private void sendPlay() {
        this.sentPlay = true;
        this.onPlay();
    }

    private void sendStop() {
        this.sentPlay = false;
        this.onStop();
    }

    private class PlayButton
    extends MapWidgetSoundButton {
        private static final int AUTOPLAY_START_DELAY = 5;
        private static final int AUTOPLAY_ACTIVATION_TIME = 20;
        private int autoPlayCtr = 0;
        private int autoPlayInterval = 20;
        private boolean autoPlayActive = false;
        private int ticksSinceLastPress = 0;
        private List<HighlightPixel> highlightPixels = new ArrayList<HighlightPixel>();
        private HighlightPixel lastDrawn = null;

        private PlayButton() {
        }

        public void disableAutoPlay() {
            if (this.autoPlayActive) {
                this.autoPlayActive = false;
                this.resetPlayCounters();
                this.invalidate();
            }
        }

        private void resetPlayCounters() {
            this.autoPlayCtr = 0;
            this.ticksSinceLastPress = 0;
            this.lastDrawn = null;
        }

        public void onAttached() {
            int y;
            int x;
            this.lastDrawn = null;
            this.highlightPixels.clear();
            for (x = this.getWidth() / 2; x <= this.getWidth() - 3; ++x) {
                this.addHighlightPixel(x, 1);
            }
            for (y = 2; y <= this.getHeight() - 3; ++y) {
                this.addHighlightPixel(this.getWidth() - 2, y);
            }
            for (x = this.getWidth() - 3; x >= 2; --x) {
                this.addHighlightPixel(x, this.getHeight() - 2);
            }
            for (y = this.getHeight() - 3; y >= 2; --y) {
                this.addHighlightPixel(1, y);
            }
            for (x = 2; x < this.getWidth() / 2; ++x) {
                this.addHighlightPixel(x, 1);
            }
            this.highlightPixels.get((int)(this.highlightPixels.size() - 1)).next = this.highlightPixels.get(0);
        }

        @Override
        public void onClick() {
            if (this.autoPlayActive) {
                this.autoPlayInterval = this.ticksSinceLastPress;
                this.resetPlayCounters();
            }
            MapWidgetSoundPlayStop.this.sendPlay();
        }

        @Override
        public void onClickHold(int ticksHeld) {
            if (!this.autoPlayActive && ticksHeld >= 5) {
                if (ticksHeld >= 25) {
                    this.autoPlayActive = true;
                    this.resetPlayCounters();
                    MapWidgetSoundPlayStop.this.sendPlay();
                }
                this.invalidate();
            } else if (ticksHeld == 1) {
                this.invalidate();
            }
        }

        public void onFocus() {
            this.ticksSinceLastPress = this.autoPlayCtr;
        }

        public void onTick() {
            if (this.autoPlayActive) {
                ++this.ticksSinceLastPress;
                if (++this.autoPlayCtr >= this.autoPlayInterval) {
                    this.autoPlayCtr = 0;
                    MapWidgetSoundPlayStop.this.sendPlay();
                }
                this.invalidate();
            }
        }

        @Override
        public void onDraw() {
            HighlightPixel p;
            byte highlightColor = this.isFocused() ? MapColorPalette.getColor((int)200, (int)200, (int)150) : MapColorPalette.getColor((int)140, (int)140, (int)0);
            boolean isPlayPressed = false;
            if (this.autoPlayActive && this.autoPlayCtr == 0 || this.pressed && this.pressedTicks == 0) {
                this.drawBackground(highlightColor, MapColorPalette.getColor((int)36, (int)89, (int)152), MapColorPalette.getColor((int)44, (int)109, (int)186));
                isPlayPressed = true;
            } else {
                isPlayPressed = this.pressed;
                super.onDraw();
            }
            if (this.autoPlayActive) {
                HighlightPixel curr = this.getHighlightProgress(this.autoPlayCtr, this.autoPlayInterval);
                if (this.lastDrawn != null && this.lastDrawn != curr) {
                    p = this.lastDrawn.next;
                    while (p != curr) {
                        this.view.writePixel(p.x, p.y, highlightColor);
                        p = p.next;
                    }
                }
                this.lastDrawn = curr;
                this.view.writePixel(curr.x, curr.y, highlightColor);
            } else if (this.pressedTicks >= 5) {
                HighlightPixel end = this.getHighlightProgress(this.pressedTicks - 5, 20);
                p = this.highlightPixels.get(0);
                while (true) {
                    this.view.writePixel(p.x, p.y, highlightColor);
                    if (p == end) break;
                    p = p.next;
                }
            }
            byte color = isPlayPressed ? (byte)30 : (byte)MapColorPalette.getColor((int)0, (int)180, (int)0);
            int play_height = this.getHeight() - 4;
            int play_width = (play_height + 1) / 2;
            int play_x = (this.getWidth() - play_width + 1) / 2;
            int play_y = 2;
            for (int dx = 0; dx < play_width && play_height > 0; ++dx) {
                for (int dy = 0; dy < play_height; ++dy) {
                    this.view.writePixel(play_x + dx, play_y + dy, color);
                }
                play_height -= 2;
                ++play_y;
            }
        }

        private HighlightPixel getHighlightProgress(int mul, int div) {
            if (div <= 0) {
                return this.highlightPixels.get(0);
            }
            int index = this.highlightPixels.size() * Math.floorMod(mul, div) / div;
            index = Math.min(index, this.highlightPixels.size() - 1);
            return this.highlightPixels.get(index);
        }

        private void addHighlightPixel(int x, int y) {
            HighlightPixel curr = new HighlightPixel(this.highlightPixels.size(), x, y);
            this.highlightPixels.add(curr);
            if (this.highlightPixels.size() > 1) {
                this.highlightPixels.get((int)(this.highlightPixels.size() - 2)).next = curr;
            }
        }

        private class HighlightPixel {
            public final int x;
            public final int y;
            public final int index;
            public HighlightPixel next;

            public HighlightPixel(int index, int x, int y) {
                this.index = index;
                this.x = x;
                this.y = y;
            }
        }
    }

    private class StopButton
    extends MapWidgetSoundButton {
        private StopButton() {
        }

        @Override
        public void onClick() {
            MapWidgetSoundPlayStop.this.play.disableAutoPlay();
            MapWidgetSoundPlayStop.this.sendStop();
        }

        @Override
        public void onDraw() {
            super.onDraw();
            this.view.fillRectangle(3, 3, this.getWidth() - 6, this.getHeight() - 6, this.pressed ? MapColorPalette.getColor((int)180, (int)0, (int)0) : (byte)18);
        }
    }
}

