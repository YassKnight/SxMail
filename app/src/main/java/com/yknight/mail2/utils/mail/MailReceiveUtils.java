package com.yknight.mail2.utils.mail;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sun.mail.pop3.POP3Folder;
import com.yknight.mail2.config.Constant;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.util.ByteArrayDataSource;

/**
 * @ProjectName: Mail2
 * @Package: com.yknight.mail2.utils.mail
 * @ClassName: MailReceiveUtils
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/6/17
 * @description: 邮件接收工具类
 */
public class MailReceiveUtils {

    private static final String TAG = "MailReceiveUtils";
    private static final String DATAFORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取送信人的姓名和邮件地址
     *
     * @param mimeMessage 邮件
     * @return
     * @throws MessagingException
     */
    public static String getFrom(MimeMessage mimeMessage) throws MessagingException {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String addr = address[0].getAddress();
        String name = address[0].getPersonal();
        if (addr == null) {
            addr = "";
        }
        if (name == null) {
            name = "";
        } else if (TranCharsetutils.parseContentTypeCharset(mimeMessage.getContentType()) == null) {
            name = TranCharsetutils.tranEncodeTOGB(name);
        }
        String nameAddr = name + "<" + addr + ">";
        return nameAddr;
    }


    /**
     * 根据类型，获取邮件地址 "TO"--收件人地址 "CC"--抄送人地址 "BCC"--密送人地址
     *
     * @param type        地址类型
     * @param mimeMessage 邮件
     * @return
     * @throws Exception
     */
    public static String getMailAddress(String type, MimeMessage mimeMessage) throws Exception {
        String mailAddr = "";
        String addType = type.toUpperCase(Locale.CHINA);
        InternetAddress[] address = null;
        if (Constant.RecipientType.TO.equalsIgnoreCase(addType)) {
            address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
        } else if (Constant.RecipientType.CC.equalsIgnoreCase(addType)) {
            address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
        } else if (Constant.RecipientType.BCC.equalsIgnoreCase(addType)) {
            address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
        } else {
            Log.e(TAG, "error type!");
            throw new Exception("Error email address type!");
        }
        if (address == null) {
            return "";
        }
        for (InternetAddress internetAddress : address) {
            String mailaddress = internetAddress.getAddress();
            if (mailaddress != null) {
                mailaddress = MimeUtility.decodeText(mailaddress);
            } else {
                mailaddress = "";
            }
            String name = internetAddress.getPersonal();
            if (name != null) {
                name = MimeUtility.decodeText(name);
            } else {
                name = "";
            }
            mailAddr = name + "<" + mailaddress + ">";
        }
        return mailAddr;
    }

    /**
     * 获取邮件主题
     *
     * @param mimeMessage 邮件
     * @return
     */
    public static String getSubject(MimeMessage mimeMessage) {
        String subject = "";
        try {
            subject = mimeMessage.getSubject();
            if (subject.contains("=?gb18030?")) {
                subject = subject.replace("gb18030", "gb2312");
            }
            subject = MimeUtility.decodeText(subject);
            if (TranCharsetutils.parseContentTypeCharset(mimeMessage.getContentType()) == null) {
                subject = TranCharsetutils.tranEncodeTOGB(subject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subject;
    }


    /**
     * 获取邮件发送日期
     *
     * @param mimeMessage 邮件
     * @return
     * @throws MessagingException
     */
    public static String getSentData(MimeMessage mimeMessage) throws MessagingException {
        Date sentdata = mimeMessage.getSentDate();
        if (sentdata != null) {
            SimpleDateFormat format = new SimpleDateFormat(DATAFORMAT, Locale.CHINA);
            return format.format(sentdata);
        } else {
            return "";
        }
    }

    /**
     * 判断邮件是否是html类型
     *
     * @param mimeMessage 邮件
     * @return
     * @throws Exception
     */
    public static boolean isHtml(MimeMessage mimeMessage) throws Exception {
        StringBuilder mailContent = compileMailContent((Part) mimeMessage, mimeMessage);
        String content = mailContent.toString();
        return content.contains("html");
    }

    /**
     * 获取邮件内容
     *
     * @param mimeMessage 邮件
     * @return
     * @throws Exception
     */
    public static String getMailContent(MimeMessage mimeMessage) throws Exception {
        StringBuilder mailContent = compileMailContent((Part) mimeMessage, mimeMessage);
        return mailContent.toString();
    }

    /**
     * 获取邮件附件列表
     *
     * @param mimeMessage 邮件
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    public static String getMailContentAttachmentName(MimeMessage mimeMessage) throws IOException, MessagingException {
        List<String> attachments = compileMailContentAttachmentName((Part) mimeMessage, new ArrayList<>());
        if (attachments.size() > 0) {
            return JSON.toJSONString(attachments);
        } else {
            return "";
        }
    }

    /**
     * 获取邮件附近输入流
     *
     * @param mimeMessage    邮件
     * @param targetFileName 目标附件名
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    public static InputStream getMailContentAttachmentInputstream(MimeMessage mimeMessage, String targetFileName) throws IOException, MessagingException {
        InputStream inputStream = null;
        inputStream = compileMailContentAttachmentInputStream((Part) mimeMessage, targetFileName);
        return inputStream;
    }

    /**
     * 是否有回执
     *
     * @param mimeMessage 邮件
     * @return
     * @throws MessagingException
     */
    public static boolean getReplySign(MimeMessage mimeMessage) throws MessagingException {
        boolean replySign = false;
        String needreply[] = mimeMessage.getHeader("Disposition-Notification-To");
        if (needreply != null) {
            replySign = true;
        }
        return replySign;
    }

    /**
     * 获取邮件的id
     *
     * @param mimeMessage 邮件
     * @return
     * @throws MessagingException
     */
    public static String getMessageID(MimeMessage mimeMessage) throws MessagingException {
//        return mimeMessage.getMessageID();
        return ((POP3Folder) mimeMessage.getFolder()).getUID(mimeMessage);
    }

    /**
     * 判断是否新邮件
     *
     * @param mimeMessage 邮件
     * @return
     * @throws MessagingException
     */
    public static boolean isNew(MimeMessage mimeMessage) throws MessagingException {
        boolean isnew = false;
        Flags flags = ((Message) mimeMessage).getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {
                isnew = true;
                break;
            }
        }
        return isnew;
    }

    /**
     * 解析邮件内容中的附件
     *
     * @return 附件名称列表
     */
    private static List<String> compileMailContentAttachmentName(Part part, List<String> attachments) throws MessagingException, IOException {
        if (part.isMimeType("multipart/*") || part.isMimeType("message/rfc822")) {
            if (part.getContent() instanceof Multipart) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContentAttachmentName(multipart.getBodyPart(i), attachments);
                }
            } else {
                Multipart multipart = new MimeMultipart(new ByteArrayDataSource(part.getInputStream(), "multipart/*"));
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContentAttachmentName(multipart.getBodyPart(i), attachments);
                }
            }
        }
        if (part.getDisposition() != null && part.getDisposition().equals(Part.ATTACHMENT)) {
            // 获取附件
            String filename = part.getFileName();
            if (filename != null) {
                if (filename.contains("=?gb18030?")) {
                    filename = filename.replace("gb18030", "gb2312");
                }
                filename = MimeUtility.decodeText(filename);
                attachments.add(filename);
            }
        }
        return attachments;
    }

    /**
     * 解析邮件内容
     *
     * @param part
     * @param mimeMessage
     * @return
     * @throws Exception
     */
    private static StringBuilder compileMailContent(Part part, MimeMessage mimeMessage) throws Exception {
        StringBuilder mailContent = new StringBuilder();
        String contentType = part.getContentType();
        boolean connName = false;
        if (contentType.contains("name")) {
            connName = true;
        }
        if (part.isMimeType("text/plain") && !connName) {
            String content = parseInputStream(new ByteArrayInputStream(((String) part.getContent()).getBytes(StandardCharsets.UTF_8)), mimeMessage);
            mailContent.append(content);
        } else if (part.isMimeType("text/html") && !connName) {
            String content = parseInputStream(new ByteArrayInputStream(((String) part.getContent()).getBytes(StandardCharsets.UTF_8)), mimeMessage);
            mailContent.append(content);
        } else if (part.isMimeType("multipart/*") || part.isMimeType("message/rfc822")) {
            if (part.getContent() instanceof Multipart) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContent(multipart.getBodyPart(i), mimeMessage);
                }
            } else {
                Multipart multipart = new MimeMultipart(new ByteArrayDataSource(part.getInputStream(), "multipart/*"));
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContent(multipart.getBodyPart(i), mimeMessage);
                }
            }
        }
        return mailContent;
    }


    /**
     * 解析邮件附件输入流
     *
     * @param part           邮件体
     * @param targetFileName 目标附件名称
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    private static InputStream compileMailContentAttachmentInputStream(Part part, String targetFileName) throws MessagingException, IOException {
        if (part.isMimeType("multipart/*") || part.isMimeType("message/rfc822")) {
            if (part.getContent() instanceof Multipart) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContentAttachmentInputStream(multipart.getBodyPart(i), targetFileName);
                }
            } else {
                Multipart multipart = new MimeMultipart(new ByteArrayDataSource(part.getInputStream(), "multipart/*"));
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContentAttachmentInputStream(multipart.getBodyPart(i), targetFileName);
                }
            }
        }
        InputStream inputStream = null;
        if (part.getDisposition() != null && part.getDisposition().equals(Part.ATTACHMENT)) {
            // 获取附件
            String filename = part.getFileName();
            if (filename != null) {
                if (filename.contains("=?gb18030?")) {
                    filename = filename.replace("gb18030", "gb2312");
                }
                filename = MimeUtility.decodeText(filename);
                if (filename.equals(targetFileName)) {
                    inputStream = part.getInputStream();
                }
            }
        }
        return inputStream;
    }

    /**
     * 解析输入流
     *
     * @param is
     * @param mimeMessage
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    private static String parseInputStream(InputStream is, MimeMessage mimeMessage) throws IOException, MessagingException {
        StringBuffer str = new StringBuffer();
        byte[] readByte = new byte[1024];
        int count;
        try {
            while ((count = is.read(readByte)) != -1) {
                if (TranCharsetutils.parseContentTypeCharset(mimeMessage.getContentType()) == null) {
                    str.append(new String(readByte, 0, count, "GBK"));
                } else {
                    str.append(new String(readByte, 0, count, TranCharsetutils.parseContentTypeCharset(mimeMessage.getContentType())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
