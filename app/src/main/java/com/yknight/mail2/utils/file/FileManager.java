package com.yknight.mail2.utils.file;

import android.content.Context;
import android.text.TextUtils;

import com.lodz.android.corekt.anko.AnkoStorageKt;
import com.lodz.android.corekt.utils.FileUtils;
import com.yknight.mail2.App;

import java.io.File;

import kotlin.Pair;

/**
 * 文件管理
 * Created by zhouL on 2016/12/26.
 */
public class FileManager {

    private FileManager() {
    }

    /**
     * 存储卡是否可用
     */
    private static boolean isStorageCanUse = false;
    /**
     * app主文件夹路径
     */
    private static String mAppFolderPath = "";
    /**
     * 缓存路径
     */
    private static String mCacheFolderPath = "";
    /**
     * 下载路径
     */
    private static String mDownloadFolderPath = "";
    /**
     * 内容路径
     */
    private static String mContentFolderPath = "";
    /**
     * 崩溃日志路径
     */
    private static String mCrashFolderPath = "";


    public static void init(Context context) {
        if (context == null) {
            context = App.getInstance();
        }
        initPath(context);
        if (isStorageCanUse) {
            initFolder();
        }
    }

    /**
     * 初始化路径
     */
    private static void initPath(Context context) {
        String rootPath = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            File file = context.getExternalFilesDir("");
            if (file != null) {
                rootPath = file.getAbsolutePath();
            }
        }
        if (TextUtils.isEmpty(rootPath)) {
            Pair<String, String> storagePathPair = AnkoStorageKt.getStoragePath(context);// 先获取内置存储路径
            rootPath = storagePathPair.getFirst(); // 先获取内置存储路径
            if (TextUtils.isEmpty(rootPath)) {// 内置为空再获取外置
                rootPath = storagePathPair.getSecond();
            }
        }
        if (TextUtils.isEmpty(rootPath)) {// 没有存储卡
            isStorageCanUse = false;
            return;
        }
        // 成功获取到存储路径
        isStorageCanUse = true;
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }
        mAppFolderPath = rootPath + "Snxun" + File.separator + "Mail" + File.separator;// 主文件夹路径
        mCacheFolderPath = mAppFolderPath + "Cache" + File.separator;// 缓存路径
        mDownloadFolderPath = mAppFolderPath + "Download" + File.separator;// 下载路径
        mContentFolderPath = mAppFolderPath + "Content" + File.separator;// 内容路径
        mCrashFolderPath = mAppFolderPath + "Crash" + File.separator;// 崩溃日志路径
    }

    /**
     * 初始化文件夹
     */
    private static void initFolder() {
        try {
            FileUtils.createFolder(mAppFolderPath);// 主文件夹路径
            FileUtils.createFolder(mCacheFolderPath);// 缓存路径
            FileUtils.createFolder(mDownloadFolderPath);// 下载路径
            FileUtils.createFolder(mContentFolderPath);// 内容路径
            FileUtils.createFolder(mCrashFolderPath);// 崩溃日志路径
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 存储是否可用
     */
    public static boolean isStorageCanUse() {
        return isStorageCanUse;
    }

    /**
     * 获取app主文件夹路径
     */
    public static String getAppFolderPath() {
        return fixPath(mAppFolderPath);
    }

    /**
     * 获取缓存路径
     */
    public static String getCacheFolderPath() {
        return fixPath(mCacheFolderPath);
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadFolderPath() {
        return fixPath(mDownloadFolderPath);
    }

    /**
     * 获取内容路径
     */
    public static String getContentFolderPath() {
        return fixPath(mContentFolderPath);
    }

    /**
     * 获取崩溃日志路径
     */
    public static String getCrashFolderPath() {
        return fixPath(mCrashFolderPath);
    }

    /**
     * 修复文件夹路径
     *
     * @param path 文件夹路径
     */
    private static String fixPath(String path) {
        if (TextUtils.isEmpty(path)) {
            // 路径为空说明未初始化
            init(null);
        }
        if (isStorageCanUse && !FileUtils.isFileExists(path)) {
            //存储可用 && 路径下的文件夹不存在 说明文件夹被删除
            initFolder();
        }
        return path;
    }
}
