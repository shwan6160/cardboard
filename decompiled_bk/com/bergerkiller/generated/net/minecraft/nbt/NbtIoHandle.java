/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;

@Template.InstanceType(value="net.minecraft.nbt.NbtIo")
public abstract class NbtIoHandle
extends Template.Handle {
    public static final NbtIoClass T = Template.Class.create(NbtIoClass.class, Common.TEMPLATE_RESOLVER);

    public static NbtIoHandle createHandle(Object handleInstance) {
        return (NbtIoHandle)T.createHandle(handleInstance);
    }

    public static CompoundTagHandle parseTagCompoundFromSNBT(String snbtContent) {
        return NbtIoHandle.T.parseTagCompoundFromSNBT.invoke(snbtContent);
    }

    public static TagHandle parseTagFromSNBT(String snbtContent) {
        return NbtIoHandle.T.parseTagFromSNBT.invoke(snbtContent);
    }

    public static String handleSNBTParseError(String snbtContent, Throwable exception) {
        return (String)NbtIoHandle.T.handleSNBTParseError.invoker.invoke(null, snbtContent, exception);
    }

    public static void uncompressed_writeTag(TagHandle nbtbase, DataOutput dataoutput) {
        NbtIoHandle.T.uncompressed_writeTag.invoke(nbtbase, dataoutput);
    }

    public static TagHandle uncompressed_readTag(DataInput datainput) {
        return NbtIoHandle.T.uncompressed_readTag.invoke(datainput);
    }

    public static void uncompressed_writeTagCompound(CompoundTagHandle nbttagcompound, DataOutput dataoutput) {
        NbtIoHandle.T.uncompressed_writeTagCompound.invoke(nbttagcompound, dataoutput);
    }

    public static CompoundTagHandle uncompressed_readTagCompound(DataInput datainput) {
        return NbtIoHandle.T.uncompressed_readTagCompound.invoke(datainput);
    }

    public static CompoundTagHandle compressed_readTagCompound(InputStream inputstream) {
        return NbtIoHandle.T.compressed_readTagCompound.invoke(inputstream);
    }

    public static void compressed_writeTagCompound(CompoundTagHandle nbttagcompound, OutputStream outputstream) {
        NbtIoHandle.T.compressed_writeTagCompound.invoke(nbttagcompound, outputstream);
    }

    public static final class NbtIoClass
    extends Template.Class<NbtIoHandle> {
        public final Template.StaticMethod.Converted<CompoundTagHandle> parseTagCompoundFromSNBT = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<TagHandle> parseTagFromSNBT = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<String> handleSNBTParseError = new Template.StaticMethod();
        public final Template.StaticMethod.Converted<Void> uncompressed_writeTag = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<TagHandle> uncompressed_readTag = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Void> uncompressed_writeTagCompound = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<CompoundTagHandle> uncompressed_readTagCompound = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<CompoundTagHandle> compressed_readTagCompound = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Void> compressed_writeTagCompound = new Template.StaticMethod.Converted();
    }
}

