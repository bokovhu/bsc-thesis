package me.bokov.bsc.surfaceviewer.sdf.threed;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import org.joml.Vector3f;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class CPUEvaluationContext implements CPUContext, Serializable {

    private Vector3f point = new Vector3f(0f);

    @Deprecated
    public static CPUEvaluationContext of(Vector3f point) {
        return new CPUEvaluationContext()
                .setPoint(point);
    }

    public CPUContext transform(Vector3f p) {
        return new CPUEvaluationContext()
                .setPoint(p);
    }

}
