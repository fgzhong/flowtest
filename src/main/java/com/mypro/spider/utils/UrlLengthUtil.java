package com.mypro.spider.utils;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/23
 */
public class UrlLengthUtil {


    public static String shortenCodeUrl(String longUrl) {
        return shortenCodeUrl(longUrl, 16);
    }

    public static String shortenCodeUrl(String longUrl, int urlLength) {
        if (urlLength < 4 ) {
            urlLength = 8;// defalut length
        }
        StringBuilder sbBuilder = new StringBuilder(urlLength + 2);
        String md5Hex = "";
        int nLen = 0;
        while (nLen < urlLength) {
            // 这个方法是先 md5 再 base64编码 参见
            // https://github.com/ndxt/centit-commons/blob/master/centit-utils/src/main/java/com/centit/support/security/Md5Encoder.java
            md5Hex = Md5Encoder.encodeBase64(md5Hex + longUrl);
            for(int i=0;i<md5Hex.length();i++){
                char c = md5Hex.charAt(i);
                if(c != '/' && c != '+'){
                    sbBuilder.append(c);
                    nLen ++;
                }
                if(nLen == urlLength){
                    break;
                }
            }
        }
        return sbBuilder.toString();
    }


    public static void main(String[] args) {
        System.out.println(shortenCodeUrl("https://list.jd.com/list.html?cat=9987,653,655&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0"));
    }
}
