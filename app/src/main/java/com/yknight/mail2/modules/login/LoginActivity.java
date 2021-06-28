package com.yknight.mail2.modules.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lodz.android.corekt.utils.ToastUtils;
import com.lodz.android.pandora.base.activity.AbsActivity;
import com.lodz.android.pandora.rx.subscribe.observer.ProgressObserver;
import com.lodz.android.pandora.rx.utils.RxUtils;
import com.yknight.mail2.App;
import com.yknight.mail2.R;
import com.yknight.mail2.modules.main.MainActivity;
import com.yknight.mail2.utils.db.DbFactory;
import com.yknight.mail2.utils.mail.MailHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import jakarta.mail.Message;

/**
 * @ProjectName : SXMail
 * @Author : yKnight
 * @Time : 2021/3/1
 * @Description : 登录页
 */
public class LoginActivity extends AbsActivity {

    private static final String TAG = "loginActivity";
    /**
     * 邮箱输入框
     */
    @BindView(R.id.mail_account_ed)
    EditText mAccountEd;
    /**
     * 密码输入框
     */
    @BindView(R.id.mail_password_ed)
    EditText mPasswordEd;
    /**
     * 注册按钮
     */
    @BindView(R.id.sign_in_btn)
    Button mSignInBtn;
    /**
     * 登录按钮
     */
    @BindView(R.id.login_in_btn)
    Button mLoginInBtn;

    /**
     * 账号
     */
    private String mAccount = "";
    /**
     * 密码
     */
    private String mPassword = "";

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        mAccount = "";
        mPassword = "";
        super.onResume();
    }

    @Override
    protected void setListeners() {
        mLoginInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkAccountAndPassword()) {
                    return;
                }
                MailHelper.mailLogin(mAccount, mPassword)
                        .compose(RxUtils.ioToMainObservable())
                        .subscribe(new ProgressObserver<Boolean>() {
                            @Override
                            public void onPgNext(Boolean aBoolean) {
                                ToastUtils.showShort(getContext(), "登录成功");
                                getEmailCatalog();
                            }

                            @Override
                            public void onPgError(@NotNull Throwable throwable, boolean b) {
                                ToastUtils.showShort(getContext(), R.string.email_login_error);
                            }
                        }.create(getContext(), R.string.email_login_is_loading, false));
            }
        });
    }

    @Override
    protected void endCreate() {
        super.endCreate();
        //测试方便,-----------------------直接登录
        if (checkAccountAndPassword()) {
            MailHelper.mailLogin(mAccount, mPassword)
                    .compose(RxUtils.ioToMainObservable())
                    .subscribe(new ProgressObserver<Boolean>() {
                        @Override
                        public void onPgNext(Boolean aBoolean) {
                            ToastUtils.showShort(getContext(), "登录成功");
                            getEmailCatalog();
                        }

                        @Override
                        public void onPgError(@NotNull Throwable throwable, boolean b) {
                            ToastUtils.showShort(getContext(), R.string.email_login_error);
                        }
                    }.create(getContext(), R.string.email_login_is_loading, false));
        }
    }


    /**
     * 检验邮箱账号和密码
     *
     * @return true:检验通过   false:检验失败
     */
    private boolean checkAccountAndPassword() {
        mAccount = mAccountEd.getText().toString().trim();
        mPassword = mPasswordEd.getText().toString().trim();
        //账号为空
        if (TextUtils.isEmpty(mAccount)) {
            ToastUtils.showShort(getContext(), getString(R.string.account_cant_empty));
            return false;
        }
        //密码为空
        if (TextUtils.isEmpty(mPassword)) {
            ToastUtils.showShort(getContext(), getString(R.string.password_cant_empty));
            return false;
        }
        //邮箱格式
        if (!MailHelper.checkEmailFormat(mAccount)) {
            ToastUtils.showShort(getContext(), getString(R.string.email_format_error));
            return false;
        }
        return true;
    }


    /**
     * 获取邮箱目录列表
     */
    private void getEmailCatalog() {
        MailHelper.getEmailCatalog()
                .compose(RxUtils.ioToMainObservable())
                .subscribe(new ProgressObserver<Boolean>() {
                    @Override
                    public void onPgNext(Boolean aBoolean) {
                        ToastUtils.showShort(getContext(), "获取邮箱目录成功");

                        //直接获取收件箱邮件
                        getMessageByCatalogAndSaveMessage2Db("INBOX");
                    }

                    @Override
                    public void onPgError(@NotNull Throwable throwable, boolean b) {
                        ToastUtils.showShort(getContext(), getString(R.string.get_message_catalog_error));
                    }
                }.create(getContext(), getString(R.string.getting_message_catalog), false));
    }


    /**
     * 获取指定目录邮件并保存至数据库中
     *
     * @param catalog 目录名称
     */
    private void getMessageByCatalogAndSaveMessage2Db(String catalog) {
        MailHelper.getMessageByCatalog(catalog)
                .flatMap(new Function<Message[], ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Message[] messages) throws Throwable {
                        return DbFactory.create().setMessageList(messages)
                                .rx();
                    }
                })
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Throwable {
                        try {
                            App.sFolder.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    }
                })
                .compose(RxUtils.ioToMainObservable())
                .subscribe(new ProgressObserver<Boolean>() {
                    @Override
                    public void onPgNext(Boolean aBoolean) {
                        MainActivity.start(getContext());
                        finish();
                    }

                    @Override
                    public void onPgError(@NotNull Throwable throwable, boolean b) {
                        ToastUtils.showShort(getContext(), getString(R.string.get_message_error));
                    }
                }.create(getContext(), getString(R.string.getting_messages), false));
    }


}
