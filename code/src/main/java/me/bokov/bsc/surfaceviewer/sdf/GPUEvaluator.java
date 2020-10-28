package me.bokov.bsc.surfaceviewer.sdf;

import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;

public interface GPUEvaluator<TContext extends GPUContext> {

    List<GLSLStatement> evaluate(TContext context);

}
