package me.bokov.bsc.surfaceviewer.sdf.threed;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.render.Texture;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

import java.io.Serializable;
import java.util.*;

@Data
@Accessors(chain = true)
public class GPUEvaluationContext implements GPUContext, Serializable {

    private String contextId;
    private String pointVariable;

    public String getResult() {
        return contextId + "_Result";
    }

    public GPUEvaluationContext branch(String suffix) {
        return new GPUEvaluationContext()
                .setPointVariable(pointVariable)
                .setContextId(contextId + "_" + suffix);
    }

    public GPUEvaluationContext transform(String suffix) {
        return new GPUEvaluationContext()
                .setPointVariable(contextId + "P" + suffix)
                .setContextId(contextId + "T" + suffix);
    }

}
