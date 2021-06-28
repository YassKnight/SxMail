package com.yknight.mail2.utils.db;

/**
 * 数据工厂
 */
public class DbFactory {

    private DbFactory() {
    }

    public static DbHelper create() {
        return new GreenDaoImpl();
    }
}
