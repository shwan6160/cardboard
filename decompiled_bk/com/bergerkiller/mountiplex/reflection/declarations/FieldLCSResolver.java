/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FieldLCSResolver {
    public static List<Pair> lcs(FieldDeclaration[] a, FieldDeclaration[] b) {
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        ArrayList<Sequence> sequences = new ArrayList<Sequence>();
        Sequence mainSequence = new Sequence(Arrays.asList(a), Arrays.asList(b));
        Sequence skipped = new Sequence();
        for (FieldDeclaration da : mainSequence.a) {
            if (da.name.isObfuscated()) {
                skipped.a.add(da);
                continue;
            }
            FieldDeclaration found = null;
            while (!mainSequence.b.isEmpty()) {
                FieldDeclaration db = mainSequence.b.remove(0);
                if (!da.match(db)) {
                    skipped.b.add(db);
                    continue;
                }
                found = db;
                break;
            }
            pairs.add(new Pair(da, found));
            if (found == null) {
                mainSequence.b.addAll(skipped.b);
                skipped.b.clear();
                continue;
            }
            int i = 0;
            while (i < skipped.b.size()) {
                Iterator<FieldDeclaration> db = skipped.b.get(i);
                if (((FieldDeclaration)((Object)db)).name.isObfuscated()) {
                    ++i;
                    continue;
                }
                pairs.add(new Pair(null, (FieldDeclaration)((Object)db)));
                skipped.b.remove(i);
            }
            if (skipped.a.size() > 0 && skipped.b.size() == 0) {
                for (FieldDeclaration fd : skipped.a) {
                    pairs.add(new Pair(fd, null));
                }
            } else if (skipped.a.size() == 0 && skipped.b.size() > 0) {
                for (FieldDeclaration fd : skipped.b) {
                    pairs.add(new Pair(null, fd));
                }
            } else if (skipped.a.size() > 0 && skipped.b.size() > 0) {
                sequences.add(skipped);
            }
            skipped = new Sequence();
        }
        skipped.b.addAll(mainSequence.b);
        if (skipped.a.size() > 0 && skipped.b.size() == 0) {
            for (FieldDeclaration fd : skipped.a) {
                pairs.add(new Pair(fd, null));
            }
        } else if (skipped.a.size() == 0 && skipped.b.size() > 0) {
            for (FieldDeclaration fd : skipped.b) {
                pairs.add(new Pair(null, fd));
            }
        } else if (skipped.a.size() > 0 && skipped.b.size() > 0) {
            sequences.add(skipped);
        }
        for (Sequence seq : sequences) {
            FieldDeclaration[] aa = seq.a.toArray(new FieldDeclaration[0]);
            FieldDeclaration[] bb = seq.b.toArray(new FieldDeclaration[0]);
            if (seq.a.size() > 0 && seq.b.size() > 0) {
                Iterator<FieldDeclaration> fa_iter = seq.a.iterator();
                block8: while (fa_iter.hasNext()) {
                    FieldDeclaration fa = fa_iter.next();
                    Iterator<FieldDeclaration> fb_iter = seq.b.iterator();
                    while (fb_iter.hasNext()) {
                        FieldDeclaration fb = fb_iter.next();
                        if (!fa.name.value().equals(fb.name.value())) continue;
                        if (fa.match(fb)) {
                            pairs.add(new Pair(fa, fb, aa, bb));
                        } else {
                            pairs.add(new Pair(fa, null, aa, bb));
                            pairs.add(new Pair(null, fb, aa, bb));
                        }
                        fa_iter.remove();
                        fb_iter.remove();
                        continue block8;
                    }
                }
            }
            for (FieldDeclaration fa : seq.a) {
                pairs.add(new Pair(fa, null, aa, bb));
            }
            for (FieldDeclaration fb : seq.b) {
                pairs.add(new Pair(null, fb, aa, bb));
            }
        }
        return pairs;
    }

    public static <T extends Declaration> void logAlternatives(String category, T[] alternatives, T declaration, boolean isRequirement) {
        if (!declaration.getResolver().getLogErrors()) {
            return;
        }
        if (isRequirement) {
            MountiplexUtil.LOGGER.warning("Requirement was not found in " + declaration.getResolver().getDeclaredClassName() + ":");
        } else {
            MountiplexUtil.LOGGER.warning("A class member of " + declaration.getResolver().getDeclaredClassName() + " was not found!");
        }
        if (alternatives.length == 0) {
            MountiplexUtil.LOGGER.warning("Failed to find " + category + " " + declaration + " (No alternatives)");
        } else {
            ArrayList<T> sorted = new ArrayList<T>(Arrays.asList(alternatives));
            Declaration.sortSimilarity(declaration, sorted);
            MountiplexUtil.LOGGER.warning("Failed to find " + category + " " + declaration + " - Alternatives:");
            int limit = 8;
            for (Declaration alter : sorted) {
                MountiplexUtil.LOGGER.warning("  - " + alter);
                if (--limit != 0) continue;
                break;
            }
        }
    }

    public static void resolve(FieldDeclaration[] inputFields, FieldDeclaration[] realFields) {
        Pair pair;
        List<Pair> pairs = FieldLCSResolver.lcs(inputFields, realFields);
        Iterator<Pair> succIter = pairs.iterator();
        while (succIter.hasNext()) {
            pair = succIter.next();
            if (pair.a == null || pair.b == null) continue;
            pair.a.copyFieldFrom(pair.b);
            succIter.remove();
        }
        succIter = pairs.iterator();
        block1: while (succIter.hasNext()) {
            pair = succIter.next();
            if (pair.a == null) continue;
            for (FieldDeclaration realField : realFields) {
                if (!pair.a.match(realField)) continue;
                pair.a.copyFieldFrom(realField);
                succIter.remove();
                continue block1;
            }
        }
        for (Pair failPair : pairs) {
            if (failPair.b != null || failPair.a == null || failPair.a.modifiers.isOptional()) continue;
            if (failPair.bb.length > 0) {
                FieldLCSResolver.logAlternatives((String)"field", (Declaration[])failPair.bb, (Declaration)failPair.a, (boolean)false);
                continue;
            }
            FieldLCSResolver.logAlternatives((String)"field", (Declaration[])realFields, (Declaration)failPair.a, (boolean)false);
        }
    }

    public static class Sequence {
        public final ArrayList<FieldDeclaration> a = new ArrayList();
        public final ArrayList<FieldDeclaration> b = new ArrayList();

        public Sequence() {
        }

        public Sequence(List<FieldDeclaration> a, List<FieldDeclaration> b) {
            this.a.addAll(a);
            this.b.addAll(b);
        }
    }

    public static class Pair {
        public final FieldDeclaration a;
        public final FieldDeclaration b;
        public final FieldDeclaration[] aa;
        public final FieldDeclaration[] bb;

        public Pair(FieldDeclaration a, FieldDeclaration b) {
            this.a = a;
            this.b = b;
            this.bb = new FieldDeclaration[0];
            this.aa = this.bb;
        }

        public Pair(FieldDeclaration a, FieldDeclaration b, FieldDeclaration[] aa, FieldDeclaration[] bb) {
            this.a = a;
            this.b = b;
            this.aa = aa;
            this.bb = bb;
        }
    }
}

