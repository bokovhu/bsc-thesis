package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class Everywhere implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>, Serializable {
    @Override
    public Float evaluate(CPUContext context) {
        return -1.0f;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(resultVar(context, literal(-1.0f)));
    }
}
