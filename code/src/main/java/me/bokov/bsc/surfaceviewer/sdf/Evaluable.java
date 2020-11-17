package me.bokov.bsc.surfaceviewer.sdf;

import java.io.Serializable;

public interface Evaluable<TOut, TContextCPU extends CPUContext, TContextGPU extends GPUContext> extends Serializable {

    static <EOut, ECPU extends CPUContext, EGPU extends GPUContext> Evaluable<EOut, ECPU, EGPU> of(
            CPUEvaluator<EOut, ECPU> cpuEvaluator,
            GPUEvaluator<EGPU> gpuEvaluator
    ) {
        return new Evaluable<EOut, ECPU, EGPU>() {
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

    static <EOut, ECPU extends CPUContext, EGPU extends GPUContext> Evaluable<EOut, ECPU, EGPU> of(
            Object object
    ) {
        return new EvaluableWrapper<>(object);
    }

    CPUEvaluator<TOut, TContextCPU> cpu();

    GPUEvaluator<TContextGPU> gpu();

    class EvaluableWrapper<T1, T2 extends CPUContext, T3 extends GPUContext> implements Serializable, Evaluable<T1, T2, T3> {

        private final Object wrapped;

        public EvaluableWrapper(Object wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public CPUEvaluator<T1, T2> cpu() {
            if (wrapped instanceof CPUEvaluator) {
                return (CPUEvaluator<T1, T2>) wrapped;
            }
            throw new UnsupportedOperationException(
                    "This evaluator does not support CPU evaluation!");
        }

        @Override
        public GPUEvaluator<T3> gpu() {
            if (wrapped instanceof GPUEvaluator) {
                return (GPUEvaluator<T3>) wrapped;
            }
            throw new UnsupportedOperationException(
                    "This evaluator does not support GPU evaluation!");
        }
    }

}
