package ru.dvs.eshop.admin.utils;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {

    //Получает MD5 хеш строки
    public static String MD5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return "NULL";
    }

    //Проверяет, является ли строка валидным MD5 хешем
    public static boolean isValidMD5(String s) {
        return s.matches("[a-fA-F0-9]{32}");
    }

    @Nullable
    public static String xorIt(String message, String key) {
        try {
            if (message == null || key == null) {
                return null;
            }

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            String newmsg = "";

            for (int i = 0; i < ml; i++) {
                newmsg = newmsg + (char) (mesg[i] ^ keys[i % kl]);
            }
            return newmsg;
        } catch (Exception e) {
            return null;
        }
    }

    public static String compress(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }

    public static String decompress(String str) {
        return new String(Base64.decode(str, Base64.DEFAULT));
    }
}
