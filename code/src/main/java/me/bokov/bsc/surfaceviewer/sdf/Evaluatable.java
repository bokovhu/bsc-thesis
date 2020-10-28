package me.bokov.bsc.surfaceviewer.sdf;

public interface Evaluatable<TOut, TContextCPU extends CPUContext, TContextGPU extends GPUContext> {

    static <EOut, ECPU extends CPUContext, EGPU extends GPUContext> Evaluatable<EOut, ECPU, EGPU> of(
            CPUEvaluator<EOut, ECPU> cpuEvaluator,
            GPUEvaluator<EGPU> gpuEvaluator
    ) {
        return new Evaluatable<EOut, ECPU, EGPU>() {
            @Override
            public CPUEvaluator<EOut, ECPU> cpu() {
                return cpuEvaluator;
            }

            @Override
            public GPUEvaluator<EGPU> gpu() {
                return gpuEvaluator;
            }
        };
    }

    static <EOut, ECPU extends CPUContext, EGPU extends GPUContext> Evaluatable<EOut, ECPU, EGPU> of(
            Object object
    ) {
        return new Evaluatable<EOut, ECPU, EGPU>() {
            @Override
            public CPUEvaluator<EOut, ECPU> cpu() {
                if (object instanceof CPUEvaluator) {
                    return (CPUEvaluator<EOut, ECPU>) object;
                }
                throw new UnsupportedOperationException(
                        "This evaluator does not support CPU evaluation!");
            }

            @Override
            public GPUEvaluator<EGPU> gpu() {
                if (object instanceof GPUEvaluator) {
                    return (GPUEvaluator<EGPU>) object;
                }
                throw new UnsupportedOperationException(
                        "This evaluator does not support GPU evaluation!");
            }
        };
    }

    CPUEvaluator<TOut, TContextCPU> cpu();

    GPUEvaluator<TContextGPU> gpu();

}
