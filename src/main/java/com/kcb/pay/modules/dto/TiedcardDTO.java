package com.kcb.pay.modules.dto;

import lombok.Data;

@Data
public class TiedcardDTO {

    //请求参数加密字段
    private String request;
    //签名
    private String signature;

}
