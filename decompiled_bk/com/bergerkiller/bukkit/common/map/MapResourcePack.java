/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.inventory.ItemStack
 *  org.yaml.snakeyaml.error.YAMLException
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.internal.resources.ResourceOverrides;
import com.bergerkiller.bukkit.common.internal.resources.builtin.GeneratedModel;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackAutoArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackClientArchive;
import com.bergerkiller.bukkit.common.map.gson.MapResourcePackDeserializer;
import com.bergerkiller.bukkit.common.map.gson.types.ResourcePackDescription;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.ModelInfo;
import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;
import com.bergerkiller.bukkit.common.map.util.VanillaResourcePack;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import com.bergerkiller.bukkit.common.wrappers.ItemRenderOptions;
import com.bergerkiller.bukkit.common.wrappers.RenderOptions;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.error.YAMLException;

public class MapResourcePack {
    public static final VanillaResourcePack VANILLA = new VanillaResourcePack();
    public static final MapResourcePack SERVER = MapResourcePack.builder().resourcePackPath("server").build();
    private final MapResourcePack baseResourcePack;
    private final PackVersion preferredPackVersion;
    protected MapResourcePackArchive archive;
    protected Metadata metadata = null;
    private final Map<String, MapTexture> textureCache = new HashMap<String, MapTexture>();
    private final Map<String, ConfigurationNode> yamlCache = new HashMap<String, ConfigurationNode>();
    private final Map<String, ItemModel> itemModelCache = new HashMap<String, ItemModel>();
    private final Map<String, Model> modelCache = new HashMap<String, Model>();
    private final Map<BlockRenderOptions, Model> blockModelCache = new HashMap<BlockRenderOptions, Model>();
    private BlockRenderProvider currProvider = null;
    private MapResourcePackDeserializer deserializer = null;
    private boolean loaded = false;

    public static Builder builder() {
        return new Builder(VANILLA);
    }

    public MapResourcePack(String resourcePackPath) {
        this(MapResourcePack.builder().resourcePackPath(resourcePackPath));
    }

    public MapResourcePack(String resourcePackPath, String resourcePackHash) {
        this(MapResourcePack.builder().resourcePackPath(resourcePackPath).resourcePackHash(resourcePackHash));
    }

    public MapResourcePack(MapResourcePack baseResourcePack, String resourcePackPath) {
        this(baseResourcePack, resourcePackPath, "");
    }

    public MapResourcePack(MapResourcePack baseResourcePack, String resourcePackPath, String resourcePackHash) {
        this(new Builder(baseResourcePack).resourcePackPath(resourcePackPath).resourcePackHash(resourcePackHash));
    }

    protected MapResourcePack(Builder builder) {
        this.baseResourcePack = builder.baseResourcePack;
        this.preferredPackVersion = builder.preferredPackVersion;
        this.archive = null;
        String resourcePackPath = builder.resourcePackPath;
        String resourcePackHash = builder.resourcePackHash;
        if (resourcePackPath != null && resourcePackPath.equalsIgnoreCase("server")) {
            if (CommonBootstrap.isTestMode()) {
                resourcePackPath = "vanilla";
            } else {
                MinecraftServerHandle mcs = MinecraftServerHandle.instance();
                resourcePackPath = mcs.getResourcePack();
                resourcePackHash = mcs.getResourcePackHash();
            }
        }
        if (resourcePackPath == null || resourcePackPath.isEmpty() || resourcePackPath.equalsIgnoreCase("vanilla") || resourcePackPath.equalsIgnoreCase("default")) {
            this.archive = this.baseResourcePack == null ? new MapResourcePackClientArchive() : null;
            return;
        }
        this.archive = new MapResourcePackAutoArchive(resourcePackPath, resourcePackHash);
    }

    public Metadata getMetadata() {
        this.handleLoad(true, false);
        return this.metadata;
    }

    public MapResourcePack getBase() {
        return this.baseResourcePack;
    }

    public void load() {
        this.handleLoad(false, false);
    }

    protected void handleLoad(boolean lazy, boolean recurse) {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        this.metadata = Metadata.fallback("Failed to load resource pack");
        this.metadata.preferredPackVersion = this.preferredPackVersion;
        if (lazy && !recurse) {
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] You must call MapResourcePack.load() when enabling your plugin!");
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] This avoids stalling the server while downloading large resource packs/Minecraft client.");
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] Potential plugins that caused this: " + DebugUtil.getPluginCauses());
        }
        if (this.archive != null) {
            this.archive.load(lazy);
            if (this.deserializer == null) {
                this.deserializer = MapResourcePackDeserializer.create();
            }
            this.metadata = this.archive.tryLoadMetadata(this.deserializer);
            this.metadata.preferredPackVersion = this.preferredPackVersion;
            this.archive.configure(this.metadata);
        }
        if (this.baseResourcePack != null) {
            this.baseResourcePack.handleLoad(lazy, true);
        }
    }

    public void clearCache() {
        this.textureCache.clear();
        this.modelCache.clear();
        this.blockModelCache.clear();
    }

    public final Model getBlockModel(Block block) {
        return this.getBlockModel(WorldUtil.getBlockData(block).getRenderOptions(block));
    }

    public final Model getBlockModel(World world, int x, int y, int z) {
        return this.getBlockModel(WorldUtil.getBlockData(world, x, y, z).getRenderOptions(world, x, y, z));
    }

    public final Model getBlockModel(Material blockMaterial) {
        return this.getBlockModel(BlockData.fromMaterial(blockMaterial));
    }

    public final Model getBlockModel(Material blockMaterial, int data) {
        return this.getBlockModel(BlockData.fromMaterialData(blockMaterial, data));
    }

    public Model getBlockModel(BlockData blockData) {
        return this.getBlockModel(blockData.getDefaultRenderOptions());
    }

    public Model getBlockModel(BlockRenderOptions blockRenderOptions) {
        Model model = this.blockModelCache.get(blockRenderOptions);
        if (model == null) {
            if (blockRenderOptions.getBlockData() != null) {
                model = this.loadBlockModel(blockRenderOptions);
            }
            if (model == null) {
                model = Model.createPlaceholderModel(blockRenderOptions);
            }
            this.blockModelCache.put(blockRenderOptions, model);
        }
        return model;
    }

    public ModelInfo getModelInfo(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Input path is null");
        }
        Model model = this.modelCache.get(path);
        if (model != null) {
            return model;
        }
        ModelInfo info = this.openGsonObject(ModelInfo.class, ResourceType.MODELS, path);
        if (info != null) {
            info.setName(path);
            return info;
        }
        return ModelInfo.createPlaceholder(path);
    }

    public ItemModel getItemModelConfig(ItemStack itemStack) {
        return this.getItemModelConfig(CommonItemStack.of(itemStack));
    }

    public ItemModel getItemModelConfig(CommonItemStack itemStack) {
        return this.getItemModelConfig(ModelInfoLookup.lookupItem(itemStack));
    }

    public ItemModel getItemModelConfig(String itemName) {
        ItemModel.Root root;
        ItemModel cached = this.itemModelCache.get(itemName);
        if (cached != null) {
            return cached;
        }
        CommonItemStack baseItemStack = ModelInfoLookup.findItemStackByModelName(itemName).orElse(null);
        if (this.getMetadata().hasItemModels()) {
            root = this.openGsonObject(ItemModel.Root.class, ResourceType.ITEMS, itemName);
        } else {
            ItemModel.Overrides overrides;
            ItemModel.MinecraftModel vanillaModel = ItemModel.MinecraftModel.of("item/" + itemName);
            root = new ItemModel.Root();
            root.model = vanillaModel;
            if (this.getMetadata().hasItemPredicateOverrides() && (overrides = this.openGsonObject(ItemModel.Overrides.class, ResourceType.MODELS, vanillaModel.model)) != null) {
                overrides.fallback = vanillaModel;
                if (baseItemStack != null) {
                    for (ItemModel.Overrides.OverriddenModel override : overrides.overrides) {
                        override.itemStack = override.tryMakeMatching(baseItemStack).orElse(null);
                    }
                }
                root.model = overrides;
            }
        }
        if (root == null) {
            root = new ItemModel.Root();
            root.model = ItemModel.MinecraftModel.NOT_SET;
        }
        root.baseItemStack = baseItemStack;
        this.itemModelCache.put(itemName, root);
        return root;
    }

    public Set<String> listOverriddenItemModelNames() {
        LinkedHashSet allOverridenModels = new LinkedHashSet();
        for (MapResourcePack p = this; p != null && p != VANILLA; p = p.getBase()) {
            for (String namespace : p.listNamespaces(false)) {
                SearchOptions searchOptions = SearchOptions.create().setResourceType(ResourceType.ITEMS).setNamespace(namespace).setIncludingParentPacks(false).setDeep(true).setPrependNamespace(!namespace.equals("minecraft"));
                p.forAllResources(searchOptions, allOverridenModels::add);
            }
        }
        return Collections.unmodifiableSet(allOverridenModels);
    }

    public Model getModel(String path) {
        Model model = this.modelCache.get(path);
        if (model != null) {
            return model;
        }
        model = this.loadModel(path);
        if (model == null) {
            model = Model.createPlaceholderModel(BlockData.AIR.getDefaultRenderOptions());
            model.setName(path);
        }
        this.modelCache.put(path, model);
        return model;
    }

    public Model getItemModel(ItemStack item) {
        return this.getItemModel(CommonItemStack.of(item));
    }

    public Model getItemModel(CommonItemStack item) {
        ItemRenderOptions options;
        Model m;
        ItemModel itemModel = this.getItemModelConfig(item);
        List<ItemModel.MinecraftModel> models = itemModel.resolveModels(item);
        if (models.isEmpty()) {
            models = ItemModel.MinecraftModel.NOT_SET_LIST;
        }
        if ((m = this.loadModel(models.get((int)0).model, options = ModelInfoLookup.lookupItemRenderOptions(item))) != null) {
            m.buildBlock(options);
            m.buildQuads();
        }
        if (m == null) {
            m = Model.createPlaceholderModel(options);
        }
        return m;
    }

    public MapTexture getItemTexture(ItemStack item, int width, int height) {
        return this.getItemTexture(CommonItemStack.of(item), width, height);
    }

    public MapTexture getItemTexture(CommonItemStack item, int width, int height) {
        Model.Display display;
        Model model = this.getItemModel(item);
        if (model == null || model.isPlaceholder()) {
            return Model.createPlaceholderTexture(width, height);
        }
        MapTexture texture = MapTexture.createEmpty(width, height);
        Matrix4x4 transform = new Matrix4x4();
        if (width != 16 || height != 16) {
            transform.scale((double)width / 16.0, 1.0, (double)height / 16.0);
        }
        if ((display = model.display.get("gui")) != null) {
            display.apply(transform);
            texture.setLightOptions(0.0f, 1.0f, new Vector3(-1.0, 1.0, -1.0));
        }
        texture.drawModel(model, transform);
        return texture;
    }

    public Set<String> listResources(ResourceType type, String folder) {
        return this.listResources(type, folder, true);
    }

    public Set<String> listResources(ResourceType type, String folder, boolean includingParentPacks) {
        return this.listResources(type, "minecraft", folder, includingParentPacks);
    }

    public Set<String> listResources(ResourceType type, String namespace, String folder) {
        return this.listResources(type, namespace, folder, true);
    }

    public Set<String> listResources(ResourceType type, String namespace, String folder, boolean includingParentPacks) {
        return this.listResources(SearchOptions.create().setResourceType(type).setNamespace(namespace).setFolder(folder).setIncludingParentPacks(includingParentPacks));
    }

    public Set<String> listResources(SearchOptions searchOptions) {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        this.forAllResources(searchOptions, result::add);
        return result;
    }

    public void forAllResources(SearchOptions searchOptions, Consumer<String> callback) {
        if (searchOptions.getResourceType() == null) {
            throw new IllegalArgumentException("Resource Type is not set");
        }
        String folder = searchOptions.getFolder();
        if (!folder.endsWith("/")) {
            folder = folder + "/";
        }
        if (searchOptions.getResourceType() == ResourceType.ITEMS && !this.getMetadata().hasItemModels()) {
            folder = folder.equals("/") ? "item" : (folder.startsWith("/") ? "item" + folder : "item/" + folder);
            this.forAllResources(searchOptions.clone().setResourceType(ResourceType.MODELS).setFolder(folder).setDeep(false).setPrependNamespace(false), path -> callback.accept(path.substring(5)));
            return;
        }
        this.forAllArchiveEntries(searchOptions.getResourceType(), searchOptions.getFullArchivePath(), false, searchOptions.isIncludingParentPacks(), searchOptions.isDeep(), path -> callback.accept(searchOptions.populatePathPrefix((String)path)));
    }

    public Set<String> listNamespaces() {
        return this.listNamespaces(true);
    }

    public Set<String> listNamespaces(boolean recurse) {
        HashSet<String> result = new HashSet<String>();
        this.forAllArchiveEntries(null, "assets/", true, recurse, false, result::add);
        return result;
    }

    public Set<String> listDirectories(ResourceType type, String folder) {
        return this.listDirectories(type, folder, true);
    }

    public Set<String> listDirectories(ResourceType type, String folder, boolean includingParentPacks) {
        return this.listDirectories(type, "minecraft", folder, includingParentPacks);
    }

    public Set<String> listDirectories(ResourceType type, String namespace, String folder) {
        return this.listDirectories(type, namespace, folder, true);
    }

    public Set<String> listDirectories(ResourceType type, String namespace, String folder, boolean includingParentPacks) {
        return this.listDirectories(SearchOptions.create().setResourceType(type).setNamespace(namespace).setFolder(folder).setIncludingParentPacks(includingParentPacks));
    }

    public Set<String> listDirectories(SearchOptions searchOptions) {
        if (searchOptions.getResourceType() == null) {
            throw new IllegalArgumentException("Resource Type is not set");
        }
        HashSet<String> result = new HashSet<String>();
        this.forAllArchiveEntries(searchOptions.getResourceType(), searchOptions.getFullArchivePath(), true, searchOptions.isIncludingParentPacks(), searchOptions.isDeep(), path -> result.add(searchOptions.populatePathPrefix((String)path)));
        return result;
    }

    public ConfigurationNode getConfig(String path) throws YAMLException {
        ConfigurationNode result = this.yamlCache.get(path);
        if (result == null) {
            result = new ConfigurationNode();
            try (InputStream inputStream = this.openFileStream(ResourceType.YAML, path);){
                if (inputStream != null) {
                    result.loadFromStream(inputStream);
                }
            }
            catch (YAMLException ex) {
                throw ex;
            }
            catch (IOException ex) {
                throw new YAMLException("Failed to open YAML file stream at " + path, (Throwable)ex);
            }
            this.yamlCache.put(path, result);
        }
        return result;
    }

    public MapTexture getTexture(String path) {
        MapTexture result = this.textureCache.get(path);
        if (result == null) {
            int num_frames;
            if (path.startsWith("#")) {
                result = Model.createPlaceholderTexture();
                this.textureCache.put(path, result);
                return result;
            }
            try (InputStream inputStream = this.openFileStream(ResourceType.TEXTURES, path);){
                if (inputStream != null) {
                    result = MapTexture.fromStream(inputStream);
                }
            }
            catch (IOException ex) {
                throw new MapTexture.TextureLoadException("Failed to open image stream at " + path, ex);
            }
            if (result == null) {
                Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Failed to load texture: " + path);
                result = Model.createPlaceholderTexture();
            }
            if ((num_frames = result.getHeight() / result.getWidth()) > 1 && num_frames * result.getWidth() == result.getHeight()) {
                InputStream metaStream = this.openFileStream(ResourceType.TEXTURES_META, path);
                if (metaStream == null) {
                    Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Failed to load animated texture (missing mcmeta): " + path);
                    result = Model.createPlaceholderTexture();
                } else {
                    result = result.getView(0, 0, result.getWidth(), result.getWidth()).clone();
                    try {
                        metaStream.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
            this.textureCache.put(path, result);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Model loadBlockModel(BlockRenderOptions blockRenderOptions) {
        if (blockRenderOptions.getBlockData().isType(Material.AIR)) {
            return new Model();
        }
        BlockRenderProvider oldProvider = this.currProvider;
        try {
            Object object;
            Object variant;
            List<BlockModelState.Variant> variants;
            Model model;
            this.currProvider = BlockRenderProvider.get(blockRenderOptions.getBlockData());
            if (this.currProvider != null && (model = this.currProvider.createModel(this, blockRenderOptions)) != null) {
                Model model2 = model;
                return model2;
            }
            String blockName = blockRenderOptions.lookupModelName();
            BlockModelState state = this.openGsonObject(BlockModelState.class, ResourceType.BLOCKSTATES, blockName);
            if (state != null) {
                variants = state.findVariants(blockRenderOptions);
            } else {
                variant = new BlockModelState.Variant();
                ((BlockModelState.Variant)variant).modelName = "block/" + blockName;
                variants = Arrays.asList(variant);
            }
            if (variants.isEmpty()) {
                variant = new Model();
                return variant;
            }
            if (variants.size() == 1) {
                variant = this.loadBlockVariant((BlockModelState.Variant)variants.get(0), blockRenderOptions);
                return variant;
            }
            Model result = new Model();
            boolean succ = true;
            for (BlockModelState.Variant variant2 : variants) {
                Model subModel = this.loadBlockVariant(variant2, blockRenderOptions);
                if (subModel != null) {
                    result.elements.addAll(subModel.elements);
                    continue;
                }
                succ = false;
            }
            if (!succ && result.elements.isEmpty()) {
                object = null;
                return object;
            }
            object = result;
            return object;
        }
        finally {
            this.currProvider = oldProvider;
        }
    }

    private Model loadBlockVariant(BlockModelState.Variant variant, BlockRenderOptions blockRenderOptions) {
        Model model = this.loadModel(variant.modelName, blockRenderOptions);
        if (model == null) {
            return null;
        }
        model.buildBlock(blockRenderOptions);
        variant.update(model);
        model.buildQuads();
        return model;
    }

    protected final Model loadModel(String path) {
        return this.loadModel(path, BlockData.AIR.getDefaultRenderOptions());
    }

    protected final Model loadModel(String path, RenderOptions options) {
        if (path.equals("builtin/generated")) {
            return new GeneratedModel();
        }
        if (path.equals(ItemModel.MinecraftModel.NOT_SET.model)) {
            return Model.createPlaceholderModel(options);
        }
        Model model = this.openGsonObject(Model.class, ResourceType.MODELS, path);
        if (model == null) {
            Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Failed to load model " + path);
            return null;
        }
        for (Model.ModelOverride override : model.getOverrides()) {
            if (!override.matches(options) || override.model.equals(path)) continue;
            return this.loadModel(override.model, options);
        }
        String parentModelName = model.getParentName();
        if (parentModelName == null && !CommonCapabilities.RESOURCE_PACK_MODEL_BASE_TRANSFORMS && !path.equals("block/block")) {
            parentModelName = "block/block";
        }
        if (parentModelName != null) {
            Model parentModel = this.loadModel(parentModelName, options);
            if (parentModel == null || parentModel.isPlaceholder()) {
                Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Parent of model " + path + " not found: " + model.getParentName());
                return null;
            }
            model.loadParent(parentModel);
        }
        model.build(this, options);
        return model;
    }

    protected void forAllArchiveEntries(ResourceType type, String rootArchivePath, boolean directories, boolean recurse, boolean deep, Consumer<String> callback) {
        this.handleLoad(true, false);
        if (this.archive != null) {
            try {
                if (directories) {
                    for (String file : this.archive.listFiles(rootArchivePath, deep)) {
                        if (!file.endsWith("/")) continue;
                        callback.accept(file.substring(0, file.length() - 1));
                    }
                } else {
                    for (String file : this.archive.listFiles(rootArchivePath, deep)) {
                        if (!type.isExtension(file)) continue;
                        callback.accept(file.substring(0, file.length() - type.getExtension().length()));
                    }
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if (recurse && this.baseResourcePack != null) {
            this.baseResourcePack.forAllArchiveEntries(type, rootArchivePath, directories, recurse, false, callback);
        }
    }

    public Resource openResource(final ResourceType type, final String path) {
        URL url;
        if (type == null) {
            throw new IllegalArgumentException("Input resource type is null");
        }
        if (path == null) {
            throw new IllegalArgumentException("Input path is null");
        }
        String fullPath = type.makePath(path);
        if (!ResourceOverrides.isResourceOverrided(fullPath)) {
            Object resource;
            this.handleLoad(true, false);
            if (this.archive != null) {
                resource = this.archive.openResource(fullPath);
                if (resource == null) {
                    resource = this.archive.openResource(fullPath.toLowerCase(Locale.ENGLISH));
                }
                if (resource != null) {
                    return resource.toPackResource(this, type, path);
                }
            }
            if (this.baseResourcePack != null && (resource = this.baseResourcePack.openResource(type, path)) != null) {
                return resource;
            }
            if (this.currProvider != null && (resource = this.currProvider.openResource(type, path)) != null) {
                return resource;
            }
        }
        if ((url = Common.class.getResource(type.makeBKCPath(path))) != null) {
            return new Resource(){
                final /* synthetic */ MapResourcePack this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public MapResourcePack getPack() {
                    return VANILLA;
                }

                @Override
                public ResourceType getType() {
                    return type;
                }

                @Override
                public String getPath() {
                    return path;
                }

                @Override
                public InputStream openStream() throws IOException {
                    return url.openStream();
                }
            };
        }
        return null;
    }

    protected InputStream openFileStream(ResourceType type, String path) {
        Resource resource = this.openResource(type, path);
        return resource == null ? null : resource.tryOpenStream();
    }

    protected final <T> T openGsonObject(Class<T> objectType, ResourceType type, String path) {
        if (type == null) {
            throw new IllegalArgumentException("Input resource type is null");
        }
        if (path == null) {
            throw new IllegalArgumentException("Input path is null");
        }
        return this.readGsonObject(objectType, this.openFileStream(type, path), path);
    }

    public final <T> T readGsonObject(Class<T> objectType, InputStream inputStream) {
        return this.readGsonObject(objectType, inputStream, null);
    }

    private <T> T readGsonObject(Class<T> objectType, InputStream inputStream, String optPath) {
        if (this.deserializer == null) {
            this.deserializer = MapResourcePackDeserializer.create();
        }
        return this.deserializer.readGsonObject(objectType, inputStream, optPath);
    }

    public static final class Builder {
        private MapResourcePack baseResourcePack;
        private String resourcePackPath;
        private String resourcePackHash;
        private PackVersion preferredPackVersion;

        public Builder(MapResourcePack baseResourcePack) {
            this.baseResourcePack = baseResourcePack;
            this.resourcePackPath = "";
            this.resourcePackHash = "";
            this.preferredPackVersion = PackVersion.SERVER;
        }

        public Builder baseResourcePack(MapResourcePack baseResourcePack) {
            this.baseResourcePack = baseResourcePack;
            return this;
        }

        public Builder resourcePackPath(String resourcePackPath) {
            this.resourcePackPath = resourcePackPath;
            return this;
        }

        public Builder resourcePackHash(String resourcePackHash) {
            this.resourcePackHash = resourcePackHash;
            return this;
        }

        public Builder preferredPackVersion(PackVersion packVersion) {
            this.preferredPackVersion = packVersion;
            return this;
        }

        public MapResourcePack build() {
            return new MapResourcePack(this);
        }
    }

    public static class Metadata {
        private int pack_format;
        private PackVersion min_format;
        private PackVersion max_format;
        private ResourcePackDescription description;
        private List<PackVersionRange> supported_formats = Collections.emptyList();
        private transient PackVersion preferredPackVersion = PackVersion.SERVER;
        private transient Overlays overlays = null;
        private final transient DeferredSupplier<PackVersion> usedPackVersion = DeferredSupplier.of(this::calculateUsedPackVersion);
        private final transient DeferredSupplier<List<Overlay>> usedOverlays = DeferredSupplier.of(this::calculateUsedOverlays);
        private final transient DeferredSupplier<Boolean> hasItemModels = DeferredSupplier.of(() -> PackVersionRange.USES_ITEM_MODELS.isSupported(this.getUsedPackVersion()));
        private final transient DeferredSupplier<Boolean> hasItemPredicateOverrides = DeferredSupplier.of(() -> PackVersionRange.USES_ITEM_PREDICATE_OVERRIDES.isSupported(this.getUsedPackVersion()));

        @Deprecated
        public int getPackFormat() {
            return this.pack_format;
        }

        public String getDescription() {
            return this.hasDescription() ? this.description.plainContent : "No Description";
        }

        public boolean hasDescription() {
            return this.description != null && !this.description.plainContent.isEmpty();
        }

        public boolean hasItemModels() {
            return this.hasItemModels.get();
        }

        public boolean hasItemPredicateOverrides() {
            return this.hasItemPredicateOverrides.get();
        }

        public PackVersion getUsedPackVersion() {
            return this.usedPackVersion.get();
        }

        private PackVersion calculateUsedPackVersion() {
            if (this.min_format != null && this.max_format != null && PackVersionRange.USES_MIN_MAX_FORMAT.isSupported(this.preferredPackVersion)) {
                if (!this.preferredPackVersion.isAtMost(this.max_format)) {
                    return this.max_format;
                }
                if (!this.preferredPackVersion.isAtLeast(this.min_format)) {
                    return this.min_format;
                }
                return this.preferredPackVersion;
            }
            if (this.supported_formats != null) {
                for (PackVersionRange range : this.supported_formats) {
                    if (!range.isSupported(this.preferredPackVersion)) continue;
                    return this.preferredPackVersion;
                }
                PackVersion bestVersion = null;
                for (PackVersionRange range : this.supported_formats) {
                    if (range.max_inclusive().isAtLeast(this.preferredPackVersion) || bestVersion != null && !range.max_inclusive().isAtLeast(bestVersion)) continue;
                    bestVersion = range.max_inclusive();
                }
                if (bestVersion != null) {
                    return bestVersion;
                }
                for (PackVersionRange range : this.supported_formats) {
                    if (range.min_inclusive().isAtMost(this.preferredPackVersion) || bestVersion != null && !range.min_inclusive().isAtMost(bestVersion)) continue;
                    bestVersion = range.max_inclusive();
                }
                if (bestVersion != null) {
                    return bestVersion;
                }
            }
            return PackVersion.of(this.pack_format);
        }

        public List<Overlay> getUsedOverlays() {
            return this.usedOverlays.get();
        }

        private List<Overlay> calculateUsedOverlays() {
            if (this.overlays == null || this.overlays.entries == null || this.overlays.entries.isEmpty()) {
                return Collections.emptyList();
            }
            PackVersion usedPackVersion = this.getUsedPackVersion();
            boolean useMinMaxFormat = PackVersionRange.USES_MIN_MAX_FORMAT.isSupported(usedPackVersion);
            return this.overlays.entries.stream().filter(o -> {
                if (useMinMaxFormat && o.min_format != null && o.max_format != null) {
                    return usedPackVersion.isAtLeast(o.min_format) && usedPackVersion.isAtMost(o.max_format);
                }
                for (PackVersionRange range : o.formats) {
                    if (!range.isSupported(usedPackVersion)) continue;
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        public static Metadata fallback(String errorReason) {
            Metadata metadata = new Metadata();
            metadata.pack_format = PackVersion.HIGHEST_KNOWN.major();
            metadata.min_format = PackVersion.HIGHEST_KNOWN;
            metadata.max_format = PackVersion.HIGHEST_KNOWN;
            metadata.description = new ResourcePackDescription("Unknown Resource pack - " + errorReason);
            return metadata;
        }

        public static Metadata vanilla(String mcVersion) {
            Metadata metadata = new Metadata();
            metadata.max_format = metadata.min_format = PackVersion.byGameVersion(mcVersion);
            metadata.pack_format = metadata.min_format.major();
            metadata.description = new ResourcePackDescription("Vanilla Minecraft " + mcVersion);
            return metadata;
        }

        public static class Overlays {
            public List<Overlay> entries = Collections.emptyList();
        }

        public static class Overlay {
            public String directory = "";
            public List<PackVersionRange> formats = Collections.emptyList();
            public PackVersion min_format = null;
            public PackVersion max_format = null;
        }

        public static class PackWrapper {
            public Metadata pack;
            public Overlays overlays = null;

            public void postLoad() {
                this.pack.overlays = this.overlays;
            }
        }
    }

    public static final class PackVersion {
        public static final PackVersion MINIMUM = PackVersion.of(0);
        public static final PackVersion MAXIMUM = PackVersion.of(Integer.MAX_VALUE);
        private static final NavigableMap<TextValueSequence, PackVersion> BY_VERSION = new TreeMap<TextValueSequence, PackVersion>();
        public static final PackVersion HIGHEST_KNOWN;
        public static final PackVersion SERVER;
        private final int major;
        private final int minor;
        private final boolean anyMinor;

        private PackVersion(int major, int minor, boolean anyMinor) {
            this.major = major;
            this.minor = minor;
            this.anyMinor = anyMinor;
        }

        public static PackVersion of(int major, int minor) {
            return new PackVersion(major, minor, false);
        }

        public static PackVersion of(int major) {
            return new PackVersion(major, 0, true);
        }

        public int major() {
            return this.major;
        }

        public boolean anyMinor() {
            return this.anyMinor;
        }

        public int minor() {
            return this.minor;
        }

        public boolean isAtLeast(PackVersion other) {
            if (this.major > other.major) {
                return true;
            }
            if (this.major < other.major) {
                return false;
            }
            if (this.anyMinor || other.anyMinor) {
                return true;
            }
            return this.minor >= other.minor;
        }

        public boolean isAtMost(PackVersion other) {
            if (this.major < other.major) {
                return true;
            }
            if (this.major > other.major) {
                return false;
            }
            if (this.anyMinor || other.anyMinor) {
                return true;
            }
            return this.minor <= other.minor;
        }

        public int hashCode() {
            return 31 * this.major + this.minor;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof PackVersion) {
                PackVersion other = (PackVersion)o;
                return this.major == other.major && this.minor == other.minor && this.anyMinor == other.anyMinor;
            }
            return false;
        }

        public String toString() {
            return this.major + "." + (this.anyMinor ? "ANY" : Integer.valueOf(this.minor));
        }

        public static PackVersion byGameVersion(String minecraftVersion) {
            TextValueSequence minecraftSeq = TextValueSequence.parse(minecraftVersion);
            Iterator iter = BY_VERSION.headMap(minecraftSeq, true).descendingMap().values().iterator();
            if (iter.hasNext()) {
                return (PackVersion)iter.next();
            }
            return MINIMUM;
        }

        static {
            BY_VERSION.put(TextValueSequence.parse("1.6.1"), PackVersion.of(1));
            BY_VERSION.put(TextValueSequence.parse("1.9"), PackVersion.of(2));
            BY_VERSION.put(TextValueSequence.parse("1.11"), PackVersion.of(3));
            BY_VERSION.put(TextValueSequence.parse("1.13"), PackVersion.of(4));
            BY_VERSION.put(TextValueSequence.parse("1.15"), PackVersion.of(5));
            BY_VERSION.put(TextValueSequence.parse("1.16.2"), PackVersion.of(6));
            BY_VERSION.put(TextValueSequence.parse("1.17"), PackVersion.of(7));
            BY_VERSION.put(TextValueSequence.parse("1.18"), PackVersion.of(8));
            BY_VERSION.put(TextValueSequence.parse("1.19"), PackVersion.of(9));
            BY_VERSION.put(TextValueSequence.parse("1.19.3"), PackVersion.of(12));
            BY_VERSION.put(TextValueSequence.parse("1.19.4"), PackVersion.of(13));
            BY_VERSION.put(TextValueSequence.parse("1.20"), PackVersion.of(15));
            BY_VERSION.put(TextValueSequence.parse("1.20.2"), PackVersion.of(18));
            BY_VERSION.put(TextValueSequence.parse("1.20.3"), PackVersion.of(22));
            BY_VERSION.put(TextValueSequence.parse("1.20.5"), PackVersion.of(32));
            BY_VERSION.put(TextValueSequence.parse("1.21"), PackVersion.of(34));
            BY_VERSION.put(TextValueSequence.parse("1.21.2"), PackVersion.of(42));
            BY_VERSION.put(TextValueSequence.parse("1.21.4"), PackVersion.of(46));
            BY_VERSION.put(TextValueSequence.parse("1.21.5"), PackVersion.of(55));
            BY_VERSION.put(TextValueSequence.parse("1.21.6"), PackVersion.of(63));
            BY_VERSION.put(TextValueSequence.parse("1.21.7"), PackVersion.of(64));
            BY_VERSION.put(TextValueSequence.parse("1.21.9"), PackVersion.of(65));
            HIGHEST_KNOWN = (PackVersion)BY_VERSION.descendingMap().values().iterator().next();
            SERVER = PackVersion.byGameVersion(CommonBootstrap.initCommonServer().getMinecraftVersion());
        }
    }

    public static enum ResourceType {
        MODELS("/models/", ".json"),
        ITEMS("/items/", ".json"),
        BLOCKSTATES("/blockstates/", ".json"),
        TEXTURES("/textures/", ".png"),
        TEXTURES_META("/textures/", ".png.mcmeta"),
        YAML("/", ".yml");

        private final String root;
        private final String ext;

        private ResourceType(String root, String ext) {
            this.root = root;
            this.ext = ext;
        }

        public String getRoot(String namespace) {
            return "assets/" + namespace + this.root;
        }

        public String getExtension() {
            return this.ext;
        }

        public boolean isExtension(String filePath) {
            int extIdx = filePath.indexOf(46);
            return extIdx != -1 && filePath.substring(extIdx).equals(this.ext);
        }

        public String makePath(String path) {
            int namespaceIndex = path.indexOf(58);
            if (namespaceIndex != -1) {
                String namespace = path.substring(0, namespaceIndex);
                path = path.substring(namespaceIndex + 1);
                if (!namespace.isEmpty()) {
                    return "assets/" + namespace + this.root + path + this.ext;
                }
            }
            return "assets/minecraft" + this.root + path + this.ext;
        }

        public String makeBKCPath(String path) {
            return "/com/bergerkiller/bukkit/common/internal/resources/assets/minecraft" + this.root + ResourceType.stripNS(path) + this.ext;
        }

        private static String stripNS(String path) {
            int namespaceIndex = path.indexOf(58);
            return namespaceIndex == -1 ? path : path.substring(namespaceIndex + 1);
        }
    }

    public static class SearchOptions
    implements Cloneable {
        private ResourceType resourceType = null;
        private String namespace = "minecraft";
        private String folder = "/";
        private boolean includingParentPacks = false;
        private boolean deep = false;
        private boolean prependNamespace = false;

        public static SearchOptions create() {
            return new SearchOptions();
        }

        public ResourceType getResourceType() {
            return this.resourceType;
        }

        public SearchOptions setResourceType(ResourceType type) {
            this.resourceType = type;
            return this;
        }

        public String getNamespace() {
            return this.namespace;
        }

        public SearchOptions setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public String getFolder() {
            return this.folder;
        }

        public SearchOptions setFolder(String folder) {
            if (!folder.endsWith("/")) {
                folder = folder + "/";
            }
            this.folder = folder;
            return this;
        }

        public boolean isIncludingParentPacks() {
            return this.includingParentPacks;
        }

        public SearchOptions setIncludingParentPacks(boolean includingParentPacks) {
            this.includingParentPacks = includingParentPacks;
            return this;
        }

        public boolean isDeep() {
            return this.deep;
        }

        public SearchOptions setDeep(boolean deep) {
            this.deep = deep;
            return this;
        }

        public boolean isPrependNamespace() {
            return this.prependNamespace;
        }

        public SearchOptions setPrependNamespace(boolean prepend) {
            this.prependNamespace = prepend;
            return this;
        }

        public boolean isRootPath() {
            return this.folder.equals("/");
        }

        public String getFullArchivePath() {
            if (this.isRootPath()) {
                return this.resourceType.getRoot(this.namespace);
            }
            return this.resourceType.getRoot(this.namespace) + this.folder;
        }

        public String populatePathPrefix(String path) {
            if (this.isRootPath()) {
                return this.isPrependNamespace() ? this.namespace + ":" + path : path;
            }
            return this.isPrependNamespace() ? this.namespace + ":" + this.folder + path : this.folder + path;
        }

        public SearchOptions clone() {
            return SearchOptions.create().setResourceType(this.getResourceType()).setNamespace(this.getNamespace()).setFolder(this.getFolder()).setIncludingParentPacks(this.isIncludingParentPacks()).setPrependNamespace(this.isPrependNamespace()).setDeep(this.isDeep());
        }
    }

    public static interface Resource {
        public MapResourcePack getPack();

        default public boolean isVanilla() {
            return this.getPack() == VANILLA;
        }

        public ResourceType getType();

        public String getPath();

        public InputStream openStream() throws IOException;

        default public InputStream tryOpenStream() {
            try {
                return this.openStream();
            }
            catch (IOException ex) {
                return null;
            }
        }
    }

    public static final class PackVersionRange {
        public static final PackVersionRange USES_MIN_MAX_FORMAT = PackVersionRange.of(PackVersion.of(65), PackVersion.MAXIMUM);
        public static final PackVersionRange USES_ITEM_PREDICATE_OVERRIDES = PackVersionRange.of(PackVersion.of(2), PackVersion.of(45));
        public static final PackVersionRange USES_ITEM_MODELS = PackVersionRange.of(PackVersion.of(46), PackVersion.MAXIMUM);
        private final PackVersion min_inclusive;
        private final PackVersion max_inclusive;

        public static PackVersionRange of(int packFormatMajorVersion) {
            PackVersion asPackVersion = PackVersion.of(packFormatMajorVersion);
            return new PackVersionRange(asPackVersion, asPackVersion);
        }

        public static PackVersionRange of(int min_inclusive_major, int max_inclusive_major) {
            return new PackVersionRange(PackVersion.of(min_inclusive_major), PackVersion.of(max_inclusive_major));
        }

        public static PackVersionRange of(PackVersion min_inclusive, PackVersion max_inclusive) {
            return new PackVersionRange(min_inclusive, max_inclusive);
        }

        private PackVersionRange(PackVersion min_inclusive, PackVersion max_inclusive) {
            this.min_inclusive = min_inclusive;
            this.max_inclusive = max_inclusive;
        }

        public boolean isSupported(PackVersion usedFormat) {
            return usedFormat.isAtMost(this.max_inclusive) && usedFormat.isAtLeast(this.min_inclusive);
        }

        public PackVersion min_inclusive() {
            return this.min_inclusive;
        }

        public PackVersion max_inclusive() {
            return this.max_inclusive;
        }
    }
}

