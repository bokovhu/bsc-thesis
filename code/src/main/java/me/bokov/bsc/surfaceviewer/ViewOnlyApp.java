package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.event.Event;

public class ViewOnlyApp extends AppBase {

    @Override
    public void run() {

        getView().run();

    }

    @Override
    public <T extends Event> void fire(T event) {
        // Do nothing
    }

    @Override
    public <T extends Event> void fire(Class<T> eventClass) {
        // Do nothing
    }
}
