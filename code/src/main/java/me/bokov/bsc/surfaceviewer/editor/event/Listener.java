package me.bokov.bsc.surfaceviewer.editor.event;

public interface Listener<T extends Event> {

    void onEvent(T event);

}
