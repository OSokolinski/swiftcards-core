package swiftcards.core.game;

import java.io.Serializable;
import java.util.Date;

public interface Activity<T> extends Serializable {
    Date getTimeStamp();
    ActivitySubject<T> getActivitySubject();

    class ActivitySubject<T> implements Serializable {
        T subject;

        protected ActivitySubject(T subj) {
            subject = subj;
        }

        public T getSubject() {
            return subject;
        }
    }
}
