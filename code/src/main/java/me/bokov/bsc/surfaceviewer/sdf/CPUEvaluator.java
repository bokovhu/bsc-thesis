package me.bokov.bsc.surfaceviewer.sdf;

public interface CPUEvaluator<TOut, TContext extends CPUContext> {

    TOut evaluate(TContext context);

}
