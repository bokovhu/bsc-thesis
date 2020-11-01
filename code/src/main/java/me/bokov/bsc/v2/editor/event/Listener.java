package me.bokov.bsc.v2.editor.event;

public interface Listener <T extends Event> {

    void onEvent(T event);

}
