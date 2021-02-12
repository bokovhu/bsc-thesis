package me.bokov.bsc.surfaceviewer.view;

import lombok.Getter;
import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.voxelization.CPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.GPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.gpuugrid.GPUUniformGridVoxelizer;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridVoxelizer;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

public final class VoxelManager {

    private View view = null;

    @Getter
    private VoxelStorage voxelStorage;

    public void install(View parent) {
        this.view = parent;
    }

    public void uninstall() {

        view = null;
        releaseVoxelStorage();

    }

    public void releaseVoxelStorage() {

        if (this.voxelStorage != null) {
            this.voxelStorage.tearDown();
            this.voxelStorage = null;
        }

    }

    public void voxelizeCPUUniformGrid(int w, int h, int d, Vector3f offs, float scale) {

        if (view.getWorld() != null) {

            releaseVoxelStorage();

            final var voxelizer = new UniformGridVoxelizer(w, h, d);
            final var context = new CPUVoxelizationContext();
            this.voxelStorage = voxelizer.voxelize(
                    view.getWorld(),
                    new MeshTransform(offs, new Vector3f(0f, 0f, 1f), 0f, scale),
                    context
            );

            voxelizer.tearDown();

        }

    }

    public void voxelizeGPUUniformGrid(int w, int h, int d, Vector3f offs, float scale) {

        if (view.getWorld() != null) {

            releaseVoxelStorage();

            final var voxelizer = new GPUUniformGridVoxelizer(w, h, d);
            final var context = new GPUVoxelizationContext();
            this.voxelStorage = voxelizer.voxelize(
                    view.getWorld(),
                    new MeshTransform(offs, new Vector3f(0f, 0f, 1f), 0f, scale),
                    context
            );

            voxelizer.tearDown();

        }

    }

    public void saveVoxelStorage(File outputDir) {

        if (this.voxelStorage != null) {

            outputDir.mkdirs();

            final var data = this.voxelStorage.toVoxelData();

            try (FileOutputStream fos = new FileOutputStream(new File(outputDir, "posv.bin"))) {

                final var buf = BufferUtils.createByteBuffer(Float.BYTES * data.getPositionAndValueBuffer().limit());
                for (int i = 0; i < data.getPositionAndValueBuffer().limit(); i++) {
                    buf.putFloat(data.getPositionAndValueBuffer().get(i));
                }
                fos.getChannel().write(buf);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileOutputStream fos = new FileOutputStream(new File(outputDir, "normal.bin"))) {

                final var buf = BufferUtils.createByteBuffer(Float.BYTES * data.getNormalBuffer().limit());
                for (int i = 0; i < data.getNormalBuffer().limit(); i++) {
                    buf.putFloat(data.getNormalBuffer().get(i));
                }
                fos.getChannel().write(buf);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileOutputStream fos = new FileOutputStream(new File(outputDir, "meta.bin"))) {

                final var buf = BufferUtils.createByteBuffer(Integer.BYTES * 3);
                buf.putInt(data.getWidth());
                buf.putInt(data.getHeight());
                buf.putInt(data.getDepth());
                fos.getChannel().write(buf.flip());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void loadVoxelStorage(File inputDir) {

        if (!inputDir.isDirectory()) {
            throw new IllegalArgumentException("Must be a directory");
        }

        final var posvFile = new File(inputDir, "posv.bin");
        final var normalFile = new File(inputDir, "normal.bin");
        final var colorFile = new File(inputDir, "color.bin");
        final var metaFile = new File(inputDir, "meta.bin");

        if (!posvFile.canRead() || !normalFile.canRead() || !colorFile.canRead() || !metaFile.canRead()) {
            throw new IllegalArgumentException("Cannot find necessary files.");
        }

        int w = 0, h = 0, d = 0;
        FloatBuffer posvBuf, normalBuf, colorBuf;

        try (FileInputStream fis = new FileInputStream(metaFile)) {

            final var buf = BufferUtils.createByteBuffer(Integer.BYTES * 3);

            fis.getChannel()
                    .read(buf);

            w = buf.getInt(0);
            h = buf.getInt(1 * Integer.BYTES);
            d = buf.getInt(2 * Integer.BYTES);

        } catch (IOException e) {
            e.printStackTrace();
        }

        final var tmpPosv = BufferUtils.createByteBuffer(Float.BYTES * 4 * w * h * d);
        final var tmpNormal = BufferUtils.createByteBuffer(Float.BYTES * 3 * w * h * d);
        final var tmpColor = BufferUtils.createByteBuffer(Float.BYTES * 4 * w * h * d);

        try (FileInputStream fis = new FileInputStream(posvFile)) {
            fis.getChannel().read(tmpPosv);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fis = new FileInputStream(normalFile)) {
            fis.getChannel().read(tmpNormal);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fis = new FileInputStream(colorFile)) {
            fis.getChannel().read(tmpColor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        posvBuf = BufferUtils.createFloatBuffer(4 * w * h * d);
        for (int i = 0; i < 4 * w * h * d; i++) {
            posvBuf.put(tmpPosv.getFloat(Float.BYTES * i));
        }

        normalBuf = BufferUtils.createFloatBuffer(3 * w * h * d);
        for (int i = 0; i < 3 * w * h * d; i++) {
            normalBuf.put(tmpNormal.getFloat(Float.BYTES * i));
        }

        colorBuf = BufferUtils.createFloatBuffer(4 * w * h * d);
        for (int i = 0; i < 4 * w * h * d; i++) {
            colorBuf.put(tmpColor.getFloat(Float.BYTES * i));
        }

        releaseVoxelStorage();
        voxelStorage = new UniformGrid(w, h, d, posvBuf, normalBuf);

    }

}
