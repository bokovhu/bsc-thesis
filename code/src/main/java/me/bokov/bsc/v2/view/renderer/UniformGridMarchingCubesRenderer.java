package me.bokov.bsc.v2.view.renderer;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.mesh.SDFMesh;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.MarchingCubes;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.util.Resources;
import me.bokov.bsc.surfaceviewer.voxelization.CPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridVoxelizer;
import me.bokov.bsc.v2.Property;
import me.bokov.bsc.v2.Scene;
import me.bokov.bsc.v2.View;
import me.bokov.bsc.v2.editor.event.RendererInitialized;
import me.bokov.bsc.v2.view.Renderer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.v2.PropertyType.*;

public class UniformGridMarchingCubesRenderer implements Renderer {

    private static final Matrix4f IDENTITY = new Matrix4f().identity();

    private static final String P_GROUP = "UniformGridMarchingCubes";

    private static final Property<Integer> P_GRID_WIDTH = new Property<>(INT, P_GROUP, "GridWidth", 64);
    private static final Property<Integer> P_GRID_HEIGHT = new Property<>(INT, P_GROUP, "GridHeight", 64);
    private static final Property<Integer> P_GRID_DEPTH = new Property<>(INT, P_GROUP, "GridDepth", 64);

    private static final Property<Vector3f> P_GRID_OFFSET = new Property<>(
            VEC3,
            P_GROUP,
            "GridOffset",
            new Vector3f(0f)
    );
    private static final Property<Vector3f> P_GRID_SCALE = new Property<>(VEC3, P_GROUP, "GridScale", new Vector3f(1f));

    private static final Property<Float> P_ISO_LEVEL = new Property<>(FLOAT, P_GROUP, "IsoLevel", 0.0f);

    private static final Property<Vector3f> P_LIGHT_ENERGY = new Property<>(
            VEC3,
            P_GROUP,
            "LightEnergy",
            new Vector3f(1.8f, 2.4f, 2.3f)
    );
    private static final Property<Vector3f> P_LIGHT_AMBIENT = new Property<>(
            VEC3,
            P_GROUP,
            "LightAmbient",
            new Vector3f(0.03f, 0.0193f, 0.02f)
    );
    private static final Property<Vector3f> P_LIGHT_DIRECTION = new Property<>(
            VEC3,
            P_GROUP,
            "LightDirection",
            new Vector3f(-1.5f, 2.5f, 1.6f).normalize()
    );

    private View view = null;

    private Voxelizer3D<UniformGrid> voxelizer;
    private UniformGrid voxelStorage;
    private MarchingCubes marchingCubes;
    private SDFMesh mesh;
    private ShaderProgram shaderProgram;

    private void voxelizeScene(Scene scene) {
        if (scene.getMeshes() != null && scene.getMeshes().size() >= 1) {

            this.voxelizer = new UniformGridVoxelizer(
                    this.view.get(P_GRID_WIDTH),
                    this.view.get(P_GRID_HEIGHT),
                    this.view.get(P_GRID_DEPTH)
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

    @Override
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
        return Arrays.asList(
                P_GRID_WIDTH, P_GRID_HEIGHT, P_GRID_DEPTH,
                P_GRID_OFFSET, P_GRID_SCALE, P_ISO_LEVEL,
                P_LIGHT_ENERGY, P_LIGHT_DIRECTION, P_LIGHT_AMBIENT
        );
    }
}
