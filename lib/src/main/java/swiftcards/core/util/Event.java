package swiftcards.core.util;

import java.io.Serializable;

public interface Event<T> extends Serializable {
    T getEventData();
}
