package swiftcards.core.util;

import swiftcards.core.networking.ExternalEventEmitted;
import swiftcards.core.util.Subscriber.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultEventBus implements EventBus {

    protected final HashMap<Class<? extends Event<?>>, List<Subscriber<?>>> subscribers;

    public DefaultEventBus() {
        subscribers = new HashMap<>();
    }

    @Override
    public synchronized <T> void on(Class<? extends Event<T>> eventClass, Subscriber<T> handler) {

        if (!subscribers.containsKey(eventClass)) {
            subscribers.put(eventClass, new ArrayList<>());
        }

        subscribers.get(eventClass).add(handler);
    }

    @Override
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
    @Override
    public synchronized <T> void emit(Event<T> event) /*throws EventEmitException*/ {

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

    @Override
    public synchronized  <T> void unsubscribe(Class<? extends Event<T>> eventClass, Subscriber<T> subscriber) {
        subscribers.get(eventClass).remove(subscriber);
    }

    @Override
    public void clear() {
        subscribers.clear();
    }

    public static class EventEmitException extends Exception {
        public EventEmitException(String details) {
            super(details);
        }
    }
}
