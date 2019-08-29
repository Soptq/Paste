package soptqs.paste.utils;

import android.content.Context;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

/**
 * Created by S0ptq on 2018/2/16.
 */

public class EncrypUtils {
    public static String encryp(Context context) {
        String output = null;
        String input = AppUtils.getIMEI(context);//input imei
        char[] ch;
        ch = input.toCharArray();
        int len = input.length();
        for (int i = 0; i < len / 2; i++) {
            ch[i] ^= ch[len - 1 - i];
            ch[len - 1 - i] ^= ch[i];
            ch[i] ^= ch[len - 1 - i];
        }
        String input2 = new String(ch);//input2 反转的imei
        try {
            output = AESCrypt.encrypt(input, input2);//密码imei,内容反转的imei
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        assert output != null;
        char[] ch2 = output.toCharArray();
        int len2 = output.length();
        for (int i = 0; i < len2 / 2; i++) {
            ch2[i] ^= ch2[len2 - 1 - i];
            ch2[len2 - 1 - i] ^= ch2[i];
            ch2[i] ^= ch2[len2 - 1 - i];
        }
        return new String(ch2);//反转output
    }

    public static String encryp(Context context, String str) {
        String output = null;
        String input = str;//input imei
        char[] ch;
        ch = input.toCharArray();
        int len = input.length();
        for (int i = 0; i < len / 2; i++) {
            ch[i] ^= ch[len - 1 - i];
            ch[len - 1 - i] ^= ch[i];
            ch[i] ^= ch[len - 1 - i];
        }
        String input2 = new String(ch);//input2 反转的imei
        try {
            output = AESCrypt.encrypt(input, input2);//密码imei,内容反转的imei
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        assert output != null;
        char[] ch2 = output.toCharArray();
        int len2 = output.length();
        for (int i = 0; i < len2 / 2; i++) {
            ch2[i] ^= ch2[len2 - 1 - i];
            ch2[len2 - 1 - i] ^= ch2[i];
            ch2[i] ^= ch2[len2 - 1 - i];
        }
        return new String(ch2);//反转output
    }

    public static Boolean decruptCheck(Context context, String input) {
        Boolean istrue = false;
        String output = null;
        String input1 = AppUtils.getIMEI(context);
        char[] ch = input.toCharArray();
        int len = input.length();
        for (int i = 0; i < len / 2; i++) {
            ch[i] ^= ch[len - 1 - i];
            ch[len - 1 - i] ^= ch[i];
            ch[i] ^= ch[len - 1 - i];
        }
        String input2 = new String(ch);//反转的output
        char[] ch2 = input1.toCharArray();
        int len2 = input1.length();
        for (int i = 0; i < len2 / 2; i++) {
            ch2[i] ^= ch2[len2 - 1 - i];
            ch2[len2 - 1 - i] ^= ch2[i];
            ch2[i] ^= ch2[len2 - 1 - i];
        }
        String input3 = new String(ch2);//反转的imei
        try {
            output = AESCrypt.decrypt(input1, input2);//密码imei,内容反转的output
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (input3.equals(output)) return true;
        return false;
    }
}
