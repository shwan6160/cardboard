package org.cardboardpowered.mixin.server.level;

import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import org.cardboardpowered.asm.TransformAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Ticket.class)
public abstract class TicketMixin {

    /**
     * @author Antigravity
     * @reason Support Ticket.of static factory method added by Paper/Spigot for BKCommonLib compatibility
     */
    @SuppressWarnings("rawtypes")
    @TransformAccess(9) // 9 is ACC_PUBLIC | ACC_STATIC. Promotes visibility to public static in postApply processor.
    private static Ticket of(TicketType type, int ticketLevel, Object key) {
        return new Ticket(type, ticketLevel);
    }
}
