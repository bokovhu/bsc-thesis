package me.bokov.bsc.surfaceviewer.sdf.threed;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.sdf.ColorGPUContext;

@Data
@Accessors(chain = true)
public class ColorGPUEvaluationContext implements ColorGPUContext {

    private String normalVariable;
    private String contextId;
    private String pointVariable;

    public String getResult() {
        return contextId + "_Result";
    }

    public ColorGPUEvaluationContext branch(String suffix) {
        return new ColorGPUEvaluationContext()
                .setPointVariable(pointVariable)
                .setNormalVariable(normalVariable)
                .setContextId(contextId + "_" + suffix);
    }

    public ColorGPUEvaluationContext transform(String suffix) {
        return new ColorGPUEvaluationContext()
                .setPointVariable(contextId + "P" + suffix)
                .setNormalVariable(normalVariable)
                .setContextId(contextId + "T" + suffix);
    }

}
