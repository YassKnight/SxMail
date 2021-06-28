package com.yknight.mail2.utils.db;


import io.reactivex.rxjava3.core.Observable;

/**
 * 数据库转换代理
 */
public class DbAgent<T> {

    private Observable<T> mData;

    DbAgent(Observable<T> mData) {
        this.mData = mData;
    }

    /**
     * 转RX模式
     */
    public Observable<T> rx() {
        return mData;
    }

    /**
     * 转同步模式
     */
    public T sync() {
        try {
            return mData.blockingFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
