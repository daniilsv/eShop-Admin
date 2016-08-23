package ru.dvs.eshop.admin.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {

    //Получает MD5 хеш строки
    public static String MD5(String input) {
        String result = input;
        if (input != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
                md.update(input.getBytes());
                BigInteger hash = new BigInteger(1, md.digest());
                result = hash.toString(16);
                while (result.length() < 32) { //40 for SHA-1
                    result = "0" + result;
                }
                return result;
            } catch (NoSuchAlgorithmException ignored) {
            }
        }
        return "NULL";
    }

    //Проверяет, является ли строка валидным MD5 хешем
    public static boolean isValidMD5(String s) {
        return s.matches("[a-fA-F0-9]{32}");
    }

}
