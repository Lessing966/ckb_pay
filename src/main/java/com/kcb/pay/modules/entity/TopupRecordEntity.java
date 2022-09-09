package com.kcb.pay.modules.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("sys_topup_record")
@Api("充值记录")
public class TopupRecordEntity {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "卡号")
    private String card;
    @ApiModelProperty(value = "照片")
    private String url;
    private Integer pId;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "部门")
    private Integer dId;
    @ApiModelProperty(value = "单位")
    private Integer uId;
    @ApiModelProperty(value = "充值地点")
    private String  fname;
    @ApiModelProperty(value = "消费类型")
    private String  expense;
    @ApiModelProperty(value = "充值金额")
    private BigDecimal sum;
    @ApiModelProperty(value = "充值日期")
    private String time;
    @ApiModelProperty(value = "充值类型")
    private String type;


}
