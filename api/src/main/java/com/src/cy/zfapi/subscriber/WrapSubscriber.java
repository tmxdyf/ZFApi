package com.src.cy.zfapi.subscriber;

import rx.Subscriber;

/**
 * Created by CY on 2016/5/13.
 */
public class WrapSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onNext(T t) {

    }
}
