package me.bokov.bsc.surfaceviewer.surfacelang;

import me.bokov.bsc.surfaceviewer.scene.*;
import me.bokov.bsc.surfaceviewer.scene.materializer.ConstantMaterial;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public class SurfaceLangFormatter {

    private static final Quaternionf Q_IDENT = new Quaternionf().identity();

    private static final String INDENT = "    ";
    private final World world;
    private final List<String> lines = new ArrayList<>();
    private final List<Integer> indentations = new ArrayList<>();
    private final Deque<String> blocks = new ArrayDeque<>();
    private final Deque<String> parentheses = new ArrayDeque<>();
    private int indentation = 0;

    private StringBuilder currentLine = new StringBuilder();

    public SurfaceLangFormatter(World world) {
        this.world = world;
    }

    private void reset() {

        lines.clear();
        indentations.clear();
        indentation = 0;

    }

    private void lineBreak(int increaseIndent) {

        lines.add(currentLine.toString());
        indentations.add(indentation);
        currentLine = new StringBuilder();

        indentation += increaseIndent;
        if (indentation < 0) {
            indentation = 0;
        }

    }

    private void step(int increaseIndent) {

        indentation += increaseIndent;
        if (indentation < 0) {
            indentation = 0;
        }

    }

    private void startBlock(String symbol) {

        append(symbol);
        blocks.addLast(symbol);
        lineBreak(1);

    }

    private void startParen(String symbol) {
        append(symbol);
        parentheses.addLast(symbol);
    }

    private void append(String text) {

        currentLine.append(text);

    }

    private void endBlock() {

        if (blocks.isEmpty()) {
            throw new IllegalStateException("No block to end!");
        }

        final var block = blocks.removeLast();

        String eblock = block;

        switch (block) {
            case "{":
                eblock = "}";
                break;
            case "[":
                eblock = "]";
                break;
            case "(":
                eblock = ")";
                break;
            case "<":
                eblock = ">";
                break;
        }

        if (!currentLine.toString().strip().isBlank()) {
            lineBreak(-1);
        } else {
            step(-1);
        }
        append(eblock);
        lineBreak(0);

    }

    private void endParen() {

        if (parentheses.isEmpty()) {
            throw new IllegalStateException("No parentheses to end!");
        }

        final var paren = parentheses.removeLast();

        String eparen = ")";

        switch (paren) {
            case "{":
                eparen = "}";
                break;
            case "]":
                eparen = "]";
                break;
            case "<":
                eparen = ">";
                break;
        }

        append(eparen);

    }

    private void space() {
        currentLine.append(" ");
    }

    private String joinLines() {

        StringBuilder sb = new StringBuilder();

        if (lines.size() != indentations.size()) {
            throw new IllegalStateException("Number of indentations does not equal number of lines.");
        }

        for (int i = 0; i < lines.size(); i++) {

            int indent = indentations.get(i);
            String content = lines.get(i).strip();

            for (int j = 0; j < indent; j++) {
                sb.append(INDENT);
            }
            sb.append(content);
            sb.append("\n");

        }

        return sb.toString().strip();

    }

    private void appendFloat(float val) {
        String formatted = String.format(Locale.ENGLISH, "%.4f", val);
        if (formatted.endsWith(".0000")) {
            formatted = formatted.substring(0, formatted.length() - ".0000".length());
        } else if (formatted.endsWith("000")) {
            formatted = formatted.substring(0, formatted.length() - "000".length());
        } else if (formatted.endsWith("00")) {
            formatted = formatted.substring(0, formatted.length() - "00".length());
        } else if (formatted.endsWith("0")) { formatted = formatted.substring(0, formatted.length() - "0".length()); }
        append(formatted);
    }

    private void appendVec2(Vector2f val) {
        startParen("(");
        appendFloat(val.x);
        append(", ");
        appendFloat(val.y);
        endParen();
    }

    private void appendVec2(float x, float y) {
        appendVec2(new Vector2f(x, y));
    }

    private void appendVec3(Vector3f val) {
        startParen("(");
        appendFloat(val.x);
        append(", ");
        appendFloat(val.y);
        append(", ");
        appendFloat(val.z);
        endParen();
    }

    private void appendVec3(float x, float y, float z) {
        appendVec3(new Vector3f(x, y, z));
    }

    private void appendVec4(Vector4f val) {
        startParen("(");
        appendFloat(val.x);
        append(", ");
        appendFloat(val.y);
        append(", ");
        appendFloat(val.z);
        append(", ");
        appendFloat(val.w);
        endParen();
    }

    private void appendVec4(float x, float y, float z, float w) {
        appendVec4(new Vector4f(x, y, z, w));
    }

    private void appendMat2(Matrix2f m) {
        startParen("(");
        lineBreak(1);
        appendVec2(m.m00, m.m10);
        append(", ");
        appendVec2(m.m01, m.m11);
        lineBreak(-1);
        endParen();
    }

    private void appendMat3(Matrix3f m) {
        startParen("(");
        lineBreak(1);
        appendVec3(m.m00, m.m10, m.m20);
        append(",");
        lineBreak(0);
        appendVec3(m.m01, m.m11, m.m21);
        append(",");
        lineBreak(0);
        appendVec3(m.m02, m.m12, m.m22);
        lineBreak(-1);
        endParen();
    }

    private void appendMat4(Matrix4f m) {
        startParen("(");
        lineBreak(1);
        appendVec4(m.m00(), m.m10(), m.m20(), m.m30());
        append(",");
        lineBreak(0);
        appendVec4(m.m01(), m.m11(), m.m21(), m.m31());
        append(",");
        lineBreak(0);
        appendVec4(m.m02(), m.m12(), m.m22(), m.m32());
        append(",");
        lineBreak(0);
        appendVec4(m.m03(), m.m13(), m.m23(), m.m33());
        lineBreak(-1);
        endParen();
    }

    private void appendInt(int v) {
        append(String.format("%d", v));
    }

    private void appendBool(boolean v) {
        append(v ? "true" : "false");
    }

    private void formatNodeTransform(SceneNode node) {

        final var T = node.localTransform();
        if (T.position().lengthSquared() > 0.001f
                || Math.abs(T.rotationAngle()) > 0.001f
                || Math.abs(T.scale() - 1.0f) > 0.001f) {

            append(" AT ");

            if (T.position().lengthSquared() > 0.001f) {

                append(" POSITION ");
                appendVec3(T.position());

            }

            if (!T.orientation().equals(Q_IDENT)) {

                append(" ROTATE AROUND ");
                appendVec3(T.rotationAxis());
                append(" BY ");
                appendFloat(T.rotationAngle());
                append(" DEGREES");

            }

            if (Math.abs(T.scale() - 1.0f) > 0.001f) {

                append(" SCALE ");
                appendFloat(T.scale());

            }

        }

    }

    private void formatNodeParams(SceneNode node) {

        if (!node.properties().getIncludedProperties().isEmpty()) {

            startParen("(");

            List<NodeTemplate.Property> propertyList = new ArrayList<>(node.properties().getIncludedProperties());

            if (propertyList.size() > 1) {
                lineBreak(1);
            }

            for (int i = 0; i < propertyList.size(); i++) {

                final var prop = propertyList.get(i);

                append(prop.getName());
                append(": ");

                switch (prop.getType()) {
                    case "float":
                        appendFloat(((Number) node.properties().getValue(prop)).floatValue());
                        break;
                    case "int":
                        appendInt(((Number) node.properties().getValue(prop)).intValue());
                        break;
                    case "vec2":
                        appendVec2((Vector2f) node.properties().getValue(prop));
                        break;
                    case "vec3":
                        appendVec3((Vector3f) node.properties().getValue(prop));
                        break;
                    case "vec4":
                        appendVec4((Vector4f) node.properties().getValue(prop));
                        break;
                    case "mat2":
                        appendMat2((Matrix2f) node.properties().getValue(prop));
                        break;
                    case "mat3":
                        appendMat3((Matrix3f) node.properties().getValue(prop));
                        break;
                    case "mat4":
                        appendMat4((Matrix4f) node.properties().getValue(prop));
                        break;
                    case "bool":
                        appendBool(Boolean.TRUE.equals(node.properties().getValue(prop)));
                        break;
                    default:
                        throw new UnsupportedOperationException("Property type not supported: " + prop.getType());
                }

                if (i != propertyList.size() - 1) {
                    append(",");
                    lineBreak(0);
                }

            }

            if (propertyList.size() > 1) {
                lineBreak(-1);
            }

            endParen();

        }

    }

    private void formatNodePorts(SceneNode node) {

        if (!node.pluggedPorts().isEmpty()) {

            startBlock("{");

            List<Map.Entry<String, SceneNode>> portEntries = new ArrayList<>(node.pluggedPorts().entrySet());

            if (portEntries.size() == 1 && node.getTemplate().ports.size() == 1) {

                final var entry = portEntries.get(0);
                formatNode(entry.getValue());

            } else {

                for (int i = 0; i < portEntries.size(); i++) {

                    final var entry = portEntries.get(i);

                    append(entry.getKey());
                    append(": ");
                    formatNode(entry.getValue());

                    if (i != portEntries.size() - 1) {
                        append(",");
                        lineBreak(0);
                    }

                }

            }

            endBlock();

        }

    }

    private void formatNodeChildren(SceneNode node) {

        if (node.pluggedPorts().isEmpty() && !node.children().isEmpty()) {

            startBlock("[");

            List<SceneNode> children = new ArrayList<>(node.children());

            int listValueIndent = indentation;

            for (int i = 0; i < children.size(); i++) {

                final var child = children.get(i);

                formatNode(child);

                if (i != children.size() - 1) {
                    append(",");
                    lineBreak(0);
                }

                indentation = listValueIndent;

            }

            endBlock();

        }

    }

    private void formatNode(SceneNode node) {

        if(node.getPrefab() != null) {
            append(node.getPrefab().getName());
        } else {
            append(node.getTemplate().name());
        }
        space();

        if (!"".equals(node.getDisplay().getName().strip())
                && !"unnamed node".equalsIgnoreCase(node.getDisplay().getName().strip())) {
            append("\"");
            append(node.getDisplay().getName());
            append("\"");
            space();
        }

        formatNodeTransform(node);
        formatNodeParams(node);
        formatNodePorts(node);
        formatNodeChildren(node);

    }

    private void formatLight(LightSource lightSource) {

        if (lightSource instanceof AmbientLight) {

            AmbientLight ambientLight = (AmbientLight) lightSource;

            append("light ambient ");
            startBlock("{");

            append("energy: ");
            appendVec3(ambientLight.getEnergy());

            endBlock();

        } else if (lightSource instanceof DirectionalLight) {

            DirectionalLight directionalLight = (DirectionalLight) lightSource;

            append("light directional ");
            startBlock("{");

            append("energy: ");
            appendVec3(directionalLight.getEnergy());
            append(",");
            lineBreak(0);
            append("direction: ");
            appendVec3(directionalLight.getDirection());

            endBlock();

        } else {
            throw new UnsupportedOperationException("Unsupported light type: " + lightSource.getClass().getName());
        }

    }

    private void formatMaterial(Materializer materializer) {

        if (materializer instanceof ConstantMaterial) {

            ConstantMaterial constantMaterial = (ConstantMaterial) materializer;

            append("material constant ");

            startBlock("{");

            // FIXME
            append("boundary: ");
            formatNode(materializer.getBoundary());
            append(",");
            lineBreak(0);
            append("diffuse: ");
            appendVec3(constantMaterial.diffuse());
            append(",");
            lineBreak(0);
            append("shininess: ");
            appendFloat(constantMaterial.shininess());

            endBlock();

        } else {
            throw new UnsupportedOperationException("Unsupported material type: " + materializer.getClass().getName());
        }

    }

    private void formatPrefab(Prefab prefab) {

        append("prefab ");
        append(prefab.getName());
        startBlock("{");
        formatNode(prefab.getNode());
        endBlock();

    }

    public String format() {

        reset();

        this.world.getLightSources().forEach(l -> {
            this.formatLight(l);
            lineBreak(0);
        });
        this.world.getMaterializers().forEach(m -> {
            this.formatMaterial(m);
            lineBreak(0);
        });
        this.world.getPrefabs().forEach(p -> {
            this.formatPrefab(p);
            lineBreak(0);
        });
        this.world.roots().forEach(n -> {
            this.formatNode(n);
            lineBreak(0);
        });

        lineBreak(0);

        return joinLines();
    }

}
