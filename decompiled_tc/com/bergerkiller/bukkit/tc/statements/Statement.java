/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.StatementBoolean;
import com.bergerkiller.bukkit.tc.statements.StatementDestination;
import com.bergerkiller.bukkit.tc.statements.StatementDirection;
import com.bergerkiller.bukkit.tc.statements.StatementEmpty;
import com.bergerkiller.bukkit.tc.statements.StatementFuel;
import com.bergerkiller.bukkit.tc.statements.StatementMob;
import com.bergerkiller.bukkit.tc.statements.StatementName;
import com.bergerkiller.bukkit.tc.statements.StatementOwners;
import com.bergerkiller.bukkit.tc.statements.StatementPassenger;
import com.bergerkiller.bukkit.tc.statements.StatementPermission;
import com.bergerkiller.bukkit.tc.statements.StatementPlayerHand;
import com.bergerkiller.bukkit.tc.statements.StatementPlayerItems;
import com.bergerkiller.bukkit.tc.statements.StatementProperty;
import com.bergerkiller.bukkit.tc.statements.StatementRandom;
import com.bergerkiller.bukkit.tc.statements.StatementRedstone;
import com.bergerkiller.bukkit.tc.statements.StatementTag;
import com.bergerkiller.bukkit.tc.statements.StatementTrainItems;
import com.bergerkiller.bukkit.tc.statements.StatementType;
import com.bergerkiller.bukkit.tc.statements.StatementVelocity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Statement {
    private static final List<Statement> statements = new ArrayList<Statement>();

    public static String[] parseArray(String text) {
        return text.split(";", -1);
    }

    public static void init() {
        Statement.register(new StatementDestination());
        Statement.register(StatementBoolean.INSTANCE);
        Statement.register(new StatementRandom());
        Statement.register(new StatementProperty());
        Statement.register(new StatementName());
        Statement.register(new StatementEmpty());
        Statement.register(new StatementPassenger());
        Statement.register(new StatementOwners());
        Statement.register(new StatementTrainItems());
        Statement.register(new StatementFuel());
        Statement.register(new StatementType());
        Statement.register(new StatementVelocity());
        Statement.register(new StatementPlayerItems());
        Statement.register(new StatementPlayerHand());
        Statement.register(new StatementMob());
        Statement.register(new StatementRedstone());
        Statement.register(new StatementPermission());
        Statement.register(new StatementDirection());
        Statement.register(new StatementTag());
    }

    public static void deinit() {
        statements.clear();
    }

    public static <T extends Statement> T register(T statement) {
        int index = Collections.binarySearch(statements, statement, (a, b) -> Integer.compare(b.priority(), a.priority()));
        if (index < 0) {
            index ^= 0xFFFFFFFF;
        }
        int itemPriority = statement.priority();
        while (index > 0 && statements.get(index - 1).priority() == itemPriority) {
            --index;
        }
        statements.add(index, statement);
        return statement;
    }

    public static boolean has(MinecartMember<?> member, String text, SignActionEvent event) {
        return Statement.has(member, null, text, event);
    }

    public static boolean has(MinecartGroup group, String text, SignActionEvent event) {
        return Statement.has(null, group, text, event);
    }

    public static boolean has(MinecartMember<?> member, MinecartGroup group, String text, SignActionEvent event) {
        return Matcher.of(text).withMember(member).withGroup(group).withSignEvent(event).match().has();
    }

    public static boolean hasMultiple(MinecartMember<?> member, Iterable<String> statementTexts, SignActionEvent event) {
        return Statement.hasMultiple(member, null, statementTexts, event);
    }

    public static boolean hasMultiple(MinecartGroup group, Iterable<String> statementTexts, SignActionEvent event) {
        return Statement.hasMultiple(null, group, statementTexts, event);
    }

    public static boolean hasMultiple(MinecartMember<?> member, MinecartGroup group, Iterable<String> statementTexts, SignActionEvent event) {
        boolean match = true;
        for (String statementText : statementTexts) {
            if (statementText.isEmpty()) continue;
            boolean isLogicAnd = true;
            if (statementText.startsWith("&")) {
                isLogicAnd = true;
                statementText = statementText.substring(1);
            } else if (statementText.startsWith("|")) {
                isLogicAnd = false;
                statementText = statementText.substring(1);
            }
            boolean result = Statement.has(member, group, statementText, event);
            if (isLogicAnd) {
                match &= result;
                continue;
            }
            match |= result;
        }
        return match;
    }

    public abstract boolean match(String var1);

    public abstract boolean matchArray(String var1);

    public boolean requiresTrain() {
        return true;
    }

    public boolean requiredEvent() {
        return false;
    }

    public boolean isConstant() {
        return false;
    }

    public int priority() {
        return 0;
    }

    public boolean hasRequiredContext(MinecartMember<?> member, MinecartGroup group, SignActionEvent event) {
        if (member == null && group == null && this.requiresTrain()) {
            return false;
        }
        return event != null || !this.requiredEvent();
    }

    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        for (MinecartMember<?> member : group) {
            if (!this.handle(member, text, event)) continue;
            return true;
        }
        return false;
    }

    public boolean handleArray(MinecartGroup group, String[] text, SignActionEvent event) {
        for (MinecartMember<?> member : group) {
            if (!this.handleArray(member, text, event)) continue;
            return true;
        }
        return false;
    }

    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        return false;
    }

    public boolean handleArray(MinecartMember<?> member, String[] text, SignActionEvent event) {
        return false;
    }

    public static class Matcher {
        private final String text;
        private MinecartGroup group;
        private MinecartMember<?> member;
        private SignActionEvent signEvent;

        private Matcher(String text) {
            this.text = text;
        }

        public static Matcher of(String text) {
            return new Matcher(text);
        }

        public Matcher withGroup(MinecartGroup group) {
            this.group = group;
            return this;
        }

        public Matcher withMember(MinecartMember<?> member) {
            this.member = member;
            return this;
        }

        public Matcher withSignEvent(SignActionEvent event) {
            this.signEvent = event;
            return this;
        }

        public MatchResult match() {
            boolean inv = false;
            String text = TCConfig.statementShortcuts.replace(this.text);
            while (!text.isEmpty() && text.charAt(0) == '!') {
                text = text.substring(1);
                inv = !inv;
            }
            if (text.isEmpty()) {
                return MatchResult.create(StatementBoolean.EMPTY, false, inv);
            }
            String lowerText = text.toLowerCase();
            int idx = lowerText.indexOf(64);
            String arrayText = idx == -1 ? null : lowerText.substring(0, idx);
            String[] array = idx == -1 ? null : Statement.parseArray(text.substring(idx + 1));
            for (Statement statement : statements) {
                if (arrayText != null && statement.matchArray(arrayText)) {
                    if (!statement.hasRequiredContext(this.member, this.group, this.signEvent)) {
                        return MatchResult.createWithMissingContext(statement, true, inv);
                    }
                    if (this.member != null) {
                        return MatchResult.create(statement, true, statement.handleArray(this.member, array, this.signEvent) != inv);
                    }
                    if (this.group != null) {
                        return MatchResult.create(statement, true, statement.handleArray(this.group, array, this.signEvent) != inv);
                    }
                    return MatchResult.create(statement, true, statement.handleArray((MinecartMember)null, array, this.signEvent) != inv);
                }
                if (!statement.match(lowerText)) continue;
                if (!statement.hasRequiredContext(this.member, this.group, this.signEvent)) {
                    return MatchResult.createWithMissingContext(statement, false, inv);
                }
                if (this.member != null) {
                    return MatchResult.create(statement, false, statement.handle(this.member, text, this.signEvent) != inv);
                }
                if (this.group != null) {
                    return MatchResult.create(statement, false, statement.handle(this.group, text, this.signEvent) != inv);
                }
                return MatchResult.create(statement, false, statement.handle((MinecartMember)null, text, this.signEvent) != inv);
            }
            return MatchResult.createWithMissingContext(StatementBoolean.EMPTY, false, inv);
        }
    }

    public static class MatchResult {
        private final Statement statement;
        private final boolean isArray;
        private final boolean isMissingContext;
        private final boolean has;

        public static MatchResult create(Statement statement, boolean isArray, boolean has) {
            return new MatchResult(statement, isArray, false, has);
        }

        public static MatchResult createWithMissingContext(Statement statement, boolean isArray, boolean inv) {
            return new MatchResult(statement, isArray, true, inv);
        }

        private MatchResult(Statement statement, boolean isArray, boolean isMissingContext, boolean has) {
            this.statement = statement;
            this.isArray = isArray;
            this.isMissingContext = isMissingContext;
            this.has = has;
        }

        public Statement statement() {
            return this.statement;
        }

        public boolean has() {
            return this.has;
        }

        public boolean isMissingContext() {
            return this.isMissingContext;
        }

        public boolean isArray() {
            return this.isArray;
        }

        public boolean isConstant() {
            return this.statement.isConstant();
        }

        public boolean isExactMatch() {
            if (this.statement == StatementBoolean.EMPTY) {
                return false;
            }
            return !(this.statement instanceof StatementTag) || this.isArray;
        }
    }
}

