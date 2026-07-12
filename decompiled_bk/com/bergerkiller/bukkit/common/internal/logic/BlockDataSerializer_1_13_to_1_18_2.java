/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

class BlockDataSerializer_1_13_to_1_18_2
extends BlockDataSerializer {
    private FastMethod<String> serializeMethod = new FastMethod();
    private FastMethod<BlockData> deserializeMethod = new FastMethod();

    BlockDataSerializer_1_13_to_1_18_2() {
    }

    @Override
    public void enable() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.commands.arguments.blocks.BlockStateParser");
        resolver.addImport("net.minecraft.world.level.block.state.BlockState");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        this.serializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static String serialize(BlockState iblockdata) {\n#if version >= 1.18\n    return BlockStateParser.serialize(iblockdata);\n#elseif version >= 1.14\n    return BlockStateParser.a(iblockdata);\n#else\n    return BlockStateParser.a(iblockdata, null);\n#endif\n}", resolver)));
        this.deserializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static BlockState deserialize(String text) {\n    com.mojang.brigadier.StringReader reader = new com.mojang.brigadier.StringReader(text);\n    BlockStateParser block;\n    try {\n#if version >= 1.18\n        block = (new BlockStateParser(reader, false)).parse(true);\n#else\n        block = (new BlockStateParser(reader, false)).a(true);\n#endif\n    } catch (com.mojang.brigadier.exceptions.CommandSyntaxException ex) {\n        return null;\n    }\n#if version >= 1.18\n    return block.getState();\n#elseif version >= 1.13.2\n    return block.getBlockData();\n#else\n    return block.b();\n#endif\n}", resolver)));
    }

    @Override
    public void disable() throws Throwable {
    }

    @Override
    public void forceInitialization() {
        this.serializeMethod.forceInitialization();
        this.deserializeMethod.forceInitialization();
    }

    @Override
    public String serialize(BlockData blockData) {
        return this.serializeMethod.invoke(null, blockData.getData());
    }

    @Override
    public BlockData deserialize(String text) {
        return BlockData.fromBlockData(this.deserializeMethod.invoke(null, text));
    }
}

