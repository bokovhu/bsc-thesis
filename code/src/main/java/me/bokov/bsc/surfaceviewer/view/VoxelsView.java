package me.bokov.bsc.surfaceviewer.view;

import me.bokov.bsc.surfaceviewer.AppScene;
import me.bokov.bsc.surfaceviewer.SurfaceViewerPlatform;
import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.render.Drawable;
import me.bokov.bsc.surfaceviewer.render.PointCloud;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.util.Resources;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGrid;
import me.bokov.bsc.surfaceviewer.voxelization.naiveugrid.UniformGridVoxelizer;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

public class VoxelsView extends AppView {

    private Voxelizer3D<UniformGrid> voxelizer;
    private UniformGrid grid;
    private ShaderProgram shaderProgram;
    private Drawable pointCloud;

    public VoxelsView(
            AppScene appScene,
            SurfaceViewerPlatform platform
    ) {
        super(appScene, platform);
    }

    @Override
    public void init() {

        this.voxelizer = new UniformGridVoxelizer(
                intOpt("grid-width", 64),
                intOpt("grid-height", 64),
                intOpt("grid-depth", 64)
        );
        this.grid = this.voxelizer.voxelize(this.appScene.sdf(), new MeshTransform(
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

        this.pointCloud = PointCloud.voxelCloud(this.grid, floatOpt("threshold", 0.0f));

        this.shaderProgram = shaderManager.load("default")
                .vertexFromResource(Resources.GLSL_VERTEX_DIRECT_TRANSFORMED)
                .fragmentFromResource(Resources.GLSL_FRAGMENT_VOXEL_POINT_CLOUD)
                .end();

        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_DST_ALPHA);
        GL46.glDepthMask(true);
        GL46.glDepthFunc(GL46.GL_LEQUAL);
        GL46.glClearDepthf(1f);
        GL46.glDepthRange(0.0f, 1.0f);
        GL46.glPointSize(3f);

    }

    @Override
    public void tearDown() {

        super.tearDown();
        voxelizer.tearDown();
        grid.tearDown();
        pointCloud.tearDown();

    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void render(float delta) {

        this.shaderProgram.use();
        this.shaderProgram.uniform("u_MVP")
                .mat4(this.camera.VP());
        this.pointCloud.draw();

    }
}
