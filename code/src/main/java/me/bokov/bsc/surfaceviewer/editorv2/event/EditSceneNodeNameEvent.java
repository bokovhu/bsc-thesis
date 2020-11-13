package me.bokov.bsc.surfaceviewer.editorv2.event;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

public class EditSceneNodeNameEvent extends Event {

    @Getter
    private final int sceneNodeId;

    public static final EventType<EditSceneNodeNameEvent> EDIT_SCENE_NODE_NAME = new EventType<>(
            EventType.ROOT,
            "EDIT_SCENE_NODE_NAME"
    );

    public EditSceneNodeNameEvent(int sceneNodeId) {
        super(EDIT_SCENE_NODE_NAME);
        this.sceneNodeId = sceneNodeId;
    }
}
