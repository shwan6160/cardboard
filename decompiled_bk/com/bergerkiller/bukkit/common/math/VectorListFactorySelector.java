/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jdk.incubator.vector.DoubleVector
 *  jdk.incubator.vector.VectorSpecies
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.math.VectorList;
import com.bergerkiller.bukkit.common.math.VectorListBasicImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import org.bukkit.util.Vector;

class VectorListFactorySelector {
    VectorListFactorySelector() {
    }

    public static VectorList.Factory initFactory() {
        String packagePath;
        try {
            Class.forName("jdk.incubator.vector.DoubleVector");
            String packagePathTmp = VectorListFactorySelector.class.getName();
            packagePath = packagePathTmp.substring(0, packagePathTmp.lastIndexOf(46));
            if (DoubleVector.SPECIES_PREFERRED.length() < 2) {
                throw new UnsupportedOperationException("SIMD not supported");
            }
        }
        catch (Throwable ignored) {
            return VectorListFactorySelector.initBasicFactory();
        }
        try {
            ClassLoader classLoader = VectorListFactorySelector.class.getClassLoader();
            FactoryList factories = new FactoryList();
            if (DoubleVector.SPECIES_PREFERRED.length() < 8 && DoubleVector.SPECIES_PREFERRED.length() >= 4) {
                Class<?> vectorListClass = Class.forName(packagePath + ".VectorListSIMD256DoubledImpl", true, classLoader);
                Field factoryField = vectorListClass.getDeclaredField("FACTORY");
                factoryField.setAccessible(true);
                VectorList.Factory octoFactory = (VectorList.Factory)factoryField.get(null);
                factories.testAdd(octoFactory);
            }
            Class<?> genericSIMDClass = Class.forName(packagePath + ".VectorListSIMDImpl", true, classLoader);
            Method createFactoryForMethod = genericSIMDClass.getDeclaredMethod("createFactoryFor", VectorSpecies.class);
            createFactoryForMethod.setAccessible(true);
            for (VectorSpecies species : new VectorSpecies[]{DoubleVector.SPECIES_128, DoubleVector.SPECIES_256, DoubleVector.SPECIES_512}) {
                if (species.length() > DoubleVector.SPECIES_PREFERRED.length()) continue;
                VectorList.Factory factory = (VectorList.Factory)createFactoryForMethod.invoke(null, species);
                factories.testAdd(factory);
            }
            if (factories.isEmpty()) {
                return VectorListFactorySelector.initBasicFactory();
            }
            factories.add(VectorListFactorySelector.initBasicFactory());
            return factories;
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.WARNING, "Failed to initialize SIMD", t);
            return VectorListFactorySelector.initBasicFactory();
        }
    }

    private static VectorList.Factory initBasicFactory() {
        return new VectorList.Factory(){

            @Override
            public VectorList copyOf(VectorList vectorValues) {
                return new VectorListBasicImpl(vectorValues);
            }

            @Override
            public VectorList createWith(int size, VectorList.VectorIterator iterator) {
                return new VectorListBasicImpl(size, iterator);
            }
        };
    }

    private static class FactoryList
    extends ArrayList<VectorList.Factory>
    implements VectorList.Factory {
        private FactoryList() {
        }

        private VectorList.Factory select(int size) {
            for (VectorList.Factory factory : this) {
                int required = factory.getRequiredSize();
                if (required != -1 && required != size) continue;
                return factory;
            }
            throw new IllegalArgumentException("No factory found for size " + size);
        }

        public void testAdd(VectorList.Factory factory) {
            try {
                VectorList list;
                int requiredSize = factory.getRequiredSize();
                if (requiredSize == -1) {
                    requiredSize = 32;
                }
                if ((list = factory.createWith(requiredSize, VectorList.VectorIterator.iterateFilled(1.0, 2.0, 3.0))) == null) {
                    throw new IllegalStateException("Factory returned null");
                }
                for (int i = 0; i < requiredSize; ++i) {
                    Vector v = list.get(i);
                    if (new Vector(1.0, 2.0, 3.0).equals((Object)v)) continue;
                    throw new IllegalStateException("Factory produced list has invalid element at " + i + ": " + v);
                }
                this.add(factory);
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.WARNING, "Failed to make use of vector list factory [name=" + factory.getClass().getName() + " req_size=" + factory.getRequiredSize() + "]", t);
            }
        }

        @Override
        public boolean isOptimizedSize(int size) {
            for (VectorList.Factory factory : this) {
                int requiredSize = factory.getRequiredSize();
                if (requiredSize != -1 && requiredSize != size) continue;
                return factory.isOptimizedSize(size);
            }
            return false;
        }

        @Override
        public VectorList copyOf(VectorList vectorValues) {
            return this.select(vectorValues.size()).copyOf(vectorValues);
        }

        @Override
        public VectorList createWith(int size, VectorList.VectorIterator iterator) {
            return this.select(size).createWith(size, iterator);
        }

        @Override
        public VectorList copyOf(Vector ... vectorValues) {
            return this.select(vectorValues.length).copyOf(vectorValues);
        }

        @Override
        public VectorList copyOf(Collection<Vector> vectorValues) {
            return this.select(vectorValues.size()).copyOf(vectorValues);
        }
    }
}

