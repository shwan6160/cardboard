/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.FileConfiguration
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.nbt.CommonTagCompound
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.tickets;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.tickets.Ticket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class TicketStore {
    public static final Ticket DEFAULT = new Ticket("");
    private static final String saveFileName = "tickets.yml";
    private static boolean hasChanges = false;
    private static final HashMap<String, Ticket> ticketMap = new HashMap();
    private static final HashMap<UUID, Ticket> editingMap = new HashMap();
    protected static final String KEY_TICKET_NAME = "ticketName";
    protected static final String KEY_TICKET_CREATION_TIME = "ticketCreationTime";
    protected static final String KEY_TICKET_NUMBER_OF_USES = "ticketNumberOfUses";
    protected static final String KEY_TICKET_OWNER_UUID = "ticketOwner";
    protected static final String KEY_TICKET_OWNER_NAME = "ticketOwnerName";

    public static Ticket createTicket(Ticket baseTicket) {
        Ticket ticket = null;
        int i = 1;
        while (ticket == null) {
            ticket = TicketStore.createTicket(baseTicket, "ticket" + i);
            ++i;
        }
        return ticket;
    }

    public static Ticket createTicket(Ticket baseTicket, String name) {
        if (ticketMap.containsKey(name)) {
            return null;
        }
        Ticket ticket = new Ticket(name);
        ticket.setProperties(baseTicket.getProperties());
        ticket.setPlayerBound(baseTicket.isPlayerBound());
        ticketMap.put(name, ticket);
        TicketStore.markChanged();
        return ticket;
    }

    public static Collection<Ticket> getAll() {
        return ticketMap.values();
    }

    public static Ticket getTicket(String ticketName) {
        return ticketMap.get(ticketName);
    }

    public static boolean removeTicket(String ticketName) {
        Ticket removed = ticketMap.remove(ticketName);
        if (removed == null) {
            return false;
        }
        Iterator<Ticket> editIter = editingMap.values().iterator();
        while (editIter.hasNext()) {
            if (editIter.next() != removed) continue;
            editIter.remove();
        }
        TicketStore.markChanged();
        return true;
    }

    public static boolean renameTicket(String oldTicketName, String newTicketName) {
        if (oldTicketName.equals(newTicketName)) {
            return true;
        }
        if (ticketMap.containsKey(newTicketName)) {
            return false;
        }
        Ticket ticket = ticketMap.remove(oldTicketName);
        if (ticket == null) {
            return false;
        }
        ticket.setName(newTicketName);
        ticketMap.put(newTicketName, ticket);
        TicketStore.markChanged();
        return true;
    }

    public static Ticket getEditing(UUID playerUUID) {
        return editingMap.get(playerUUID);
    }

    public static Ticket getEditing(Player player) {
        return TicketStore.getEditing(player.getUniqueId());
    }

    public static void setEditing(UUID playerUUID, Ticket ticket) {
        editingMap.put(playerUUID, ticket);
    }

    public static void setEditing(Player player, Ticket ticket) {
        TicketStore.setEditing(player.getUniqueId(), ticket);
    }

    public static boolean isTicketItem(ItemStack item) {
        return TicketStore.isTicketItem(CommonItemStack.of((ItemStack)item));
    }

    public static boolean isTicketItem(CommonItemStack item) {
        CommonTagCompound tag = item.getCustomData();
        return tag.containsKey((Object)KEY_TICKET_NAME) && ((String)tag.getValue("plugin", (Object)"")).equals("TrainCarts");
    }

    public static Ticket getTicketFromItem(ItemStack item) {
        return TicketStore.getTicketFromItem(CommonItemStack.of((ItemStack)item));
    }

    public static Ticket getTicketFromItem(CommonItemStack item) {
        CommonTagCompound nbt = item.getCustomData();
        if (nbt.containsKey((Object)KEY_TICKET_NAME)) {
            return ticketMap.get(item.getCustomData().getValue(KEY_TICKET_NAME, (Object)""));
        }
        return null;
    }

    public static int getNumberOfUses(ItemStack item) {
        return TicketStore.getNumberOfUses(CommonItemStack.of((ItemStack)item));
    }

    public static int getNumberOfUses(CommonItemStack item) {
        return (Integer)item.getCustomData().getValue(KEY_TICKET_NUMBER_OF_USES, (Object)0);
    }

    public static boolean isTicketExpired(ItemStack item) {
        return TicketStore.isTicketExpired(CommonItemStack.of((ItemStack)item));
    }

    public static boolean isTicketExpired(CommonItemStack item) {
        long timeCreated;
        long timeNow;
        int numberOfUses;
        Ticket ticket = TicketStore.getTicketFromItem(item);
        if (ticket == null) {
            return true;
        }
        CommonTagCompound tag = item.getCustomData();
        if (ticket.getMaxNumberOfUses() >= 0 && (numberOfUses = ((Integer)tag.getValue(KEY_TICKET_NUMBER_OF_USES, (Object)0)).intValue()) >= ticket.getMaxNumberOfUses()) {
            return true;
        }
        return ticket.getExpirationTime() >= 0L && (timeNow = System.currentTimeMillis()) >= (timeCreated = ((Long)tag.getValue(KEY_TICKET_CREATION_TIME, (Object)timeNow)).longValue()) + ticket.getExpirationTime();
    }

    public static boolean isTicketOwner(Player player, ItemStack item) {
        return TicketStore.isTicketOwner(player, CommonItemStack.of((ItemStack)item));
    }

    public static boolean isTicketOwner(Player player, CommonItemStack item) {
        Ticket ticket = TicketStore.getTicketFromItem(item);
        if (ticket == null || !ticket.isPlayerBound()) {
            return true;
        }
        CommonTagCompound tag = item.getCustomData();
        UUID ownerUUID = tag.getUUID(KEY_TICKET_OWNER_UUID);
        if (ownerUUID == null) {
            return true;
        }
        return ownerUUID.equals(player.getUniqueId());
    }

    public static boolean handleTickets(Player player, TrainProperties trainProperties) {
        if (trainProperties.getTickets().isEmpty()) {
            return true;
        }
        CommonItemStack mainHand = CommonItemStack.of((ItemStack)HumanHand.getItemInMainHand((HumanEntity)player));
        CommonItemStack offHand = CommonItemStack.of((ItemStack)HumanHand.getItemInOffHand((HumanEntity)player));
        if (TicketStore.isSuitableTicket(mainHand, trainProperties)) {
            if (TicketStore.isSuitableTicket(offHand, trainProperties)) {
                Localization.TICKET_CONFLICT.message((CommandSender)player, new String[0]);
                return false;
            }
            if (TicketStore.preUseTicket(player, mainHand, trainProperties)) {
                HumanHand.setItemInMainHand((HumanEntity)player, (ItemStack)TicketStore.useTicketItem(mainHand).toBukkit());
                return true;
            }
            return false;
        }
        if (TicketStore.isSuitableTicket(offHand, trainProperties)) {
            if (TicketStore.preUseTicket(player, offHand, trainProperties)) {
                HumanHand.setItemInOffHand((HumanEntity)player, (ItemStack)TicketStore.useTicketItem(offHand).toBukkit());
                return true;
            }
            return false;
        }
        Ticket mainHandTicket = TicketStore.getTicketFromItem(mainHand);
        Ticket offHandTicket = TicketStore.getTicketFromItem(offHand);
        if (mainHandTicket != null || offHandTicket != null) {
            if (mainHandTicket != null) {
                Localization.TICKET_CONFLICT_TYPE.message((CommandSender)player, new String[]{mainHandTicket.getName()});
            }
            if (offHandTicket != null) {
                Localization.TICKET_CONFLICT_TYPE.message((CommandSender)player, new String[]{offHandTicket.getName()});
            }
            return false;
        }
        TicketHandleResult result = TicketStore.handleTicketsInventory(player, true, trainProperties);
        if (result == TicketHandleResult.MISSING) {
            result = TicketStore.handleTicketsInventory(player, false, trainProperties);
        }
        if (result == TicketHandleResult.MISSING) {
            Localization.TICKET_REQUIRED.message((CommandSender)player, new String[0]);
        }
        return result == TicketHandleResult.OK;
    }

    private static TicketHandleResult handleTicketsInventory(Player player, boolean quickbar, TrainProperties trainProperties) {
        PlayerInventory inventory = player.getInventory();
        int ticketInvIndex = -1;
        int start = quickbar ? 0 : 9;
        int end = quickbar ? 9 : inventory.getSize();
        for (int i = start; i < end; ++i) {
            CommonItemStack item = CommonItemStack.of((ItemStack)inventory.getItem(i));
            if (!TicketStore.isSuitableTicket(item, trainProperties) || TicketStore.isTicketExpired(item)) continue;
            if (ticketInvIndex != -1) {
                Localization.TICKET_CONFLICT.message((CommandSender)player, new String[0]);
                return TicketHandleResult.FAILURE;
            }
            ticketInvIndex = i;
        }
        if (ticketInvIndex == -1) {
            return TicketHandleResult.MISSING;
        }
        CommonItemStack ticketItem = CommonItemStack.of((ItemStack)inventory.getItem(ticketInvIndex));
        if (TicketStore.preUseTicket(player, ticketItem, trainProperties)) {
            inventory.setItem(ticketInvIndex, TicketStore.useTicketItem(ticketItem).toBukkit());
            return TicketHandleResult.OK;
        }
        return TicketHandleResult.FAILURE;
    }

    private static boolean isSuitableTicket(CommonItemStack item, TrainProperties trainProperties) {
        Ticket ticket = TicketStore.getTicketFromItem(item);
        if (ticket != null) {
            for (String allowed : trainProperties.getTickets()) {
                if (!ticket.getName().equals(allowed) && (LogicUtil.nullOrEmpty((String)ticket.getRealm()) || !ticket.getRealm().equals(allowed))) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean preUseTicket(Player player, CommonItemStack item, TrainProperties trainProperties) {
        String ticketName = (String)item.getCustomData().getValue(KEY_TICKET_NAME, (Object)"UNKNOWN");
        if (!TicketStore.isTicketOwner(player, item)) {
            String ownerName = (String)item.getCustomData().getValue(KEY_TICKET_OWNER_NAME, (Object)"UNKNOWN");
            Localization.TICKET_CONFLICT_OWNER.message((CommandSender)player, new String[]{ticketName, ownerName});
            return false;
        }
        if (TicketStore.isTicketExpired(item)) {
            Localization.TICKET_EXPIRED.message((CommandSender)player, new String[]{ticketName});
            return false;
        }
        Localization.TICKET_USED.message((CommandSender)player, new String[]{ticketName});
        Ticket ticket = TicketStore.getTicketFromItem(item);
        if (ticket != null) {
            ConfigurationNode ticketTrainProperties = ticket.getProperties().clone();
            ticketTrainProperties.remove("carts");
            trainProperties.apply(ticketTrainProperties);
            MinecartGroup group = trainProperties.getHolder();
            if (group != null) {
                group.onPropertiesChanged();
            }
        }
        return true;
    }

    private static CommonItemStack useTicketItem(CommonItemStack item) {
        Ticket ticket = TicketStore.getTicketFromItem(item);
        if (ticket == null) {
            return CommonItemStack.empty();
        }
        item = item.clone();
        if (ticket.getMaxNumberOfUses() < 0 || item.getAmount() <= 1) {
            item.updateCustomData(tag -> tag.putValue(KEY_TICKET_NUMBER_OF_USES, (Object)((Integer)tag.getValue(KEY_TICKET_NUMBER_OF_USES, (Object)0) + 1)));
        } else {
            item.setAmount(item.getAmount() - 1);
        }
        if (TicketStore.isTicketExpired(item)) {
            return CommonItemStack.empty();
        }
        return item;
    }

    public static void markChanged() {
        hasChanges = true;
    }

    public static void load(TrainCarts traincarts) {
        FileConfiguration config = new FileConfiguration((JavaPlugin)traincarts, saveFileName);
        config.load();
        ticketMap.clear();
        editingMap.clear();
        for (ConfigurationNode node : config.getNodes()) {
            Ticket ticket = new Ticket(node.getName());
            ticket.load(node);
            ticketMap.put(ticket.getName(), ticket);
        }
        hasChanges = false;
        traincarts.getDataFile(new String[]{"images"}).mkdirs();
    }

    public static void save(TrainCarts traincarts, boolean autosave) {
        if (autosave && !hasChanges) {
            return;
        }
        FileConfiguration config = new FileConfiguration((JavaPlugin)traincarts, saveFileName);
        for (Ticket ticket : ticketMap.values()) {
            ticket.save(config.getNode(ticket.getName()));
        }
        config.save();
        hasChanges = false;
    }

    private static enum TicketHandleResult {
        MISSING,
        FAILURE,
        OK;

    }
}

