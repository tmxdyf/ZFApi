package com.src.cy.zfapi.util;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.src.cy.zfapi.ZFApi;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESUtil {

    public static String getDesKey() {
        if (TextUtils.isEmpty(ZFApi.getDesKey())) {
            throw new NullPointerException();
        }
        return ZFApi.getDesKey();
    }


    /**
     * 解密
     *
     * @param message 需要解密的字符串
     * @return 返回解密后字符串
     */
    public static String decryptDoNet(String message) {
        byte[] bytesrc = Base64.decode(message.getBytes(), Base64.DEFAULT);
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(getDesKey().getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(getDesKey().getBytes("UTF-8"));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] retByte = cipher.doFinal(bytesrc);
            return new String(retByte);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DES", "解密异常：" + message);
            return message;
        }

    }

    /**
     * 加密
     *
     * @param message 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String encryptAsDoNet(String message) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(getDesKey().getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(getDesKey().getBytes("UTF-8"));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptbyte = cipher.doFinal(message.getBytes());
            return new String(Base64.encode(encryptbyte, Base64.DEFAULT));
        } catch (Exception e) {
            return message;
        }
    }


}
