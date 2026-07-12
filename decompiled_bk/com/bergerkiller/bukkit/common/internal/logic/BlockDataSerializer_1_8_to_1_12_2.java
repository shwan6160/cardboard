/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

class BlockDataSerializer_1_8_to_1_12_2
extends BlockDataSerializer {
    private FastMethod<Object> findBlockByNameMethod = new FastMethod();
    private FastMethod<Object> createLegacyBlockDataMethod = new FastMethod();
    private FastMethod<Object> setBlockDataKeyValueMethod = new FastMethod();

    BlockDataSerializer_1_8_to_1_12_2() {
    }

    @Override
    public void enable() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.world.level.block.Block");
        resolver.addImport("net.minecraft.world.level.block.Blocks");
        resolver.addImport("net.minecraft.world.level.block.state.BlockState");
        resolver.addImport("net.minecraft.world.level.block.state.properties.Property");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        this.findBlockByNameMethod.init(new MethodDeclaration(resolver, "public static Object findBlockByName(net.minecraft.resources.Identifier minecraftKey) {\n    Block block = Block.REGISTRY.get((Object)minecraftKey);\n    if (block == Blocks.AIR) {\n        if (!Block.REGISTRY.d((Object)minecraftKey)) {\n            return null;\n        }\n    }\n    return block;\n}"));
        this.createLegacyBlockDataMethod.init(new MethodDeclaration(resolver, "public static BlockState createLegacyBlockData(Block block, int legacyData) {\n    return block.fromLegacyData(legacyData);\n}"));
        this.setBlockDataKeyValueMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static BlockState setBlockDataKeyValue(BlockState iblockdata, String keyText, String valueText) {\n    net.minecraft.world.level.block.state.StateDefinition blockstatelist;\n#if version >= 1.11\n    blockstatelist = iblockdata.getBlock().s();\n#elseif version >= 1.9\n    blockstatelist = iblockdata.getBlock().t();\n#elseif version > 1.8\n    blockstatelist = iblockdata.getBlock().P();\n#else\n    blockstatelist = iblockdata.getBlock().O();\n#endif\n    Property iblockstate;\n    Comparable value;\n#if version >= 1.10.2\n    if ((iblockstate = blockstatelist.a(keyText)) == null) return null;\n    value = (Comparable) iblockstate.b(valueText).orNull();\n#else\n    {\n        java.util.Iterator state_iter = blockstatelist.d().iterator();\n        while (true) {\n            if (!state_iter.hasNext()) {\n                return null; //not found\n            }\n            iblockstate = (Property) state_iter.next();\n            if (iblockstate.a().equals(keyText)) {\n                break;\n            }\n        }\n    }\n    if (iblockstate instanceof net.minecraft.world.level.block.state.properties.BooleanProperty) {\n        if (valueText.equals(\"true\")) {\n            value = Boolean.TRUE;\n        } else if (valueText.equals(\"false\")) {\n            value = Boolean.FALSE;\n        } else {\n            value = null;\n        }\n    } else if (iblockstate instanceof net.minecraft.world.level.block.state.properties.IntegerProperty) {\n        try {\n            Integer intValue = Integer.valueOf(Integer.parseInt(valueText));\n            if (iblockstate.c().contains(intValue)) {\n                value = intValue;\n            } else {\n                value = null;\n            }\n        } catch (NumberFormatException ex) {\n            value = null;\n        }\n    } else if (iblockstate instanceof net.minecraft.world.level.block.state.properties.EnumProperty) {\n        java.util.Iterator iter = iblockstate.c().iterator();\n        while (true) {\n            if (!iter.hasNext()) {\n                value = null; //not found\n                break;\n            }\n            net.minecraft.util.StringRepresentable option = (net.minecraft.util.StringRepresentable) iter.next();\n            if (option.getName().equals(valueText)) {\n                value = (Comparable) option;\n                break;\n            }\n        }\n    } else {\n        value = null; //no idea\n    }\n#endif\n    if (value == null) return null;\n    return iblockdata.set(iblockstate, value);\n}", resolver)));
    }

    @Override
    public void disable() throws Throwable {
    }

    @Override
    public void forceInitialization() {
        this.findBlockByNameMethod.forceInitialization();
        this.createLegacyBlockDataMethod.forceInitialization();
        this.setBlockDataKeyValueMethod.forceInitialization();
    }

    @Override
    public String serialize(BlockData blockData) {
        return blockData.getData().toString();
    }

    @Override
    public BlockData deserialize(String text) {
        String blockName;
        String stateText = null;
        int legacyDataValue = -1;
        int index = text.indexOf(91);
        if (index != -1) {
            blockName = text.substring(0, index);
            int endIndex = text.indexOf(93, index + 1);
            stateText = endIndex != -1 ? text.substring(index + 1, endIndex).trim() : text.substring(index + 1).trim();
        } else {
            String dataText;
            blockName = text;
            index = text.lastIndexOf(58);
            if (index != -1 && ParseUtil.isNumeric(dataText = text.substring(index + 1).trim())) {
                try {
                    legacyDataValue = Integer.parseInt(stateText);
                    if (legacyDataValue < 0 || legacyDataValue > 15) {
                        return null;
                    }
                    blockName = text.substring(0, index);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
        IdentifierHandle key = IdentifierHandle.createNew(blockName);
        if (key == null) {
            return null;
        }
        Object nmsBlock = this.findBlockByNameMethod.invoke(null, key.getRaw());
        if (nmsBlock == null) {
            return null;
        }
        if (legacyDataValue != -1) {
            return BlockData.fromBlockData(this.createLegacyBlockDataMethod.invoke(null, nmsBlock, legacyDataValue));
        }
        Object nmsBlockState = ((Template.Method)BlockHandle.T.getBlockData.raw).invoke(nmsBlock);
        if (stateText != null && !stateText.isEmpty()) {
            int nextElement;
            int index2 = 0;
            do {
                String valueText;
                nextElement = stateText.indexOf(44, index2);
                int nextValue = stateText.indexOf(61, index2);
                if (nextValue == -1 || nextElement != -1 && nextValue > nextElement) {
                    return null;
                }
                String keyText = stateText.substring(index2, nextValue);
                if ((nmsBlockState = this.setBlockDataKeyValueMethod.invoke(null, nmsBlockState, keyText, valueText = nextElement == -1 ? stateText.substring(nextValue + 1) : stateText.substring(nextValue + 1, nextElement))) != null) continue;
                return null;
            } while ((index2 = nextElement + 1) != 0);
        }
        return BlockData.fromBlockData(nmsBlockState);
    }
}

