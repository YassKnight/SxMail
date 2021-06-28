package com.yknight.mail2.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.lodz.android.pandora.widget.dialog.BaseCenterDialog;
import com.yknight.mail2.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 确认弹框
 */
public class CheckDialog extends BaseCenterDialog {

    /**
     * 标题
     */
    @BindView(R.id.title_txt)
    TextView mTitleTv;
    /**
     * 内容
     */
    @BindView(R.id.content_msg)
    TextView mContentMsg;
    /**
     * 确认按钮
     */
    @BindView(R.id.positive_btn)
    TextView mPositiveBtn;
    /**
     * 取消按钮
     */
    @BindView(R.id.negative_btn)
    TextView mNegativeBtn;

    /**
     * 标题
     */
    private String mTitle = "";
    /**
     * 标题颜色
     */
    @ColorRes
    private int mTitleColor = 0;
    /**
     * 内容
     */
    private String mContent = "";
    /**
     * 内容颜色
     */
    @ColorRes
    private int mContentColor = 0;

    /**
     * 确定
     */
    private String mPositiveText = "";
    /**
     * 确定颜色
     */
    @ColorRes
    private int mPositiveColor = 0;
    /**
     * 确定监听器
     */
    private OnClickListener mPositiveListener = null;

    /**
     * 取消
     */
    private String mNegativeText = "";
    /**
     * 取消颜色
     */
    @ColorRes
    private int mNegativeColor = 0;
    /**
     * 取消监听器
     */
    private OnClickListener mNegativeListener = null;

    public CheckDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_check;
    }

    @Override
    protected void findViews() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mTitleTv.setText(mTitle);
        if (mTitleColor != 0) {
            mTitleTv.setTextColor(ContextCompat.getColor(getContext(), mTitleColor));
        }
        mContentMsg.setText(mContent);
        if (mContentColor != 0) {
            mContentMsg.setTextColor(ContextCompat.getColor(getContext(), mContentColor));
        }

        if (!TextUtils.isEmpty(mPositiveText)) {
            if (mPositiveColor != 0) {
                mPositiveBtn.setTextColor(ContextCompat.getColor(getContext(), mPositiveColor));
            }
            setBtn(mPositiveBtn, mPositiveText, mPositiveListener);
        }

        if (!TextUtils.isEmpty(mNegativeText)) {
            if (mNegativeColor != 0) {
                mNegativeBtn.setTextColor(ContextCompat.getColor(getContext(), mNegativeColor));
            }
            setBtn(mNegativeBtn, mNegativeText, mNegativeListener);
        }
    }

    /**
     * 设置标题文本
     *
     * @param title 标题
     */
    public void setTitleMsg(@NonNull String title) {
        mTitle = title;
    }

    /**
     * 设置标题文本
     *
     * @param resId 标题资源id
     */
    public void setTitleMsg(@StringRes int resId) {
        mTitle = getContext().getString(resId);
    }

    /**
     * 设置标题文本
     *
     * @param title 标题
     * @param color 文字颜色
     */
    public void setTitleMsg(@NonNull String title, @ColorRes int color) {
        mTitle = title;
        mTitleColor = color;
    }

    /**
     * 设置内容文本
     *
     * @param contentMsg 内容文本
     */
    public void setContentMsg(@NonNull String contentMsg) {
        mContent = contentMsg;
    }

    /**
     * 设置内容文本
     *
     * @param resId 内容文本资源id
     */
    public void setContentMsg(@StringRes int resId) {
        mContent = getContext().getString(resId);
    }

    /**
     * 设置内容文本
     *
     * @param contentMsg 内容文本
     * @param color      文字颜色
     */
    public void setContentMsg(@NonNull String contentMsg, @ColorRes int color) {
        mContent = contentMsg;
        mContentColor = color;
    }

    /**
     * 设置确认按钮文本
     *
     * @param positiveText 确认文本
     * @param listener     点击监听
     */
    public void setPositiveText(String positiveText, OnClickListener listener) {
        mPositiveText = positiveText;
        mPositiveListener = listener;
    }

    /**
     * 设置确认按钮文本
     *
     * @param resId    确认文本资源id
     * @param listener 点击监听
     */
    public void setPositiveText(@StringRes int resId, OnClickListener listener) {
        mPositiveText = getContext().getString(resId);
        mPositiveListener = listener;
    }

    /**
     * 设置确认按钮文本
     *
     * @param positiveText 确认文本
     * @param color        文字颜色
     * @param listener     点击监听
     */
    public void setPositiveText(String positiveText, @ColorRes int color, OnClickListener listener) {
        mPositiveText = positiveText;
        mPositiveListener = listener;
        mPositiveColor = color;
    }

    /**
     * 设置取消按钮文本
     *
     * @param negativeText 取消文本
     * @param listener     点击监听
     */
    public void setNegativeText(String negativeText, OnClickListener listener) {
        mNegativeText = negativeText;
        mNegativeListener = listener;
    }

    /**
     * 设置取消按钮文本
     *
     * @param resId    取消文本资源id
     * @param listener 点击监听
     */
    public void setNegativeText(@StringRes int resId, OnClickListener listener) {
        mNegativeText = getContext().getString(resId);
        mNegativeListener = listener;
    }

    /**
     * 设置取消按钮文本
     *
     * @param negativeText 取消文本
     * @param color        文字颜色
     * @param listener     点击监听
     */
    public void setNegativeText(String negativeText, @ColorRes int color, OnClickListener listener) {
        mNegativeText = negativeText;
        mNegativeColor = color;
        mNegativeListener = listener;
    }

    /**
     * 设置按钮
     *
     * @param btn      控件
     * @param test     文本
     * @param listener 监听器
     */
    private void setBtn(TextView btn, String test, final OnClickListener listener) {
        btn.setVisibility(View.VISIBLE);
        btn.setText(test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(getDialogInterface(), 0);
                }
            }
        });
    }
}
