package com.yknight.mail2.config;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @ProjectName: SXMail
 * @Package: com.snxun.sxmail.config
 * @ClassName: Constant
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/5/14
 * @description: qq邮箱相关配置常量
 */
public class Constant {

    public static String SENT_PROTOCOL = "smtp";

    /**
     * 协议名字
     */
    @StringDef({ProtocolName.IMAP, ProtocolName.POP3})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProtocolName {
        /**
         * imap
         */
        String IMAP = "imap";
        /**
         * pop3
         */
        String POP3 = "pop3";
    }

    /**
     * 邮件目录英文名
     */
    @StringDef({CatalogEnName.INBOX, CatalogEnName.SENTMESSAGES, CatalogEnName.DRAFTS, CatalogEnName.DELETED_MESSAGES, CatalogEnName.JUNK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CatalogEnName {
        String INBOX = "INBOX";
        String SENTMESSAGES = "Sent Messages";
        String DRAFTS = "Drafts";
        String DELETED_MESSAGES = "Deleted Messages";
        String JUNK = "Junk";
    }


    /**
     * 邮件目录中文名
     */
    @StringDef({CatalogEnName.INBOX, CatalogEnName.SENTMESSAGES, CatalogEnName.DRAFTS, CatalogEnName.DELETED_MESSAGES, CatalogEnName.JUNK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CatalogZhName {
        String INBOX = "收件箱";
        String SENTMESSAGES = "已发送";
        String DRAFTS = "草稿箱";
        String DELETED_MESSAGES = "已删除";
        String JUNK = "垃圾箱";
    }

    /**
     * 收件人类型
     */
    @StringDef({RecipientType.TO, RecipientType.CC, RecipientType.BCC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecipientType {
        String TO = "TO";
        String CC = "CC";
        String BCC = "BCC";
    }


    /**
     * 使用的协议
     */
    public static String PROTOCOL = ProtocolName.POP3;
    /**
     * 发信邮箱服务器地址
     */
    public static String SEND_MAIL_SERVIER_HOST = "smtp.qq.com";
    /**
     * IMAP收信邮箱服务器地址
     */
    public static String IMAP_RECEIVE_MAIL_SERVIER_HOST = "imap.qq.com";
    /**
     * POP3收信邮箱服务器地址
     */
    public static String POP3_RECEIVE_MAIL_SERVIER_HOST = "pop3.qq.com";
    /**
     * 邮箱服务器端口
     */
    public static int SEND_MAIL_SERVIER_PORT = 465;
    /**
     * IMAP收信服务器端口
     */
    public static int IMAP_RECEIVE_MAIL_SERVIER_PORT = 993;
    /**
     * POP3收信服务器端口
     */
    public static int POP3_RECEIVE_MAIL_SERVIER_PORT = 995;
    /**
     * 是否需要验证
     */
    public static boolean VALIDATE = true;
}
