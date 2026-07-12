/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.controller.functions.inputs;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelection;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentSelector;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInput;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInputSpeed;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.util.Collections;
import java.util.List;

public class TransferFunctionInputSeatOccupied
extends TransferFunctionInput {
    public static final TransferFunction.Serializer<TransferFunctionInputSeatOccupied> SERIALIZER = new TransferFunction.Serializer<TransferFunctionInputSeatOccupied>(){

        @Override
        public String typeId() {
            return "INPUT-SEAT-OCCUPIED";
        }

        @Override
        public String title() {
            return "In: Seat Occupied";
        }

        @Override
        public boolean isInput() {
            return true;
        }

        @Override
        public boolean isListed(TransferFunctionHost host) {
            return host.isAttachment();
        }

        @Override
        public TransferFunctionInputSeatOccupied createNew(TransferFunctionHost host) {
            TransferFunctionInputSeatOccupied function = new TransferFunctionInputSeatOccupied();
            function.updateSource(host);
            return function;
        }

        @Override
        public TransferFunctionInputSeatOccupied load(TransferFunctionHost host, ConfigurationNode config) {
            TransferFunctionInputSeatOccupied function = new TransferFunctionInputSeatOccupied();
            function.setSeatSelector(AttachmentSelector.readFromConfig(config, "seat").withType(CartAttachmentSeat.class));
            function.updateSource(host);
            return function;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionInputSeatOccupied function) {
            function.getSeatSelector().writeToConfig(config, "seat");
        }
    };
    private AttachmentSelector<CartAttachmentSeat> seatSelector = AttachmentSelector.all(CartAttachmentSeat.class);

    public void setNameFilter(String name) {
        this.seatSelector = this.seatSelector.withName(name);
    }

    public void setSeatSelector(AttachmentSelector<CartAttachmentSeat> selector) {
        this.seatSelector = selector;
    }

    public AttachmentSelector<CartAttachmentSeat> getSeatSelector() {
        return this.seatSelector;
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public TransferFunctionInput.ReferencedSource createSource(TransferFunctionHost host) {
        Attachment attachment = host.getAttachment();
        if (attachment != null) {
            return new SeatOccupiedReferencedSource(attachment.getSelection(this.seatSelector));
        }
        return TransferFunctionInput.ReferencedSource.NONE;
    }

    @Override
    protected TransferFunctionInput cloneInput() {
        return new TransferFunctionInputSpeed();
    }

    @Override
    public boolean isBooleanOutput() {
        return true;
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 0, 3, (byte)30, (CharSequence)"<Seat Occupied>");
    }

    @Override
    public void openDialog(final TransferFunction.Dialog dialog) {
        super.openDialog(dialog);
        dialog.addLabel(29, 21, (byte)18, "Monitored Seats");
        dialog.addWidget(new SeatNameWidget(this){
            final /* synthetic */ TransferFunctionInputSeatOccupied this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onChanged() {
                dialog.markChanged();
            }

            @Override
            public List<String> getSeatNames(AttachmentSelector<CartAttachmentSeat> allSelector) {
                Attachment attachment = dialog.getHost().getAttachment();
                if (attachment != null) {
                    return attachment.getSelection(allSelector).names();
                }
                return Collections.emptyList();
            }
        }).setBounds(11, 27, 92, 13);
    }

    private static class SeatOccupiedReferencedSource
    extends TransferFunctionInput.ReferencedSource {
        private final AttachmentSelection<CartAttachmentSeat> seatSelection;

        public SeatOccupiedReferencedSource(AttachmentSelection<CartAttachmentSeat> seatSelection) {
            this.seatSelection = seatSelection;
        }

        @Override
        public void onTick() {
            this.seatSelection.sync();
            double result = 0.0;
            for (CartAttachmentSeat seat : this.seatSelection) {
                if (seat.getEntity() == null) continue;
                result = 1.0;
                break;
            }
            this.value = result;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof SeatOccupiedReferencedSource) {
                SeatOccupiedReferencedSource other = (SeatOccupiedReferencedSource)o;
                return this.seatSelection.selector().equals(other.seatSelection.selector());
            }
            return false;
        }
    }

    private abstract class SeatNameWidget
    extends MapWidget {
        private final byte COLOR_BG_DEFAULT = MapColorPalette.getColor((int)199, (int)199, (int)199);
        private final byte COLOR_BG_FOCUSED = MapColorPalette.getColor((int)255, (int)252, (int)245);

        public SeatNameWidget() {
            this.setFocusable(true);
        }

        public abstract void onChanged();

        public abstract List<String> getSeatNames(AttachmentSelector<CartAttachmentSeat> var1);

        public void onActivate() {
            this.getParent().addWidget(new MapWidgetAttachmentSelector<CartAttachmentSeat>(TransferFunctionInputSeatOccupied.this.getSeatSelector()){

                @Override
                public List<String> getAttachmentNames(AttachmentSelector<CartAttachmentSeat> allSelector) {
                    return SeatNameWidget.this.getSeatNames(allSelector);
                }

                @Override
                public void onSelected(AttachmentSelector<CartAttachmentSeat> selection) {
                    TransferFunctionInputSeatOccupied.this.setSeatSelector(selection);
                    SeatNameWidget.this.onChanged();
                }
            }.setTitle("Set Seat name").includeAny("<Any Seat>"));
        }

        public void onDraw() {
            byte textColor;
            String text;
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
            this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.isFocused() ? this.COLOR_BG_FOCUSED : this.COLOR_BG_DEFAULT);
            if (TransferFunctionInputSeatOccupied.this.seatSelector.nameFilter().isPresent()) {
                text = TransferFunctionInputSeatOccupied.this.seatSelector.nameFilter().get();
                textColor = this.isFocused() ? (byte)50 : 119;
            } else {
                text = "<Any Seat>";
                textColor = MapColorPalette.getColor((int)128, (int)128, (int)128);
            }
            int textWidth = (int)this.view.calcFontSize(MapFont.MINECRAFT, (CharSequence)text).getWidth();
            this.view.draw(MapFont.MINECRAFT, (this.getWidth() - textWidth + 1) / 2, 3, textColor, (CharSequence)text);
        }
    }
}

