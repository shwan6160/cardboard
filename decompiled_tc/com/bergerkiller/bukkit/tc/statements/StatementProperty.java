/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.tc.CollisionMode;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.CollisionMobCategory;
import com.bergerkiller.bukkit.tc.statements.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class StatementProperty
extends Statement {
    private ArrayList<String> properties = new ArrayList();
    private String[] maxspeed = this.add("maxspeed", "speedlimit");
    private String[] playerEnter = this.add("playerenter", "playersenter");
    private String[] playerExit = this.add("playerexit", "playersexit");
    private String[] mobEnter = this.add("mobenter", "mobsenter");

    private String[] add(String ... properties) {
        Collections.addAll(this.properties, properties);
        return properties;
    }

    private boolean match(String[] property, String text) {
        for (String propval : property) {
            if (!text.startsWith(propval)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean match(String text) {
        for (String property : this.properties) {
            if (!text.startsWith(property)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean matchArray(String text) {
        return false;
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        TrainProperties prop = group.getProperties();
        String lower = text.toLowerCase(Locale.ENGLISH);
        if (this.match(this.maxspeed, lower)) {
            return Util.evaluate(prop.getSpeedLimit(), text);
        }
        if (this.match(this.playerEnter, lower)) {
            return prop.getPlayersEnter();
        }
        if (this.match(this.playerExit, lower)) {
            return prop.getPlayersExit();
        }
        if (this.match(this.mobEnter, lower)) {
            return prop.getCollision().mobModes().values().contains((Object)CollisionMode.ENTER);
        }
        CollisionMobCategory category = CollisionMobCategory.findMobType(lower, null, "enter");
        if (category != null) {
            return prop.getCollision().mobMode(category) == CollisionMode.ENTER;
        }
        return super.handle(group, text, event);
    }

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        CartProperties prop = member.getProperties();
        String lower = text.toLowerCase(Locale.ENGLISH);
        if (this.match(this.playerEnter, lower)) {
            return prop.getPlayersEnter();
        }
        if (this.match(this.playerExit, lower)) {
            return prop.getPlayersExit();
        }
        return this.handle(member.getGroup(), text, event);
    }

    @Override
    public boolean requiredEvent() {
        return false;
    }
}

