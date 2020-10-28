package me.bokov.bsc.surfaceviewer.sdf;

public interface CPUEvaluator<TOut, TContext> {

    TOut evaluate(TContext context);

}
