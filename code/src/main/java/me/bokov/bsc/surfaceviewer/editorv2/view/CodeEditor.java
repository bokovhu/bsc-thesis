package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.surfacelang.SurfaceLangExpression;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import java.net.URL;
import java.util.*;

public class CodeEditor extends VBox implements Initializable {

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @Getter
    private ObjectProperty<SurfaceLangExpression> expressionProperty = new SimpleObjectProperty<>(new SurfaceLangExpression());

    @FXML
    private TextArea codeArea;

    @FXML
    private VBox errorsVBox;

    public CodeEditor() {
        FXMLUtil.loadForComponent("/fxml/CodeEditor.fxml", this);
    }

    @FXML
    public void onSyncCode(ActionEvent event) {

        try {

            final var expr = new SurfaceLangExpression();
            expr.format(worldProperty.get());

            expressionProperty.setValue(expr);
            codeArea.setText(expressionProperty.get().getCode());

            errorsVBox.getChildren().clear();

        } catch (Exception exc) {

            errorsVBox.getChildren().clear();
            errorsVBox.getChildren()
                    .add(
                            new Label("Error: " + exc.getMessage())
                    );
            exc.printStackTrace();

        }

    }

    @FXML
    public void onCompileCode(ActionEvent event) {

        try {

            final var expr = new SurfaceLangExpression();
            expr.parse(codeArea.getText());

            worldProperty.setValue(expr.getWorld());
            expressionProperty.setValue(expr);

            errorsVBox.getChildren().clear();

        } catch (Exception exc) {

            errorsVBox.getChildren().clear();
            errorsVBox.getChildren()
                    .add(
                            new Label("Error: " + exc.getMessage())
                    );
            exc.printStackTrace();

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        codeArea.addEventFilter(
                KeyEvent.KEY_PRESSED,
                (KeyEvent event) -> {
                    if (event.getCode() == KeyCode.TAB) {
                        codeArea.insertText(
                                codeArea.getCaretPosition(),
                                "    "
                        );
                        event.consume();
                    }

                    if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {

                        onCompileCode(new ActionEvent(this, event.getTarget()));
                        event.consume();

                    }
                }
        );

    }
}
