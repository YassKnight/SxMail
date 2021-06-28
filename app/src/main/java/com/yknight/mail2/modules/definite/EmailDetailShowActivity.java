package com.yknight.mail2.modules.definite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lodz.android.corekt.utils.ToastUtils;
import com.lodz.android.pandora.base.activity.AbsActivity;
import com.lodz.android.pandora.rx.subscribe.observer.ProgressObserver;
import com.lodz.android.pandora.rx.utils.RxUtils;
import com.yknight.mail2.R;
import com.yknight.mail2.bean.MessageTableBean;
import com.yknight.mail2.utils.file.FileManager;
import com.yknight.mail2.utils.file.IOUtils;
import com.yknight.mail2.utils.mail.MailHelper;
import com.yknight.mail2.utils.mail.MailReceiveUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * @ProjectName: JakartaMail
 * @Package: com.yknight.jakartamail.modules.definite
 * @ClassName: DefiniteActivity
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/5/28
 * @description: 信件细节展示页
 */
public class EmailDetailShowActivity extends AbsActivity {
    private static final String DATA_KEY = "data";
    /**
     * 附件布局
     */
    @BindView(R.id.attach_layout)
    LinearLayout mAttachMentLayout;
    /**
     * 发件人
     */
    @BindView(R.id.from_tv)
    TextView mFrom;
    /**
     * 收件人
     */
    @BindView(R.id.recipients_tv)
    TextView mRecipients;
    /**
     * 发送时间
     */
    @BindView(R.id.time_tv)
    TextView mTime;
    /**
     * 邮件正文
     */
    @BindView(R.id.letter_content_tv)
    TextView mContent;
    /**
     * html内容
     */
    @BindView(R.id.content_wv)
    WebView mContentWebview;

    private MessageTableBean mMessageTableBean = null;
    private List<String> mAttachMentList = null;


    public static void start(Context context, MessageTableBean bean) {
        Intent intent = new Intent(context, EmailDetailShowActivity.class);
        intent.putExtra(DATA_KEY, bean);
        context.startActivity(intent);
    }

    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_definite;
    }


    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        super.findViews(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mMessageTableBean = (MessageTableBean) getIntent().getSerializableExtra(DATA_KEY);
        mAttachMentList = parseAttachMentList();
    }

    @Override
    protected void endCreate() {
        super.endCreate();
        mFrom.setText(mMessageTableBean.from);
        mRecipients.setText(mMessageTableBean.addressee);
        mTime.setText(String.valueOf(mMessageTableBean.sentData));
        if (mMessageTableBean.isHtml) {
            mContent.setVisibility(View.GONE);
            mContentWebview.setVisibility(View.VISIBLE);
            mContentWebview.loadDataWithBaseURL(null, mMessageTableBean.content, "text/html", "utf-8", null);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int scale = dm.densityDpi;
            if (scale == 240) {
                mContentWebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            } else if (scale == 160) {
                mContentWebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
            } else {
                mContentWebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
            }
            mContentWebview.setWebChromeClient(new WebChromeClient());
        } else {
            mContent.setVisibility(View.VISIBLE);
            mContentWebview.setVisibility(View.GONE);
            mContent.setText(mMessageTableBean.content);
        }

        createAttachMentView();
    }


    /**
     * 创建附件视图-----后续优化为自定义附件控件,满足预览图显示
     */
    public void createAttachMentView() {
        if (mAttachMentList != null) {
            for (int i = 0; i < mAttachMentList.size(); i++) {
                Button btn = new Button(getContext());
                btn.setText(mAttachMentList.get(i));
                btn.setTag(mAttachMentList.get(i));
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 158);
                btn.setLayoutParams(params);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //先查找对应邮件
                        getMessageByCatalogAndUid(v.getTag().toString());
                    }
                });
                mAttachMentLayout.addView(btn);
            }
        }
    }

    /**
     * 解析附件列表
     *
     * @return
     */
    public List<String> parseAttachMentList() {
        String attachmentListStr = mMessageTableBean.attachmentList;
        if (attachmentListStr.isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(attachmentListStr, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 查询对应邮件
     *
     * @param attachmentName 附件名称
     */
    public void getMessageByCatalogAndUid(String attachmentName) {
        MailHelper.getMessageByCatalogAndUid(mMessageTableBean.catalog, mMessageTableBean.uid)
                .compose(RxUtils.ioToMainObservable())
                .subscribe(new ProgressObserver<Message>() {
                    @Override
                    public void onPgNext(Message message) {
                        try {
                            downloadAttachment((MimeMessage) message, attachmentName);
                        } catch (IOException | MessagingException e) {
                            e.printStackTrace();
                            ToastUtils.showShort(getContext(), R.string.getting_messages_error);
                        }
                    }

                    @Override
                    public void onPgError(@NotNull Throwable throwable, boolean b) {
                        ToastUtils.showShort(getContext(), R.string.getting_messages_error);
                    }
                }.create(getContext(), R.string.getting_messages, false));
    }


    /**
     * 下载邮件附件
     *
     * @param mimeMessage    邮件
     * @param targetFileName 目标附件名
     */
    public void downloadAttachment(MimeMessage mimeMessage, String targetFileName) throws IOException, MessagingException {
        InputStream inputStream = MailReceiveUtils.getMailContentAttachmentInputstream(mimeMessage, targetFileName);
        if (inputStream == null) {
            ToastUtils.showShort(getContext(), R.string.download_attachment_error);
            return;
        }
        String path = IOUtils.stream2file(inputStream, FileManager.getDownloadFolderPath() + mMessageTableBean.uid + File.separator);
        if (path != null)
            ToastUtils.showShort(getContext(), path);
    }
}
