package me.bokov.bsc.surfaceviewer.editorv2.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.Getter;
import me.bokov.bsc.surfaceviewer.App;
import me.bokov.bsc.surfaceviewer.editorv2.service.RenderSceneTask;
import me.bokov.bsc.surfaceviewer.editorv2.view.input.IntInput;
import me.bokov.bsc.surfaceviewer.util.FXMLUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.*;

public class RenderDialog extends VBox implements Initializable {

    @FXML
    private IntInput renderWidthInput;

    @FXML
    private IntInput renderHeightInput;

    @FXML
    private Button saveButton;

    @FXML
    private ImageView renderPreviewImage;

    @Getter
    private ObjectProperty<App> appProperty = new SimpleObjectProperty<>();

    public RenderDialog() {
        FXMLUtil.loadForComponent("/fxml/RenderDialogContent.fxml", this);
    }

    private void onRenderDone(BufferedImage img) {

        renderPreviewImage.imageProperty()
                .setValue(SwingFXUtils.toFXImage(img, null));

    }

    @FXML
    public void onRender(ActionEvent event) {

        renderWidthInput.collectValue();
        renderHeightInput.collectValue();


        final var task = new RenderSceneTask();

        task.getAppProperty().bind(appProperty);
        task.getRenderWidthProperty().setValue(renderWidthInput.getValueProperty().get());
        task.getRenderHeightProperty().setValue(renderHeightInput.getValueProperty().get());

        task.setOnSucceeded(
                successEvent -> onRenderDone(task.getValue())
        );

        task.run();

    }

    @FXML
    public void onSave(ActionEvent event) {

        final var image = renderPreviewImage.imageProperty()
                .get();
        final var bufferedImage = SwingFXUtils.fromFXImage(image, null);

        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setTitle("Save rendered image");

        final File imageFile = fileChooser.showSaveDialog(getScene().getWindow());

        try {

            ImageIO.write(
                    bufferedImage,
                    fileChooser.getSelectedExtensionFilter()
                            .getDescription(),
                    imageFile
            );

            final var msg = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Saved image to " + imageFile.getAbsolutePath()
            );
            msg.showAndWait();

        } catch (Exception exc) {
            final var msg = new Alert(Alert.AlertType.ERROR, "Could not save the image: " + exc.getMessage());
            msg.showAndWait();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        saveButton.disableProperty()
                .set(true);

        renderPreviewImage.imageProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue == null) {
                                saveButton.disableProperty()
                                        .set(true);
                            } else {
                                saveButton.disableProperty()
                                        .set(false);
                            }
                        }
                );


        renderWidthInput.getLabelProperty().setValue("Image width");
        renderWidthInput.getValueProperty().set(1920);


        renderHeightInput.getLabelProperty().setValue("Image height");
        renderHeightInput.getValueProperty().set(1080);

    }
}
