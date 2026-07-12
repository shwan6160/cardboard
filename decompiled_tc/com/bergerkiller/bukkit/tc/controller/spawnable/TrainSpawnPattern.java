/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.controller.spawnable;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableMember;
import com.bergerkiller.bukkit.tc.properties.SavedTrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public abstract class TrainSpawnPattern {
    public static final int MAX_SPAWNABLE_TRAIN_LENGTH = 1024;
    private final QuantityPrefix quantity;

    protected TrainSpawnPattern(QuantityPrefix quantity) {
        this.quantity = quantity;
    }

    public QuantityPrefix quantity() {
        return this.quantity;
    }

    public int amount() {
        return this.quantity().amount;
    }

    protected abstract Applier newGroupApplier();

    protected Applier repeatWithAmount(Applier callback) {
        int amount = this.quantity().amount;
        if (amount <= 0) {
            return (group, random, savedTrainMatcher) -> {};
        }
        if (amount == 1) {
            return callback;
        }
        return (group, random, savedTrainMatcher) -> {
            for (int n = 0; n < amount; ++n) {
                callback.apply(group, random, savedTrainMatcher);
            }
        };
    }

    public static ParsedSpawnPattern parse(String spawnPattern, Function<String, String> savedTrainMatcher) {
        Parser parser = new Parser(spawnPattern, 0, savedTrainMatcher);
        parser.parse();
        return parser.createSpawnPattern();
    }

    public static String findNameInSortedList(List<String> sortedNames, String input) {
        int index = Collections.binarySearch(sortedNames, input);
        if (index >= 0) {
            return sortedNames.get(index);
        }
        String longestPrefix = null;
        ListIterator<String> iter = sortedNames.listIterator(-(index + 1));
        while (iter.hasPrevious()) {
            String name = iter.previous();
            if (input.startsWith(name) && (longestPrefix == null || name.length() > longestPrefix.length())) {
                longestPrefix = name;
                continue;
            }
            if (longestPrefix == null) continue;
            break;
        }
        return longestPrefix;
    }

    private static Applier twoStage(Function<SpawnableGroup, List<SpawnableMember>> initializer) {
        return new TwoStageApplier(initializer);
    }

    public static interface Applier {
        public void apply(SpawnableGroup var1, Random var2, Function<String, String> var3);

        default public Applier reverse() {
            Applier base = this;
            return (group, random, savedTrainMatcher) -> {
                int countBefore = group.getMembers().size();
                try {
                    base.apply(group, random, savedTrainMatcher);
                }
                finally {
                    List<SpawnableMember> added = group.getMembers().subList(countBefore, group.getMembers().size());
                    Collections.reverse(added);
                    ListIterator<SpawnableMember> iter = added.listIterator();
                    while (iter.hasNext()) {
                        iter.set(iter.next().cloneReversed());
                    }
                }
            };
        }
    }

    public static class QuantityPrefix {
        public static final QuantityPrefix ZERO = new QuantityPrefix(0);
        public static final QuantityPrefix ONE = new QuantityPrefix(1);
        public final int amount;
        public final double chanceWeight;

        public QuantityPrefix(int amount) {
            this(amount, Double.NaN);
        }

        public QuantityPrefix(int amount, double chanceWeight) {
            this.amount = amount;
            this.chanceWeight = chanceWeight;
        }

        public boolean isOne() {
            return this.amount == 1 && Double.isNaN(this.chanceWeight);
        }

        public boolean hasChanceWeight() {
            return !Double.isNaN(this.chanceWeight);
        }

        public String toString() {
            if (this.hasChanceWeight()) {
                StringBuilder str = new StringBuilder();
                if (this.chanceWeight == Math.floor(this.chanceWeight)) {
                    str.append((int)this.chanceWeight);
                } else {
                    str.append(this.chanceWeight);
                }
                str.append('%');
                if (this.amount != 1) {
                    str.append(this.amount);
                }
                return str.toString();
            }
            return this.isOne() ? "" : Integer.toString(this.amount);
        }
    }

    private static class Parser {
        private final StringBuilder quantityBuilder = new StringBuilder();
        private final String spawnPattern;
        private final int startIndex;
        private final Function<String, String> savedTrainMatcher;
        public final List<TrainSpawnPattern> patterns = new ArrayList<TrainSpawnPattern>();
        public SpawnableGroup.CenterMode centerMode = SpawnableGroup.CenterMode.NONE;
        private boolean foundSequenceEnd = false;

        public Parser(String spawnPattern, int startIndex, Function<String, String> savedTrainMatcher) {
            this.spawnPattern = spawnPattern;
            this.startIndex = startIndex;
            this.savedTrainMatcher = savedTrainMatcher;
        }

        public boolean hasPatterns() {
            return !this.patterns.isEmpty();
        }

        public boolean hasParsedContent() {
            return !this.patterns.isEmpty() || !this.quantityBuilder.toString().trim().isEmpty();
        }

        public SequenceSpawnPattern toSequence(QuantityPrefix quantity) {
            return new SequenceSpawnPattern(quantity, this.patterns);
        }

        public void addPattern(TrainSpawnPattern pattern) {
            this.patterns.add(pattern);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public QuantityPrefix consumeQuantity() {
            try {
                int chanceIndex = this.quantityBuilder.indexOf("%");
                if (chanceIndex != -1) {
                    double chanceWeight = ParseUtil.parseDouble((String)this.quantityBuilder.substring(0, chanceIndex), (double)0.0);
                    if (chanceWeight <= 0.0) {
                        QuantityPrefix quantityPrefix = QuantityPrefix.ZERO;
                        return quantityPrefix;
                    }
                    int amount = ParseUtil.parseInt((String)this.quantityBuilder.substring(chanceIndex + 1), (int)1);
                    QuantityPrefix quantityPrefix = new QuantityPrefix(amount, chanceWeight);
                    return quantityPrefix;
                }
                int amount = ParseUtil.parseInt((String)this.quantityBuilder.toString(), (int)1);
                QuantityPrefix quantityPrefix = new QuantityPrefix(amount);
                return quantityPrefix;
            }
            finally {
                this.quantityBuilder.setLength(0);
            }
        }

        public ParsedSpawnPattern createSpawnPattern() {
            return this.toSequence(QuantityPrefix.ONE).simplify().asSpawnPattern(this.centerMode);
        }

        public int parse() {
            int index = this.startIndex;
            String spawnPattern = this.spawnPattern;
            while (index < spawnPattern.length()) {
                char c = spawnPattern.charAt(index);
                if (LogicUtil.containsChar((char)c, (CharSequence)"[<({")) {
                    Parser subParser = new Parser(spawnPattern, index + 1, this.savedTrainMatcher);
                    int subEndIndex = subParser.parse();
                    if (subEndIndex == spawnPattern.length() && this.startIndex == 0 && !this.hasParsedContent()) {
                        this.centerMode = subParser.foundSequenceEnd ? SpawnableGroup.CenterMode.MIDDLE : SpawnableGroup.CenterMode.RIGHT;
                        for (TrainSpawnPattern pattern : subParser.patterns) {
                            this.addPattern(pattern);
                        }
                        return subEndIndex;
                    }
                    this.addPattern(subParser.toSequence(this.consumeQuantity()));
                    index = subEndIndex;
                    continue;
                }
                if (LogicUtil.containsChar((char)c, (CharSequence)"]>)}")) {
                    if (this.startIndex > 0) {
                        this.foundSequenceEnd = true;
                        return index + 1;
                    }
                    this.centerMode = SpawnableGroup.CenterMode.LEFT;
                    ++index;
                    continue;
                }
                String name = this.savedTrainMatcher.apply(spawnPattern.substring(index));
                if (!(name == null || name.length() <= 1 && SpawnableGroup.VanillaCartType.parse(c).isPresent())) {
                    index += name.length();
                    this.addPattern(new SavedTrainSpawnPattern(this.consumeQuantity(), name));
                    continue;
                }
                Optional<SpawnableGroup.VanillaCartType> type = SpawnableGroup.VanillaCartType.parse(c);
                if (type.isPresent()) {
                    ++index;
                    this.addPattern(new VanillaCartSpawnPattern(this.consumeQuantity(), type.get()));
                    continue;
                }
                ++index;
                if (!Character.isDigit(c) && c != '.' && c != '%') continue;
                this.quantityBuilder.append(c);
            }
            return index;
        }
    }

    public static class ParsedSpawnPattern
    extends SequenceSpawnPattern {
        private final SpawnableGroup.CenterMode centerMode;

        protected ParsedSpawnPattern(SequenceSpawnPattern sequence, SpawnableGroup.CenterMode centerMode) {
            super(sequence);
            this.centerMode = centerMode;
        }

        public SpawnableGroup.CenterMode centerMode() {
            return this.centerMode;
        }

        @Override
        public String toString() {
            String str = super.toString();
            return str.substring(1, str.length() - 1);
        }
    }

    private static class TwoStageApplier
    implements Applier {
        private final Function<SpawnableGroup, List<SpawnableMember>> initializer;
        private List<SpawnableMember> initializedMembers = null;

        public TwoStageApplier(Function<SpawnableGroup, List<SpawnableMember>> initializer) {
            this.initializer = initializer;
        }

        @Override
        public void apply(SpawnableGroup spawnableGroup, Random random, Function<String, String> savedTrainMatcher) {
            List<SpawnableMember> initializedMembers = this.initializedMembers;
            if (initializedMembers != null) {
                if (spawnableGroup.getMembers().size() + initializedMembers.size() > 1024) {
                    throw new TrainTooLongException();
                }
                initializedMembers.forEach(spawnableGroup::addMember);
            } else {
                this.initializedMembers = this.initializer.apply(spawnableGroup);
                if (spawnableGroup.getMembers().size() > 1024) {
                    for (int n = 0; n < initializedMembers.size() && !spawnableGroup.getMembers().isEmpty(); ++n) {
                        spawnableGroup.getMembers().remove(spawnableGroup.getMembers().size() - 1);
                    }
                    throw new TrainTooLongException();
                }
            }
        }
    }

    public static class TrainTooLongException
    extends RuntimeException {
    }

    public static class SequenceSpawnPattern
    extends TrainSpawnPattern {
        private final List<TrainSpawnPattern> patterns;

        public SequenceSpawnPattern(SequenceSpawnPattern copy) {
            this(copy.quantity(), copy.patterns());
        }

        public SequenceSpawnPattern(QuantityPrefix quantity, List<TrainSpawnPattern> patterns) {
            super(quantity);
            this.patterns = patterns;
        }

        public List<TrainSpawnPattern> patterns() {
            return this.patterns;
        }

        public SequenceSpawnPattern simplify() {
            TrainSpawnPattern p;
            if (this.patterns.size() == 1 && this.quantity().isOne() && (p = this.patterns.get(0)) instanceof SequenceSpawnPattern) {
                return (SequenceSpawnPattern)p;
            }
            return this;
        }

        public ParsedSpawnPattern asSpawnPattern(SpawnableGroup.CenterMode centerMode) {
            return new ParsedSpawnPattern(this, centerMode);
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(this.quantity().toString());
            str.append("[");
            boolean first = true;
            for (TrainSpawnPattern p : this.patterns) {
                if (first) {
                    first = false;
                } else {
                    str.append(" ");
                }
                str.append(p.toString());
            }
            str.append("]");
            return str.toString();
        }

        @Override
        protected Applier newGroupApplier() {
            ArrayList<Object> appliers;
            boolean hasChanceWeight = false;
            for (TrainSpawnPattern pattern : this.patterns) {
                if (!pattern.quantity().hasChanceWeight()) continue;
                hasChanceWeight = true;
                break;
            }
            if (hasChanceWeight) {
                appliers = new ArrayList<Object>(this.patterns.size());
                double chanceWeightPosition = 0.0;
                for (TrainSpawnPattern pattern : this.patterns) {
                    if (pattern.quantity().hasChanceWeight()) {
                        double nextChanceWeightPosition = chanceWeightPosition + pattern.quantity().chanceWeight;
                        appliers.add(new WeightedApplier(pattern.newGroupApplier(), chanceWeightPosition, nextChanceWeightPosition));
                        chanceWeightPosition = nextChanceWeightPosition;
                        continue;
                    }
                    appliers.add(new WeightedApplier(pattern.newGroupApplier()));
                }
                double totalChanceWeight = chanceWeightPosition;
                return this.repeatWithAmount((group, random, savedTrainMatcher) -> {
                    double chanceWeightPosition = random.nextDouble(totalChanceWeight);
                    appliers.forEach(applier -> applier.apply(group, random, savedTrainMatcher, chanceWeightPosition));
                });
            }
            appliers = new ArrayList(this.patterns.size());
            for (TrainSpawnPattern pattern : this.patterns) {
                appliers.add(pattern.newGroupApplier());
            }
            return this.repeatWithAmount((group, random, savedTrainMatcher) -> appliers.forEach(applier -> applier.apply(group, random, savedTrainMatcher)));
        }

        private static class WeightedApplier {
            private final Applier applier;
            private final boolean always;
            private final double chanceWeightRangeStart;
            private final double chanceWeightRangeEnd;

            public WeightedApplier(Applier applier) {
                this.applier = applier;
                this.always = true;
                this.chanceWeightRangeStart = Double.NaN;
                this.chanceWeightRangeEnd = Double.NaN;
            }

            public WeightedApplier(Applier applier, double chanceWeightRangeStart, double chanceWeightRangeEnd) {
                this.applier = applier;
                this.always = false;
                this.chanceWeightRangeStart = chanceWeightRangeStart;
                this.chanceWeightRangeEnd = chanceWeightRangeEnd;
            }

            public void apply(SpawnableGroup group, Random random, Function<String, String> savedTrainMatcher, double chanceWeightPosition) {
                if (this.always || chanceWeightPosition >= this.chanceWeightRangeStart && chanceWeightPosition < this.chanceWeightRangeEnd) {
                    this.applier.apply(group, random, savedTrainMatcher);
                }
            }
        }
    }

    public static class SavedTrainSpawnPattern
    extends TrainSpawnPattern {
        private final String name;

        public SavedTrainSpawnPattern(QuantityPrefix quantity, String name) {
            super(quantity);
            this.name = name;
        }

        public String name() {
            return this.name;
        }

        public String toString() {
            return this.quantity().toString() + this.name;
        }

        @Override
        protected Applier newGroupApplier() {
            return new Applier(){
                Applier applier = null;

                @Override
                public void apply(SpawnableGroup group, Random random, Function<String, String> savedTrainMatcher) {
                    Applier applier = this.applier;
                    if (applier == null) {
                        this.applier = applier = this.repeatWithAmount(this.createApplier(group, random, savedTrainMatcher));
                    }
                    applier.apply(group, random, savedTrainMatcher);
                }

                private Applier createApplier(SpawnableGroup group, Random random, Function<String, String> savedTrainMatcher) {
                    SavedTrainProperties properties = group.getTrainCarts().getSavedTrains().getProperties(name);
                    if (!properties.hasSpawnPattern()) {
                        return TrainSpawnPattern.twoStage(g -> g.addTrainWithConfig(properties));
                    }
                    Function<String, String> filteredSavedTrainMatcher = name -> {
                        String foundName = (String)savedTrainMatcher.apply((String)name);
                        if (foundName != null && foundName.equals(name)) {
                            foundName = null;
                        }
                        return foundName;
                    };
                    Applier patternApplier = TrainSpawnPattern.parse(properties.getSpawnPattern(), filteredSavedTrainMatcher).newGroupApplier();
                    if (((Boolean)properties.getConfig().getOrDefault("flipped", (Object)false)).booleanValue()) {
                        patternApplier = patternApplier.reverse();
                    }
                    return patternApplier;
                }
            };
        }
    }

    public static class VanillaCartSpawnPattern
    extends TrainSpawnPattern {
        private final SpawnableGroup.VanillaCartType type;

        public VanillaCartSpawnPattern(QuantityPrefix quantity, SpawnableGroup.VanillaCartType type) {
            super(quantity);
            this.type = type;
        }

        public SpawnableGroup.VanillaCartType type() {
            return this.type;
        }

        public String toString() {
            return this.quantity().toString() + this.type.getCode();
        }

        @Override
        protected Applier newGroupApplier() {
            return this.repeatWithAmount(TrainSpawnPattern.twoStage(group -> {
                ConfigurationNode standardCartConfig = TrainPropertiesStore.getDefaultsByName("spawner").getConfig().clone();
                standardCartConfig.remove("carts");
                group.addTrainWithConfig(standardCartConfig);
                standardCartConfig.set("entityType", (Object)this.type.getType());
                return Collections.singletonList(group.addMember(standardCartConfig));
            }));
        }
    }
}

