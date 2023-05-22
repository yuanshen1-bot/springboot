package com.example.demo.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * mp代码生成器
 *
 */

public class CodeGenerator
{
    public static void main(String[] args)
    {
        generate();
    }

    private static void generate()
    {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/jjm?serverTimezone=GMT%2b8", "root", "root")
                .globalConfig(builder -> {
                    builder.author("jjm") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("F:\\codes\\SpringBoot\\demo\\src\\main\\java\\"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.demo") // 设置父包名
                            .moduleName(null) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "F:\\codes\\SpringBoot\\demo\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
//                    builder.mapperBuilder().enableMapperAnnotation().build(); //给mapper类接口加上@Mapper
                    builder.controllerBuilder().enableHyphenStyle().enableRestStyle();   //使得返回的是json格式数据
                    builder.addInclude("sys_hello") // 设置需要生成的表名
                            .addTablePrefix("t_", "sys_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
        
    }
    

}
