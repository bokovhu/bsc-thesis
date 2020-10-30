package me.bokov.bsc.surfaceviewer.sdf;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;

import java.util.*;

public interface GPUEvaluator<TContext extends GPUContext> {

    List<GLSLStatement> evaluate(TContext context);

}
