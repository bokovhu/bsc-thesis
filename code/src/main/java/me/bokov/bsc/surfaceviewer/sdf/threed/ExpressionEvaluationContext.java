package me.bokov.bsc.surfaceviewer.sdf.threed;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

@Data
@Accessors(chain = true)
public class ExpressionEvaluationContext implements GPUContext {

    private String contextId;
    private String pointVariable;

    public String getResult() {
        return contextId + "_Result";
    }

    public ExpressionEvaluationContext branch(String suffix) {
        return new ExpressionEvaluationContext()
                .setPointVariable(pointVariable)
                .setContextId(contextId + "_" + suffix);
    }

    public ExpressionEvaluationContext transform(String suffix) {
        return new ExpressionEvaluationContext()
                .setPointVariable(contextId + "_P_" + suffix)
                .setContextId(contextId);
    }

}
