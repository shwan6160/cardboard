/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 */
package com.bergerkiller.bukkit.common.internal.cdn;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.cdn.MojangIO;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MojangMappings {
    private final Map<String, ClassMappings> classesByName = new HashMap<String, ClassMappings>();

    public Collection<ClassMappings> classes() {
        return this.classesByName.values();
    }

    public ClassMappings forClassIfExists(String name) {
        return this.classesByName.get(name);
    }

    public MojangMappings translateClassNames(Function<String, String> translatorFunction) {
        MojangMappings translated = new MojangMappings();
        ClassSignatureCache signatureCache = new ClassSignatureCache(translatorFunction);
        for (ClassMappings mappings : this.classesByName.values()) {
            ClassMappings translatedMappings = new ClassMappings(mappings, translatorFunction);
            translated.classesByName.put(translatedMappings.name, translatedMappings);
            signatureCache.put(mappings.name, translatedMappings);
        }
        for (ClassMappings mappings : translated.classesByName.values()) {
            ListIterator<MethodSignature> iter = mappings.methods.listIterator();
            while (iter.hasNext()) {
                iter.set(iter.next().translate(mappings, signatureCache));
            }
        }
        return translated;
    }

    public static MojangMappings fromCacheOrDownload(String minecraftVersion) {
        File cacheFolder = MojangIO.getCacheFolder();
        File mappingsFile = new File(cacheFolder, MojangMappings.getMappingFile(minecraftVersion));
        if (mappingsFile.exists()) {
            try {
                return MojangMappings.readMappings(mappingsFile);
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to parse cached server mappings, redownloading", t);
            }
        }
        return MojangMappings.download(minecraftVersion);
    }

    public static MojangMappings download(String minecraftVersion) throws DownloadException {
        File cacheFolder = MojangIO.getCacheFolder();
        File tempFile = new File(cacheFolder, MojangMappings.getMappingFile(minecraftVersion) + ".tmp");
        File mappingsFile = new File(cacheFolder, MojangMappings.getMappingFile(minecraftVersion));
        Logging.LOGGER.warning("Since Minecraft 1.17 the server is obfuscated and requires the server mappings to interpret it correctly");
        Logging.LOGGER.warning("BKCommonLib will now download Minecraft " + minecraftVersion + " server mappings from Mojang's servers");
        Logging.LOGGER.warning("The file will be installed in: " + mappingsFile.toString());
        Logging.LOGGER.warning("By downloading these files you further agree with Mojang's EULA.");
        Logging.LOGGER.warning("The EULA can be read here: https://account.mojang.com/documents/minecraft_eula");
        try {
            return MojangMappings.downloadMain(minecraftVersion, tempFile, mappingsFile);
        }
        catch (DownloadException ex) {
            Logging.LOGGER.severe("Failed to download the server mappings. You can try manually downloading the file instead.");
            Logging.LOGGER.severe("Install the server mappings in the following location:");
            Logging.LOGGER.severe("> " + mappingsFile.getAbsolutePath());
            throw ex;
        }
    }

    private static MojangMappings downloadMain(String minecraftVersion, File tempFile, File mappingsFile) throws DownloadException {
        MojangIO.VersionManifest.VersionAssets versionAssets;
        MojangIO.VersionManifest versionManifest;
        try {
            versionManifest = MojangIO.downloadJson(MojangIO.VersionManifest.class, "https://launchermeta.mojang.com/mc/game/version_manifest.json");
        }
        catch (IOException ex) {
            throw new DownloadException("Failed to download the game version manifest from Mojangs servers", ex);
        }
        MojangIO.VersionManifest.Version currentVersion = versionManifest.findVersion(minecraftVersion);
        if (currentVersion == null) {
            throw new DownloadException("This Minecraft version is not available");
        }
        try {
            versionAssets = MojangIO.downloadJson(MojangIO.VersionManifest.VersionAssets.class, currentVersion.url);
        }
        catch (IOException ex) {
            throw new DownloadException("Failed to download game version asset information", ex);
        }
        MojangIO.VersionManifest.VersionAssets.Download download = versionAssets.downloads.get("server_mappings");
        if (download == null) {
            throw new DownloadException("This Minecraft version has no downloadable server mappings");
        }
        try {
            MojangIO.downloadFile("Server Mappings", download, tempFile);
        }
        catch (IOException ex) {
            throw new DownloadException("Failed to download server mappings", ex);
        }
        mappingsFile.delete();
        if (!tempFile.renameTo(mappingsFile)) {
            throw new DownloadException("Failed to move " + tempFile + " to " + mappingsFile);
        }
        try {
            return MojangMappings.readMappings(mappingsFile);
        }
        catch (Throwable t) {
            throw new DownloadException("Failed to parse server mappings", t);
        }
    }

    private static MojangMappings readMappings(File mappingsFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(mappingsFile));){
            MojangMappings mojangMappings = new ProGuardParser().parse(br);
            return mojangMappings;
        }
    }

    private static String getMappingFile(String minecraftVersion) {
        return minecraftVersion + "_server_mappings.txt";
    }

    public static class ClassMappings
    extends ClassSignature {
        public final BiMap<String, String> fields_obfuscated_to_name;
        public final BiMap<String, String> fields_name_to_obfuscated;
        public final List<MethodSignature> methods;

        public ClassMappings(ClassMappings original, Function<String, String> translatorFunction) {
            super(translatorFunction.apply(original.name), original.name_obfuscated);
            this.fields_obfuscated_to_name = original.fields_obfuscated_to_name;
            this.fields_name_to_obfuscated = original.fields_name_to_obfuscated;
            this.methods = new ArrayList<MethodSignature>(original.methods);
        }

        public ClassMappings(String name, String name_obfuscated) {
            super(name, name_obfuscated);
            this.fields_obfuscated_to_name = HashBiMap.create();
            this.fields_name_to_obfuscated = this.fields_obfuscated_to_name.inverse();
            this.methods = new ArrayList<MethodSignature>();
        }

        public void addField(String obfuscatedName, String mojangName) {
            this.fields_obfuscated_to_name.put((Object)obfuscatedName, (Object)mojangName);
        }

        public void addMethod(MethodSignature methodSig) {
            this.methods.add(methodSig);
        }
    }

    private static class ClassSignatureCache {
        private final Map<String, ClassSignature> cache = new ConcurrentHashMap<String, ClassSignature>();
        private final Function<String, String> nameTranslator;

        public ClassSignatureCache() {
            this.nameTranslator = Function.identity();
        }

        public ClassSignatureCache(Function<String, String> nameTranslator) {
            this.nameTranslator = nameTranslator;
        }

        public void put(String name, ClassSignature sig) {
            this.cache.put(name, sig);
        }

        public ClassSignature get(String name) {
            ClassSignature sig = this.cache.get(name);
            if (sig == null) {
                sig = this.compute(name);
                this.cache.put(name, sig);
            }
            return sig;
        }

        private ClassSignature compute(String name) {
            boolean changed = false;
            String classNameBare = name;
            String postfix = "";
            while (classNameBare.endsWith("[]")) {
                classNameBare = classNameBare.substring(0, classNameBare.length() - 2);
                postfix = postfix + "[]";
                changed = true;
            }
            if (changed) {
                ClassSignature sig = this.get(classNameBare);
                return new ClassSignature(sig.name + postfix, sig.name_obfuscated + postfix);
            }
            String translated = this.nameTranslator.apply(classNameBare);
            return new ClassSignature(translated, translated);
        }
    }

    public static class ClassSignature {
        public final String name;
        public final String name_obfuscated;

        public ClassSignature(String name, String name_obfuscated) {
            this.name = name;
            this.name_obfuscated = name_obfuscated;
        }

        public String toString() {
            if (this.name.equals(this.name_obfuscated)) {
                return this.name;
            }
            return this.name + ":" + this.name_obfuscated;
        }
    }

    public static class MethodSignature {
        public final ClassMappings declaring;
        public final String name;
        public final String name_obfuscated;
        public final ClassSignature returnType;
        public final List<ClassSignature> parameterTypes;

        public MethodSignature(ClassMappings declaring, String name, String name_obfuscated, ClassSignature returnType, List<ClassSignature> parameterTypes) {
            this.declaring = declaring;
            this.name = name;
            this.name_obfuscated = name_obfuscated;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        public MethodSignature translate(ClassMappings declaring, ClassSignatureCache signatureCache) {
            ArrayList<ClassSignature> tParameterTypes = new ArrayList<ClassSignature>(this.parameterTypes.size());
            for (ClassSignature paramType : this.parameterTypes) {
                tParameterTypes.add(signatureCache.get(paramType.name));
            }
            return new MethodSignature(declaring, this.name, this.name_obfuscated, signatureCache.get(this.returnType.name), tParameterTypes);
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append("<").append(this.declaring.toString()).append("> ");
            str.append(this.returnType).append(' ');
            str.append(this.name).append(":").append(this.name_obfuscated);
            str.append(" (");
            boolean first = true;
            for (ClassSignature param : this.parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    str.append(", ");
                }
                str.append(param.toString());
            }
            str.append(")");
            return str.toString();
        }
    }

    public static class DownloadException
    extends RuntimeException {
        private static final long serialVersionUID = 6481322561950832096L;

        public DownloadException(String reason) {
            super(reason);
        }

        public DownloadException(String reason, Throwable cause) {
            super(reason, cause);
        }
    }

    private static class ProGuardParser {
        private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("\\s+[\\[\\]<>\\w\\._\\-$]+\\s([\\w_\\-$]+)\\s->\\s([\\w_\\-$]+)");
        private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("\\s+(\\d+:\\d+:)?([\\[\\]<>\\w\\._\\-$]+)\\s([\\w_\\-$]+)\\(([\\[\\]<>\\w\\._\\-,$]*)\\)\\s->\\s([\\w_\\-$]+)");
        private final MojangMappings result = new MojangMappings();
        private final ClassSignatureCache classSigCache = new ClassSignatureCache();

        private ProGuardParser() {
        }

        public MojangMappings parse(BufferedReader reader) throws IOException {
            String line;
            AsyncLineReader lineReader = new AsyncLineReader(reader);
            lineReader.start();
            ClassMappings currClassMappings = null;
            ArrayList<PendingMethod> pendingMethods = new ArrayList<PendingMethod>();
            while ((line = lineReader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                if (line.endsWith(":") && !line.startsWith(" ")) {
                    int nameEnd = line.indexOf(" -> ");
                    if (nameEnd <= 0 || nameEnd >= line.length() - 4) {
                        currClassMappings = null;
                    } else {
                        currClassMappings = new ClassMappings(line.substring(0, nameEnd), line.substring(nameEnd + 4, line.length() - 1));
                        this.classSigCache.put(currClassMappings.name, currClassMappings);
                        if (ProGuardParser.isValidClass(currClassMappings.name)) {
                            this.result.classesByName.put(currClassMappings.name, currClassMappings);
                        }
                    }
                }
                if (currClassMappings == null || !line.startsWith("    ")) continue;
                Matcher m2 = FIELD_NAME_PATTERN.matcher(line);
                if (m2.matches()) {
                    this.processField(currClassMappings, m2);
                    continue;
                }
                m2 = METHOD_NAME_PATTERN.matcher(line);
                if (!m2.matches()) continue;
                pendingMethods.add(new PendingMethod(currClassMappings, m2));
            }
            ((Stream)pendingMethods.stream().parallel()).map(m -> m.makeSignature(this.classSigCache)).forEachOrdered(s -> s.declaring.addMethod((MethodSignature)s));
            return this.result;
        }

        private static boolean isValidClass(String className) {
            return !className.endsWith(".package-info");
        }

        private void processField(ClassMappings classMappings, Matcher m) {
            String obfuscatedName = m.group(2);
            String mojangName = m.group(1);
            if (mojangName.startsWith("this$")) {
                return;
            }
            classMappings.addField(obfuscatedName, mojangName);
        }

        private static final class AsyncLineReader {
            private static final String STOP_SIGNAL = "MAPPINGS_DONE_READING";
            private final BufferedReader reader;
            private CompletableFuture<Void> readerFuture;
            private final BlockingQueue<String> pending = new LinkedBlockingDeque<String>();

            public AsyncLineReader(BufferedReader reader) {
                this.reader = reader;
            }

            public String readLine() throws IOException {
                try {
                    String line = this.pending.poll(1000L, TimeUnit.MILLISECONDS);
                    if (line == STOP_SIGNAL) {
                        try {
                            this.readerFuture.join();
                        }
                        catch (CompletionException ex) {
                            if (ex.getCause() instanceof IOException) {
                                throw (IOException)ex.getCause();
                            }
                            throw ex;
                        }
                        return null;
                    }
                    return line;
                }
                catch (InterruptedException e) {
                    throw new IllegalStateException("Read interrupted");
                }
            }

            public void start() {
                this.readerFuture = CommonPlugin.runIOTaskAsync(() -> {
                    BlockingQueue<String> pending = this.pending;
                    try {
                        String line;
                        while ((line = this.reader.readLine()) != null) {
                            pending.offer(line, 1000L, TimeUnit.MILLISECONDS);
                        }
                    }
                    finally {
                        pending.offer(STOP_SIGNAL);
                    }
                });
            }
        }

        private static final class PendingMethod {
            public final ClassMappings classMappings;
            public final String obfuscatedName;
            public final String mojangName;
            public final String returnTypeName;
            public final String[] paramTypeNames;

            public PendingMethod(ClassMappings classMappings, Matcher m) {
                this.classMappings = classMappings;
                this.obfuscatedName = m.group(5);
                this.mojangName = m.group(3);
                this.returnTypeName = m.group(2);
                String fullArgsStr = m.group(4);
                this.paramTypeNames = fullArgsStr.isEmpty() ? new String[0] : fullArgsStr.split(",", -1);
            }

            public MethodSignature makeSignature(ClassSignatureCache signatureCache) {
                ClassSignature returnType = signatureCache.get(this.returnTypeName);
                ArrayList<ClassSignature> params = new ArrayList<ClassSignature>(this.paramTypeNames.length);
                for (String paramTypeName : this.paramTypeNames) {
                    params.add(signatureCache.get(paramTypeName));
                }
                return new MethodSignature(this.classMappings, this.mojangName, this.obfuscatedName, returnType, params);
            }
        }
    }
}

