package swiftcards.core.util;

public interface EventBus {
    <T> void on(Class<? extends Event<T>> eventClass, Subscriber<T> handler);
    <T> void on(Class<? extends Event<T>> eventClass, Subscriber.ConsumerBase<T> handler);
    <T> void emit(Event<T> event);
    <T> void unsubscribe(Class<? extends Event<T>> eventClass, Subscriber<T> subscriber);
    void clear();
}
