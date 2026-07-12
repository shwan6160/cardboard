/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import com.bergerkiller.bukkit.common.dep.cloud.services.PipelineException;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServicePipeline;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServicePump;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServiceRepository;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ServiceSpigot<Context, Result> {
    private final Context context;
    private final ServicePipeline pipeline;
    private final ServiceRepository<Context, Result> repository;

    ServiceSpigot(@NonNull ServicePipeline pipeline, @NonNull Context context, @NonNull TypeToken<? extends Service<@NonNull Context, @NonNull Result>> type) {
        this.context = context;
        this.pipeline = pipeline;
        this.repository = pipeline.getRepository(type);
    }

    /*
     * Exception decompiling
     */
    public @NonNull Result complete() throws IllegalStateException, PipelineException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * java.lang.NullPointerException: Cannot invoke "org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance$Annotated.pathIterator()" because the return value of "org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance$Annotated.access$300(org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance$Annotated)" is null
         *     at org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance$Annotated$Iterator.moveNested(JavaRefTypeInstance.java:200)
         *     at org.benf.cfr.reader.entities.attributes.TypePathPartNested.apply(TypePathPartNested.java:14)
         *     at org.benf.cfr.reader.bytecode.analysis.types.TypeAnnotationHelper.apply(TypeAnnotationHelper.java:54)
         *     at org.benf.cfr.reader.bytecode.analysis.types.TypeAnnotationHelper.apply(TypeAnnotationHelper.java:45)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.transformers.TypeAnnotationTransformer.handleStatement(TypeAnnotationTransformer.java:141)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredAssignment.rewriteExpressions(StructuredAssignment.java:144)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.transformers.TypeAnnotationTransformer.transform(TypeAnnotationTransformer.java:61)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.transform(Op04StructuredStatement.java:680)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.transformStructuredChildren(Block.java:421)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.transformers.TypeAnnotationTransformer.transform(TypeAnnotationTransformer.java:60)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.transform(Op04StructuredStatement.java:680)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.transformers.TypeAnnotationTransformer.transform(TypeAnnotationTransformer.java:55)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.applyTypeAnnotations(Op04StructuredStatement.java:733)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:957)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void complete(@NonNull BiConsumer<Result, Throwable> consumer) {
        try {
            consumer.accept(this.complete(), null);
        }
        catch (PipelineException pipelineException) {
            consumer.accept(null, pipelineException.getCause());
        }
        catch (Exception e) {
            consumer.accept(null, e);
        }
    }

    public @NonNull CompletableFuture<Result> completeAsynchronously() {
        return CompletableFuture.supplyAsync(this::complete, this.pipeline.executor());
    }

    public @NonNull ServicePump<Result> forward() {
        return this.pipeline.pump(this.complete());
    }

    public @NonNull CompletableFuture<ServicePump<Result>> forwardAsynchronously() {
        return this.completeAsynchronously().thenApply(this.pipeline::pump);
    }
}

