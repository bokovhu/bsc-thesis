package me.bokov.bsc.surfaceviewer.voxelization.octree;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.voxelization.Voxel;
import org.joml.Vector3f;

import java.io.Serializable;

@Setter
@Getter
@Accessors(chain = true)
public class OctreeNode implements Serializable {

    private boolean leaf;
    private Voxel voxel;

    private Vector3f p000;
    private Vector3f p111;

    private OctreeNode n000;
    private OctreeNode n001;
    private OctreeNode n010;
    private OctreeNode n011;

    private OctreeNode n100;
    private OctreeNode n101;
    private OctreeNode n110;
    private OctreeNode n111;

    public boolean containsPoint(Vector3f point) {
        return p000.x <= point.x && p000.y < point.y && p000.z <= point.z
                && p111.x >= point.x && p111.y >= point.y && p111.z >= point.z;
    }

    public OctreeNode findNode(Vector3f point) {

        if (!containsPoint(point)) { return null; }

        if (leaf) { return this; }

        OctreeNode found = null;

        found = (found == null && n000 != null) ? n000.findNode(point) : n000;
        found = (found == null && n001 != null) ? n001.findNode(point) : n001;
        found = (found == null && n010 != null) ? n010.findNode(point) : n010;
        found = (found == null && n011 != null) ? n011.findNode(point) : n011;

        found = (found == null && n100 != null) ? n100.findNode(point) : n100;
        found = (found == null && n101 != null) ? n101.findNode(point) : n101;
        found = (found == null && n110 != null) ? n110.findNode(point) : n110;
        found = (found == null && n111 != null) ? n111.findNode(point) : n111;

        return found;

    }

    public void divide() {

        setLeaf(false);

        Vector3f h000 = new Vector3f(p111).sub(p000).mul(0.5f);

        n000 = new OctreeNode()
                .setLeaf(true).setP000(new Vector3f(p000)).setP111(new Vector3f(p000).add(h000));
        n001 = new OctreeNode()
                .setLeaf(true)
                .setP000(new Vector3f(p000).add(0f, 0f, h000.z))
                .setP111(new Vector3f(p111).sub(h000.x, h000.y, 0f));

        n010 = new OctreeNode()
                .setLeaf(true)
                .setP000(new Vector3f(p000).add(0f, h000.y, 0f))
                .setP111(new Vector3f(p111).sub(h000.x, 0f, h000.z));
        n011 = new OctreeNode()
                .setLeaf(true)
                .setP000(new Vector3f(p000).add(0f, h000.y, h000.z))
                .setP111(new Vector3f(p111).sub(h000.x, 0f, 0f));

        n100 = new OctreeNode()
                .setLeaf(true)
                .setP000(new Vector3f(p000).add(h000.x, 0f, 0f))
                .setP111(new Vector3f(p111).sub(0f, h000.y, h000.z));
        n101 = new OctreeNode()
                .setLeaf(true)
                .setP000(new Vector3f(p000).add(h000.x, 0f, h000.z))
                .setP111(new Vector3f(p111).sub(0f, h000.y, 0f));

        n110 = new OctreeNode()
                .setLeaf(true)
                .setP000(new Vector3f(p000).add(h000.x, h000.y, 0f))
                .setP111(new Vector3f(p111).sub(0f, 0f, h000.z));
        n111 = new OctreeNode()
                .setLeaf(true)
                .setP000(new Vector3f(p000).add(h000.x, h000.y, h000.z))
                .setP111(new Vector3f(p111).sub(0f, 0f, 0f));

    }

    public void join() {

        this.voxel = new Voxel(
                n000.getVoxel().getC000(),
                n001.getVoxel().getC001(),
                n010.getVoxel().getC010(),
                n011.getVoxel().getC011(),
                n100.getVoxel().getC100(),
                n101.getVoxel().getC101(),
                n110.getVoxel().getC110(),
                n111.getVoxel().getC111(),
                getP000(), getP111()
        );
        this.leaf = true;

        this.n000 = null;
        this.n001 = null;
        this.n010 = null;
        this.n011 = null;
        this.n100 = null;
        this.n101 = null;
        this.n110 = null;
        this.n111 = null;

    }

    public int count() {

        if (this.leaf) { return 1; }

        return 1 + this.n000.count() + this.n001.count() + this.n010.count() + this.n011.count()
                + this.n100.count() + this.n101.count() + this.n110.count() + this.n111.count();

    }

}
