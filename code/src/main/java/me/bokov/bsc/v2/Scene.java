package me.bokov.bsc.v2;

import lombok.Getter;
import lombok.Setter;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.Evaluetables;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;

import java.io.*;
import java.util.*;
import java.util.stream.*;

@Getter
@Setter
public class Scene implements Serializable {

    private String name = "New scene";
    private String fileSystemPath = new File(System.getProperty("user.dir"), "New scene.bin").getAbsolutePath();
    private List<SceneMesh> meshes = new ArrayList<>();
    private List<SceneLight> lights = new ArrayList<>();

    public static Scene cloneScene(Scene scene) {

        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(scene);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);

            return (Scene) ois.readObject();

        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }

    }

    public Evaluatable<Float, CPUContext, GPUContext> toUnion() {

        return Evaluetables.union(meshes.stream().map(m -> m.getSurface().toEvaluatable())
                .collect(Collectors.toList()));

    }

}
