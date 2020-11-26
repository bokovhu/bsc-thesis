package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.*;
import javafx.concurrent.Task;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.MarchingCubes;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.voxelization.CPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridVoxelizer;
import org.joml.Vector3f;

import java.io.File;
import java.util.*;

public class ExportMarchingCubesTask extends Task<List<Drawables.Face>> {

    @Getter
    private final ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @Getter
    private final IntegerProperty gridWidthProperty = new SimpleIntegerProperty(64);

    @Getter
    private final IntegerProperty gridHeightProperty = new SimpleIntegerProperty(64);

    @Getter
    private final IntegerProperty gridDepthProperty = new SimpleIntegerProperty(64);

    @Getter
    private final ObjectProperty<Vector3f> gridOffsetProperty = new SimpleObjectProperty<>(
            new Vector3f(-2f, -2f, -2f)
    );

    @Getter
    private final FloatProperty gridScaleProperty = new SimpleFloatProperty(4f);

    @Override
    protected List<Drawables.Face> call() throws Exception {

        World world = worldProperty.get();

        UniformGridVoxelizer voxelizer = new UniformGridVoxelizer(
                64, 64, 64
        );
        final var voxelStorage = voxelizer.voxelize(
                world,
                new MeshTransform(
                        gridOffsetProperty.get(),
                        new Vector3f(0f, 1f, 0f), 0f,
                        gridScaleProperty.get()
                ),
                new CPUVoxelizationContext()
        );
        final var marchingCubes = new MarchingCubes(0.0f);

        return marchingCubes.generateTriangles(voxelStorage);
    }
}
