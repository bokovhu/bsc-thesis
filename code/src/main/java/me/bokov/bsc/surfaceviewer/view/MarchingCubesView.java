package me.bokov.bsc.surfaceviewer.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import me.bokov.bsc.surfaceviewer.AppScene;
import me.bokov.bsc.surfaceviewer.SurfaceViewerPlatform;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.mesh.SDFMesh;
import me.bokov.bsc.surfaceviewer.mesh.mccpu.MarchingCubes;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.render.text.Text;
import me.bokov.bsc.surfaceviewer.render.text.TextureFont;
import me.bokov.bsc.surfaceviewer.util.Resources;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStorageLoader;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelStoragePersister;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridLoader;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridPersister;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridVoxelizer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

public class MarchingCubesView extends AppView {

    private static final Matrix4f IDENTITY = new Matrix4f().identity();
    private Voxelizer3D<UniformGrid> voxelizer;
    private UniformGrid voxelStorage;
    private VoxelStoragePersister<UniformGrid> voxelStoragePersister;
    private VoxelStorageLoader<UniformGrid> voxelStorageLoader;
    private MarchingCubes marchingCubes;
    private SDFMesh mesh;
    private ShaderProgram shaderProgram;
    private ShaderProgram fontProgram;
    private Text testText;
    private TextureFont emojis;

    public MarchingCubesView(AppScene appScene,
            SurfaceViewerPlatform platform
    ) {
        super(appScene, platform);
    }

    private void voxelizeScene() {
        this.voxelizer = new UniformGridVoxelizer(
                intOpt("grid-width", 64),
                intOpt("grid-height", 64),
                intOpt("grid-depth", 64)
        );
        this.voxelStorage = this.voxelizer.voxelize(this.appScene.sdf(), new MeshTransform(
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
        ));
    }

    private boolean loadScene() {
        String dataFilename = stringOpt("data", null);
        if (dataFilename != null) {
            if (Files.exists(new File(dataFilename).toPath())) {
                this.voxelStorageLoader = new UniformGridLoader();
                try (FileInputStream f = new FileInputStream(new File(dataFilename))) {
                    this.voxelStorage = this.voxelStorageLoader.load(f);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    private void persistScene() {

        String dataFilename = stringOpt("data", null);
        if (dataFilename != null) {
            try (FileOutputStream f = new FileOutputStream(new File(dataFilename))) {
                this.voxelStoragePersister.persist(this.voxelStorage, f);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    @Override
    public void init() {

        emojis = this.fontManager.load(
                "emoji",
                "fonts/emoji_16px.fnt",
                "fonts/emoji_32px.fnt",
                "fonts/emoji_64px.fnt"
        );

        this.fontProgram = shaderManager.load("font")
                .vertexFromResource(Resources.GLSL_VERTEX_STANDARD_2D_TRANSFORMED)
                .fragmentFromResource(Resources.GLSL_FRAGMENT_TEXT)
                .end();

        testText = new Text()
                .ofText("✔✔✔✔", emojis.getDefault());

        this.shaderProgram = shaderManager.load("default")
                .vertexFromResource(
                        stringOpt("vs-resource", Resources.GLSL_VERTEX_STANDARD_3D_TRANSFORMED))
                .fragmentFromResource(stringOpt("fs-resource", Resources.GLSL_FRAGMENT_BLINN_PHONG))
                .end();

        this.voxelStorageLoader = new UniformGridLoader();
        this.voxelStoragePersister = new UniformGridPersister();

        if (!loadScene()) {
            voxelizeScene();
        }

        persistScene();

        this.marchingCubes = new MarchingCubes(floatOpt("iso-level", 0.0f));
        this.mesh = this.marchingCubes
                .generate(voxelStorage);

    }

    @Override
    public void tearDown() {

        super.tearDown();
        marchingCubes.tearDown();
        voxelizer.tearDown();
        mesh.tearDown();

    }

    @Override
    public void render(float delta) {

        GL46.glEnable(GL46.GL_DEPTH_TEST);

        this.shaderProgram.use();

        this.shaderProgram.uniform("u_Le").vec3(appScene.lighting().Le());
        this.shaderProgram.uniform("u_La").vec3(appScene.lighting().La());
        this.shaderProgram.uniform("u_Ld").vec3(appScene.lighting().Ld());
        this.shaderProgram.uniform("u_eye").vec3(camera.eye());
        this.shaderProgram.uniform("u_M").mat4(IDENTITY);
        this.shaderProgram.uniform("u_MVP").mat4(camera.VP());

        this.mesh.draw();

        GL46.glDisable(GL46.GL_DEPTH_TEST);

        this.fontProgram.use();
        this.fontProgram.uniform("u_MVP")
                .mat4(ui.VP());

        this.testText.draw(emojis.getDefault(), fontProgram.uniform("u_fontTexture"));

    }

    @Override
    public void onSceneChanged(AppScene scene) {
        super.onSceneChanged(scene);
        this.mesh.tearDown();
        this.mesh = this.marchingCubes
                .generate(this.voxelizer.voxelize(this.appScene.sdf(), new MeshTransform(
                        new Vector3f(-3f, -3f, -3f),
                        new Quaternionf(),
                        new Vector3f(6f, 6f, 6f)
                )));
    }
}
