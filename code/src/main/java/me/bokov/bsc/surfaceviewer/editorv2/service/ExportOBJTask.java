package me.bokov.bsc.surfaceviewer.editorv2.service;

import javafx.beans.property.*;
import javafx.concurrent.Task;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class ExportOBJTask extends Task<File> {

    @Getter
    private final ObjectProperty<List<Drawables.Face>> triangleListProperty = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty outputPathProperty = new SimpleStringProperty();

    @Getter
    private final BooleanProperty flipNormalsProperty = new SimpleBooleanProperty(true);

    @Override
    protected File call() throws Exception {

        File outputFile = new File(outputPathProperty.get());
        boolean fn = flipNormalsProperty.get();

        List<Vector3f> posList = new ArrayList<>();
        List<Vector3f> normList = new ArrayList<>();
        List<int[][]> faceList = new ArrayList<>();

        int idx = 1;

        for (Drawables.Face f : triangleListProperty.get()) {

            int f1i = idx++;
            int f2i = idx++;
            int f3i = idx++;

            posList.add(f.pos1);
            posList.add(f.pos2);
            posList.add(f.pos3);
            normList.add(f.normal1.mul(fn ? -1f : 1f));
            normList.add(f.normal2.mul(fn ? -1f : 1f));
            normList.add(f.normal3.mul(fn ? -1f : 1f));

            faceList.add(
                    new int[][]{
                            {f1i, f1i},
                            {f2i, f2i},
                            {f3i, f3i}
                    }
            );

        }


        try (final FileOutputStream fos = new FileOutputStream(outputFile);
             PrintStream ps = new PrintStream(fos)) {

            for (Vector3f p : posList) {
                ps.printf(Locale.ENGLISH, "v %.4f %.4f %.4f\n", p.x, p.y, p.z);
            }
            for (Vector3f n : normList) {
                ps.printf(Locale.ENGLISH, "vn %.4f %.4f %.4f\n", n.x, n.y, n.z);
            }
            for (int[][] face : faceList) {
                ps.printf(
                        Locale.ENGLISH,
                        "f %d//%d %d//%d %d//%d\n",
                        face[0][0],
                        face[0][1],
                        face[1][0],
                        face[1][1],
                        face[2][0],
                        face[2][1]
                );
            }

        }

        return outputFile;

    }
}
