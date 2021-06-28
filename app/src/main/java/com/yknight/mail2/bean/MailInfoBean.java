package com.yknight.mail2.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @ProjectName: SXMail
 * @ClassName: MailInfo
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/5/14
 * @description: 邮件基本属性
 */
public class MailInfoBean implements Serializable {

    public static final long serialVersionUID = 1L;
    /**
     * 发送邮件的服务器的IP和端口
     */
//    public String mailServerHost = Constant.MAIL_SERVIER_HOST;
    public String sendMailServerHost = "";
    public String receiveMailServerHost = "";
    public int sendMailServerPort = -1;
    public int receiveMailServerPort = -1;
    /**
     * 登陆邮件发送服务器的用户名和密码
     */
    public String userName;
    public String password;
    /**
     * 是否需要身份验证
     */
    public boolean validate = false;
    /**
     * 目录列表
     */
    public List<String> catalogList = new ArrayList<>();
    /**
     * 目录字典
     */
    public List<CatalogNameBean> catalogNameBeanList=new ArrayList<>();


    /**
     * 获得SMTP协议属性
     */
    public Properties getSmtpProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", this.sendMailServerHost);
        p.put("mail.smtp.port", this.sendMailServerPort);
        p.put("mail.smtp.auth", validate ? "true" : "false");

        p.put("mail.smtp.starttls.enable", "true");
        //需要加上
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.put("mail.smtp.socketFactory.port", "465");

        return p;
    }

    /**
     * 获取POP3协议属性
     *
     * @return
     */
    public Properties getPop3Properties() {
        Properties prop = new Properties();
        prop.setProperty("mail.debug", "true");
        prop.setProperty("mail.store.protocol", "pop3");
        prop.setProperty("mail.pop3.host", this.receiveMailServerHost);
        return prop;
    }


    /**
     * 获取IMAP协议属性
     *
     * @return
     */
    public Properties getImapProperties() {
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", this.receiveMailServerHost);
        props.setProperty("mail.imap.port", "143");

        //建立ssl连接
        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.starttls.enable", "true");
        props.setProperty("mail.imap.socketFactory.port", "993");
        props.setProperty("mail.imap.usesocketchannels", "true");
        return props;
    }
}
