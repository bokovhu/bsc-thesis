package me.bokov.bsc.surfaceviewer.sdf;

import me.bokov.bsc.surfaceviewer.render.Texture;

public interface GPUContext {

    String getContextId();

    String getPointVariable();

    String getResult();

    GPUContext branch(String branchName);

    GPUContext transform(String transformationName);

}
