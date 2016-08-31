package io.github.ryanhoo.music;

import android.util.Log;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 6/25/16
 * Time: 3:01 PM
 * Desc: An EventBus powered by RxJava.
 * But before you use this RxBus, bear in mind this very IMPORTANT note:
 * - Be very careful when error occurred here, this can terminate the whole
 * event observer pattern. If one error ever happened, new events won't be
 * received because this subscription has be terminated after onError(Throwable).
 */

public class RxBus {

    private static final String TAG = "RxBus";

    private static volatile RxBus sInstance;

    public static RxBus getInstance() {
        if (sInstance == null) {
            synchronized (RxBus.class) {
                if (sInstance == null) {
                    sInstance = new RxBus();
                }
            }
        }
        return sInstance;
    }

    /**
     * PublishSubject<Object> subject = PublishSubject.create();
     * // observer1 will receive all onNext and onCompleted events
     * subject.subscribe(observer1);
     * subject.onNext("one");
     * subject.onNext("two");
     * // observer2 will only receive "three" and onCompleted
     * subject.subscribe(observer2);
     * subject.onNext("three");
     * subject.onCompleted();
     */
    private PublishSubject<Object> mEventBus = PublishSubject.create();

    public void post(Object event) {
        mEventBus.onNext(event);
    }

    public Observable<Object> toObservable() {
        return mEventBus;
    }

    /**
     * A simple logger for RxBus which can also prevent
     * potential crash(OnErrorNotImplementedException) caused by error in the workflow.
     */
    public static Subscriber<Object> defaultSubscriber() {
        return new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "Duty off!!!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "What is this? Please solve this as soon as possible!", e);
            }

            @Override
            public void onNext(Object o) {
                Log.d(TAG, "New event received: " + o);
            }
        };
    }
}
