package me.bokov.bsc.surfaceviewer.editorv2.view.laf;

import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

public interface Borders {

    Border ERROR_BORDER = new Border(
            new BorderStroke(
                    Paint.valueOf("#ec9090"),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(4.0),
                    new BorderWidths(1.0)
            )
    );

}
