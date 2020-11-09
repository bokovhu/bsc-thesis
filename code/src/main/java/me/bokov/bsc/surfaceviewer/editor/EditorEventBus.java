package me.bokov.bsc.surfaceviewer.editor;

import me.bokov.bsc.surfaceviewer.Editor;
import me.bokov.bsc.surfaceviewer.Installable;
import me.bokov.bsc.surfaceviewer.event.Event;
import me.bokov.bsc.surfaceviewer.event.Listener;

import java.util.*;

public final class EditorEventBus implements Installable<Editor> {

    private Map<Class<? extends Event>, List<SubscribedListener<?>>> subscribedListeners = new HashMap<>();

    public <T extends Event> EditorEventBus subscribe(Class<T> event, Listener<T> listener) {
        subscribedListeners.computeIfAbsent(
                event,
                key -> new ArrayList<>()
        ).add(
                new SubscribedListener<>(false, listener)
        );
        return this;
    }

    public <T extends Event> EditorEventBus subscribeOnce(Class<T> event, Listener<T> listener) {
        subscribedListeners.computeIfAbsent(
                event,
                key -> new ArrayList<>()
        ).add(
                new SubscribedListener<>(true, listener)
        );
        return this;
    }

    public <T extends Event> EditorEventBus unsubscribe(Class<T> event, Listener<T> listener) {
        subscribedListeners.computeIfAbsent(
                event,
                key -> new ArrayList<>()
        ).removeIf(sl -> sl.listener == listener);
        return this;
    }

    public <T extends Event> EditorEventBus fire(T event) {
        final var listenerIterator = subscribedListeners.computeIfAbsent(
                event.getClass(),
                key -> new ArrayList<>()
        ).iterator();

        while (listenerIterator.hasNext()) {

            final var listener = listenerIterator.next();

            ((Listener<T>) listener.listener)
                    .onEvent(event);

            if (listener.once) {
                listenerIterator.remove();
            }

        }

        return this;
    }

    public <T extends Event> EditorEventBus fire(Class<T> event) {
        final var listenerIterator = subscribedListeners.computeIfAbsent(
                event,
                key -> new ArrayList<>()
        ).iterator();

        while (listenerIterator.hasNext()) {

            final var listener = listenerIterator.next();

            ((Listener<T>) listener.listener)
                    .onEvent(null);

            if (listener.once) {
                listenerIterator.remove();
            }

        }

        return this;
    }

    @Override
    public void install(Editor parent) {

        subscribedListeners.clear();

    }

    @Override
    public void uninstall() {

        subscribedListeners.clear();

    }

    private class SubscribedListener<T extends Event> {

        private final boolean once;
        private final Listener<T> listener;

        private SubscribedListener(boolean once, Listener<T> listener) {
            this.once = once;
            this.listener = listener;
        }
    }

}
