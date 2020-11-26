package me.bokov.bsc.surfaceviewer.editorv2.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import lombok.Data;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.render.Drawables;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExportGLTFTask extends Task<File> {

    @Getter
    private final ObjectProperty<List<Drawables.Face>> triangleListProperty = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty outputPathProperty = new SimpleStringProperty();

    @Getter
    private final BooleanProperty flipNormalsProperty = new SimpleBooleanProperty(true);

    @Override
    protected File call() throws Exception {

        File outputFile = new File(outputPathProperty.get());
        File outputBinFile = new File(outputPathProperty.get() + ".bin");
        File outputIboBinFile = new File(outputPathProperty.get() + ".ibin");
        List<Drawables.Face> faces = triangleListProperty.get();

        int byteLength = faces.size() * 3 * (3 + 3 + 4) * Float.BYTES;
        int indexByteLength = faces.size() * 3 * Integer.BYTES;
        ByteBuffer vboDataBuffer = BufferUtils.createByteBuffer(byteLength);
        ByteBuffer iboDataBuffer = BufferUtils.createByteBuffer(indexByteLength);

        int i = 0;

        boolean fn = flipNormalsProperty.get();

        for (Drawables.Face f : faces) {

            float[] faceData = new float[]{
                    f.pos1.x, f.pos1.y, f.pos1.z,
                    (fn ? -1.0f : 1.0f) * f.normal1.x, (fn ? -1.0f : 1.0f) * f.normal1.y, (fn ? -1.0f : 1.0f) * f.normal1.z,
                    f.color1.x, f.color1.y, f.color1.z, f.color1.w,

                    f.pos2.x, f.pos2.y, f.pos2.z,
                    (fn ? -1.0f : 1.0f) * f.normal2.x, (fn ? -1.0f : 1.0f) * f.normal2.y, (fn ? -1.0f : 1.0f) * f.normal2.z,
                    f.color2.x, f.color2.y, f.color2.z, f.color2.w,

                    f.pos3.x, f.pos3.y, f.pos3.z,
                    (fn ? -1.0f : 1.0f) * f.normal3.x, (fn ? -1.0f : 1.0f) * f.normal3.y, (fn ? -1.0f : 1.0f) * f.normal3.z,
                    f.color3.x, f.color3.y, f.color3.z, f.color3.w,
            };

            for (float fl : faceData) { vboDataBuffer.putFloat(fl); }

            iboDataBuffer.putInt(i++).putInt(i++).putInt(i++);

        }

        vboDataBuffer.flip();
        iboDataBuffer.flip();

        try (final FileOutputStream fos = new FileOutputStream(outputBinFile)) {
            fos.getChannel()
                    .write(vboDataBuffer);

        }

        try (final FileOutputStream fos = new FileOutputStream(outputIboBinFile)) {
            fos.getChannel()
                    .write(iboDataBuffer);
        }

        GLTF gltf = new GLTF();

        final var buffer = new GLTFBuffer();
        buffer.setByteLength(byteLength);
        buffer.setUri(outputBinFile.getName());


        final var iboBuffer = new GLTFBuffer();
        iboBuffer.setByteLength(indexByteLength);
        iboBuffer.setUri(outputIboBinFile.getName());


        gltf.getBuffers().add(buffer);
        gltf.getBuffers().add(iboBuffer);

        final var positionView = new GLTFBufferView();
        positionView.setBuffer(0);
        positionView.setByteLength(byteLength);
        positionView.setByteOffset(0);
        positionView.setByteStride((3 + 3 + 4) * Float.BYTES);
        positionView.setTarget(GL46.GL_ARRAY_BUFFER);

        final var normalView = new GLTFBufferView();
        normalView.setBuffer(0);
        normalView.setByteLength(byteLength - 3 * Float.BYTES);
        normalView.setByteOffset(3 * Float.BYTES);
        normalView.setByteStride((3 + 3 + 4) * Float.BYTES);
        normalView.setTarget(GL46.GL_ARRAY_BUFFER);

        final var colorView = new GLTFBufferView();
        colorView.setBuffer(0);
        colorView.setByteLength(byteLength - (3 + 3) * Float.BYTES);
        colorView.setByteOffset((3 + 3) * Float.BYTES);
        colorView.setByteStride((3 + 3 + 4) * Float.BYTES);
        colorView.setTarget(GL46.GL_ARRAY_BUFFER);


        final var indexView = new GLTFBufferView();
        indexView.setBuffer(1);
        indexView.setByteLength(indexByteLength);
        indexView.setByteOffset(0);
        indexView.setByteStride(null);
        indexView.setTarget(GL46.GL_ELEMENT_ARRAY_BUFFER);


        gltf.getBufferViews().add(positionView);
        gltf.getBufferViews().add(normalView);
        gltf.getBufferViews().add(colorView);
        gltf.getBufferViews().add(indexView);


        final var positionAccessor = new GLTFAccessor();
        positionAccessor.setBufferView(0);
        positionAccessor.setByteOffset(0);
        positionAccessor.setComponentType(GL46.GL_FLOAT);
        positionAccessor.setCount(3 * faces.size());
        positionAccessor.setType("VEC3");
        positionAccessor.getMin().add(-10000.0);
        positionAccessor.getMin().add(-10000.0);
        positionAccessor.getMin().add(-10000.0);
        positionAccessor.getMax().add(10000.0);
        positionAccessor.getMax().add(10000.0);
        positionAccessor.getMax().add(10000.0);


        final var normalAccessor = new GLTFAccessor();
        normalAccessor.setBufferView(1);
        normalAccessor.setByteOffset(0);
        normalAccessor.setComponentType(GL46.GL_FLOAT);
        normalAccessor.setCount(3 * faces.size());
        normalAccessor.setType("VEC3");
        normalAccessor.getMin().add(-1.0f);
        normalAccessor.getMin().add(-1.0f);
        normalAccessor.getMin().add(-1.0f);
        normalAccessor.getMax().add(1.0f);
        normalAccessor.getMax().add(1.0f);
        normalAccessor.getMax().add(1.0f);


        final var colorAccessor = new GLTFAccessor();
        colorAccessor.setBufferView(2);
        colorAccessor.setByteOffset(0);
        colorAccessor.setComponentType(GL46.GL_FLOAT);
        colorAccessor.setCount(3 * faces.size());
        colorAccessor.setType("VEC4");
        colorAccessor.getMin().add(0.0f);
        colorAccessor.getMin().add(0.0f);
        colorAccessor.getMin().add(0.0f);
        colorAccessor.getMin().add(0.0f);
        colorAccessor.getMax().add(1.0f);
        colorAccessor.getMax().add(1.0f);
        colorAccessor.getMax().add(1.0f);
        colorAccessor.getMax().add(1.0f);


        final var indexAccessor = new GLTFAccessor();
        indexAccessor.setBufferView(3);
        indexAccessor.setByteOffset(0);
        indexAccessor.setComponentType(GL46.GL_UNSIGNED_INT);
        indexAccessor.setCount(3 * faces.size());
        indexAccessor.setType("SCALAR");
        indexAccessor.getMin().add(0);
        indexAccessor.getMax().add(3 * faces.size() - 1);

        gltf.getAccessors().add(positionAccessor);
        gltf.getAccessors().add(normalAccessor);
        gltf.getAccessors().add(colorAccessor);
        gltf.getAccessors().add(indexAccessor);


        final var scene = new GLTFScene();
        scene.getNodes().add(0);

        gltf.getScenes().add(scene);


        final var node = new GLTFNode();
        node.setMesh(0);
        gltf.getNodes().add(node);


        final var mesh = new GLTFMesh();
        mesh.setName("Exported");
        final var primitive = new GLTFPrimitive();
        primitive.getAttributes().put("POSITION", 0);
        primitive.getAttributes().put("NORMAL", 1);
        primitive.getAttributes().put("COLOR_0", 2);
        primitive.setIndices(3);
        primitive.setMode(GL46.GL_TRIANGLES);

        mesh.getPrimitives().add(primitive);
        gltf.getMeshes().add(mesh);


        String gltfJson = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(gltf);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(gltfJson.getBytes(StandardCharsets.UTF_8));
        }

        return outputFile;
    }

    @Data
    public static class GLTF implements Serializable {
        private GLTFAsset asset = new GLTFAsset();
        private int scene = 0;
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<GLTFBuffer> buffers = new ArrayList<>();
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<GLTFScene> scenes = new ArrayList<>();
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<GLTFNode> nodes = new ArrayList<>();
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<GLTFMesh> meshes = new ArrayList<>();
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<GLTFAccessor> accessors = new ArrayList<>();
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<GLTFBufferView> bufferViews = new ArrayList<>();
    }

    @Data
    public static class GLTFAsset implements Serializable {
        private String generator = "me.bokov.bsc";
        private String version = "2.0";
    }

    @Data
    public static class GLTFScene implements Serializable {
        private List<Integer> nodes = new ArrayList<>();
    }

    @Data
    public static class GLTFNode implements Serializable {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<Integer> children = new ArrayList<>();
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<Number> matrix = new ArrayList<>();
        private Integer mesh;
    }

    @Data
    public static class GLTFMesh implements Serializable {
        private List<GLTFPrimitive> primitives = new ArrayList<>();
        private String name;
    }

    @Data
    public static class GLTFPrimitive implements Serializable {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private Map<String, Integer> attributes = new HashMap<>();
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        private Integer indices;
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        private Integer mode;
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        private Integer material;
    }

    @Data
    public static class GLTFAccessor implements Serializable {
        private int bufferView;
        private int byteOffset;
        private int componentType;
        private int count;
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<Number> min = new ArrayList<>();
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        private List<Number> max = new ArrayList<>();
        private String type = "SCALAR";
    }

    @Data
    public static class GLTFBufferView implements Serializable {
        private int buffer;
        private int byteOffset;
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        private Integer byteLength;
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        private Integer byteStride;
        private int target;
    }

    @Data
    public static class GLTFBuffer implements Serializable {
        private String uri;
        private int byteLength;
    }

}
