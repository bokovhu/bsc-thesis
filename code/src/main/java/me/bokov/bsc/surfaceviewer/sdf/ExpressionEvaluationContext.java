package me.bokov.bsc.surfaceviewer.sdf;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;

@Data
@Accessors(chain = true)
public class ExpressionEvaluationContext {

    private String contextId;
    private String pointVariable;
    private GLSLStatement parentStatement;

    public String resultVariable() {
        return contextId + "_Result";
    }

}
