package com.yknight.mail2;

import android.content.Context;

import com.lodz.android.pandora.base.application.BaseApplication;
import com.snxun.mp.greendaolib.GreenDaoManager;
import com.yknight.mail2.bean.MailInfoBean;
import com.yknight.mail2.utils.file.FileManager;

import jakarta.mail.Folder;
import jakarta.mail.Session;
import jakarta.mail.Store;

/**
 * @ProjectName: Mail2
 * @Package: com.yknight.mail2
 * @ClassName: App
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/6/3
 * @description:
 */
public class App extends BaseApplication {
    /**
     * 邮箱信息
     */
    public static MailInfoBean sMailInfoBean = new MailInfoBean();
    /**
     * 发信会话
     */
    public static Session sSendSession = null;
    /**
     * 收信会话
     */
    public static Session sReceiveSession = null;
    /**
     * 收信store
     * 由于收信需要保存信息到数据库中,但是从服务器上面获取数据完关闭store和folder会导致在保存数据时,读取folder中的message时报错(folder关闭)
     * 所以定义成全局变量,等待数据保存结束,在进行关闭
     */
    public static Store sStore = null;
    /**
     * 邮件文件夹folder
     */
    public static Folder sFolder = null;

    public static App getInstance() {
        return (App) App.get();
    }

    @Override
    public void onExit() {
        //退出时,清空信息
        sSendSession = null;
        sReceiveSession = null;
        sMailInfoBean = null;
        System.exit(0);
    }

    @Override
    public void onStartCreate() {
        //配置统一的标题栏样式
        configTitleBarLayout();

        FileManager.init(this);
        initDb(getApplicationContext());// 初始化数据库
    }

    /**
     * 初始化数据库
     */
    private void initDb(Context context) {
        GreenDaoManager.get().init(context);
        GreenDaoManager.get().setPrintLog(true);
    }

    /**
     * 配置标题栏
     */
    private void configTitleBarLayout() {
        getBaseLayoutConfig().getTitleBarLayoutConfig().setBackgroundColor(R.color.color_1a83d4);
        getBaseLayoutConfig().getTitleBarLayoutConfig().setTitleTextColor(R.color.white);
    }

}
