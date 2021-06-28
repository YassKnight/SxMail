package com.yknight.mail2.utils.mail;

import com.yknight.mail2.bean.CatalogNameBean;
import com.yknight.mail2.config.Constant;

import java.util.ArrayList;
import java.util.List;

import jakarta.mail.Folder;

/**
 * @ProjectName: Mail2
 * @Package: com.yknight.mail2.utils.mail
 * @ClassName: CatalogUtils
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/6/4
 * @description: 目录工具类
 */
public class CatalogUtils {
    /**
     * 获取邮箱目录集合
     *
     * @param folders 所有文件
     * @return
     */
    public static List<String> getCatalogFormFolder(Folder[] folders) {
        if (folders == null) {
            return null;
        }
        List<String> catalogList = new ArrayList<>();
        for (Folder folder : folders) {
            catalogList.add(folder.getName());
        }
        return catalogList;
    }


    /**
     * 获取邮箱目录字典------暂时性实现,后续要考虑动态的方式应对不同的邮箱登录
     *
     * @param catalogList 目录集合
     * @return
     */
    public static List<CatalogNameBean> getCatalogNameBeanList(List<String> catalogList) {
        if (catalogList == null) {
            return null;
        }
        List<CatalogNameBean> catalogNameBeanList = new ArrayList<>();

        for (String catalog : catalogList) {
            CatalogNameBean bean = new CatalogNameBean();
            bean.catalogEnName = catalog;
            bean.catalogZhName = getCatalogZhNameFromEnName(catalog);
            catalogNameBeanList.add(bean);
        }

        return catalogNameBeanList;
    }

    /**
     * 通过英文目录名称获取中文目录名称
     *
     * @param EnName 英文名称
     * @return
     */
    public static String getCatalogZhNameFromEnName(String EnName) {
        String ZhName = "";
        switch (EnName) {
            case Constant.CatalogEnName.INBOX:
                ZhName = Constant.CatalogZhName.INBOX;
                break;
            case Constant.CatalogEnName.SENTMESSAGES:
                ZhName = Constant.CatalogZhName.SENTMESSAGES;
                break;
            case Constant.CatalogEnName.DRAFTS:
                ZhName = Constant.CatalogZhName.DRAFTS;
                break;
            case Constant.CatalogEnName.DELETED_MESSAGES:
                ZhName = Constant.CatalogZhName.DELETED_MESSAGES;
                break;
            case Constant.CatalogEnName.JUNK:
                ZhName = Constant.CatalogZhName.JUNK;
                break;
            default:
                ZhName = EnName;
                break;

        }
        return ZhName;
    }

}
