package me.bokov.bsc.surfaceviewer.editorv2.event;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

@Getter
public class OpenEditorTabEvent extends Event {

    private final int targetId;

    public static final EventType<OpenEditorTabEvent> OPEN_SCENE_NODE_EDITOR = new EventType<>(
            EventType.ROOT,
            "OPEN_SCENE_NODE_EDITOR"
    );
    public static final EventType<OpenEditorTabEvent> OPEN_LIGHT_EDITOR = new EventType<>(
            EventType.ROOT,
            "OPEN_LIGHT_EDITOR"
    );

    public OpenEditorTabEvent(int targetId) {
        super(OPEN_SCENE_NODE_EDITOR);
        this.targetId = targetId;
    }

    public OpenEditorTabEvent(EventType<OpenEditorTabEvent> type, int id) {
        super(type);
        this.targetId = id;
    }

}
