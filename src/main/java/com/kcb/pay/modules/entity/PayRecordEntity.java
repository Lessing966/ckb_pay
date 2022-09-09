package com.kcb.pay.modules.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("sys_pay_record")
public class PayRecordEntity {

    private Integer pId;

    private String outId;

    private String applyId;

    private String account;

    private BigDecimal applyAmount;

    private String createTime;

    private String updateTime;
}
