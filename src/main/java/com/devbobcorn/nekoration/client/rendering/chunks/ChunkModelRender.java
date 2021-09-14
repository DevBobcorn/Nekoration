package com.devbobcorn.nekoration.client.rendering.chunks;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkModelRender implements net.minecraftforge.client.extensions.IForgeRenderChunk {
    public static final int SIZE = 16;
    public final int index;
    public final AtomicReference<CompiledModelChunk> compiled = new AtomicReference<>(
            CompiledModelChunk.UNCOMPILED);
    @Nullable
    private ChunkModelRender.RebuildTask lastRebuildTask;
    @Nullable
    private ResortTransparencyTask lastResortTransparencyTask;
    private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
    private final Map<RenderType, VertexBuffer> buffers = RenderType.chunkBufferLayers().stream()
            .collect(Collectors.toMap((p_112837_) -> {
                return p_112837_;
            }, (p_112834_) -> {
                return new VertexBuffer();
            }));
    public AABB bb;
    private int lastFrame = -1;
    private boolean dirty = true;
    final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos(-1, -1, -1);
    private final BlockPos.MutableBlockPos[] relativeOrigins = Util.make(new BlockPos.MutableBlockPos[6],
            (p_112831_) -> {
                for (int i = 0; i < p_112831_.length; ++i) {
                    p_112831_[i] = new BlockPos.MutableBlockPos();
                }

            });
    private boolean playerChanged;

    // Additional fields...
    private Level level;
    private final LevelRenderer renderer;
    private Vec3 camera = Vec3.ZERO;
    public final ChunkBufferBuilderPack fixedBuffers;

    // Constructor with additional data
    public ChunkModelRender(Level wd, LevelRenderer rd, int idx){
        this.level = wd;
        this.renderer = rd;
        this.fixedBuffers = new ChunkBufferBuilderPack();
        this.index = idx;
    }

    public void setCamera(Vec3 cam){
        this.camera = cam;
    }

    private CompletableFuture<Void> doUploadChunkLayer(BufferBuilder p_228904_1_, VertexBuffer p_228904_2_) {
        return p_228904_2_.uploadLater(p_228904_1_);
    }

    public void compileModel(){
        this.compileSync();
    }
    // Modification end

    private boolean doesChunkExistAt(BlockPos p_112823_) {
        return this.level.getChunk(SectionPos.blockToSectionCoord(p_112823_.getX()),
                SectionPos.blockToSectionCoord(p_112823_.getZ()), ChunkStatus.FULL, false) != null;
    }

    public boolean hasAllNeighbors() {
        if (!(this.getDistToPlayerSqr() > 576.0D)) {
            return true;
        } else {
            return this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()])
                    && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()])
                    && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()])
                    && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
        }
    }

    public boolean setFrame(int p_112800_) {
        if (this.lastFrame == p_112800_) {
            return false;
        } else {
            this.lastFrame = p_112800_;
            return true;
        }
    }

    public VertexBuffer getBuffer(RenderType p_112808_) {
        return this.buffers.get(p_112808_);
    }

    public void setOrigin(int p_112802_, int p_112803_, int p_112804_) {
        if (p_112802_ != this.origin.getX() || p_112803_ != this.origin.getY() || p_112804_ != this.origin.getZ()) {
            this.reset();
            this.origin.set(p_112802_, p_112803_, p_112804_);
            this.bb = new AABB((double) p_112802_, (double) p_112803_, (double) p_112804_, (double) (p_112802_ + 16),
                    (double) (p_112803_ + 16), (double) (p_112804_ + 16));

            for (Direction direction : Direction.values()) {
                this.relativeOrigins[direction.ordinal()].set(this.origin).move(direction, 16);
            }

        }
    }

    @SuppressWarnings("resource")
    protected double getDistToPlayerSqr() {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double d0 = this.bb.minX + 8.0D - camera.getPosition().x;
        double d1 = this.bb.minY + 8.0D - camera.getPosition().y;
        double d2 = this.bb.minZ + 8.0D - camera.getPosition().z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    void beginLayer(BufferBuilder p_112806_) {
        p_112806_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
    }

    public CompiledModelChunk getCompiledChunk() {
        return this.compiled.get();
    }

    private void reset() {
        this.cancelTasks();
        this.compiled.set(CompiledModelChunk.UNCOMPILED);
        this.dirty = true;
    }

    public void releaseBuffers() {
        this.reset();
        this.buffers.values().forEach(VertexBuffer::close);
    }

    public BlockPos getOrigin() {
        return this.origin;
    }

    public void setDirty(boolean p_112829_) {
        boolean flag = this.dirty;
        this.dirty = true;
        this.playerChanged = p_112829_ | (flag && this.playerChanged);
    }

    public void setNotDirty() {
        this.dirty = false;
        this.playerChanged = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public boolean isDirtyFromPlayer() {
        return this.dirty && this.playerChanged;
    }

    public BlockPos getRelativeOrigin(Direction p_112825_) {
        return this.relativeOrigins[p_112825_.ordinal()];
    }

    public boolean resortTransparency(RenderType p_112810_, ChunkRenderDispatcher p_112811_) {
        CompiledModelChunk chunkrenderdispatcher$compiledchunk = this.getCompiledChunk();
        if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
        }

        if (!chunkrenderdispatcher$compiledchunk.hasLayer.contains(p_112810_)) {
            return false;
        } else {
            this.lastResortTransparencyTask = new ResortTransparencyTask(
                    new net.minecraft.world.level.ChunkPos(getOrigin()), this.getDistToPlayerSqr(),
                    chunkrenderdispatcher$compiledchunk);
            //p_112811_.schedule(this.lastResortTransparencyTask);
            this.lastResortTransparencyTask.doTask(this.fixedBuffers);
            return true;
        }
    }

    protected void cancelTasks() {
        if (this.lastRebuildTask != null) {
            this.lastRebuildTask.cancel();
            this.lastRebuildTask = null;
        }

        if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
            this.lastResortTransparencyTask = null;
        }

    }

    public ChunkCompileTask createCompileTask() {
        this.cancelTasks();
        BlockPos blockpos = this.origin.immutable();
        RenderChunkRegion renderchunkregion = createRegionRenderCache(this.level,
                blockpos.offset(-1, -1, -1), blockpos.offset(16, 16, 16), 1);
        this.lastRebuildTask = new RebuildTask(
                new net.minecraft.world.level.ChunkPos(getOrigin()), this.getDistToPlayerSqr(), renderchunkregion);
        return this.lastRebuildTask;
    }

    public void rebuildChunkAsync() {
        ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this
                .createCompileTask();
        //p_112821_.schedule(chunkrenderdispatcher$renderchunk$chunkcompiletask);
        chunkrenderdispatcher$renderchunk$chunkcompiletask.doTask(fixedBuffers);
    }

    void updateGlobalBlockEntities(Set<BlockEntity> p_112827_) {
        Set<BlockEntity> set = Sets.newHashSet(p_112827_);
        Set<BlockEntity> set1 = Sets.newHashSet(this.globalBlockEntities);
        set.removeAll(this.globalBlockEntities);
        set1.removeAll(p_112827_);
        this.globalBlockEntities.clear();
        this.globalBlockEntities.addAll(p_112827_);
        this.renderer.updateGlobalBlockEntities(set1, set);
    }

    public void compileSync() {
        ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this
                .createCompileTask();
        chunkrenderdispatcher$renderchunk$chunkcompiletask.doTask(this.fixedBuffers);
    }

    @OnlyIn(Dist.CLIENT)
    abstract class ChunkCompileTask implements Comparable<ChunkCompileTask> {
        protected final double distAtCreation;
        protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
        protected java.util.Map<net.minecraft.core.BlockPos, net.minecraftforge.client.model.data.IModelData> modelData;

        public ChunkCompileTask(double p_112852_) {
            this(null, p_112852_);
        }

        @SuppressWarnings("resource")
        public ChunkCompileTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_112852_) {
            this.distAtCreation = p_112852_;
            if (pos == null) {
                this.modelData = java.util.Collections.emptyMap();
            } else {
                this.modelData = net.minecraftforge.client.model.ModelDataManager
                        .getModelData(net.minecraft.client.Minecraft.getInstance().level, pos);
            }
        }

        public abstract CompletableFuture<ModelChunkTaskResult> doTask(
                ChunkBufferBuilderPack p_112853_);

        public abstract void cancel();

        public int compareTo(ChunkCompileTask p_112855_) {
            return Doubles.compare(this.distAtCreation, p_112855_.distAtCreation);
        }

        public net.minecraftforge.client.model.data.IModelData getModelData(net.minecraft.core.BlockPos pos) {
            return modelData.getOrDefault(pos, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class RebuildTask extends ChunkCompileTask {
        @Nullable
        protected RenderChunkRegion region;

        @Deprecated
        public RebuildTask(@Nullable double p_112862_, RenderChunkRegion p_112863_) {
            this(null, p_112862_, p_112863_);
        }

        public RebuildTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_112862_,
                @Nullable RenderChunkRegion p_112863_) {
            super(pos, p_112862_);
            this.region = p_112863_;
        }

        public CompletableFuture<ModelChunkTaskResult> doTask(ChunkBufferBuilderPack p_112872_) {
            if (this.isCancelled.get()) {
                return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
            } else if (!ChunkModelRender.this.hasAllNeighbors()) {
                this.region = null;
                ChunkModelRender.this.setDirty(false);
                this.isCancelled.set(true);
                return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
                return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
            } else {
                Vec3 cam = ChunkModelRender.this.camera;
                float camX = (float) cam.x;
                float camY = (float) cam.y;
                float camZ = (float) cam.z;
                CompiledModelChunk chunkrenderdispatcher$compiledchunk = new CompiledModelChunk();
                Set<BlockEntity> set = this.compile(camX, camY, camZ, chunkrenderdispatcher$compiledchunk, p_112872_);
                ChunkModelRender.this.updateGlobalBlockEntities(set);
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
                } else {
                    List<CompletableFuture<Void>> list = Lists.newArrayList();
                    chunkrenderdispatcher$compiledchunk.hasLayer.forEach((p_112884_) -> {
                        list.add(ChunkModelRender.this.doUploadChunkLayer(p_112872_.builder(p_112884_),
                                ChunkModelRender.this.getBuffer(p_112884_)));
                    });
                    return Util.sequenceFailFast(list).handle((p_112875_, p_112876_) -> {
                        if (p_112876_ != null && !(p_112876_ instanceof CancellationException)
                                && !(p_112876_ instanceof InterruptedException)) {
                            Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_112876_, "Rendering chunk"));
                        }

                        if (this.isCancelled.get()) {
                            return ModelChunkTaskResult.CANCELLED;
                        } else {
                            ChunkModelRender.this.compiled.set(chunkrenderdispatcher$compiledchunk);
                            return ModelChunkTaskResult.SUCCESSFUL;
                        }
                    });
                }
            }
        }

        private Set<BlockEntity> compile(float p_112866_, float p_112867_, float p_112868_,
                CompiledModelChunk p_112869_, ChunkBufferBuilderPack p_112870_) {
            BlockPos blockpos = ChunkModelRender.this.origin.immutable();
            BlockPos blockpos1 = blockpos.offset(15, 15, 15);
            VisGraph visgraph = new VisGraph();
            Set<BlockEntity> set = Sets.newHashSet();
            RenderChunkRegion renderchunkregion = this.region;
            this.region = null;
            PoseStack posestack = new PoseStack();
            if (renderchunkregion != null) {
                ModelBlockRenderer.enableCaching();
                Random random = new Random();
                BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();

                for (BlockPos blockpos2 : BlockPos.betweenClosed(blockpos, blockpos1)) {
                    BlockState blockstate = renderchunkregion.getBlockState(blockpos2);
                    if (blockstate.isSolidRender(renderchunkregion, blockpos2)) {
                        visgraph.setOpaque(blockpos2);
                    }

                    if (blockstate.hasBlockEntity()) {
                        BlockEntity blockentity = renderchunkregion.getBlockEntity(blockpos2,
                                LevelChunk.EntityCreationType.CHECK);
                        if (blockentity != null) {
                            this.handleBlockEntity(p_112869_, set, blockentity);
                        }
                    }

                    FluidState fluidstate = renderchunkregion.getFluidState(blockpos2);
                    net.minecraftforge.client.model.data.IModelData modelData = getModelData(blockpos2);
                    for (RenderType rendertype : RenderType.chunkBufferLayers()) {
                        net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
                        if (!fluidstate.isEmpty() && ItemBlockRenderTypes.canRenderInLayer(fluidstate, rendertype)) {
                            BufferBuilder bufferbuilder = p_112870_.builder(rendertype);
                            if (p_112869_.hasLayer.add(rendertype)) {
                                ChunkModelRender.this.beginLayer(bufferbuilder);
                            }

                            if (blockrenderdispatcher.renderLiquid(blockpos2, renderchunkregion, bufferbuilder,
                                    fluidstate)) {
                                p_112869_.isCompletelyEmpty = false;
                                p_112869_.hasBlocks.add(rendertype);
                            }
                        }

                        if (blockstate.getRenderShape() != RenderShape.INVISIBLE
                                && ItemBlockRenderTypes.canRenderInLayer(blockstate, rendertype)) {
                            RenderType rendertype1 = rendertype;
                            BufferBuilder bufferbuilder2 = p_112870_.builder(rendertype1);
                            if (p_112869_.hasLayer.add(rendertype1)) {
                                ChunkModelRender.this.beginLayer(bufferbuilder2);
                            }

                            posestack.pushPose();
                            posestack.translate((double) (blockpos2.getX() & 15), (double) (blockpos2.getY() & 15),
                                    (double) (blockpos2.getZ() & 15));
                            if (blockrenderdispatcher.renderBatched(blockstate, blockpos2, renderchunkregion, posestack,
                                    bufferbuilder2, true, random, modelData)) {
                                p_112869_.isCompletelyEmpty = false;
                                p_112869_.hasBlocks.add(rendertype1);
                            }

                            posestack.popPose();
                        }
                    }
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);

                if (p_112869_.hasBlocks.contains(RenderType.translucent())) {
                    BufferBuilder bufferbuilder1 = p_112870_.builder(RenderType.translucent());
                    bufferbuilder1.setQuadSortOrigin(p_112866_ - (float) blockpos.getX(),
                            p_112867_ - (float) blockpos.getY(), p_112868_ - (float) blockpos.getZ());
                    p_112869_.transparencyState = bufferbuilder1.getSortState();
                }

                p_112869_.hasLayer.stream().map(p_112870_::builder).forEach(BufferBuilder::end);
                ModelBlockRenderer.clearCache();
            }

            p_112869_.visibilitySet = visgraph.resolve();
            return set;
        }

        private <E extends BlockEntity> void handleBlockEntity(CompiledModelChunk p_112878_,
                Set<BlockEntity> p_112879_, E p_112880_) {
            BlockEntityRenderer<E> blockentityrenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher()
                    .getRenderer(p_112880_);
            if (blockentityrenderer != null) {
                if (blockentityrenderer.shouldRenderOffScreen(p_112880_)) {
                    p_112879_.add(p_112880_);
                } else
                    p_112878_.renderableBlockEntities.add(p_112880_); // FORGE: Fix MC-112730
            }

        }

        public void cancel() {
            this.region = null;
            if (this.isCancelled.compareAndSet(false, true)) {
                ChunkModelRender.this.setDirty(false);
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    class ResortTransparencyTask extends ChunkCompileTask {
        private final CompiledModelChunk compiledChunk;

        @Deprecated
        public ResortTransparencyTask(double p_112889_, CompiledModelChunk p_112890_) {
            this(null, p_112889_, p_112890_);
        }

        public ResortTransparencyTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_112889_,
                CompiledModelChunk p_112890_) {
            super(pos, p_112889_);
            this.compiledChunk = p_112890_;
        }

        public CompletableFuture<ModelChunkTaskResult> doTask(ChunkBufferBuilderPack p_112893_) {
            if (this.isCancelled.get()) {
                return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
            } else if (!ChunkModelRender.this.hasAllNeighbors()) {
                this.isCancelled.set(true);
                return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
                return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
            } else {
                Vec3 vec3 = ChunkModelRender.this.camera;
                float f = (float) vec3.x;
                float f1 = (float) vec3.y;
                float f2 = (float) vec3.z;
                BufferBuilder.SortState bufferbuilder$sortstate = this.compiledChunk.transparencyState;
                if (bufferbuilder$sortstate != null
                        && this.compiledChunk.hasBlocks.contains(RenderType.translucent())) {
                    BufferBuilder bufferbuilder = p_112893_.builder(RenderType.translucent());
                    ChunkModelRender.this.beginLayer(bufferbuilder);
                    bufferbuilder.restoreSortState(bufferbuilder$sortstate);
                    bufferbuilder.setQuadSortOrigin(f - (float) ChunkModelRender.this.origin.getX(),
                            f1 - (float) ChunkModelRender.this.origin.getY(), f2 - (float) ChunkModelRender.this.origin.getZ());
                    this.compiledChunk.transparencyState = bufferbuilder.getSortState();
                    bufferbuilder.end();
                    if (this.isCancelled.get()) {
                        return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
                    } else {
                        CompletableFuture<ModelChunkTaskResult> completablefuture = ChunkModelRender.this
                                .doUploadChunkLayer(p_112893_.builder(RenderType.translucent()),
                                        ChunkModelRender.this.getBuffer(RenderType.translucent()))
                                .thenApply((p_112898_) -> {
                                    return ModelChunkTaskResult.CANCELLED;
                                });
                        return completablefuture.handle((p_112895_, p_112896_) -> {
                            if (p_112896_ != null && !(p_112896_ instanceof CancellationException)
                                    && !(p_112896_ instanceof InterruptedException)) {
                                Minecraft.getInstance()
                                        .delayCrash(CrashReport.forThrowable(p_112896_, "Rendering chunk"));
                            }

                            return this.isCancelled.get() ? ModelChunkTaskResult.CANCELLED
                                    : ModelChunkTaskResult.SUCCESSFUL;
                        });
                    }
                } else {
                    return CompletableFuture.completedFuture(ModelChunkTaskResult.CANCELLED);
                }
            }
        }

        public void cancel() {
            this.isCancelled.set(true);
        }
    }
}