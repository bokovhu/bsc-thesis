package me.bokov.bsc.surfaceviewer.voxelization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joml.Vector3f;

import java.io.Serializable;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Accessors(chain = true)
public class Corner<TData> implements Serializable {

    private final Vector3f point;
    private final Vector3f normal;
    @Setter
    private float value;
    @Setter
    private TData data = null;

    public Corner(Vector3f point, float value, Vector3f normal) {
        this.point = new Vector3f(point);
        this.value = value;
        this.normal = new Vector3f(normal);
    }
}
