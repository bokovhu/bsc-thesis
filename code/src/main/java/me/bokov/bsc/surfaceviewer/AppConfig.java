package me.bokov.bsc.surfaceviewer;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

@Data
@Accessors(chain = true)
public class AppConfig {

    private int width = 1280;
    private int height = 720;
    private long monitor = 0L;
    private boolean fullscreen = false;
    private String sceneName = "cube-minus-sphere";
    private String viewName = "marching-cubes";
    private Map<String, String> viewOpts = new HashMap<>();

}
