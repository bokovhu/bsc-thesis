package me.bokov.bsc.surfaceviewer.scene;

import lombok.Builder;
import lombok.Getter;
import org.joml.*;

import java.util.*;

@Getter
@Builder
public class SurfaceFactoryRequest {

    private List<SceneNode> children;
    private Map<String, SceneNode> ports;
    @Builder.Default
    private Map<String, Float> floatProperties = new HashMap<>();
    @Builder.Default
    private Map<String, Vector2f> vec2Properties = new HashMap<>();
    @Builder.Default
    private Map<String, Vector3f> vec3Properties = new HashMap<>();
    @Builder.Default
    private Map<String, Vector4f> vec4Properties = new HashMap<>();
    @Builder.Default
    private Map<String, Matrix2f> mat2Properties = new HashMap<>();
    @Builder.Default
    private Map<String, Matrix3f> mat3Properties = new HashMap<>();
    @Builder.Default
    private Map<String, Matrix4f> mat4Properties = new HashMap<>();
    @Builder.Default
    private Map<String, Integer> intProperties = new HashMap<>();
    @Builder.Default
    private Map<String, Boolean> booleanProperties = new HashMap<>();
    @Builder.Default
    private Map<String, String> stringProperties = new HashMap<>();

}
