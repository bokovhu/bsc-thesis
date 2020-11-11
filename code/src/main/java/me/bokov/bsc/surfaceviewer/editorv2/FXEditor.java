package me.bokov.bsc.surfaceviewer.editorv2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import me.bokov.bsc.surfaceviewer.FXEditorApp;

public class FXEditor extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene fxScene = new Scene(
                new EditorWindow()
        );
        primaryStage.setScene(fxScene);

        primaryStage.setTitle("Surface editor");
        primaryStage.setOnCloseRequest(
                event -> FXEditorApp.INSTANCE.markShouldQuit()
        );

        primaryStage.show();

    }

    public static void main(String [] args) {
        launch(FXEditor.class, args);
    }

}
