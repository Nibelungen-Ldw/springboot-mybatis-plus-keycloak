package com.wistron.keycloak.utils;


import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.management.openmbean.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * @author Nieblungen_Liu
 */
public class AESUtils {
    /**生成key，作为加密和解密密钥且只有密钥相同解密加密才会成功
     * @return
     */
    public static Key createKey() {

        try {
            // 生成key
            KeyGenerator keyGenerator;
            //构造密钥生成器，指定为AES算法,不区分大小写
            keyGenerator = KeyGenerator.getInstance("AES");
            //生成一个128位的随机源,根据传入的字节数组
            keyGenerator.init(128);
            //产生原始对称密钥
            SecretKey secretKey = keyGenerator.generateKey();
            //获得原始对称密钥的字节数组
            byte[] keyBytes = secretKey.getEncoded();
            // key转换,根据字节数组生成AES密钥
            Key key = new SecretKeySpec(keyBytes, "AES");
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**加密
     * @param context 需要加密的明文
     * @param key 加密用密钥
     * @return
     */
    public static byte[] jdkAES(String context, Key key) {
        try {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, key);
            //将加密并编码后的内容解码成字节数组
            byte[] result = cipher.doFinal(context.getBytes());

//            System.out.println("jdk aes:" + Base64.encode(result));

            return result;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | java.security.InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** 解密
     * @param result 加密后的密文
     * @param key 解密用密钥
     * @return
     */
    public static byte[] decrypt(byte[] result, Key key) {
        Cipher cipher;
        try {

            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(result);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | java.security.InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {

            e.printStackTrace();
            return null;
        }

//        System.out.println("jdk aes desrypt:" + new String(result));
    }
}
