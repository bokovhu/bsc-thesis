package me.bokov.bsc.surfaceviewer.render;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.lwjgl.opengl.GL46;

public class Drawable {

    private int vboHandle;
    private int vaoHandle;
    private int primitiveType = GL46.GL_TRIANGLES;
    private int vertexCount = 0;
    private List<Attribute> vertexAttributes = new ArrayList<>();

    public Drawable() {
    }

    public static Drawable standard2D() {
        return new Drawable()
                .attrib(
                        0, 2, GL46.GL_FLOAT,
                        false
                ) // layout(location = 0) in vec2 a_vertexPosition;
                .attrib(
                        1, 2, GL46.GL_FLOAT,
                        false
                ) // layout(location = 1) in vec2 a_vertexTexCoords;
                .attrib(2, 4, GL46.GL_FLOAT, false); // layout(location = 2) in vec4 a_vertexColor;
    }

    public static Drawable standard3D() {
        return new Drawable()
                .attrib(
                        0, 3, GL46.GL_FLOAT,
                        false
                ) // layout(location = 0) in vec3 a_vertexPosition;
                .attrib(1, 3, GL46.GL_FLOAT, false) // layout(location = 1) in vec3 a_vertexNormal;
                .attrib(2, 4, GL46.GL_FLOAT, false); // layout(location = 2) in vec4 a_vertexColor;
    }

    public static Drawable direct() {
        return new Drawable()
                .attrib(0, 4, GL46.GL_FLOAT, false) // layout(location = 0) in vec4 a_NDC;
                .attrib(1, 2, GL46.GL_FLOAT, false) // layout(location = 1) in vec2 a_texCoords;
                .attrib(2, 4, GL46.GL_FLOAT, false); // layout(location = 2) in vec4 a_color;
    }

    public Drawable attrib(int location, int size, int type, boolean normalized) {

        this.vertexAttributes.add(
                new Attribute(location, size, type, Attribute.sizeOfGlType(type), normalized)
        );
        return this;

    }

    private void createVAO() {

        this.vaoHandle = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(this.vaoHandle);

    }

    private void createVBO() {

        this.vboHandle = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.vboHandle);

    }

    private void setupVAO() {

        final int vertexSizeBytes = this.vertexAttributes
                .stream()
                .mapToInt(a -> a.size * a.elementSize)
                .sum();

        List<Attribute> attributesInLocationOrder = this.vertexAttributes.stream()
                .sorted(Comparator.comparingInt(a -> a.location))
                .collect(Collectors.toList());

        long offset = 0;

        for(int i = 0; i < attributesInLocationOrder.size(); i++) {

            final Attribute attr = attributesInLocationOrder.get(i);

            GL46.glEnableVertexAttribArray(attr.location);
            GL46.glVertexAttribPointer(
                    attr.location,
                    attr.size,
                    attr.type,
                    attr.normalized,
                    vertexSizeBytes,
                    offset
            );

            offset += attr.size * attr.elementSize;

        }

    }

    public void init() {

        createVAO();
        createVBO();
        setupVAO();

    }

    public void upload(FloatBuffer vertexData, int primitiveType, int vertexCount) {

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.vboHandle);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, vertexData, GL46.GL_STATIC_DRAW);

        this.primitiveType = primitiveType;
        this.vertexCount = vertexCount;

    }

    public void draw() {

        GL46.glBindVertexArray(this.vaoHandle);
        GL46.glDrawArrays(this.primitiveType, 0, this.vertexCount);

    }

    public int vertexElementCount() {
        return this.vertexAttributes.stream()
                .mapToInt(a -> a.size)
                .sum();
    }

    public void tearDown() {

        GL46.glBindVertexArray(0);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);

        GL46.glDeleteVertexArrays(vaoHandle);
        GL46.glDeleteBuffers(vboHandle);

    }

    private static final class Attribute {

        private final int location;
        private final int size;
        private final int type;
        private final int elementSize;
        private final boolean normalized;

        private Attribute(int location, int size, int type, int elementSize, boolean normalized) {
            this.location = location;
            this.size = size;
            this.type = type;
            this.elementSize = elementSize;
            this.normalized = normalized;
        }

        private static int sizeOfGlType(int glType) {
            switch (glType) {
                case GL46.GL_UNSIGNED_BYTE:
                case GL46.GL_BYTE:
                    return 1;
                case GL46.GL_UNSIGNED_SHORT:
                case GL46.GL_SHORT:
                    return Short.BYTES;
                case GL46.GL_UNSIGNED_INT:
                case GL46.GL_INT:
                    return Integer.BYTES;
                case GL46.GL_FLOAT:
                    return Float.BYTES;
                case GL46.GL_DOUBLE:
                    return Double.BYTES;
                default:
                    return 4;
            }
        }

    }

}