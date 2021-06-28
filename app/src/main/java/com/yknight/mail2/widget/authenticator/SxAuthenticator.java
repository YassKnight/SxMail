package com.yknight.mail2.widget.authenticator;


import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

/**
 * @ProjectName: SXMail
 * @Package: com.snxun.sxmail.widget.authenticator
 * @ClassName: SxAuthenticator
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/5/14
 * @description: 密码验证器
 */
public class SxAuthenticator extends Authenticator {
    private String mAccount;
    private String mPassword;

    public SxAuthenticator(String account, String password) {
        this.mAccount = account;
        this.mPassword = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mAccount, mPassword);
    }
}
