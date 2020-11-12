package me.bokov.bsc.surfaceviewer.util;

import javafx.fxml.FXMLLoader;

public final class FXMLUtil {

    private FXMLUtil() {
        throw new UnsupportedOperationException();
    }

    public static void loadForComponent(String name, Object rootController) {

        try {

            FXMLLoader loader = new FXMLLoader(rootController.getClass().getResource(name));

            loader.setRoot(rootController);
            loader.setController(rootController);

            loader.load();

        } catch (Exception exc) {
            throw new RuntimeException("Could not load component from " + name, exc);
        }

    }

}
