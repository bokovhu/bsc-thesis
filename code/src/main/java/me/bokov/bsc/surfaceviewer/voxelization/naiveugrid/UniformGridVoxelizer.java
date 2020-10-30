package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.util.MetricsLogger;
import me.bokov.bsc.surfaceviewer.voxelization.Corner;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.sdf.threed.CPUEvaluationContext.*;

public class UniformGridVoxelizer implements Voxelizer3D<UniformGrid> {

    private static final float EPSILON = 0.002f;
    private final int width, height, depth;
    private final Vector3f dxPlus = new Vector3f();
    private final Vector3f dxMinus = new Vector3f();
    private final Vector3f dyPlus = new Vector3f();
    private final Vector3f dyMinus = new Vector3f();
    private final Vector3f dzPlus = new Vector3f();
    private final Vector3f dzMinus = new Vector3f();

    public UniformGridVoxelizer(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    private Vector3f normal(CPUEvaluator<Float, CPUContext> generator, UniformGrid grid, Vector3f p) {
        return new Vector3f(
                generator.evaluate(of(dxPlus.set(p).add(EPSILON, 0f, 0f))) - generator
                        .evaluate(of(dxMinus.set(p).add(-EPSILON, 0f, 0f))),
                generator.evaluate(of(dyPlus.set(p).add(0f, EPSILON, 0f))) - generator
                        .evaluate(of(dyMinus.set(p).add(0f, -EPSILON, 0f))),
                generator.evaluate(of(dzPlus.set(p).add(0f, 0f, EPSILON))) - generator
                        .evaluate(of(dzMinus.set(p).add(0f, 0f, -EPSILON)))
        ).normalize();
    }

    private void makeSheetCorners(CPUEvaluator<Float, CPUContext> generator, UniformGrid grid, int z, Corner[] out) {

        final Vector3f tmpP = new Vector3f();
        final Vector3f tmpGlobal = new Vector3f();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int idx = y * width + x;
                grid.localToGlobal(tmpP.set(x, y, z), tmpGlobal);
                out[y * width + x] = new Corner(
                        tmpGlobal,
                        generator.evaluate(of(tmpGlobal)),
                        normal(generator, grid, tmpGlobal)
                );
            }
        }

    }

    @Override
    public UniformGrid voxelize(
            Evaluatable<Float, CPUContext, GPUContext> generator,
            MeshTransform transform,
            VoxelizationContext context
    ) {

        long start = System.currentTimeMillis();

        UniformGrid result = new UniformGrid(width - 1, height - 1, depth - 1)
                .applyTransform(transform);

        Corner[] lastSheet = new Corner[width * height];
        Corner[] currSheet = new Corner[width * height];

        makeSheetCorners(generator.cpu(), result, 0, lastSheet);

        for (int z = 0; z < depth - 1; z++) {

            makeSheetCorners(generator.cpu(), result, z + 1, currSheet);

            for (int y = 0; y < height - 1; y++) {
                for (int x = 0; x < width - 1; x++) {

                    Voxel voxel = new Voxel(
                            lastSheet[y * width + x],
                            currSheet[y * width + x],
                            lastSheet[(y + 1) * width + x],
                            currSheet[(y + 1) * width + x],

                            lastSheet[y * width + x + 1],
                            currSheet[y * width + x + 1],
                            lastSheet[(y + 1) * width + x + 1],
                            currSheet[(y + 1) * width + x + 1],

                            result.localToGlobal(new Vector3f(x, y, z)),
                            result.localToGlobal(new Vector3f(x + 1, y + 1, z + 1))
                    );

                    result.putVoxel(x, y, z, voxel);

                }
            }

            Corner[] temp = lastSheet;
            lastSheet = currSheet;
            currSheet = temp;
            Arrays.fill(currSheet, null);

        }

        long end = System.currentTimeMillis();

        MetricsLogger.logMetrics(
                "Uniform grid voxelization",
                Map.of(
                        "Runtime", (end - start) + " ms",
                        "Number of voxels generated", result.getWidth() * result.getHeight() * result.getDepth()
                )
        );

        return result;

    }
}
