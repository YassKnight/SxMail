package com.snxun.mp.greendaolib.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class EmailMessageTable {

    /**
     * 自增主键
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 邮件uid
     */
    @NotNull
    private String uid;
    /**
     * 发件人
     */
    @NotNull
    private String from;
    /**
     * 收件人
     */
    @NotNull
    private String addressee;
    /**
     * 当前登录的用户
     */
    @NotNull
    private String user;
    /**
     * 邮件所在目录
     */
    @NotNull
    private String catalog;
    /**
     * 抄送人
     */
    private String copying;
    /**
     * 密送人
     */
    private String airtight;
    /**
     * 发送时间
     */
    private String sentData;
    /**
     * 主题
     */
    private String subject;
    /**
     * 是否是html类型
     */
    private boolean isHtml;
    /**
     * 是否是新邮件
     */
    private boolean isNew;
    /**
     * 文本内容
     */
    private String content;
    /**
     * 附件列表
     */
    private String attachmentList;
    /**
     * 附件路径
     */
    private String attachmentUrl;
    @Generated(hash = 2007350042)
    public EmailMessageTable(Long id, @NotNull String uid, @NotNull String from,
            @NotNull String addressee, @NotNull String user,
            @NotNull String catalog, String copying, String airtight,
            String sentData, String subject, boolean isHtml, boolean isNew,
            String content, String attachmentList, String attachmentUrl) {
        this.id = id;
        this.uid = uid;
        this.from = from;
        this.addressee = addressee;
        this.user = user;
        this.catalog = catalog;
        this.copying = copying;
        this.airtight = airtight;
        this.sentData = sentData;
        this.subject = subject;
        this.isHtml = isHtml;
        this.isNew = isNew;
        this.content = content;
        this.attachmentList = attachmentList;
        this.attachmentUrl = attachmentUrl;
    }
    @Generated(hash = 1618477633)
    public EmailMessageTable() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getFrom() {
        return this.from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getAddressee() {
        return this.addressee;
    }
    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }
    public String getUser() {
        return this.user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getCatalog() {
        return this.catalog;
    }
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
    public String getCopying() {
        return this.copying;
    }
    public void setCopying(String copying) {
        this.copying = copying;
    }
    public String getAirtight() {
        return this.airtight;
    }
    public void setAirtight(String airtight) {
        this.airtight = airtight;
    }
    public String getSentData() {
        return this.sentData;
    }
    public void setSentData(String sentData) {
        this.sentData = sentData;
    }
    public String getSubject() {
        return this.subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public boolean getIsHtml() {
        return this.isHtml;
    }
    public void setIsHtml(boolean isHtml) {
        this.isHtml = isHtml;
    }
    public boolean getIsNew() {
        return this.isNew;
    }
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAttachmentList() {
        return this.attachmentList;
    }
    public void setAttachmentList(String attachmentList) {
        this.attachmentList = attachmentList;
    }
    public String getAttachmentUrl() {
        return this.attachmentUrl;
    }
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }



}
