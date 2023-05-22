package com.example.demo.service;

import com.example.demo.controller.dto.UserDTO;
import com.example.demo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jjm
 * @since 2023-04-03
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);
    User register(UserDTO userDTO);
}
