package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.AppScene;
import me.bokov.bsc.surfaceviewer.SurfaceViewerPlatform;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.mesh.SDFMesh;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.MarchingCubes;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.util.Resources;
import me.bokov.bsc.surfaceviewer.voxelization.CPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import me.bokov.bsc.surfaceviewer.voxelization.octree.OctreeGrid;
import me.bokov.bsc.surfaceviewer.voxelization.octree.OctreeGridVoxelizer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

@Deprecated
public class OctreeMarchingCubesView extends AppView {

    private static final Matrix4f IDENTITY = new Matrix4f().identity();
    private Voxelizer3D<OctreeGrid> voxelizer;
    private OctreeGrid voxelStorage;
    private MarchingCubes marchingCubes;
    private SDFMesh mesh;
    private ShaderProgram shaderProgram;
    private ShaderProgram fontProgram;

    public OctreeMarchingCubesView(
            AppScene appScene,
            SurfaceViewerPlatform platform
    ) {
        super(appScene, platform);
    }

    private void voxelizeScene() {
        this.voxelizer = new OctreeGridVoxelizer(
                intOpt("octree-depth", 8)
        );
        this.voxelStorage = this.voxelizer.voxelize(
                this.appScene.sdf(),
                new MeshTransform(
                        new Vector3f(
                                floatOpt("grid-offset-x", 0f),
                                floatOpt("grid-offset-y", 0f),
                                floatOpt("grid-offset-z", 0f)
                        ),
                        new Quaternionf(),
                        new Vector3f(
                                floatOpt("grid-scale-x", 1f),
                                floatOpt("grid-scale-y", 1f),
                                floatOpt("grid-scale-z", 1f)
                        )
                ), new CPUVoxelizationContext()
        );
    }

    private void executeMarchingCubes() {

        this.marchingCubes = new MarchingCubes(floatOpt("iso-level", 0.0f));
        this.mesh = this.marchingCubes
                .generate(voxelStorage);

    }

    @Override
    public void init() {

        this.fontProgram = shaderManager.load("font")
                .vertexFromResource(Resources.GLSL_VERTEX_STANDARD_2D_TRANSFORMED)
                .fragmentFromResource(Resources.GLSL_FRAGMENT_TEXT)
                .end();

        this.shaderProgram = shaderManager.load("default")
                .vertexFromResource(
                        stringOpt("vs-resource", Resources.GLSL_VERTEX_STANDARD_3D_TRANSFORMED))
                .fragmentFromResource(stringOpt("fs-resource", Resources.GLSL_FRAGMENT_BLINN_PHONG))
                .end();

        voxelizeScene();

        this.marchingCubes = new MarchingCubes(floatOpt("iso-level", 0.0f));
        this.mesh = this.marchingCubes
                .generate(voxelStorage);

    }

    @Override
    protected void render(float delta) {

        GL46.glEnable(GL46.GL_DEPTH_TEST);

        this.shaderProgram.use();

        this.shaderProgram.uniform("u_Le").vec3(appScene.lighting().Le());
        this.shaderProgram.uniform("u_La").vec3(appScene.lighting().La());
        this.shaderProgram.uniform("u_Ld").vec3(appScene.lighting().Ld());
        this.shaderProgram.uniform("u_eye").vec3(camera.eye());
        this.shaderProgram.uniform("u_M").mat4(IDENTITY);
        this.shaderProgram.uniform("u_MVP").mat4(camera.VP());

        this.mesh.draw();

    }

    @Override
    public void onSceneChanged(AppScene scene) {
        super.onSceneChanged(scene);
        this.mesh.tearDown();
        this.voxelizer.tearDown();
        this.voxelStorage.tearDown();
        voxelizeScene();
        executeMarchingCubes();
    }
}
