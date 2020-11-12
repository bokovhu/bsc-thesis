package me.bokov.bsc.surfaceviewer.editorv2.event;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

@Getter
public class OpenSceneNodeEditorEvent extends Event {

    private final int sceneNodeId;

    public static final EventType<OpenSceneNodeEditorEvent> OPEN_SCENE_NODE_EDITOR = new EventType<>(
            EventType.ROOT,
            "OPEN_SCENE_NODE_EDITOR"
    );

    public OpenSceneNodeEditorEvent(int sceneNodeId) {
        super(OPEN_SCENE_NODE_EDITOR);
        this.sceneNodeId = sceneNodeId;
    }
}
