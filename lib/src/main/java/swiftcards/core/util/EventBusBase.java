package swiftcards.core.util;

import swiftcards.core.util.Subscriber.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class EventBusBase implements EventBus {

    protected static EventBus instance = null;

    protected final HashMap<Class<? extends Event<?>>, List<Subscriber<?>>> subscribers;

    protected EventBusBase() {
        subscribers = new HashMap<>();
    }

    public <T> void on(Class<? extends Event<T>> eventClass, Subscriber<T> handler) {

        if (!subscribers.containsKey(eventClass)) {
            subscribers.put(eventClass, new ArrayList<>());
        }

        subscribers.get(eventClass).add(handler);
    }

    public <T> void on(Class<? extends Event<T>> eventClass, ConsumerBase<T> handler) {

        Subscriber<T> subscriber;

        if (handler instanceof BasicConsumer) {
            subscriber = new Subscriber<T>((BasicConsumer<T>) handler);
        }
        else if (handler instanceof ContextConsumer) {
            subscriber = new Subscriber<T>((ContextConsumer<T>) handler);
        }
        else return;

        on(eventClass, subscriber);
    }

    @SuppressWarnings("unchecked")
    public <T> void emit(Event<T> event) /*throws EventEmitException*/ {

        List<Subscriber<?>> handlers = subscribers.getOrDefault(event.getClass(), new ArrayList<>());

        for (Subscriber<?> handlerCandidate : handlers) {

            Subscriber<T> handler;

            try {
                handler = (Subscriber<T>) handlerCandidate;
            }
            catch (ClassCastException e) {
                break;
                //throw new EventEmitException("Fatal casting error.");
            }

            new Thread(() -> {
                handler.consume(event.getEventData());
            }).start();

        }

    }
    
    public <T> void unsubscribe(Class<? extends Event<T>> eventClass, Subscriber<T> subscriber) {
        subscribers.get(eventClass).remove(subscriber);
    }

    public static class EventEmitException extends Exception {
        public EventEmitException(String details) {
            super(details);
        }
    }
}
