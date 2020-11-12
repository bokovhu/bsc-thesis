package me.bokov.bsc.surfaceviewer.editorv2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.bokov.bsc.surfaceviewer.run.FXEditorApp;

public class FXEditor extends Application {

    public static void main(String[] args) {
        launch(FXEditor.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {

            final EditorWindow editorWindow = new EditorWindow();
            editorWindow.getAppProperty()
                    .setValue(FXEditorApp.INSTANCE);
            Scene fxScene = new Scene(
                    editorWindow
            );
            primaryStage.setScene(fxScene);

            primaryStage.setTitle("Surface editor");
            primaryStage.setOnCloseRequest(
                    event -> FXEditorApp.INSTANCE.markShouldQuit()
            );

            primaryStage.show();

        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

}
