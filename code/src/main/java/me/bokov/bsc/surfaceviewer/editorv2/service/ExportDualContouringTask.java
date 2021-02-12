package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.*;
import javafx.concurrent.Task;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.mesh.dccpu.GridDualContouring;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.voxelization.CPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridVoxelizer;
import org.joml.Vector3f;

import java.util.*;

public class ExportDualContouringTask extends Task<List<Drawables.Face>> {

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
                gridWidthProperty.get(),
                gridHeightProperty.get(),
                gridDepthProperty.get()
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
        final var dc = new GridDualContouring();

        return dc.generateTriangles(voxelStorage);
    }
}
