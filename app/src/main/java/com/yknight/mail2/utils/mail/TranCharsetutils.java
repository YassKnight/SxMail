package com.yknight.mail2.utils.mail;

/**
 * @ProjectName: Mail2
 * @Package: com.yknight.mail2.utils.mail
 * @ClassName: TranCharsetutils
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/6/17
 * @description: 字符集转换工具
 */
public class TranCharsetutils {

    /**
     * 将字符串编码格式转成GB2312
     *
     * @param str 要转换的字符串
     * @return
     */
    public static String tranEncodeTOGB(String str) {
        try {
            return new String(str.getBytes(getEncoding(str)), "GBK");
        } catch (java.io.IOException ex) {
            return null;
        }
    }


    /**
     * 判断字符串的编码
     *
     * @param str 要判断的字符串
     * @return
     */
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception ignored) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception ignored) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception ignored) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception ignored) {
        }
        return "";
    }


    /**
     * 判断输入字符是否为gb2312的编码格式
     *
     * @param c 输入字符
     * @return 如果是gb2312返回真，否则返回假
     */
    public static boolean isGB2312(char c) {
        String sCh = Character.toString(c);
        try {
            byte[] bb = sCh.getBytes("gb2312");
            if (bb.length > 1) {
                return true;
            }
        } catch (java.io.UnsupportedEncodingException ex) {
            return false;
        }
        return false;
    }

    /**
     * 解析字符集编码
     *
     * @param contentType 邮件内容类型
     * @return
     */
    public static String parseContentTypeCharset(String contentType) {
        if (!contentType.contains("charset")) {
            return null;
        }
        if (contentType.contains("gbk")) {
            return "GBK";
        } else if (contentType.contains("GB2312") || contentType.contains("gb18030")) {
            return "gb2312";
        } else {
            String sub = contentType.substring(contentType.indexOf("charset") + 8).replace("\"", "");
            if (sub.contains(";")) {
                return sub.substring(0, sub.indexOf(";"));
            } else {
                return sub;
            }
        }
    }

}
