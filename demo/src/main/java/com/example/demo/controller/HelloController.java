package com.example.demo.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.Result;
// 这种加了美元符号相当于变成动态的
import com.example.demo.service.IHelloService;
import com.example.demo.entity.Hello;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jjm
 * @since 2023-05-22
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Resource
    private IHelloService helloService;

    //entity 相当于User类， table.entityPath相当于user对象
    //新增或更新
    @PostMapping
    public Result save(@RequestBody Hello hello){
        helloService.saveOrUpdate(hello);
        return Result.success();
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        helloService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        helloService.removeByIds(ids);
        return Result.success();
    }

    // 查询所有
    @GetMapping
    public Result findAll(){
        return Result.success(helloService.list());
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){
        return Result.success(helloService.getById(id));
    }

    // 分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize){
        QueryWrapper<Hello> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(helloService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }



}
