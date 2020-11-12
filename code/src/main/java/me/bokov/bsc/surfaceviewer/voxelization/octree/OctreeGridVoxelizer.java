package me.bokov.bsc.surfaceviewer.voxelization.octree;

import me.bokov.bsc.surfaceviewer.mesh.MeshTransform;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.Evaluable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.util.MetricsLogger;
import me.bokov.bsc.surfaceviewer.voxelization.Corner;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import me.bokov.bsc.surfaceviewer.voxelization.VoxelizationContext;
import me.bokov.bsc.surfaceviewer.voxelization.Voxelizer3D;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.sdf.threed.CPUEvaluationContext.*;

public class OctreeGridVoxelizer implements Voxelizer3D<OctreeGrid> {

    private static final float EPSILON = 0.002f;
    private final int maxDepth;
    private final Vector3f dxPlus = new Vector3f();
    private final Vector3f dxMinus = new Vector3f();
    private final Vector3f dyPlus = new Vector3f();
    private final Vector3f dyMinus = new Vector3f();
    private final Vector3f dzPlus = new Vector3f();
    private final Vector3f dzMinus = new Vector3f();

    private final Vector3f tmp000 = new Vector3f();
    private final Vector3f tmp001 = new Vector3f();
    private final Vector3f tmp010 = new Vector3f();
    private final Vector3f tmp011 = new Vector3f();
    private final Vector3f tmp100 = new Vector3f();
    private final Vector3f tmp101 = new Vector3f();
    private final Vector3f tmp110 = new Vector3f();
    private final Vector3f tmp111 = new Vector3f();

    private int totalDivisions = 0;
    private int totalJoins = 0;

    public OctreeGridVoxelizer(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private Vector3f normal(CPUEvaluator<Float, CPUContext> generator, Vector3f p) {
        return new Vector3f(
                generator.evaluate(of(dxPlus.set(p).add(EPSILON, 0f, 0f))) - generator
                        .evaluate(of(dxMinus.set(p).add(-EPSILON, 0f, 0f))),
                generator.evaluate(of(dyPlus.set(p).add(0f, EPSILON, 0f))) - generator
                        .evaluate(of(dyMinus.set(p).add(0f, -EPSILON, 0f))),
                generator.evaluate(of(dzPlus.set(p).add(0f, 0f, EPSILON))) - generator
                        .evaluate(of(dzMinus.set(p).add(0f, 0f, -EPSILON)))
        ).normalize();
    }

    private Voxel voxelForNode(OctreeNode node, Evaluable<Float, CPUContext, GPUContext> generator) {

        tmp000.set(node.getP000().x, node.getP000().y, node.getP000().z);
        tmp001.set(node.getP000().x, node.getP000().y, node.getP111().z);
        tmp010.set(node.getP000().x, node.getP111().y, node.getP000().z);
        tmp011.set(node.getP000().x, node.getP111().y, node.getP111().z);

        tmp100.set(node.getP111().x, node.getP000().y, node.getP000().z);
        tmp101.set(node.getP111().x, node.getP000().y, node.getP111().z);
        tmp110.set(node.getP111().x, node.getP111().y, node.getP000().z);
        tmp111.set(node.getP111().x, node.getP111().y, node.getP111().z);

        return new Voxel(
                new Corner(tmp000, generator.cpu().evaluate(of(tmp000)), normal(generator.cpu(), tmp000)),
                new Corner(tmp001, generator.cpu().evaluate(of(tmp001)), normal(generator.cpu(), tmp001)),
                new Corner(tmp010, generator.cpu().evaluate(of(tmp010)), normal(generator.cpu(), tmp010)),
                new Corner(tmp011, generator.cpu().evaluate(of(tmp011)), normal(generator.cpu(), tmp011)),
                new Corner(tmp100, generator.cpu().evaluate(of(tmp100)), normal(generator.cpu(), tmp100)),
                new Corner(tmp101, generator.cpu().evaluate(of(tmp101)), normal(generator.cpu(), tmp101)),
                new Corner(tmp110, generator.cpu().evaluate(of(tmp110)), normal(generator.cpu(), tmp110)),
                new Corner(tmp111, generator.cpu().evaluate(of(tmp111)), normal(generator.cpu(), tmp111)),
                node.getP000(), node.getP111()
        );

    }

    private boolean voxelHasEdgeTransition(Voxel v) {
        return Math.signum(v.getC000().getValue()) != Math.signum(v.getC100().getValue())
                || Math.signum(v.getC100().getValue()) != Math.signum(v.getC101().getValue())
                || Math.signum(v.getC101().getValue()) != Math.signum(v.getC001().getValue())
                || Math.signum(v.getC001().getValue()) != Math.signum(v.getC000().getValue())
                || Math.signum(v.getC010().getValue()) != Math.signum(v.getC110().getValue())
                || Math.signum(v.getC110().getValue()) != Math.signum(v.getC111().getValue())
                || Math.signum(v.getC111().getValue()) != Math.signum(v.getC011().getValue())
                || Math.signum(v.getC011().getValue()) != Math.signum(v.getC010().getValue())
                || Math.signum(v.getC000().getValue()) != Math.signum(v.getC010().getValue())
                || Math.signum(v.getC100().getValue()) != Math.signum(v.getC110().getValue())
                || Math.signum(v.getC101().getValue()) != Math.signum(v.getC111().getValue())
                || Math.signum(v.getC001().getValue()) != Math.signum(v.getC011().getValue());
    }

    private void processNode(int depth, OctreeNode node, Evaluable<Float, CPUContext, GPUContext> generator) {

        if (depth == maxDepth) {
            node.setLeaf(true);
            node.setN000(null);
            node.setN001(null);
            node.setN010(null);
            node.setN011(null);
            node.setN100(null);
            node.setN101(null);
            node.setN110(null);
            node.setN111(null);

            node.setVoxel(voxelForNode(node, generator));
        } else {

            if (depth < maxDepth) {

                node.divide();
                ++totalDivisions;

                processNode(depth + 1, node.getN000(), generator);
                processNode(depth + 1, node.getN001(), generator);
                processNode(depth + 1, node.getN010(), generator);
                processNode(depth + 1, node.getN011(), generator);

                processNode(depth + 1, node.getN100(), generator);
                processNode(depth + 1, node.getN101(), generator);
                processNode(depth + 1, node.getN110(), generator);
                processNode(depth + 1, node.getN111(), generator);

            }

            if (node.getN000().isLeaf() && node.getN001().isLeaf() && node.getN010().isLeaf() && node.getN011().isLeaf()
                    && node.getN100().isLeaf() && node.getN101().isLeaf() && node.getN110().isLeaf() && node.getN111()
                    .isLeaf()) {

                boolean joinNode = !voxelHasEdgeTransition(node.getN000().getVoxel())
                        && !voxelHasEdgeTransition(node.getN001().getVoxel())
                        && !voxelHasEdgeTransition(node.getN010().getVoxel())
                        && !voxelHasEdgeTransition(node.getN011().getVoxel())
                        && !voxelHasEdgeTransition(node.getN100().getVoxel())
                        && !voxelHasEdgeTransition(node.getN101().getVoxel())
                        && !voxelHasEdgeTransition(node.getN110().getVoxel())
                        && !voxelHasEdgeTransition(node.getN111().getVoxel());

                if (joinNode) {
                    node.join();
                    ++totalJoins;
                }

            }

        }

    }

    @Override
    public OctreeGrid voxelize(
            Evaluable<Float, CPUContext, GPUContext> generator,
            MeshTransform transform,
            VoxelizationContext context
    ) {

        long start = System.currentTimeMillis();

        totalDivisions = 0;
        totalJoins = 0;

        OctreeGrid grid = new OctreeGrid(this.maxDepth);
        final OctreeNode root = grid.root().setP000(new Vector3f(0f, 0f, 0f))
                .setP111(new Vector3f(1f, 1f, 1f));

        grid.applyTransform(transform);

        root.getP000().set(grid.localToGlobal(root.getP000()));
        root.getP111().set(grid.localToGlobal(root.getP111()));

        processNode(1, root, generator);

        long processEnd = System.currentTimeMillis();

        final int nodeCount = root.count();

        long countEnd = System.currentTimeMillis();

        MetricsLogger.logMetrics(
                "Octree Voxelization",
                Map.of(
                        "Runtime", (countEnd - start) + " ms",
                        "Process runtime", (processEnd - start) + " ms",
                        "Count runtime", (countEnd - processEnd) + " ms",
                        "Node count", nodeCount,
                        "Total division count", totalDivisions,
                        "Total join count", totalJoins
                )
        );

        return grid;

    }
}
