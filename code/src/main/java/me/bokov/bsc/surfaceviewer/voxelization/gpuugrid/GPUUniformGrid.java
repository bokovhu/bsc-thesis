package me.bokov.bsc.surfaceviewer.voxelization.gpuugrid;

import java.nio.FloatBuffer;
import java.util.Iterator;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.voxelization.Corner;
import me.bokov.bsc.surfaceviewer.voxelization.SDFVoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

public class GPUUniformGrid implements SDFVoxelStorage {

    private final int width, height, depth;
    private final FloatBuffer positionAndValueBuffer, normalBuffer;
    private int positionAndValueTextureId;
    private int normalTextureId;
    private MeshTransform transform;
    private UniformGrid cpuGrid;
    private boolean preparedTextures = false;

    public GPUUniformGrid(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.transform = new MeshTransform(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(),
                new Vector3f(
                        1f, 1f, 1f
                )
        );

        positionAndValueBuffer = BufferUtils.createFloatBuffer(width * height * depth * 4);
        normalBuffer = BufferUtils.createFloatBuffer(width * height * depth * 3);
    }

    public GPUUniformGrid applyTransform(MeshTransform transform) {
        this.transform = transform;
        return this;
    }

    private Corner newCorner() {
        return new Corner(new Vector3f(), 0.0f, new Vector3f());
    }

    private void prepareCPUVoxels() {

        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    cpuGrid.putVoxel(
                            x, y, z,
                            new Voxel(
                                    newCorner(), newCorner(), newCorner(), newCorner(),
                                    newCorner(), newCorner(), newCorner(), newCorner(),
                                    new Vector3f(), new Vector3f()
                            )
                    );
                }
            }
        }

    }

    private int idx(int x, int y, int z) {
        return z * width * height + y * width + x;
    }

    private void adjustCorner(Corner c, int x, int y, int z) {
        c.getPoint().set(
                positionAndValueBuffer.get(4 * idx(x, y, z)),
                positionAndValueBuffer.get(4 * idx(x, y, z) + 1),
                positionAndValueBuffer.get(4 * idx(x, y, z) + 2)
        );
        c.getNormal().set(
                positionAndValueBuffer.get(3 * idx(x, y, z)),
                positionAndValueBuffer.get(3 * idx(x, y, z) + 1),
                positionAndValueBuffer.get(3 * idx(x, y, z) + 2)
        );
        c.setValue(positionAndValueBuffer.get(4 * idx(x + 1, y, z) + 3));
    }

    public void downloadToCPUGrid() {

        if (cpuGrid == null) {
            cpuGrid = new UniformGrid(width, height, depth);
            prepareCPUVoxels();
        }

        GL46.glFlush();
        GL46.glFinish();

        GL46.glBindTexture(GL46.GL_TEXTURE_3D, positionAndValueTextureId);
        positionAndValueBuffer.clear();
        GL46.glGetTexImage(
                GL46.GL_TEXTURE_3D, 0, GL46.GL_RGBA, GL46.GL_FLOAT, positionAndValueBuffer);

        GL46.glBindTexture(GL46.GL_TEXTURE_3D, normalTextureId);
        normalBuffer.clear();
        GL46.glGetTexImage(GL46.GL_TEXTURE_3D, 0, GL46.GL_RGB, GL46.GL_FLOAT, normalBuffer);

        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final Voxel v = cpuGrid.at(idx(x, y, z));

                    adjustCorner(v.getC000(), x, y, z);
                    adjustCorner(v.getC001(), x, y, z + 1);
                    adjustCorner(v.getC010(), x, y + 1, z);
                    adjustCorner(v.getC011(), x, y + 1, z + 1);

                    adjustCorner(v.getC100(), x + 1, y, z);
                    adjustCorner(v.getC101(), x + 1, y, z + 1);
                    adjustCorner(v.getC110(), x + 1, y + 1, z);
                    adjustCorner(v.getC111(), x + 1, y + 1, z + 1);

                    v.getP1().set(v.getC000().getPoint());
                    v.getP2().set(v.getC111().getPoint());
                }
            }
        }

    }

    public void prepareTexture() {

        if (preparedTextures) return;

        this.positionAndValueTextureId = GL46.glGenTextures();
        this.normalTextureId = GL46.glGenTextures();

        GL46.glBindTexture(GL46.GL_TEXTURE_3D, positionAndValueTextureId);
        GL46.glTexStorage3D(
                GL46.GL_TEXTURE_3D,
                1,
                GL46.GL_RGBA32F,
                width, height, depth
        );

        GL46.glBindTexture(GL46.GL_TEXTURE_3D, normalTextureId);
        GL46.glTexStorage3D(
                GL46.GL_TEXTURE_3D,
                1,
                GL46.GL_RGB32F,
                width, height, depth
        );

        preparedTextures = true;

    }

    @Override
    public Iterator<Voxel> voxelIterator() {
        return cpuGrid.voxelIterator();
    }

    @Override
    public Voxel closestVoxel(Vector3f p) {
        return cpuGrid.closestVoxel(p);
    }
}
