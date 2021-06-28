package com.yknight.mail2.modules.splash;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lodz.android.corekt.anko.AnkoAppKt;
import com.lodz.android.corekt.anko.AnkoArrayKt;
import com.lodz.android.corekt.anko.AnkoIntentKt;
import com.lodz.android.corekt.utils.ToastUtils;
import com.lodz.android.pandora.base.activity.AbsActivity;
import com.yknight.mail2.App;
import com.yknight.mail2.R;
import com.yknight.mail2.modules.login.LoginActivity;
import com.yknight.mail2.widget.dialog.CheckDialog;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * @ProjectName: SXMail
 * @Package: com.snxun.sxmail.modules.splash
 * @ClassName: SplashActivity
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/5/13
 */
@RuntimePermissions
public class SplashActivity extends AbsActivity {

    /**
     * 标题
     */
    @BindView(R.id.splash_title)
    TextView mTitleTv;
    /**
     * 进度条
     */
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    /**
     * 进度提示
     */
    @BindView(R.id.msg)
    TextView mMsgTv;
    /**
     * 版本号
     */
    @BindView(R.id.version)
    TextView mVersionTv;
    /**
     * 版权
     */
    @BindView(R.id.copy_right)
    TextView mCopyRightTv;

    /**
     * 是否拒绝权限后重新授予
     */
    private boolean isRequestPermissionAgain = false;


    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setView();
    }

    /**
     * 设置初始页面
     */
    private void setView() {
        mProgressBar.setVisibility(View.GONE);
        mMsgTv.setVisibility(View.GONE);
        mVersionTv.setText(getString(R.string.splash_version, AnkoAppKt.getVersionName(getContext())));
    }

    /**
     * 显示加载提示语
     *
     * @param msg 提示语
     */
    public void showProgressView(String msg) {
        mProgressBar.setVisibility(View.VISIBLE);
        mMsgTv.setVisibility(View.VISIBLE);
        mMsgTv.setText(msg);
    }

    @Override
    protected void initData() {
        super.initData();
        if (!isFirstStartApp()) {// 不是第一次启动APP直接跳转MainActivity
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0以上的手机对权限进行动态申请
            showProgressView(getString(R.string.splash_request_permission));
            SplashActivityPermissionsDispatcher.requestPermissionWithPermissionCheck(this);//申请权限
        } else {
            init();
        }
    }

    private void init() {
        LoginActivity.start(getContext());
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);//将回调交给代理类处理
    }

    /**
     * 权限申请成功
     */
    @NeedsPermission({
            Manifest.permission.READ_EXTERNAL_STORAGE,// 存储卡读写
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 存储卡读写
    })
    protected void requestPermission() {
        if (!AnkoAppKt.isPermissionGranted(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }
        if (!AnkoAppKt.isPermissionGranted(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }
        init();
        if (isRequestPermissionAgain) {
            onResume();
        }
    }

    /**
     * 被拒绝
     */
    @OnPermissionDenied({
            Manifest.permission.READ_EXTERNAL_STORAGE,// 存储卡读写
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 存储卡读写
    })
    protected void onDenied() {
        SplashActivityPermissionsDispatcher.requestPermissionWithPermissionCheck(this);//申请权限
    }

    /**
     * 用户拒绝后再次申请前告知用户为什么需要该权限
     */
    @OnShowRationale({
            Manifest.permission.READ_EXTERNAL_STORAGE,// 存储卡读写
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 存储卡读写

    })
    protected void showRationaleBeforeRequest(PermissionRequest request) {
        request.proceed();//请求权限
    }

    /**
     * 被拒绝并且勾选了不再提醒
     */
    @OnNeverAskAgain({
            Manifest.permission.READ_EXTERNAL_STORAGE,// 存储卡读写
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 存储卡读写

    })
    protected void onNeverAskAgain() {
        ToastUtils.showShort(getContext(), R.string.splash_check_permission_tips);
        showPermissionCheckDialog();
        AnkoIntentKt.goAppDetailSetting(this);
    }

    /**
     * 显示权限核对弹框
     */
    private void showPermissionCheckDialog() {
        CheckDialog dialog = new CheckDialog(getContext());
        dialog.setContentMsg(R.string.splash_check_permission_title);
        dialog.setPositiveText(R.string.splash_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isRequestPermissionAgain = true;
                SplashActivityPermissionsDispatcher.requestPermissionWithPermissionCheck(SplashActivity.this);//申请权限
                dialog.dismiss();
            }
        });
        dialog.setNegativeText(R.string.splash_unconfirmed, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AnkoIntentKt.goAppDetailSetting(getContext());
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ToastUtils.showShort(getContext(), R.string.splash_check_permission_cancel);
                App.getInstance().exit();
            }
        });
        dialog.show();
    }

    @Override
    protected boolean onPressBack() {
        App.getInstance().exit();
        return true;
    }


    /**
     * 是否第一次启动Activity
     */
    private boolean isFirstStartApp() {
        // 获取activity任务栈
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return true;
        }
        List<ActivityManager.RunningTaskInfo> list = manager.getRunningTasks(1);
        if (AnkoArrayKt.isNullOrEmpty(list)) {
            return true;
        }
        ActivityManager.RunningTaskInfo info = list.get(0);
        if (info == null) {
            return true;
        }
        ComponentName baseActivity = info.baseActivity;
        if (baseActivity == null) {
            return true;
        }
        return baseActivity.getClassName().equals(SplashActivity.class.getName());
    }
}
