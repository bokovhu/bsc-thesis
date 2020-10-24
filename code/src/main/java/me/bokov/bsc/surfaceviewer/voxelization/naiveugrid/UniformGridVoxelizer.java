package me.bokov.bsc.surfaceviewer.voxelization.naiveugrid;

import java.nio.FloatBuffer;
import java.util.Arrays;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.SDFGenerator;
import me.bokov.bsc.surfaceviewer.voxelization.Corner;
import me.bokov.bsc.surfaceviewer.voxelization.SDFVoxelizer;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class UniformGridVoxelizer implements SDFVoxelizer<UniformGrid> {

    private static final float EPSILON = 0.002f;
    private final int width, height, depth;
    private final Vector3f dxPlus = new Vector3f();
    private final Vector3f dxMinus = new Vector3f();
    private final Vector3f dyPlus = new Vector3f();
    private final Vector3f dyMinus = new Vector3f();
    private final Vector3f dzPlus = new Vector3f();
    private final Vector3f dzMinus = new Vector3f();
    private final Vector3f[] tmpQueryPoints;
    private final FloatBuffer tmpQueryOut;

    private final Vector3f[] tmpNormalQueryPoints;
    private final FloatBuffer tmpNormalQueryOut;

    public UniformGridVoxelizer(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        tmpQueryPoints = new Vector3f[width * height];
        for (int i = 0; i < width * height; i++) {
            tmpQueryPoints[i] = new Vector3f();
        }
        tmpQueryOut = BufferUtils.createFloatBuffer(width * height);

        tmpNormalQueryPoints = new Vector3f[width * height * 6];
        for (int i = 0; i < width * height * 6; i++) {
            tmpNormalQueryPoints[i] = new Vector3f();
        }
        tmpNormalQueryOut = BufferUtils.createFloatBuffer(width * height * 6);
    }

    private Vector3f normal(SDFGenerator generator, UniformGrid grid, Vector3f p) {
        return new Vector3f(
                generator.query(dxPlus.set(p).add(EPSILON, 0f, 0f)) - generator
                        .query(dxMinus.set(p).add(-EPSILON, 0f, 0f)),
                generator.query(dyPlus.set(p).add(0f, EPSILON, 0f)) - generator
                        .query(dyMinus.set(p).add(0f, -EPSILON, 0f)),
                generator.query(dzPlus.set(p).add(0f, 0f, EPSILON)) - generator
                        .query(dzMinus.set(p).add(0f, 0f, -EPSILON))
        ).normalize();
    }

    private void makeSheetCorners(SDFGenerator generator, UniformGrid grid, int z, Corner[] out) {

        final Vector3f tmp = new Vector3f();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final Vector3f p = grid.localToGlobal(tmp.set(x, y, z));
                tmpQueryPoints[y * width + x].set(p);

                tmpNormalQueryPoints[6 * (y * width + x)].set(dxPlus.set(p).add(EPSILON, 0f, 0f));
                tmpNormalQueryPoints[6 * (y * width + x) + 1]
                        .set(dxMinus.set(p).add(-EPSILON, 0f, 0f));
                tmpNormalQueryPoints[6 * (y * width + x) + 2]
                        .set(dyPlus.set(p).add(0f, EPSILON, 0f));
                tmpNormalQueryPoints[6 * (y * width + x) + 3]
                        .set(dyMinus.set(p).add(0f, -EPSILON, 0f));
                tmpNormalQueryPoints[6 * (y * width + x) + 4]
                        .set(dzPlus.set(p).add(0f, 0f, EPSILON));
                tmpNormalQueryPoints[6 * (y * width + x) + 5]
                        .set(dyMinus.set(p).add(0f, 0f, -EPSILON));
            }
        }

        tmpQueryOut.clear();
        tmpNormalQueryOut.clear();
        generator.query(tmpQueryOut, tmpQueryPoints);
        generator.query(tmpNormalQueryOut, tmpNormalQueryPoints);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int idx = y * width + x;
                final Vector3f cornerPoint = tmpQueryPoints[idx];
                final Vector3f cornerNormal = new Vector3f(
                        tmpNormalQueryOut.get(idx * 6) - tmpNormalQueryOut.get(idx * 6 + 1),
                        tmpNormalQueryOut.get(idx * 6 + 2) - tmpNormalQueryOut.get(idx * 6 + 3),
                        tmpNormalQueryOut.get(idx * 6 + 4) - tmpNormalQueryOut.get(idx * 6 + 5)
                ).normalize();
                out[y * width + x] = new Corner(
                        new Vector3f(cornerPoint), tmpQueryOut.get(idx), cornerNormal);
            }
        }

    }

    @Override
    public UniformGrid voxelize(SDFGenerator generator, MeshTransform transform) {

        long start = System.currentTimeMillis();

        UniformGrid result = new UniformGrid(width - 1, height - 1, depth - 1)
                .applyTransform(transform);

        Corner[] lastSheet = new Corner[width * height];
        Corner[] currSheet = new Corner[width * height];

        makeSheetCorners(generator, result, 0, lastSheet);

        for (int z = 0; z < depth - 1; z++) {

            makeSheetCorners(generator, result, z + 1, currSheet);

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
