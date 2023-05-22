package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;


//用于创建get和set函数
@Data
//生成无参构造方法
@NoArgsConstructor
//生成该类下所有属性的构造方法
@AllArgsConstructor
//声明查找表的名字
@TableName(value = "sys_user")
@ApiModel(value = "User对象", description = "")
@ToString
public class User {

    @ApiModelProperty
    //设置主键,这样变量名就可以不和主键名一致了
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty
    private String username;
    @ApiModelProperty
    @JsonIgnore //忽略这个字段，不展示给前端
    private String password;
    @ApiModelProperty
    private String nickname;
    @ApiModelProperty
    private String email;
    @ApiModelProperty
    private String phone;

    @ApiModelProperty
    // 声明该变量在数据库里对应的字段名，这样变量名和数据库字段名可以不一致了
    @TableField(value = "address")
    private String address;
    @ApiModelProperty
    private Date createTime;
    @ApiModelProperty
    private String avatarUrl;
    @ApiModelProperty("角色")
    private String role;
}
