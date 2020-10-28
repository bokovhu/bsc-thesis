package me.bokov.bsc.surfaceviewer.voxelization;

import java.io.Serializable;
import org.joml.Vector3f;

public final class Voxel implements Serializable {

    private final Corner c000;
    private final Corner c001;
    private final Corner c010;
    private final Corner c011;
    private final Corner c100;
    private final Corner c101;
    private final Corner c110;
    private final Corner c111;

    private final Vector3f p1;
    private final Vector3f p2;

    public Voxel(Corner c000, Corner c001, Corner c010, Corner c011,
            Corner c100,
            Corner c101,
            Corner c110,
            Corner c111,
            Vector3f p1,
            Vector3f p2
    ) {
        this.c000 = c000;
        this.c001 = c001;
        this.c010 = c010;
        this.c011 = c011;
        this.c100 = c100;
        this.c101 = c101;
        this.c110 = c110;
        this.c111 = c111;
        this.p1 = new Vector3f(p1);
        this.p2 = new Vector3f(p2);
    }

    public Corner getC000() {
        return c000;
    }

    public Corner getC001() {
        return c001;
    }

    public Corner getC010() {
        return c010;
    }

    public Corner getC011() {
        return c011;
    }

    public Corner getC100() {
        return c100;
    }

    public Corner getC101() {
        return c101;
    }

    public Corner getC110() {
        return c110;
    }

    public Corner getC111() {
        return c111;
    }

    public Vector3f getP1() {
        return p1;
    }

    public Vector3f getP2() {
        return p2;
    }

}
