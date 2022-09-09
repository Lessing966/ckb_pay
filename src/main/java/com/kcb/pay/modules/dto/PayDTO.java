package com.kcb.pay.modules.dto;

import lombok.Data;

@Data
public class PayDTO {
    //编号
    private String schoolNo;
    //工号
    private String studentId;
    //手机号
    private String account;

    private String applyId;

    private int applyAmount;

}
