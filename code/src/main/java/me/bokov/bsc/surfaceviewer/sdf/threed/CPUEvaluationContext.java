package me.bokov.bsc.surfaceviewer.sdf.threed;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.render.TextureView;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

@Data
@Accessors(chain = true)
public class CPUEvaluationContext implements CPUContext, Serializable {

    private Vector3f point = new Vector3f(0f);

    private Map<String, TextureView> textureViewMap = new HashMap<>();

    @Override
    public TextureView getTexture(String name) {
        return textureViewMap.get(name);
    }

    public CPUContext transform(Vector3f p) {
        return new CPUEvaluationContext()
                .setPoint(p)
                .setTextureViewMap(new HashMap<>(textureViewMap));
    }

    @Override
    public CPUContext withTexture(String name, TextureView view) {
        final Map<String, TextureView> newMap = new HashMap<>(textureViewMap);
        newMap.put(name, view);
        return new CPUEvaluationContext()
                .setPoint(point)
                .setTextureViewMap(newMap);
    }

    @Deprecated
    public static CPUEvaluationContext of(Vector3f point) {
        return new CPUEvaluationContext()
                .setPoint(point)
                .setTextureViewMap(new HashMap<>());
    }

}
