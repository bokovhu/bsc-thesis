package me.bokov.bsc.surfaceviewer.sdf;

public interface GPUContext {

    String getContextId();
    String getPointVariable();
    String getResult();

    GPUContext branch(String branchName);
    GPUContext transform(String transformationName);

}
