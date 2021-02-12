package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.scene.World;
import me.bokov.bsc.surfaceviewer.surfacelang.*;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ListTokenSource;
import org.antlr.v4.runtime.Token;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.time.Duration;
import java.util.*;

public class CodeEditor extends VBox implements Initializable {

    private static final Set<String> KEYWORDS = new HashSet<>(
            List.of("object", "material", "light", "position", "rotate", "around", "at", "by", "degrees", "prefab")
    );

    @Getter
    private ObjectProperty<World> worldProperty = new SimpleObjectProperty<>();

    @Getter
    private ObjectProperty<SurfaceLangExpression> expressionProperty = new SimpleObjectProperty<>(new SurfaceLangExpression());

    @Getter
    private StringProperty sourceCodeProperty = new SimpleStringProperty("");

    @FXML
    private CodeArea codeArea;

    @FXML
    private VBox errorsVBox;

    public CodeEditor() {
        FXMLUtil.loadForComponent("/fxml/CodeEditor.fxml", this);
    }

    @FXML
    public void onSyncCode(ActionEvent event) {

        try {

            final var formatter = new SurfaceLangFormatter(worldProperty.get());
            final var code = formatter.format();

            final var expr = new SurfaceLangExpression();
            expr.parse(code);

            expressionProperty.setValue(expr);
            worldProperty.setValue(expr.getWorld());
            codeArea.replaceText(code);
            highlightCode();

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
            final var formatter = new SurfaceLangFormatter(expr.getWorld());

            worldProperty.setValue(expr.getWorld());
            expressionProperty.setValue(expr);
            codeArea.replaceText(formatter.format());
            highlightCode();

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

    private void highlightCode() {

        final String code = codeArea.getText();
        codeArea.clearStyle(0, code.length());

        try {

            final var lexer = new SurfaceLangLexer(CharStreams.fromString(code));
            final var tokens = lexer.getAllTokens();

            for (Token t : tokens) {
                final String text = t.getText();
                final int i = t.getStartIndex();
                final int j = t.getStopIndex() + 1;
                if (KEYWORDS.contains(text.toLowerCase())) {
                    codeArea.setStyle(i, j, List.of("text", "keyword"));
                }
            }

            final var parser = new SurfaceLangParser(new CommonTokenStream(new ListTokenSource(tokens)));
            parser.addParseListener(
                    new SurfaceLangBaseListener() {
                        @Override
                        public void exitNumberValue(SurfaceLangParser.NumberValueContext ctx) {
                            super.exitNumberValue(ctx);
                            codeArea.setStyle(
                                    ctx.start.getStartIndex(),
                                    ctx.stop.getStopIndex() + 1,
                                    List.of("text", "number")
                            );
                        }

                        @Override
                        public void exitVec2Value(SurfaceLangParser.Vec2ValueContext ctx) {
                            super.exitVec2Value(ctx);
                            codeArea.setStyle(
                                    ctx.start.getStartIndex() + 1,
                                    ctx.stop.getStopIndex(),
                                    List.of("text", "number")
                            );
                        }

                        @Override
                        public void exitVec3Value(SurfaceLangParser.Vec3ValueContext ctx) {
                            super.exitVec3Value(ctx);
                            codeArea.setStyle(
                                    ctx.start.getStartIndex() + 1,
                                    ctx.stop.getStopIndex(),
                                    List.of("text", "number")
                            );
                        }

                        @Override
                        public void exitVec4Value(SurfaceLangParser.Vec4ValueContext ctx) {
                            super.exitVec4Value(ctx);
                            codeArea.setStyle(
                                    ctx.start.getStartIndex() + 1,
                                    ctx.stop.getStopIndex(),
                                    List.of("text", "number")
                            );
                        }

                        @Override
                        public void exitExpressionAlias(SurfaceLangParser.ExpressionAliasContext ctx) {
                            super.exitExpressionAlias(ctx);
                            codeArea.setStyle(
                                    ctx.start.getStartIndex(),
                                    ctx.stop.getStopIndex() + 1,
                                    List.of("text", "string")
                            );
                        }

                        @Override
                        public void exitExpressionName(SurfaceLangParser.ExpressionNameContext ctx) {
                            super.exitExpressionName(ctx);
                            codeArea.setStyle(
                                    ctx.start.getStartIndex(),
                                    ctx.stop.getStopIndex() + 1,
                                    List.of("text", "type")
                            );
                        }
                    }
            );
            final var ctx = parser.world();


        } catch (Exception ignore) {
            ignore.printStackTrace();
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

        codeArea.plainTextChanges()
                .successionEnds(Duration.ofMillis(400L))
                .feedTo(v -> highlightCode());

        codeArea.getStylesheets()
                .add(getClass().getResource("/css/code-highlight.css").toExternalForm());

    }
}
