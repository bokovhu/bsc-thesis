package me.bokov.bsc.surfaceviewer.editorv2;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.logging.*;

@Log
public class EditorWindow extends AnchorPane {

    public EditorWindow() {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/EditorRoot.fxml")
        );
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not initialize EditorWindow!", e);
        }

    }

}
