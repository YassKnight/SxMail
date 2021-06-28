package com.yknight.mail2.utils.mail;


import android.text.TextUtils;

import com.lodz.android.corekt.utils.FileUtils;
import com.lodz.android.pandora.rx.utils.RxUtils;
import com.yknight.mail2.bean.MessageTableBean;
import com.yknight.mail2.utils.db.DbFactory;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;


/**
 * @ProjectName: SXMail
 * @Package: com.snxun.sxmail.utils
 * @ClassName: MessageUtils
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/5/17
 * @description: 邮件帮助类
 */
public class MessageUtils {
    /**
     * 保存Message信息
     *
     * @param message
     * @return
     * @throws MessagingException
     */
    public static ArrayList<Message> changeMessage2List(Message[] message) throws MessagingException {
        ArrayList<Message> messageInfos = new ArrayList<>();
        for (Message msg : message) {
            messageInfos.add((MimeMessage) msg);
        }
        return messageInfos;
    }

    /**
     * 解析邮件,并设置缓存
     */
    public static void parseMessage() {

    }


    /**
     * 将address数组转为String字符串
     *
     * @param addresses
     * @return
     */
    public static String addressArray2String(Address[] addresses) {
        String result = "";
        if (addresses == null || addresses.length < 1) {
            return result;
        }
        for (Address address : addresses) {
            result += address.toString() + ";";
        }
        return result.substring(0, result.length() - 1);
    }


    /**
     * 获取邮件的文本信息
     *
     * @param message
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static String getMessageContent(Message message) {
        try {
            String messageContent = "";
            // 邮件类型不是mixed时，表示邮件中不包含附件，直接输出邮件内容
            if (!message.isMimeType("multipart/mixed")) {
                messageContent = (String) message.getContent();
            } else {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    //MIME消息头中不包含disposition字段， 并且MIME消息类型不为mixed时，
                    //表示当前获得的MIME消息为邮件正文
                    if (!bodyPart.isMimeType("multipart/mixed") && bodyPart.getDisposition() == null) {
                        messageContent = (String) bodyPart.getContent();
                        break;
                    }
                }
            }
            return messageContent;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取邮件的附近列表
     *
     * @param message
     * @return
     */
    public static String getMessageAttachmentList(Message message) {
        try {
            StringBuilder messageAttachmentList = new StringBuilder();
            // 邮件类型不是mixed时，表示邮件中不包含附件
            if (!message.isMimeType("multipart/*")) {
                messageAttachmentList = new StringBuilder();
            } else {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getDisposition() != null) {
                        messageAttachmentList.append(bodyPart.getFileName()).append(";");
                    }
                }
                messageAttachmentList = new StringBuilder(messageAttachmentList.substring(0, messageAttachmentList.length() - 1));
            }
            return messageAttachmentList.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static Observable<Boolean> getAttachMent(String uid, String catalog) {
        return MailHelper.getMessageByCatalog(catalog).flatMap(new Function<Message[], ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Message[] messages) throws Throwable {
                return null;
            }
        })
                .flatMap(new Function<String, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(String url) throws Throwable {
                        return Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                                //如果文件夹存在,表示附近已保存,则取出附件展示
                                if (FileUtils.isFileExists(url)) {
                                }
                            }
                        });
                    }
                });
    }

    public static Observable<Boolean> isHasAttachment(String uid) {
        return DbFactory.create().getMessageByUid(uid).rx()
                .compose(RxUtils.ioToMainObservable())
                .flatMap(new Function<MessageTableBean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(MessageTableBean bean) throws Throwable {
                        if (TextUtils.isEmpty(bean.attachmentList)) {
                            return Observable.just(false);
                        } else {
                            return Observable.just(true);
                        }
                    }
                });
    }

//    public static void parseAttachmentAndSave() {
//        try {
//
//            if (!msg.isMimeType("multipart/mixed")) {
//                response.setContentType("message/rfc822");
//                msg.writeTo(sos);
//            } else {
//                // 查找并输出邮件中的邮件正文
//                Multipart mp = (Multipart) msg.getContent();
//                int bodynum = mp.getCount();
//                for (int i = 0; i < bodynum; i++) {
//                    BodyPart bp = mp.getBodyPart(i);
//                    /*
//                     * MIME消息头中不包含disposition字段， 并且MIME消息类型不为mixed时，
//                     * 表示当前获得的MIME消息为邮件正文
//                     */
//                    if (!bp.isMimeType("multipart/mixed") && bp.getDisposition() == null) {
//                        response.setContentType("message/rfc822");
//                        bp.writeTo(sos);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
