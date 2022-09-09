package com.kcb.pay.common;

import com.alibaba.fastjson.JSONObject;
import com.kcb.pay.modules.dto.CardDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class SHA256RSAUtils {


    @Value("${sign.publicKey}")
    String publicKey;

    @Value("${sign.privateKey}")
    String privateKey;

    @Value("${des.key}")
    String deskey;



    public Map<String,Object> getsgin(Object json){
        Map<String,Object> map =new HashMap<>();
        String jsonObject = JSONObject.toJSONString(json);
        log.info("待加密的返回数据：{}",jsonObject);
        //进行3DES加密
        String  value= DesUtils.encode3Des(deskey, jsonObject);
        //加密后进行签名
        String sign = rsa256Sign(value,privateKey,"SHA256withRSA","UTF-8");
        map.put("request",value);
        map.put("signature",sign);
        return map;
    }

    public Object destostring(String request,Class<?> o){
        String desvalue = DesUtils.decode3Des(deskey, request); //解密结果
        log.info("解密后明文：{}",desvalue);
        Object object = JSONObject.parseObject(desvalue, o);
        log.info("对象为：{}",object);
        return object;
    }


    //根据私钥生产签名
    public String rsa256Sign(String content, String privateKey, String signType, String charset) throws MyException {
        log.info("content:" + content);
        try {
            //getPrivateKeyFromPKCS8(); 见下文
            PrivateKey priKey = getPrivateKeyFromPKCS8("RSA", new ByteArrayInputStream(privateKey.getBytes()));
            Signature signature = Signature.getInstance(signType);
            signature.initSign(priKey);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed));
        } catch (Exception var7) {
            throw new MyException("RSAcontent = " + content + "; charset = " + charset, var7);
        }
    }

    public PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins) throws Exception {
        if (ins != null && !StringUtils.isEmpty(algorithm)) {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            byte[] encodedKey = StreamUtil.readText(ins).getBytes();
            encodedKey = Base64.decodeBase64(encodedKey);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } else {
            return null;
        }
    }

    //公钥解密
    public boolean rsaCheckContent(String content, String sign, String signType, String publicKey, String charset) throws MyException {
        log.info("签名方式为RSA");
        log.info("待验证签名为：" + sign);
        log.info("待验证签名内容为：" + content);
        try {
            //getPublicKeyFromX509(); 见下文
            PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));
            Signature signature = Signature.getInstance(signType);
            signature.initVerify(pubKey);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception var7) {
            log.error(var7.getMessage());
            throw new MyException("无效的签名",var7);
        }
    }

    public PublicKey getPublicKeyFromX509(String algorithm, InputStream ins)
            throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = Base64.decodeBase64(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }


}
