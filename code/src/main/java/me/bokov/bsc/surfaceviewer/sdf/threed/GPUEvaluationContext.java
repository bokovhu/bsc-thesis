package me.bokov.bsc.surfaceviewer.sdf.threed;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.render.Texture;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

import java.io.Serializable;
import java.util.*;

@Data
@Accessors(chain = true)
public class GPUEvaluationContext implements GPUContext, Serializable {

    private String contextId;
    private String pointVariable;
    private Map<String, Texture> textureMap = new HashMap<>();
    private Map<String, String> textureUniformMap = new HashMap<>();

    public String getResult() {
        return contextId + "_Result";
    }

    @Override
    public Texture getTexture(String name) {
        return textureMap.get(name);
    }

    @Override
    public String getTextureUniformName(String textureName) {
        return textureUniformMap.get(textureName);
    }

    public GPUEvaluationContext branch(String suffix) {
        return new GPUEvaluationContext()
                .setPointVariable(pointVariable)
                .setContextId(contextId + "_" + suffix)
                .setTextureMap(new HashMap<>(textureMap));
    }

    public GPUEvaluationContext transform(String suffix) {
        return new GPUEvaluationContext()
                .setPointVariable(contextId + "P" + suffix)
                .setContextId(contextId + "T" + suffix)
                .setTextureMap(new HashMap<>(textureMap));
    }

    @Override
    public GPUContext withTexture(String name, Texture texture) {
        final Map<String, Texture> newMap = new HashMap<>(textureMap);
        newMap.put(name, texture);
        return new GPUEvaluationContext()
                .setPointVariable(pointVariable)
                .setContextId(contextId)
                .setTextureMap(newMap);
    }

    public GPUEvaluationContext withTextureUniform(String name, String u) {
        this.textureUniformMap.put(name, u);
        return this;
    }

}
