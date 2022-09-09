package com.kcb.pay.common;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;


public class DesUtils {

    public static String  encode3Des(String key,String srcStr){
        //hex(val); 见下文
        byte[] keybyte = hex(key);
        byte[] src = srcStr.getBytes();
        try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
            //加密
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(1, deskey);

            String pwd = Base64.encodeBase64String(c1.doFinal(src));
//           return c1.doFinal(src);//在单一方面的加密或解密
            return pwd;
        } catch (java.security.NoSuchAlgorithmException e1) {
            // TODO: handle exception
            e1.printStackTrace();
        }catch(javax.crypto.NoSuchPaddingException e2){
            e2.printStackTrace();
        }catch(Exception e3){
            e3.printStackTrace();
        }
        return null;
    }


    public static byte[] hex(String key) {
        String f = DigestUtils.md5Hex(key);
        byte[] bkeys = (new String(f)).getBytes();
        byte[] enk = new byte[24];
        for(int i = 0; i < 24; ++i) {
            enk[i] = bkeys[i];
        }
        return enk;
    }


    public static String decode3Des(String key, String desStr) {
        Base64 base64 = new Base64();
        byte[] keybyte = hex(key);
        byte[] src = base64.decode(desStr);
        try {
            SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(2, deskey);
            byte[] pwd = c1.doFinal(src);
            return new String(pwd);
        } catch (NoSuchAlgorithmException var8) {
            var8.printStackTrace();
        } catch (NoSuchPaddingException var9) {
            var9.printStackTrace();
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        return null;
    }


}
