package me.bokov.bsc.surfaceviewer.sdf;

import java.nio.FloatBuffer;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public interface CPUEvaluator <TOut, TContext> {

    TOut evaluate(TContext context);

}
