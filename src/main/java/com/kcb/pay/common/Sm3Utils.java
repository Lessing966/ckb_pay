package com.kcb.pay.common;

import java.io.UnsupportedEncodingException;

public class Sm3Utils {

    /**
     * sm3计算后进行16进制转换
     *
     * @param data
     *            待计算的数据
     * @param encoding
     *            编码
     * @return 计算结果
     */

//    public static String sm3X16Str(String data, String encoding) {
//        byte[] bytes = sm3(data, encoding);
//        StringBuilder sm3StrBuff = new StringBuilder();
//        for (int i = 0; i < bytes.length; i++) {
//            if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
//                sm3StrBuff.append("0").append(
//                        Integer.toHexString(0xFF & bytes[i]));
//            } else {
//                sm3StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
//            }
//        }
//        return sm3StrBuff.toString();
//    }

    /**
     * sm3计算
     *
     * @param datas
     *            待计算的数据
     * @param encoding
     *            字符集编码
     * @return
     */
//    private static byte[] sm3(String datas, String encoding) {
//        try {
//            return sm3(datas.getBytes(encoding));
//        } catch (UnsupportedEncodingException e) {
//            LogUtil.writeErrorLog("SM3计算失败", e);
//            return null;
//        }
//    }

    /**
     * SM3计算.
     *
     * @param datas
     *            待计算的数据
     * @return 计算结果
     */
//    private static byte[] sm3(byte[] data) {
//        SM3Digest sm3 = new SM3Digest();
//        sm3.update(data, 0, data.length);
//        byte[] result = new byte[sm3.getDigestSize()];
//        sm3.doFinal(result, 0);
//        return result;
//    }

}
