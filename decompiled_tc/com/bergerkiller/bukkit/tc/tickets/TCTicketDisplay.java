/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapSessionMode
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.tickets;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapSessionMode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.tickets.Ticket;
import com.bergerkiller.bukkit.tc.tickets.TicketStore;
import org.bukkit.entity.Player;

public class TCTicketDisplay
extends MapDisplay {
    public void onAttached() {
        this.setSessionMode(MapSessionMode.VIEWING);
        this.setGlobal(false);
        this.renderBackground();
        this.renderTicket();
    }

    public void onMapItemChanged() {
        this.renderTicket();
    }

    public void renderBackground() {
        Ticket ticket = TicketStore.getTicketFromItem(this.getMapItem());
        MapTexture bg = ticket == null ? Ticket.getDefaultBackgroundImage() : ticket.loadBackgroundImage();
        this.getLayer().draw((MapCanvas)bg, 0, 0);
    }

    private void renderTicket() {
        this.getLayer(1).clear();
        Ticket ticket = TicketStore.getTicketFromItem(this.getMapItem());
        if (ticket == null) {
            this.getLayer(1).draw(MapFont.MINECRAFT, 10, 40, (byte)18, (CharSequence)Localization.TICKET_MAP_INVALID.get(new String[0]));
        } else {
            this.getLayer(1).draw(MapFont.MINECRAFT, 10, 40, (byte)119, (CharSequence)ticket.getName());
            if (TicketStore.isTicketExpired(this.getMapItem())) {
                this.getLayer(1).draw(MapFont.MINECRAFT, 10, 57, (byte)18, (CharSequence)Localization.TICKET_MAP_EXPIRED.get(new String[0]));
            } else {
                int numUses;
                int maxUses = ticket.getMaxNumberOfUses();
                int n = numUses = maxUses == 1 ? 0 : TicketStore.getNumberOfUses(this.getMapItem());
                if (maxUses < 0) {
                    maxUses = -1;
                }
                String text = Localization.TICKET_MAP_USES.get(Integer.toString(maxUses), Integer.toString(numUses));
                this.getLayer(1).draw(MapFont.MINECRAFT, 10, 57, (byte)119, (CharSequence)text);
            }
            String ownerName = (String)this.getCommonMapItem().getCustomData().getValue("ticketOwnerName", (Object)"Unknown Owner");
            ownerName = StringUtil.stripChatStyle((String)ownerName);
            if (TicketStore.isTicketOwner((Player)this.getOwners().get(0), this.getMapItem())) {
                this.getLayer(1).draw(MapFont.MINECRAFT, 10, 74, (byte)119, (CharSequence)ownerName);
            } else {
                this.getLayer(1).draw(MapFont.MINECRAFT, 10, 74, (byte)18, (CharSequence)ownerName);
            }
        }
    }
}

