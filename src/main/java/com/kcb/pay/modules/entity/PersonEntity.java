package com.kcb.pay.modules.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@TableName("sys_person")
public class PersonEntity {
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "排序")
    private Integer number;


    @ApiModelProperty(value = "人脸图")
    private String url;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "单位id")
    private Integer uId;


    @ApiModelProperty(value = "部门id")
    private Integer dId;


    @ApiModelProperty(value = "联系方式")
    private String phone;


    @ApiModelProperty(value = "人员类型")
    private String type;


    @ApiModelProperty(value = "卡号")
    private String card;

    @ApiModelProperty(value = "卡编号")
    private String cardNum;


    @ApiModelProperty(value = "门禁组ID")
    private String gId;


    @ApiModelProperty(value = "门禁权限")
    private String gPoint;


    @ApiModelProperty(value = "卡状态")
    private String cardStatus;


    @ApiModelProperty(value = "卡类型")
    private String  cardTpye;


    @ApiModelProperty(value = "补贴金额")
    private BigDecimal subsidies;


    @ApiModelProperty(value = "现金金额")
    private BigDecimal cash;


    @ApiModelProperty(value = "有限期")
    private String deadline;

    @ApiModelProperty(value = "消费类型")
    private String expense;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;
    @ApiModelProperty(value = "长期有效")
    private Boolean forlong;
    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "人员状态")
    private Integer status;


    @TableField(exist = false)
    private String applyId;

    @TableField(exist = false)
    private BigDecimal applyAmount;
}
