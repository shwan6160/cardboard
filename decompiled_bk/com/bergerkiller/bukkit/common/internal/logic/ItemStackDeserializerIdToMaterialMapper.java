/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.io.VersionedMappingsFileIO;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;

public class ItemStackDeserializerIdToMaterialMapper
extends VersionedMappingsFileIO<Map<String, String>> {
    public ItemStackDeserializerIdToMaterialMapper() {
        super(Comparator.comparingInt(Integer::parseInt), m -> m.mappings);
    }

    public void loadMappings() {
        String idToMaterialMappingsFile = "/com/bergerkiller/bukkit/common/internal/resources/id_to_material_mappings.dat";
        try (InputStream in = ItemStackDeserializerIdToMaterialMapper.class.getResourceAsStream(idToMaterialMappingsFile);){
            this.read(in);
        }
        catch (IOException ex) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to read id-to-material mappings (corrupted jar?)", ex);
        }
        int dataVersion = CraftMagicNumbersHandle.getDataVersion();
        if (dataVersion < 4325) {
            return;
        }
        if (!this.getOrNewer(Integer.toString(dataVersion)).isPresent()) {
            Logging.LOGGER.warning("Id-to-material mappings are missing for data version " + dataVersion + " and will be regenerated");
            this.storeCurrentDataVersion();
        }
    }

    public boolean storeCurrentDataVersion() {
        try {
            Method getKeyMethod = Material.class.getMethod(CommonBootstrap.evaluateMCVersion(">=", "1.21.5") ? "getKeyOrNull" : "getKey", new Class[0]);
            String dataVersion = Integer.toString(CraftMagicNumbersHandle.getDataVersion());
            HashMap<String, String> newMappings = new HashMap<String, String>();
            for (Material material : MaterialUtil.getAllMaterials()) {
                Object key;
                if (MaterialUtil.isLegacyType(material) || (key = getKeyMethod.invoke((Object)material, new Object[0])) == null) continue;
                newMappings.put(key.toString(), material.name());
            }
            if (newMappings.equals(this.get(dataVersion).orElse(null))) {
                return false;
            }
            this.store(dataVersion, newMappings);
            return true;
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }
}

