package com.snxun.mp.greendaolib;

import android.content.Context;
import android.util.Log;

import com.snxun.mp.greendaolib.dao.DaoMaster;
import com.snxun.mp.greendaolib.dao.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * 数据库管理类
 * Created by zhouL on 2018/5/9.
 */
public class GreenDaoManager {
    /**
     * 日志标签
     */
    public static final String TAG = "dbtag";
    /**
     * 数据库名称
     */
    private static final String DB_NAME = "snxun_mail_db.db";

    private static GreenDaoManager mInstance = new GreenDaoManager();

    public static GreenDaoManager get() {
        return mInstance;
    }

    private GreenDaoManager() {
    }

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     * 数据库
     */
    private Database mDatabase;
    /**
     * 是否打印日志
     */
    private boolean isPrintLog;

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        UpgradeDevOpenHelper devOpenHelper = new UpgradeDevOpenHelper(context, DB_NAME, null);
        mDatabase = devOpenHelper.getWritableDb();
        mDaoSession = new DaoMaster(mDatabase).newSession();
        Log.d(TAG, "greendao init complete");
    }

    /**
     * 设置打印日志
     *
     * @param isPrint 是否打印
     */
    public void setPrintLog(boolean isPrint) {
        isPrintLog = isPrint;
    }

    protected boolean isPrintLog() {
        return isPrintLog;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public Database getDatabase() {
        return mDatabase;
    }
}


//----------------------------------- 数据库使用方法备注 --------------------------------------------

// -----------------------------------获取Note表的dao对象-----------------------------------
//    NoteDao mNoteDao = GreenDaoManager.get().getDaoSession().getNoteDao();

// -----------------------------------查询Note表的全部数据-----------------------------------
//    List<Note> list = mNoteDao.queryBuilder().list();

//        // RxJava形式
//        GreenDaoManager.get().getDaoSession().getUserDao().queryBuilder()
//                .orderDesc(UserDao.Properties.Id)// 根据id降序排列
//                .rx()
//                .list()
//                .flatMap(new Func1<List<User>, Observable<User>>() {
//                    @Override
//                    public Observable<User> call(List<User> users) {
//                        if (users == null || users.size() == 0){// 如果查不到数据就返回null给订阅者
//                            return Observable.just(null);
//                        }
//                        return Observable.from(users);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.w("dbtest", "onCompleted");// 操作结束
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("dbtest", e.toString());
//                    }
//
//                    @Override
//                    public void onNext(User user) {
//                        if (user == null){
//                            Log.i("dbtest", "无数据");
//                        }else {
//                            Log.i("dbtest", user.getId() + " ; " +user.getName() + " ; " + user.getAge() + " ; " + user.getNum());
//                        }
//                    }
//                });


// -----------------------------------查询Note表num为1的数据-----------------------------------
//    List<Note> list = mNoteDao.queryBuilder()
//            .where(NoteDao.Properties.Num.eq(1))
//            .list();

//        // RxJava形式
//        GreenDaoManager.get().getDaoSession().getUserDao().queryBuilder()
//                .where(UserDao.Properties.Age.eq(27))
//                .rx()
//                .list()
//                .flatMap(new Func1<List<User>, Observable<User>>() {
//                    @Override
//                    public Observable<User> call(List<User> users) {
//                        if (users == null || users.size() == 0){// 如果查不到数据就返回null给订阅者
//                            return Observable.just(null);
//                        }
//                        return Observable.from(users);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.w("dbtest", "onCompleted");// 操作结束
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("dbtest", e.toString());
//                    }
//
//                    @Override
//                    public void onNext(User user) {
//                        if (user == null){
//                            Log.i("dbtest", "无数据");
//                        }else {
//                            Log.i("dbtest", user.getId() + " ; " +user.getName() + " ; " + user.getAge() + " ; " + user.getNum());
//                        }
//                    }
//                });


// -----------------------------------删除Note表的全部数据-----------------------------------
//    mNoteDao.deleteAll();

//        // RxJava形式
//        GreenDaoManager.get().getDaoSession().getUserDao()
//                .rx()
//                .deleteAll()
//                .subscribe(new Subscriber<Void>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.w("dbtest", "onCompleted");// 操作结束
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("dbtest", e.toString());
//                    }
//
//                    @Override
//                    public void onNext(Void aVoid) {
//                        Log.d("dbtest", "delete all");
//                    }
//                });

// -----------------------------------删除Note表num为1的数据（先查询再删除）-----------------------------------
//    List<Note> list = mNoteDao.queryBuilder()
//            .where(NoteDao.Properties.Num.eq(1))
//            .list();
//    for (int i = 0; i < list.size(); i++){
//        mNoteDao.delete(listnum.get(i));
//    }

//        // RxJava形式
//        GreenDaoManager.get().getDaoSession().getUserDao().queryBuilder()
//                .whereOr(UserDao.Properties.Age.eq(27), UserDao.Properties.Age.eq(28))
//                .rx()
//                .list()
//                .flatMap(new Func1<List<User>, Observable<User>>() {
//                    @Override
//                    public Observable<User> call(List<User> users) {
//                        if (users == null || users.size() == 0){// 如果查不到数据就返回null给订阅者
//                            return Observable.just(null);
//                        }
//                        return Observable.from(users);
//                    }
//                })
//                .flatMap(new Func1<User, Observable<Void>>() {
//                    @Override
//                    public Observable<Void> call(User user) {
//                        if (user == null){
//                            return null;// 直接进入订阅者的onCompleted方法
//                        }
//                        return GreenDaoManager.get().getDaoSession().getUserDao().rx().delete(user);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Void>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.w("dbtest", "onCompleted");// 操作结束
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("dbtest", e.toString());
//                    }
//
//                    @Override
//                    public void onNext(Void aVoid) {
//                        Log.i("dbtest", "删除结束");// 删除几个会调用几次
//                    }
//                });

// -----------------------------------修改Note表num为2的数据（先查询再修改）-----------------------------------
//    List<Note> list = mNoteDao.queryBuilder()
//            .where(NoteDao.Properties.Num.eq(2))
//            .list();
//    for (int i = 0; i < list.size(); i++){
//        Note note = list.get(i);
//        note.setText("wewqsasf");
//        mNoteDao.update(note);
//    }

//        // RxJava形式
//        GreenDaoManager.get().getDaoSession().getUserDao().queryBuilder()
//                .whereOr(UserDao.Properties.Age.eq(27), UserDao.Properties.Age.eq(28))
//                .rx()
//                .list()
//                .flatMap(new Func1<List<User>, Observable<User>>() {
//                    @Override
//                    public Observable<User> call(List<User> users) {
//                        if (users == null || users.size() == 0){// 如果查不到数据就返回null给订阅者
//                            return Observable.just(null);
//                        }
//                        return Observable.from(users);
//                    }
//                })
//                .flatMap(new Func1<User, Observable<User>>() {
//                    @Override
//                    public Observable<User> call(User user) {
//                        if (user == null){
//                            return null;// 直接进入订阅者的onCompleted方法
//                        }
//                        user.setName("Macross");
//                        return GreenDaoManager.get().getDaoSession().getUserDao().rx().update(user);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.w("dbtest", "onCompleted");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("dbtest", e.toString());
//                    }
//
//                    @Override
//                    public void onNext(User user) {
//                        Log.i("dbtest", user.getId() + " ; " +user.getName() + " ; " + user.getAge() + " ; " + user.getNum());
//                    }
//                });

// -----------------------------------在Note表新增一条数据-----------------------------------
//    Note note = new Note(null, num, comment, "20160214");
//    mNoteDao.insert(note);

//        // RxJava形式
//        GreenDaoManager.get().getDaoSession().getUserDao()
//                .rx()
//                .insertOrReplaceInTx(list)// 这里是批量插入
//                .flatMap(new Func1<Iterable<User>, Observable<User>>() {
//                    @Override
//                    public Observable<User> call(Iterable<User> users) {
//                        return Observable.from(users);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.w("dbtest", "onCompleted");// 操作结束
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("dbtest", e.toString());
//                    }
//
//                    @Override
//                    public void onNext(User user) {
//                        Log.d("dbtest", user.getId() + " ; " +user.getName() + " ; " + user.getAge() + " ; " + user.getNum());
//                    }
//                });



