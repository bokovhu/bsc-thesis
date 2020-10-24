package me.bokov.bsc.surfaceviewer.sdf;

import java.util.Collections;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;

public interface GLSLDistanceExpression {

    List<GLSLStatement> evaluate(ExpressionEvaluationContext context);

}
