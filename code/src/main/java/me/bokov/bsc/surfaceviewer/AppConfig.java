package me.bokov.bsc.surfaceviewer;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AppConfig {

    private int width = -1;
    private int height = -1;
    private long monitor = 0L;
    private boolean fullscreen = true;
    private String sceneName = "cube-minus-sphere";
    private String viewName = "marching-cubes";
    private Map<String, String> viewOpts = new HashMap<>();

}
