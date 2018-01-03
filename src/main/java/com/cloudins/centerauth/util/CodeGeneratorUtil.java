package com.cloudins.centerauth.util;

/**
 * 密码生成采用随机生成8位字符串
 *Created by gpl on 12.18
 */
public class CodeGeneratorUtil {
    public static String generatorValue() {
        String result = "";
        for (int i = 0; i < 8; i++) {
           int intVale = (int)(Math.random()*26 + 97);
           result = result + (char)intVale;
        }
        return result;
    }

}
