package me.bokov.bsc.surfaceviewer.view.renderer;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.View;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.mesh.dccpu.GridDualContouring;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.MarchingCubes;
import me.bokov.bsc.surfaceviewer.mesh.mcgpu.GPUMarchingCubes;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.render.blinnphong.BlinnPhongShaderGenerator;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.util.IOUtil;
import me.bokov.bsc.surfaceviewer.util.ResourceUtil;
import me.bokov.bsc.surfaceviewer.util.Resources;
import me.bokov.bsc.surfaceviewer.view.Renderer;
import me.bokov.bsc.surfaceviewer.view.RendererConfig;
import me.bokov.bsc.surfaceviewer.voxelization.CPUVoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorage;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import me.bokov.bsc.surfaceviewer.voxelization.gpuugrid.GPUUniformGrid;
import me.bokov.bsc.surfaceviewer.voxelization.gpuugrid.GPUUniformGridVoxelizer;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridVoxelizer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

public class MeshRenderer implements Renderer {

    private static final Matrix4f IDENTITY = new Matrix4f().identity();

    private View view = null;

    private Voxelizer3D<UniformGrid> voxelizer;
    private Voxelizer3D<GPUUniformGrid> gpuVoxelizer;

    private UniformGrid voxelStorage;
    private GPUUniformGrid gpuVoxelStorage;

    private VoxelStorage marchingCubesInputStorage = null;

    private MarchingCubes marchingCubes;
    private GridDualContouring gridDualContouring;
    private GPUMarchingCubes gpuMarchingCubes;
    private Drawable mesh;
    private ShaderProgram shaderProgram;
    @Getter
    private Config config = new Config()
            .setGridWidth(64).setGridHeight(64).setGridDepth(64)
            .setGridOffset(new Vector3f(-2f)).setGridScale(new Vector3f(4f))
            .setIsoLevel(0.0f);

    private void createShader(World world) {

        if (this.shaderProgram != null) { this.shaderProgram.tearDown(); }

        this.shaderProgram = null;

        if (this.shaderProgram == null && world != null) {

            final var generator = new BlinnPhongShaderGenerator(world);

            this.shaderProgram = new ShaderProgram();
            this.shaderProgram.init();
            this.shaderProgram.attachVertexShaderFromSource(
                    ResourceUtil.readResource(Resources.GLSL_VERTEX_STANDARD_3D_TRANSFORMED)
            );
            this.shaderProgram
                    .attachFragmentShaderFromSource(generator.generateFragmentSource());
            this.shaderProgram.linkAndValidate();

        }

    }

    private void voxelizeSceneGPU(World world) {

        if (world != null) {
            this.gpuVoxelizer = new GPUUniformGridVoxelizer(
                    config.getGridWidth(),
                    config.getGridHeight(),
                    config.getGridDepth(),
                    !config.useGPUMeshGenerator
            );
            this.gpuVoxelStorage = this.gpuVoxelizer.voxelize(
                    world,
                    new MeshTransform(
                            config.getGridOffset(),
                            new Vector3f(0f, 1f, 0f),
                            0f,
                            Math.max(
                                    config.getGridScale().x,
                                    Math.max(
                                            config.getGridScale().y,
                                            config.getGridScale().z
                                    )
                            )
                    ), new CPUVoxelizationContext()
            );
            this.marchingCubesInputStorage = this.gpuVoxelStorage;
        }
    }

    private void voxelizeSceneCPU(World world) {

        if (world != null) {
            this.voxelizer = new UniformGridVoxelizer(
                    config.getGridWidth(),
                    config.getGridHeight(),
                    config.getGridDepth()
            );
            this.voxelStorage = this.voxelizer.voxelize(
                    world,
                    new MeshTransform(
                            config.getGridOffset(),
                            new Vector3f(0f, 1f, 0f),
                            0f,
                            Math.max(
                                    config.getGridScale().x,
                                    Math.max(
                                            config.getGridScale().y,
                                            config.getGridScale().z
                                    )
                            )
                    ), new CPUVoxelizationContext()
            );
            this.marchingCubesInputStorage = this.voxelStorage;
            if (config.dumpVoxels) {

                Iterator<Voxel> voxelIterator = this.voxelStorage.voxelIterator();

                while (voxelIterator.hasNext()) {
                    final Voxel voxel = voxelIterator.next();
                    if (voxel == null) {
                        System.out.println("NULL Voxel\n");
                    } else {
                        System.out.println("Voxel \n" +
                                "  000: " + voxel.x000() + ", " + voxel.y000() + ", " + voxel.z000() + ", " + voxel.v000() + "\n" +
                                "  001: " + voxel.x001() + ", " + voxel.y001() + ", " + voxel.z001() + ", " + voxel.v001() + "\n" +
                                "  010: " + voxel.x010() + ", " + voxel.y010() + ", " + voxel.z010() + ", " + voxel.v010() + "\n" +
                                "  011: " + voxel.x011() + ", " + voxel.y011() + ", " + voxel.z011() + ", " + voxel.v011() + "\n" +
                                "  100: " + voxel.x100() + ", " + voxel.y100() + ", " + voxel.z100() + ", " + voxel.v100() + "\n" +
                                "  101: " + voxel.x101() + ", " + voxel.y101() + ", " + voxel.z101() + ", " + voxel.v101() + "\n" +
                                "  110: " + voxel.x110() + ", " + voxel.y110() + ", " + voxel.z110() + ", " + voxel.v110() + "\n" +
                                "  111: " + voxel.x111() + ", " + voxel.y111() + ", " + voxel.z111() + ", " + voxel.v111() + "\n"
                        );
                    }
                }

            }
        }
    }

    private void voxelizeScene(World world) {

        if (this.voxelizer != null) {
            this.voxelizer.tearDown();
            this.voxelizer = null;
        }
        if (this.voxelStorage != null) {
            this.voxelStorage.tearDown();
            this.voxelStorage = null;
        }
        if (this.gpuVoxelizer != null) {
            this.gpuVoxelizer.tearDown();
            this.gpuVoxelizer = null;
        }
        if (this.gpuVoxelStorage != null) {
            this.gpuVoxelStorage.tearDown();
            this.gpuVoxelStorage = null;
        }

        if (config.useGPUVoxelization) {
            voxelizeSceneGPU(world);
        } else {
            voxelizeSceneCPU(world);
        }

    }

    private void executeMarchingCubesCPU() {

        if (this.mesh != null) {
            this.mesh.tearDown();
            this.mesh = null;
        }

        if (this.marchingCubesInputStorage != null) {

            this.marchingCubes = new MarchingCubes(config.getIsoLevel());
            this.mesh = this.marchingCubes
                    .generate(marchingCubesInputStorage);

        }

    }

    private void executeMarchingCubesGPU() {

        if (this.mesh != null) {
            this.mesh.tearDown();
            this.mesh = null;
        }

        if (this.gpuMarchingCubes != null) {
            this.gpuMarchingCubes.tearDown();
            this.gpuMarchingCubes = null;
        }

        if (this.marchingCubesInputStorage instanceof GPUUniformGrid) {

            final var storage = (GPUUniformGrid) this.marchingCubesInputStorage;
            this.gpuMarchingCubes = new GPUMarchingCubes();

            this.mesh = this.gpuMarchingCubes
                    .generate(storage);

        }

    }

    private void executeDualContouring() {

        if (this.mesh != null) {
            this.mesh.tearDown();
            this.mesh = null;
        }

        if (this.marchingCubesInputStorage != null) {

            this.gridDualContouring = new GridDualContouring();
            this.mesh = this.gridDualContouring
                    .generate(marchingCubesInputStorage);

        }

    }

    private void generateMesh() {

        if (config.useDualContouring) {
            executeDualContouring();
        } else {
            if (config.useGPUMeshGenerator) {
                executeMarchingCubesGPU();
            } else {
                executeMarchingCubesCPU();
            }
        }

    }

    @Override
    public void render(World world) {

        if (this.mesh == null) {

            this.createShader(world);
            this.voxelizeScene(world);
            this.generateMesh();

        }

        if (this.mesh != null && this.shaderProgram != null) {

            this.shaderProgram.use();

            this.shaderProgram.uniform("u_eye").vec3(this.view.getCamera().eye());
            this.shaderProgram.uniform("u_M").mat4(IDENTITY);
            this.shaderProgram.uniform("u_MVP").mat4(this.view.getCamera().VP());

            this.mesh.draw();

        }

        // this.mesh = null;

    }

    @Override
    public void install(View parent) {
        this.view = parent;

        this.view.getApp().onViewReport(
                "RendererInstalled",
                Map.of("config", IOUtil.serialize(this.getConfig()))
        );

    }

    @Override
    public void configure(RendererConfig config) {
        this.config = (MeshRenderer.Config) config;
        this.view.getApp().onViewReport(
                "RendererConfigured",
                Map.of("config", IOUtil.serialize(this.getConfig()))
        );

        this.mesh = null;
    }

    @Override
    public void uninstall() {

        if (marchingCubes != null) { marchingCubes.tearDown(); }

        if (gpuMarchingCubes != null) { gpuMarchingCubes.tearDown(); }

        if (voxelizer != null) { voxelizer.tearDown(); }

        if (mesh != null) { mesh.tearDown(); }

        if (voxelStorage != null) { voxelStorage.tearDown(); }

        if (gpuVoxelizer != null) { gpuVoxelizer.tearDown(); }

        if (gpuVoxelStorage != null) { gpuVoxelStorage.tearDown(); }

        if (gridDualContouring != null) { gridDualContouring.tearDown(); }

        this.voxelizer = null;
        this.voxelStorage = null;
        this.marchingCubes = null;
        this.gpuMarchingCubes = null;
        this.mesh = null;
        this.gpuVoxelizer = null;
        this.gpuVoxelStorage = null;
        this.marchingCubesInputStorage = null;
        this.gridDualContouring = null;

        this.view = null;

    }

    @Data
    @Accessors(chain = true)
    public static class Config implements RendererConfig {

        private Integer gridWidth, gridHeight, gridDepth;
        private Float isoLevel;
        private Vector3f gridOffset, gridScale;
        private Boolean dumpVoxels = false;
        private Boolean useGPUVoxelization = true;
        private Boolean useGPUMeshGenerator = true;
        private Boolean useDualContouring = true;

    }

}
