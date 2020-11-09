package me.bokov.bsc.surfaceviewer.event;

public interface Listener<T extends Event> {

    void onEvent(T event);

}
