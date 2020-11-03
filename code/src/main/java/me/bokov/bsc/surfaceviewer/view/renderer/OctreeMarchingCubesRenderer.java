package me.bokov.bsc.surfaceviewer.view.renderer;

import me.bokov.bsc.surfaceviewer.Property;
import me.bokov.bsc.surfaceviewer.PropertyType;
import me.bokov.bsc.surfaceviewer.Scene;
import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.editor.event.RendererInitialized;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.MarchingCubes;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.util.Resources;
import me.bokov.bsc.surfaceviewer.view.Renderer;
import me.bokov.bsc.surfaceviewer.voxelization.CPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import me.bokov.bsc.surfaceviewer.voxelization.octree.OctreeGrid;
import me.bokov.bsc.surfaceviewer.voxelization.octree.OctreeGridVoxelizer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.PropertyType.*;

public class OctreeMarchingCubesRenderer implements Renderer {

    private static final String GROUP = "OctreeMarchingCubesRenderer";

    private static final Property<Integer> P_DEPTH = new Property<>(PropertyType.INT, GROUP, "Depth", 7);
    private static final Property<Vector3f> P_GRID_OFFSET = new Property<>(
            PropertyType.VEC3,
            GROUP,
            "GridOffset",
            new Vector3f(0f)
    );
    private static final Property<Vector3f> P_GRID_SCALE = new Property<>(
            PropertyType.VEC3,
            GROUP,
            "GridScale",
            new Vector3f(1f, 1f, 1f)
    );
    private static final Property<Float> P_ISO_LEVEL = new Property<>(PropertyType.FLOAT, GROUP, "IsoLevel", 0.0f);

    private static final Property<Vector3f> P_LIGHT_ENERGY = new Property<>(
            VEC3,
            GROUP,
            "LightEnergy",
            new Vector3f(1.8f, 2.4f, 2.3f)
    );
    private static final Property<Vector3f> P_LIGHT_AMBIENT = new Property<>(
            VEC3,
            GROUP,
            "LightAmbient",
            new Vector3f(0.03f, 0.0193f, 0.02f)
    );
    private static final Property<Vector3f> P_LIGHT_DIRECTION = new Property<>(
            VEC3,
            GROUP,
            "LightDirection",
            new Vector3f(-1.5f, 2.5f, 1.6f).normalize()
    );
    private static final Matrix4f IDENTITY = new Matrix4f().identity();
    private Voxelizer3D<OctreeGrid> voxelizer;
    private OctreeGrid voxelStorage;
    private MarchingCubes marchingCubes;
    private Drawable mesh;
    private ShaderProgram shaderProgram;

    private View view = null;

    private void voxelizeScene(Scene scene) {
        if (scene.getMeshes() != null && scene.getMeshes().size() >= 1) {

            this.voxelizer = new OctreeGridVoxelizer(
                    this.view.get(P_DEPTH)
            );
            this.voxelStorage = this.voxelizer.voxelize(
                    scene.toUnion(),
                    new MeshTransform(
                            this.view.get(P_GRID_OFFSET),
                            new Quaternionf(),
                            this.view.get(P_GRID_SCALE)
                    ), new CPUVoxelizationContext()
            );

        }
    }

    private void executeMarchingCubes() {

        if (this.voxelStorage != null) {

            this.marchingCubes = new MarchingCubes(this.view.get(P_ISO_LEVEL));
            this.mesh = this.marchingCubes
                    .generate(voxelStorage);

        }

    }

    public void render(Scene scene) {
        if (this.mesh == null) {

            this.voxelizeScene(scene);
            this.executeMarchingCubes();

        }

        if (this.mesh != null) {

            this.shaderProgram.use();

            this.shaderProgram.uniform("u_Le").vec3(
                    this.view.get(P_LIGHT_ENERGY)
            );
            this.shaderProgram.uniform("u_La").vec3(
                    this.view.get(P_LIGHT_AMBIENT)
            );
            this.shaderProgram.uniform("u_Ld").vec3(
                    this.view.get(P_LIGHT_DIRECTION)
            );
            this.shaderProgram.uniform("u_eye").vec3(
                    this.view.getCamera().eye()
            );
            this.shaderProgram.uniform("u_M").mat4(IDENTITY);
            this.shaderProgram.uniform("u_MVP").mat4(
                    this.view.getCamera().VP()
            );

            this.mesh.draw();

        }

    }

    @Override
    public void install(View parent) {
        this.view = parent;

        this.shaderProgram = parent.getShaderManager().load("default")
                .vertexFromResource(
                        Resources.GLSL_VERTEX_STANDARD_3D_TRANSFORMED)
                .fragmentFromResource(Resources.GLSL_FRAGMENT_BLINN_PHONG)
                .end();

        this.view.getApp()
                .fireEditorEvent(RendererInitialized.class);

    }

    @Override
    public void uninstall() {

        if (marchingCubes != null) { marchingCubes.tearDown(); }

        if (voxelizer != null) { voxelizer.tearDown(); }

        if (mesh != null) { mesh.tearDown(); }

        if (voxelStorage != null) { voxelStorage.tearDown(); }

        this.voxelizer = null;
        this.voxelStorage = null;
        this.marchingCubes = null;
        this.mesh = null;

        this.view = null;

    }

    @Override
    public List<Property<?>> getConfigurationProperties() {
        return List.of(
                P_DEPTH, P_GRID_SCALE, P_GRID_OFFSET, P_ISO_LEVEL,
                P_LIGHT_AMBIENT, P_LIGHT_ENERGY, P_LIGHT_DIRECTION
        );
    }
}
