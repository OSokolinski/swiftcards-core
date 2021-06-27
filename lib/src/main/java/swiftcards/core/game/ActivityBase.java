package swiftcards.core.game;

import java.util.Date;

public abstract class ActivityBase<T> implements Activity<T> {

    protected Date timeStamp;
    protected ActivitySubject<T> activitySubject;

    protected ActivityBase(T subject) {
        timeStamp = new Date();
        activitySubject = new ActivitySubject<T>(subject);
    }

    @Override
    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public ActivitySubject<T> getActivitySubject() {
        return activitySubject;
    }
}
