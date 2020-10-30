package me.bokov.bsc.surfaceviewer.sdf;

import me.bokov.bsc.surfaceviewer.render.Texture;

public interface GPUContext {

    String getContextId();

    String getPointVariable();

    String getResult();

    Texture getTexture(String name);

    String getTextureUniformName(String textureName);

    GPUContext branch(String branchName);

    GPUContext transform(String transformationName);

    GPUContext withTexture(String name, Texture texture);

}
