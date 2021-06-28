package com.yknight.mail2.utils.db;

import com.lodz.android.corekt.anko.AnkoArrayKt;
import com.lodz.android.corekt.log.PrintLog;
import com.snxun.mp.greendaolib.GreenDaoManager;
import com.snxun.mp.greendaolib.dao.EmailMessageTableDao;
import com.snxun.mp.greendaolib.table.EmailMessageTable;
import com.sun.mail.pop3.POP3Folder;
import com.yknight.mail2.App;
import com.yknight.mail2.bean.MessageTableBean;
import com.yknight.mail2.config.Constant;
import com.yknight.mail2.utils.mail.MailReceiveUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;

/**
 * GreenDao接口实现
 */
public class GreenDaoImpl implements DbHelper {

    private EmailMessageTable createEmailMessageTable(EmailMessageTable table, Message message) throws Exception {
        if (table == null) {
            table = new EmailMessageTable();
        }
//        table.setUid(((POP3Folder) message.getFolder()).getUID(message));
//        table.setSender(MessageUtils.addressArray2String(message.getFrom()));
//        table.setAddressee(MessageUtils.addressArray2String(message.getRecipients(Message.RecipientType.TO)));
//        table.setCopying(MessageUtils.addressArray2String(message.getRecipients(Message.RecipientType.CC)));
//        table.setAirtight(MessageUtils.addressArray2String(message.getRecipients(Message.RecipientType.BCC)));
//        table.setSendTime(message.getSentDate());
//        table.setUser(App.mailInfo.userName);
//        table.setCatalog(message.getFolder().getName());
//        table.setSubject(message.getSubject());
//        table.setContentType(message.getContentType());
//        table.setContent(MessageUtils.getMessageContent(message));
//        table.setAttachmentList(MessageUtils.getMessageAttachmentList(message));
//        table.setAttachmentUrl("");
        if (message == null) {
            return table;
        }
        MimeMessage mimeMessage = (MimeMessage) message;

        table.setUid(MailReceiveUtils.getMessageID(mimeMessage));
        table.setFrom(MailReceiveUtils.getFrom(mimeMessage));
        table.setAddressee(MailReceiveUtils.getMailAddress(Constant.RecipientType.TO, mimeMessage));
        table.setCopying(MailReceiveUtils.getMailAddress(Constant.RecipientType.CC, mimeMessage));
        table.setAirtight(MailReceiveUtils.getMailAddress(Constant.RecipientType.BCC, mimeMessage));
        table.setSentData(MailReceiveUtils.getSentData(mimeMessage));
        table.setUser(App.sMailInfoBean.userName);
        table.setCatalog(message.getFolder().getName());
        table.setSubject(MailReceiveUtils.getSubject(mimeMessage));
        table.setIsHtml(MailReceiveUtils.isHtml(mimeMessage));
        table.setIsNew(MailReceiveUtils.isNew(mimeMessage));
        table.setContent(MailReceiveUtils.getMailContent(mimeMessage));
        table.setAttachmentList(MailReceiveUtils.getMailContentAttachmentName(mimeMessage));
        table.setAttachmentUrl("");
        return table;
    }

    /**
     * 保存邮箱对应文件目录下的邮件列表
     *
     * @param array 服务端获取的某一个文件目录下的邮件列表
     * @return
     */
    @Override
    public DbAgent<Boolean> setMessageList(Message[] array) {
        return new DbAgent<>(Observable.just(array).map(new Function<Message[], Boolean>() {
            @Override
            public Boolean apply(Message[] messages) throws Throwable {
                PrintLog.d(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 配置邮件列表");

                if (AnkoArrayKt.isNullOrEmpty(array)) {
                    EmailMessageTable table = GreenDaoManager.get().getDaoSession()
                            .getEmailMessageTableDao()
                            .queryBuilder()
                            .where(EmailMessageTableDao.Properties.User.eq(App.sMailInfoBean.userName))
                            .unique();
                    if (table != null) {
                        GreenDaoManager.get().getDaoSession().getEmailMessageTableDao().delete(createEmailMessageTable(table, null));
                    }
                    PrintLog.i(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 配置邮件列表");
                    return true;
                }

                List<EmailMessageTable> tables = GreenDaoManager.get().getDaoSession()
                        .getEmailMessageTableDao()
                        .queryBuilder()
                        .list();

                // 本地无数据把后台邮件列表直接插入
                if (AnkoArrayKt.isNullOrEmpty(tables)) {
                    for (Message message : array) {
                        GreenDaoManager.get().getDaoSession().getEmailMessageTableDao().insert(createEmailMessageTable(null, message));
                    }
                    PrintLog.i(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 配置邮件列表");
                    return true;
                }

                // 本地有数据
                for (Message message : array) {
                    EmailMessageTable table = GreenDaoManager.get().getDaoSession()
                            .getEmailMessageTableDao()
                            .queryBuilder()
                            .where(EmailMessageTableDao.Properties.Uid.eq(((POP3Folder) message.getFolder()).getUID(message)))
                            .unique();
                    if (table == null) {
                        GreenDaoManager.get().getDaoSession().getEmailMessageTableDao().insert(createEmailMessageTable(null, message));
                    } else {
                        GreenDaoManager.get().getDaoSession().getEmailMessageTableDao().update(createEmailMessageTable(table, message));
                    }
                }

                PrintLog.i(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 配置邮件列表");
                return true;
            }
        }));
    }

    /**
     * 获取数据库中的所有邮件数据
     *
     * @return
     */
    @Override
    public DbAgent<List<MessageTableBean>> getAllMessageList() {
        return new DbAgent<>(Observable.just("").map(new Function<String, List<MessageTableBean>>() {
            @Override
            public List<MessageTableBean> apply(String s) throws Exception {
                PrintLog.d(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 获取全部邮件列表");

                List<EmailMessageTable> tables = GreenDaoManager.get().getDaoSession()
                        .getEmailMessageTableDao()
                        .queryBuilder()
                        .orderDesc(EmailMessageTableDao.Properties.Id)// 根据id降序排列
                        .where(EmailMessageTableDao.Properties.User.eq(App.sMailInfoBean.userName))
                        .list();

                List<MessageTableBean> list = new ArrayList<>();
                if (tables != null) {
                    for (EmailMessageTable table : tables) {
                        list.add(createMessageTableBean(table));
                    }
                }

                PrintLog.i(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 获取全部邮件列表");
                return list;
            }
        }));
    }

    /**
     * 根据目录获取对应的邮件列表
     *
     * @param Catalog 目录
     * @return
     */
    @Override
    public DbAgent<List<MessageTableBean>> getMessageListByCatalog(String Catalog) {
        return new DbAgent<>(Observable.just("").map(new Function<String, List<MessageTableBean>>() {
            @Override
            public List<MessageTableBean> apply(String s) throws Exception {
                PrintLog.d(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 获取对应目录的邮件列表");

                List<MessageTableBean> list = getAllMessageList().sync();// 获取全部邮件列表
                List<MessageTableBean> results = new ArrayList<>();
                for (MessageTableBean bean : list) {
                    if (Catalog.equals(bean.catalog)) {// 把目录下邮件加入结果集
                        results.add(bean);
                    }
                }
                PrintLog.i(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 获取对应目录的邮件列表");
                return results;
            }
        }));

    }

    /**
     * 通过message uid 获取对应的邮件信息
     *
     * @param uid 邮件的uid
     * @return
     */
    @Override
    public DbAgent<MessageTableBean> getMessageByUid(String uid) {
        return new DbAgent<>(Observable.just("").map(new Function<String, MessageTableBean>() {
            @Override
            public MessageTableBean apply(String s) throws Throwable {
                PrintLog.d(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 获取对应uid的邮件");

                List<MessageTableBean> list = getAllMessageList().sync();// 获取全部邮件列表
                MessageTableBean results = null;
                for (MessageTableBean bean : list) {
                    if (uid.equals(bean.uid)) {// 获取对应uid的邮件
                        results = bean;
                        break;
                    }
                }
                PrintLog.i(GreenDaoManager.TAG, Thread.currentThread().getName() + " --- 获取对应uid的邮件");
                return results;
            }
        }));
    }

    /**
     * 创建邮件表格数据实体类
     *
     * @param table 数据库的邮件table
     * @return
     */
    private MessageTableBean createMessageTableBean(EmailMessageTable table) {
        MessageTableBean bean = new MessageTableBean();
        bean.uid = table.getUid();
        bean.addressee = table.getAddressee();
        bean.airtight = table.getAirtight();
        bean.from = table.getFrom();
        bean.copying = table.getCopying();
        bean.catalog = table.getCatalog();
        bean.user = table.getUser();
        bean.sentData = table.getSentData();
        bean.subject = table.getSubject();
        bean.isHtml = table.getIsHtml();
        bean.isNew = table.getIsNew();
        bean.content = table.getContent();
        bean.attachmentList = table.getAttachmentList();
        bean.attachmentUrl = table.getAttachmentUrl();
        return bean;
    }


}
