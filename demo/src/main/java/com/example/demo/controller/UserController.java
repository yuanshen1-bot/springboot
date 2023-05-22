package com.example.demo.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Constants;
import com.example.demo.common.Result;
import com.example.demo.controller.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.mapper.FileMapper;
import com.example.demo.service.IUserService;
import com.example.demo.utils.TokenUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jjm
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;


    // @RequestBody用于把前端传来的json转为java对象
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO)
    {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlankIfStr(username) || StrUtil.isBlankIfStr(password))
        {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO dto = userService.login(userDTO);

        return Result.success(dto);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO)
    {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlankIfStr(username) || StrUtil.isBlankIfStr(password))
        {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(userService.register(userDTO));
    }

    //新增或更新
    @PostMapping
    public Result save(@RequestBody User user) {return Result.success(userService.saveOrUpdate(user));}

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {return Result.success(userService.removeById(id));}

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {return Result.success(userService.removeByIds(ids));}

    // 查询所有
    @GetMapping
    public Result findAll() {return Result.success(userService.list());}

    // 根据id查询
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {return Result.success(userService.getById(id));}

    @GetMapping("/username/{username}")
    // 只要路由不一致，方法名一致也没事
    public Result findOne(@PathVariable String username)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 查出所有数据库中所有username一致的数据
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }

    // 分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String username,
                               @RequestParam(defaultValue = "") String email,
                               @RequestParam(defaultValue = "") String address)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        // !Strings.isEmpty()用于限制该句的执行，如果是否的话，这句话就不执行了
        queryWrapper.like(!Strings.isEmpty(username), "username", username);
        queryWrapper.like(!Strings.isEmpty(email), "email", email);
        queryWrapper.like(!Strings.isEmpty(address), "address", address);
        // 重复使用QueryWrapper.like，会自动加个SQL语句中的AND

        User currentUser = TokenUtils.getCurrentUser();
        System.out.println("获取当前用户信息======================================" + currentUser.getNickname());

        return Result.success(userService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }


    // 导出
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception
    {
        // 从数据库查询出所有数据
        List<User> list = userService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatarUrl", "头像");
        // 一次性写出list内的独享到excel，使用默认模式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // 输出流返回到浏览器
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    // 导入
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception
    {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("用户名", "username");
        reader.addHeaderAlias("昵称", "nickname");
        reader.addHeaderAlias("邮箱", "email");
        reader.addHeaderAlias("密码", "password");
        reader.addHeaderAlias("电话", "phone");
        reader.addHeaderAlias("地址", "address");
        reader.addHeaderAlias("创建时间", "createTime");
        reader.addHeaderAlias("头像", "avatarUrl");
        List<User> list = reader.readAll(User.class);

        // 保存到数据库
        userService.saveBatch(list);
        return Result.success(true);
    }


}
