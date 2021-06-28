package com.yknight.mail2.utils.mail;

import com.lodz.android.corekt.threadpool.ThreadPoolManager;
import com.lodz.android.pandora.rx.subscribe.observer.RxObserver;
import com.lodz.android.pandora.rx.utils.RxUtils;
import com.sun.mail.pop3.POP3Folder;
import com.yknight.mail2.App;
import com.yknight.mail2.config.Constant;
import com.yknight.mail2.utils.db.DbFactory;
import com.yknight.mail2.widget.authenticator.SxAuthenticator;

import org.jetbrains.annotations.NotNull;

import java.util.Properties;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;

/**
 * @ProjectName: JakartaMail
 * @Package: com.yknight.jakartamail.utils
 * @ClassName: MailUtils
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/5/19
 * @description: 邮箱工具类
 */
public class MailHelper {
    private static final String TAG = "MailUtils";

    /**
     * 检验邮箱账号格式
     *
     * @param mailAccount 邮箱账号
     */
    public static boolean checkEmailFormat(String mailAccount) {
        if (mailAccount.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            return true;
        }
        return false;
    }

    /**
     * 设置邮箱用户参数
     *
     * @param account  账号
     * @param password 密码
     */
    private static void setMailInfo(String account, String password) {
        //由Constant默认一种邮箱的host,后续扩展可以通过增加用户配置页修改Constant的值
//                    App.mailInfo.mailServerHost = "smtp." + account.substring(account.lastIndexOf("@") + 1);
        App.sMailInfoBean.sendMailServerHost = Constant.SEND_MAIL_SERVIER_HOST;
        App.sMailInfoBean.sendMailServerPort = Constant.SEND_MAIL_SERVIER_PORT;

        //获取用于接收的imap协议的Session实例
        if (Constant.PROTOCOL.equals(Constant.ProtocolName.IMAP)) {
            App.sMailInfoBean.receiveMailServerHost = Constant.IMAP_RECEIVE_MAIL_SERVIER_HOST;
            App.sMailInfoBean.receiveMailServerPort = Constant.IMAP_RECEIVE_MAIL_SERVIER_PORT;
        } else if (Constant.PROTOCOL.equals(Constant.ProtocolName.POP3)) {
            //获取用于接收的pop3协议的Session实例
            App.sMailInfoBean.receiveMailServerHost = Constant.POP3_RECEIVE_MAIL_SERVIER_HOST;
            App.sMailInfoBean.receiveMailServerPort = Constant.POP3_RECEIVE_MAIL_SERVIER_PORT;
        }
        App.sMailInfoBean.userName = account;
        App.sMailInfoBean.password = password;
        App.sMailInfoBean.validate = Constant.VALIDATE;
    }

    /**
     * 获取Session实例
     *
     * @param props Session会话属性
     * @return
     */
    private static Session getSessionInstance(Properties props) {
        SxAuthenticator authenticator = null;
        //验证账号密码
        if (App.sMailInfoBean.validate) {
            authenticator = new SxAuthenticator(App.sMailInfoBean.userName, App.sMailInfoBean.password);
        }
        return Session.getInstance(props, authenticator);
    }

    /**
     * 邮箱登录
     *
     * @param account  邮箱账号
     * @param password 邮箱密码
     * @return
     */
    public static Observable<Boolean> mailLogin(String account, String password) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                Transport transport = null;
                try {
                    //设置邮箱信息
                    setMailInfo(account, password);
                    //获取发信Session会话实例
                    App.sSendSession = getSessionInstance(App.sMailInfoBean.getSmtpProperties());
                    //获取收信Session会话实例
                    if (Constant.ProtocolName.IMAP.equals(Constant.PROTOCOL)) {
                        App.sReceiveSession = getSessionInstance(App.sMailInfoBean.getImapProperties());
                    } else if (Constant.ProtocolName.POP3.equals(Constant.PROTOCOL)) {
                        App.sReceiveSession = getSessionInstance(App.sMailInfoBean.getPop3Properties());
                    }
                    //尝试建立发信连接,如果抛异常则判定为登录不成功
                    transport = App.sSendSession.getTransport(Constant.SENT_PROTOCOL);
                    transport.connect(App.sMailInfoBean.sendMailServerHost, account, password);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                } finally {
                    if (transport != null) {
                        transport.close();
                    }
                }
                if (App.sSendSession != null && App.sReceiveSession != null) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(true);
                        emitter.onComplete();
                    }
                }
            }
        });
    }


    /**
     * 获取邮箱文件列表
     *
     * @return
     */
    public static Observable<Boolean> getEmailCatalog() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                Store store = null;
                Folder[] folders = null;
                try {
                    store = App.sReceiveSession.getStore(Constant.PROTOCOL);
                    store.connect(Constant.IMAP_RECEIVE_MAIL_SERVIER_HOST, App.sMailInfoBean.userName, App.sMailInfoBean.password);
                    folders = store.getDefaultFolder().list();
                    App.sMailInfoBean.catalogList.clear();
                    App.sMailInfoBean.catalogNameBeanList.clear();
                    //获取目录列表,并保存为集合
                    App.sMailInfoBean.catalogList = CatalogUtils.getCatalogFormFolder(folders);
                    //获取目录列表字典
                    App.sMailInfoBean.catalogNameBeanList = CatalogUtils.getCatalogNameBeanList(App.sMailInfoBean.catalogList);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                } finally {
                    if (store != null)
                        store.close();
                }
                if (App.sMailInfoBean.catalogList == null || App.sMailInfoBean.catalogNameBeanList == null) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(new Throwable("获取目录列表为空"));
                    }
                    return;
                }
                if (!emitter.isDisposed()) {
                    emitter.onNext(true);
                    emitter.onComplete();
                }
            }

        });
    }


    /**
     * 保存邮件至数据库中
     *
     * @param messages 邮件
     * @return
     */
    public static Observable<Boolean> saveMessages2Db(Message[] messages) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                try {
                    DbFactory.create().setMessageList(messages)
                            .rx()
                            .compose(RxUtils.ioToMainObservable())
                            .subscribe(new RxObserver<Boolean>() {
                                @Override
                                public void onRxNext(Boolean aBoolean) {
                                    ThreadPoolManager.get().executeNormal(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                App.sFolder.close();
                                                App.sStore.close();
                                            } catch (MessagingException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    if (!emitter.isDisposed()) {
                                        emitter.onNext(aBoolean);
                                        emitter.onComplete();
                                    }
                                }

                                @Override
                                public void onRxError(@NotNull Throwable throwable, boolean b) {
                                    ThreadPoolManager.get().executeNormal(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                App.sFolder.close();
                                                App.sStore.close();
                                            } catch (MessagingException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    if (!emitter.isDisposed()) {
                                        emitter.onError(throwable);
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        });
    }

    /**
     * 获取特定目录的邮件
     *
     * @param catalog 目录
     * @return
     */
    public static Observable<Message[]> getMessageByCatalog(String catalog) {
        return Observable.create(new ObservableOnSubscribe<Message[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Message[]> emitter) throws Throwable {
                Message[] messages = null;
                try {
                    App.sStore = App.sReceiveSession.getStore(Constant.PROTOCOL);
                    App.sStore.connect(Constant.IMAP_RECEIVE_MAIL_SERVIER_HOST, App.sMailInfoBean.userName, App.sMailInfoBean.password);
                    App.sFolder = App.sStore.getFolder(catalog);
                    App.sFolder.open(Folder.READ_WRITE);
                    messages = App.sFolder.getMessages();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
                if (messages == null) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(new Throwable("获取邮件为空"));
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onNext(messages);
                    emitter.onComplete();
                }
            }
        });
    }


    /**
     * 获取指定目录下的对应uid的邮件
     *
     * @param catalog 目录
     * @param uid     uid
     * @return
     */
    public static Observable<Message> getMessageByCatalogAndUid(String catalog, String uid) {
        return Observable.create(new ObservableOnSubscribe<Message>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Message> emitter) throws Throwable {
                Message message = null;
                try {
                    App.sStore = App.sReceiveSession.getStore(Constant.PROTOCOL);
                    App.sStore.connect(Constant.IMAP_RECEIVE_MAIL_SERVIER_HOST, App.sMailInfoBean.userName, App.sMailInfoBean.password);
                    App.sFolder = App.sStore.getFolder(catalog);
                    App.sFolder.open(Folder.READ_WRITE);
                    Message[] messages = App.sFolder.getMessages();
                    for (Message msg : messages) {
                        if (((POP3Folder) msg.getFolder()).getUID(msg).equals(uid)) {
                            message = msg;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
                if (message == null) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(new Throwable("获取邮件为空"));
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onNext(message);
                    emitter.onComplete();
                }
            }
        });
    }


}
