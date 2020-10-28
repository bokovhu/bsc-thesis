package me.bokov.bsc.surfaceviewer.voxelization;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joml.Vector3f;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Accessors(chain = true)
public class Corner<TData> implements Serializable {

    private final Vector3f point;
    @Setter
    private float value;
    private final Vector3f normal;
    @Setter
    private TData data = null;

    public Corner(Vector3f point, float value, Vector3f normal) {
        this.point = point;
        this.value = value;
        this.normal = normal;
    }
}
