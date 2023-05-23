package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author jjm
 * @since 2023-05-22
 */
@Getter
@Data
@Setter
@NoArgsConstructor
@TableName("sys_hello")
@ApiModel(value = "Hello对象", description = "")
public class Hello implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("name")
      private String name;

      @ApiModelProperty("gender")
      private String gender;

      @ApiModelProperty("email")
      private String email;

      @ApiModelProperty("phone")
      private Integer phone;

      @ApiModelProperty("address")
      private String address;

      @ApiModelProperty("school")
      private String school;
}
