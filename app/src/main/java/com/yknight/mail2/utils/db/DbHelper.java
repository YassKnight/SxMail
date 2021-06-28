package com.yknight.mail2.utils.db;


import com.yknight.mail2.bean.MessageTableBean;

import java.util.List;

import jakarta.mail.Message;

/**
 * 数据库业务接口
 */
public interface DbHelper {

    /**
     * 保存邮箱对应文件目录下的邮件列表
     *
     * @param array 服务端获取的某一个文件目录下的邮件列表
     * @return
     */
    DbAgent<Boolean> setMessageList(Message[] array);

    /**
     * 获取所有邮件
     *
     * @return
     */
    DbAgent<List<MessageTableBean>> getAllMessageList();

    /**
     * 根据目录获取对应目录下的邮件
     *
     * @return
     */
    DbAgent<List<MessageTableBean>> getMessageListByCatalog(String Catalog);

    /**
     * 根据uid获取对应邮件
     *
     * @param uid
     * @return
     */
    DbAgent<MessageTableBean> getMessageByUid(String uid);

}
