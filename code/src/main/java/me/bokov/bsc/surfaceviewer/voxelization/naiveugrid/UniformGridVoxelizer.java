package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import java.util.Arrays;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.threed.ExpressionEvaluationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Corner;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import org.joml.Vector3f;

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

    private Vector3f normal(CPUEvaluator<Float, Vector3f> generator, UniformGrid grid, Vector3f p) {
        return new Vector3f(
                generator.evaluate(dxPlus.set(p).add(EPSILON, 0f, 0f)) - generator
                        .evaluate(dxMinus.set(p).add(-EPSILON, 0f, 0f)),
                generator.evaluate(dyPlus.set(p).add(0f, EPSILON, 0f)) - generator
                        .evaluate(dyMinus.set(p).add(0f, -EPSILON, 0f)),
                generator.evaluate(dzPlus.set(p).add(0f, 0f, EPSILON)) - generator
                        .evaluate(dzMinus.set(p).add(0f, 0f, -EPSILON))
        ).normalize();
    }

    private void makeSheetCorners(CPUEvaluator<Float, Vector3f> generator, UniformGrid grid, int z, Corner[] out) {

        final Vector3f tmpP = new Vector3f();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int idx = y * width + x;
                grid.localToGlobal(tmpP.set(x, y, z));
                out[y * width + x] = new Corner(
                        new Vector3f(tmpP), generator.evaluate(tmpP),
                        normal(generator, grid, tmpP)
                );
            }
        }

    }

    @Override
    public UniformGrid voxelize(Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator,
            MeshTransform transform
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
        System.out.println("Voxelization took " + (end - start) + "ms");

        return result;

    }
}
