package com.example.demo.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Files;
import com.example.demo.entity.User;
import com.example.demo.mapper.FileMapper;
import com.example.demo.utils.TokenUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件上传接口
 * @paramfile 前端传来的文件
 *
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FileMapper fileMapper;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);   // 文件类型（后缀）
        long size = file.getSize();

        // 定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        if (!uploadFile.getParentFile().exists())
        {
            uploadFile.getParentFile().mkdirs();
        }

        String url;
        // 上传文件到磁盘
        file.transferTo(uploadFile);
        // 获取文件的md5
        String md5 = SecureUtil.md5(uploadFile);  // 只有当文件存在的时候，他才有md5
        // 从数据库查询是否有相同md5的记录
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles != null) {
            url = dbFiles.getUrl();
            // 由于文件已存在，删除刚才上传的重复文件
            uploadFile.delete();
        }
        else
            // 若数据库不存在重复文件，不删除刚才上传的文件
            url = "http://localhost:8080/file/" + fileUUID;

        // 把文件各个属性存储到数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size/1024); // 单位B转为KB
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);

        return url;
    }

    /**
     * 文件下载接口    http://localhost:8080/file/{fileUUID}
     * @param fileUUID
     * @param response
     * @throws IOException
     */

    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUID);
        // 设置输出流格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");
        // 读取文件字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();

    }
    /**
     * 通过文件的md5查询文件
     * @param md5
     * @return
     */
    private Files getFileByMd5(String md5)
    {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);   // 查找数据库中有没有相同md5的字段
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    /**
     * 分页查询文件接口
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name)
    {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        // 按照ID排序
        queryWrapper.orderByDesc("id");
        // 查询未删除记录
        queryWrapper.eq("is_delete", false);
        // eq和like区别在于，一个是必须要完全一样，一个只需要像就可以了
        // !Strings.isEmpty()用于限制该句的执行，如果是否的话，这句话就不执行了
        queryWrapper.like(!Strings.isEmpty(name), "name", name);
        // 重复使用QueryWrapper.like，会自动加个SQL语句中的AND

        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
    }

    //新增或更新
    @PostMapping("/update")
    public Result save(@RequestBody Files files) {return Result.success(fileMapper.updateById(files));}


    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id)
    {
        Files files = fileMapper.selectById(id);
        files.setIsDelete(true);
        fileMapper.updateById(files);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids)
    {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        // 查询未删除记录
        queryWrapper.eq("is_delete", false);
        // select * from sys_file where id in (id, id, id, ....)
        queryWrapper.in("id", ids);
        List<Files> files = fileMapper.selectList(queryWrapper);
        for (Files file : files) {
            file.setIsDelete(true);
            fileMapper.updateById(file);
        }
        return Result.success();
    }


}
