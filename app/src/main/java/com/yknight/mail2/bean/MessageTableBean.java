package com.yknight.mail2.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @ProjectName: JakartaMail
 * @Package: com.yknight.jakartamail.bean
 * @ClassName: MessageTableBean
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/6/1
 * @description:
 */
public class MessageTableBean implements Serializable {
    /**
     * 邮件uid
     */
    public String uid;
    /**
     * 发件人
     */
    public String from;
    /**
     * 收件人
     */
    public String addressee;
    /**
     * 当前登录的用户
     */
    public String user;
    /**
     * 邮件所在目录
     */
    public String catalog;
    /**
     * 抄送人
     */
    public String copying;
    /**
     * 密送人
     */
    public String airtight;
    /**
     * 发送时间
     */
    public String sentData;
    /**
     * 主题
     */
    public String subject;
    /**
     * 是否是html类型
     */
    public boolean isHtml;
    /**
     * 是否是新邮件
     */
    public boolean isNew;
    /**
     * 文本内容
     */
    public String content;
    /**
     * 附件列表
     */
    public String attachmentList;
    /**
     * 附件路径
     */
    public String attachmentUrl;


}
