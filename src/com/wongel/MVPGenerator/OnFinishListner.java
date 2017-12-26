package com.wongel.MVPGenerator;

/**
 * Created by tseringwongelgurung on 12/26/17.
 */

interface OnFinishListner<T> {
    void onFinished(T result);

    void onFailed(String msg);
}
