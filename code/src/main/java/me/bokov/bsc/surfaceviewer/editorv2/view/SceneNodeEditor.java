package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.*;
import me.bokov.bsc.surfaceviewer.glsl.*;
import me.bokov.bsc.surfaceviewer.scene.NodeProperties;
import me.bokov.bsc.surfaceviewer.scene.NodeTemplate;
import me.bokov.bsc.surfaceviewer.scene.SceneNode;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import me.bokov.bsc.surfaceviewer.util.IOUtil;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.net.URL;
import java.util.*;

public class SceneNodeEditor extends VBox implements Initializable {

    @Getter
    private ObjectProperty<SceneNode> sceneNodeProperty = new SimpleObjectProperty<>();

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @FXML
    private VBox settingsVBox;

    @FXML
    private TextArea glslResultTextArea;

    @FXML
    private Vec3Input positionInput;

    @FXML
    private FloatInput scaleInput;

    @FXML
    private Vec3Input rotationAxisInput;

    @FXML
    private FloatInput rotationAngleInput;

    public SceneNodeEditor() {
        FXMLUtil.loadForComponent("/fxml/SceneNodeEditor.fxml", this);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        positionInput.getLabelProperty().setValue("Position");
        positionInput.getValueProperty().setValue(new Vector3f(0f, 0f, 0f));
        positionInput.getValueProperty().addListener(
                (observable, oldValue, newValue) -> onNodeTransformChanged()
        );

        scaleInput.getLabelProperty().setValue("Scale");
        scaleInput.getValueProperty().setValue(1f);
        scaleInput.getValueProperty().addListener(
                (observable, oldValue, newValue) -> onNodeTransformChanged()
        );


        rotationAxisInput.getLabelProperty().setValue("Rotation axis");
        rotationAxisInput.getValueProperty().setValue(new Vector3f(0f, 1f, 0f));
        rotationAxisInput.getValueProperty().addListener(
                (observable, oldValue, newValue) -> onNodeTransformChanged()
        );

        rotationAngleInput.getLabelProperty().setValue("Rotation angle (degrees)");
        rotationAngleInput.getValueProperty().setValue(0.0f);
        rotationAngleInput.getValueProperty().addListener(
                (observable, oldValue, newValue) -> onNodeTransformChanged()
        );

        sceneNodeProperty.addListener(
                (observable, oldValue, newValue) -> onSceneNodeChanged(newValue)
        );
        worldProperty.addListener(
                (observable, oldValue, newValue) -> onWorldChanged(newValue)
        );

    }

    private void onSceneNodeChanged(SceneNode newNode) {
        generateGLSL();
        makeSceneNodeSettings();
    }

    private void onWorldChanged(World world) {
        sceneNodeProperty.setValue(
                world.findById(
                        sceneNodeProperty.get().getId()
                ).get()
        );
        generateGLSL();
    }

    private void onNodeTransformChanged() {

        final var world = IOUtil.serialize(worldProperty.get());
        final var updatedNode = world.findById(sceneNodeProperty.get().getId()).get();

        updatedNode.localTransform()
                .applyPosition(positionInput.getValueProperty().get());
        updatedNode.localTransform()
                .applyScale(scaleInput.getValueProperty().get());
        updatedNode.localTransform()
                .applyOrientation(
                        new Quaternionf()
                        .fromAxisAngleDeg(
                                rotationAxisInput.getValueProperty().get(),
                                rotationAngleInput.getValueProperty().get()
                        )
                );

        world.roots().forEach(SceneNode::update);

        worldProperty.setValue(world);

    }

    private void onNodePropertyChanged(NodeTemplate.Property property, NodeProperties properties, Object newValue) {

        final var world = IOUtil.serialize(worldProperty.get());
        final var updatedNode = world.findById(sceneNodeProperty.get().getId()).get();

        properties.include(property, newValue);
        updatedNode.properties()
                .copyFrom(properties);

        worldProperty.setValue(world);

    }

    private Node inputForProperty(NodeTemplate.Property property, NodeProperties properties) {

        switch (property.getType()) {
            case "float":
                final var floatInput = new FloatInput();
                floatInput.getLabelProperty().setValue(property.getName());
                floatInput.getValueProperty().setValue(
                        properties.getFloat(property.getName(), ((Number) property.getDefaultValue()).floatValue())
                );
                floatInput.getValueProperty().addListener(
                        (observable, oldValue, newValue) -> onNodePropertyChanged(property, properties, newValue)
                );
                return floatInput;

            case "int":
                final var intInput = new IntInput();
                intInput.getLabelProperty().setValue(property.getName());
                intInput.getValueProperty().setValue(
                        properties.getInt(property.getName(), ((Number) property.getDefaultValue()).intValue())
                );
                intInput.getValueProperty().addListener(
                        (observable, oldValue, newValue) -> onNodePropertyChanged(property, properties, newValue)
                );
                return intInput;

            case "vec2":
                final var vec2Input = new Vec2Input();
                vec2Input.getLabelProperty().setValue(property.getName());
                vec2Input.getValueProperty().setValue(
                        properties.getVec2(property.getName(), (Vector2f) property.getDefaultValue())
                );
                vec2Input.getValueProperty().addListener(
                        (observable, oldValue, newValue) -> onNodePropertyChanged(property, properties, newValue)
                );
                return vec2Input;

            case "vec3":
                final var vec3Input = new Vec3Input();
                vec3Input.getLabelProperty().setValue(property.getName());
                vec3Input.getValueProperty().setValue(
                        properties.getVec3(property.getName(), (Vector3f) property.getDefaultValue())
                );
                vec3Input.getValueProperty().addListener(
                        (observable, oldValue, newValue) -> onNodePropertyChanged(property, properties, newValue)
                );
                return vec3Input;

            case "vec4":
                final var vec4Input = new Vec4Input();
                vec4Input.getLabelProperty().setValue(property.getName());
                vec4Input.getValueProperty().setValue(
                        properties.getVec4(property.getName(), (Vector4f) property.getDefaultValue())
                );
                vec4Input.getValueProperty().addListener(
                        (observable, oldValue, newValue) -> onNodePropertyChanged(property, properties, newValue)
                );
                return vec4Input;

            case "bool":
                final var boolInput = new BoolInput();
                boolInput.getLabelProperty().setValue(property.getName());
                boolInput.getValueProperty().setValue(
                        properties.getBool(property.getName(), Boolean.TRUE.equals(property.getDefaultValue()))
                );
                boolInput.getValueProperty().addListener(
                        (observable, oldValue, newValue) -> onNodePropertyChanged(property, properties, newValue)
                );
                return boolInput;
            default:
                return null;
        }

    }

    private void makeSceneNodeSettings() {

        settingsVBox.getChildren().clear();

        final var node = sceneNodeProperty.get();

        for (NodeTemplate.Property nodeProperty : node.getTemplate().properties) {

            var propertyInput = inputForProperty(nodeProperty, node.properties());
            if (propertyInput == null) {
                propertyInput = new Label("Property '" + nodeProperty.getName() + "' has unknown property type: " + nodeProperty
                        .getType());
            }

            settingsVBox.getChildren()
                    .add(propertyInput);

        }

    }

    private void generateGLSL() {

        final var node = sceneNodeProperty.get();

        if (node != null) {

            GLSLProgram glslProgram = new GLSLProgram();

            GLSLFunctionStatement f = new GLSLFunctionStatement(
                    "float",
                    "csgExecute",
                    List.of(
                            new GLSLFunctionStatement.GLSLFunctionParameterStatement("", "vec3", "CSG_InputPoint")
                    ),
                    new ArrayList<>()
            );

            final GLSLFunctionStatement csgExecuteFunction = new GLSLFunctionStatement(
                    "float",
                    "csgExecute",
                    List.of(new GLSLFunctionStatement.GLSLFunctionParameterStatement("", "vec3", "CSG_InputPoint")),
                    Collections.emptyList()
            );
            final GPUEvaluationContext expressionEvaluationContext = new GPUEvaluationContext()
                    .setPointVariable("CSG_InputPoint")
                    .setContextId("CSG_Root");

            csgExecuteFunction.body(
                    node.toEvaluable()
                            .gpu()
                            .evaluate(expressionEvaluationContext)
                            .toArray(new GLSLStatement[0])
            );
            csgExecuteFunction.body(
                    new GLSLReturnStatement(
                            new GLSLRawStatement(expressionEvaluationContext.getResult()))
            );

            glslProgram.add(csgExecuteFunction);

            glslResultTextArea.setText(glslProgram.render());


        }
    }

}
