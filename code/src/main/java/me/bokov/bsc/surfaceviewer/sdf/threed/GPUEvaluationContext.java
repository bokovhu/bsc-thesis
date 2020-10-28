package me.bokov.bsc.surfaceviewer.sdf.threed;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

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
                .setPointVariable(contextId + "_P_" + suffix)
                .setContextId(contextId);
    }

}
