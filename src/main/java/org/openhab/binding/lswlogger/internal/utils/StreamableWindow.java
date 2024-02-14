package org.openhab.binding.lswlogger.internal.utils;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class StreamableWindow<T> {

    private final Stream<T> stream;

    public StreamableWindow(Stream<T> stream) {
        this.stream = stream;
    }

    public void forEach(BiConsumer<T, T> consumer) {
        StreamableWindow<T>.Window window = new Window();
        stream.forEach(item -> {
            window.shift(item);
            window.call(consumer);
        });
        window.shift(null);
        window.call(consumer);
    }

    private class Window {
        private T current;
        private T next;

        public void shift(T newNext) {
            current = next;
            next = newNext;
        }

        public void call(BiConsumer<T, T> consumer) {
            consumer.accept(current, next);
        }
    }
}
