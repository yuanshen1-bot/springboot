package com.example.demo.service;

import com.example.demo.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jjm
 * @since 2023-04-06
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
