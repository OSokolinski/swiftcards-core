package swiftcards.core.game;

import java.util.Date;

public interface Activity<T> {
    Date getTimeStamp();
    ActivitySubject<T> getActivitySubject();

    class ActivitySubject<T> {
        T subject;

        protected ActivitySubject(T subj) {
            subject = subj;
        }

        public T getSubject() {
            return subject;
        }
    }
}
