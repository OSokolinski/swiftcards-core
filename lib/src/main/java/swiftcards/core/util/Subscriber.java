package swiftcards.core.util;

import java.util.function.BiConsumer;

public class Subscriber<T> {

    private final ConsumerBase<T> subscriberConsumer;

    public Subscriber(BasicConsumer<T> consumer) {
        subscriberConsumer = consumer;
    }

    public Subscriber(ContextConsumer<T> consumer) {
        subscriberConsumer = consumer;
    }

    public void consume(T data) {

        if (subscriberConsumer instanceof ContextConsumer) {
            ((ContextConsumer<T>) subscriberConsumer).accept(data, this);
        }
        else if(subscriberConsumer instanceof BasicConsumer) {
            ((BasicConsumer<T>) subscriberConsumer).accept(data);
        }

    }

    @FunctionalInterface
    public interface BasicConsumer<T> extends ConsumerBase<T> {
        void accept(T data);
    }

    @FunctionalInterface
    public interface ContextConsumer<T> extends ConsumerBase<T> {
        void accept(T data, Subscriber<T> subscriberCtx);
    }

    public interface ConsumerBase<T> {}
}
