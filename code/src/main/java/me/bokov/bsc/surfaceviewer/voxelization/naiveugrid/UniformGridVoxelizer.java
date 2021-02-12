package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.scene.Materializer;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.threed.CPUEvaluationContext;
import me.bokov.bsc.surfaceviewer.util.MetricsLogger;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.*;

public class UniformGridVoxelizer implements Voxelizer3D<UniformGrid> {

    private static final float EPSILON = 0.002f;
    private final int width, height, depth;
    private final boolean smoothNormals;
    private final Vector3f dxPlus = new Vector3f();
    private final Vector3f dxMinus = new Vector3f();
    private final Vector3f dyPlus = new Vector3f();
    private final Vector3f dyMinus = new Vector3f();
    private final Vector3f dzPlus = new Vector3f();
    private final Vector3f dzMinus = new Vector3f();
    private final Vector3f tmpNormal = new Vector3f();

    public UniformGridVoxelizer(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.smoothNormals = true;
    }

    public UniformGridVoxelizer(int width, int height, int depth, boolean smoothNormals) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.smoothNormals = smoothNormals;
    }

    private void normal(CPUEvaluator<Float, CPUContext> generator, CPUContext context, Vector3f p) {

        tmpNormal.set(
                generator.evaluate(context.transform(dxPlus.set(p).add(EPSILON, 0f, 0f))) - generator
                        .evaluate(context.transform(dxMinus.set(p).add(-EPSILON, 0f, 0f))),
                generator.evaluate(context.transform(dyPlus.set(p).add(0f, EPSILON, 0f))) - generator
                        .evaluate(context.transform(dyMinus.set(p).add(0f, -EPSILON, 0f))),
                generator.evaluate(context.transform(dzPlus.set(p).add(0f, 0f, EPSILON))) - generator
                        .evaluate(context.transform(dzMinus.set(p).add(0f, 0f, -EPSILON)))
        ).normalize();
    }

    @Override
    public UniformGrid voxelize(
            World world,
            MeshTransform transform,
            VoxelizationContext context
    ) {

        long start = System.currentTimeMillis();

        FloatBuffer positionValueBuffer = BufferUtils.createFloatBuffer(4 * width * height * depth);
        FloatBuffer normalBuffer = smoothNormals ? BufferUtils.createFloatBuffer(3 * width * height * depth) : null;

        final Vector3f p = new Vector3f();
        final Vector3f col = new Vector3f();
        final float pScale = 1.0f / Math.max(width, Math.max(height, depth));
        final var cpu = world.toEvaluable().cpu();

        CPUContext rootContext = new CPUEvaluationContext().setPoint(p);

        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    p.set(x, y, z).mul(pScale);
                    transform.M().transformPosition(p);

                    rootContext = new CPUEvaluationContext().setPoint(p);

                    positionValueBuffer.put(p.x).put(p.y).put(p.z)
                            .put(cpu.evaluate(rootContext));

                    if (smoothNormals) {
                        normal(cpu, rootContext, p);

                        normalBuffer.put(tmpNormal.x).put(tmpNormal.y).put(tmpNormal.z);
                    }

                }
            }
        }

        long computeEnd = System.currentTimeMillis();

        UniformGrid result = new UniformGrid(
                width, height, depth,
                positionValueBuffer,
                normalBuffer
        );

        long end = System.currentTimeMillis();

        MetricsLogger.logMetrics(
                "Uniform grid voxelization",
                Map.of(
                        "Runtime", (end - start) + " ms",
                        "Computation runtime", (computeEnd - start) + " ms",
                        "Result generation runtime", (end - computeEnd) + " ms",
                        "Number of voxels generated", result.xVoxelCount() * result.yVoxelCount() * result.zVoxelCount()
                )
        );

        return result;

    }
}
