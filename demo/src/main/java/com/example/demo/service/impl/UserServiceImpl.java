package com.example.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.Constants;
import com.example.demo.controller.dto.UserDTO;
import com.example.demo.entity.Menu;
import com.example.demo.entity.User;
import com.example.demo.exception.ServiceException;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.RoleMenuMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.IMenuService;
import com.example.demo.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jjm
 * @since 2023-04-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Override
    public UserDTO login(UserDTO userDTO)  // 从user表查到role表再查到rolemenu表再查到menu表
    {
        User one = getUserInfo(userDTO, true);
        if (one != null)
        {
            BeanUtil.copyProperties(one, userDTO, true);
            // 设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(token);

            String role = one.getRole();  // 获取到ROLE_ADMIN或者ROLE_USER
            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);

            return userDTO;
        }
        else
        {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    @Override
    public User register(UserDTO userDTO)
    {
        User one = getUserInfo(userDTO, false);
        if (one == null)  // 如果数据库中没查到这个信息
        {
            one = new User();
            BeanUtil.copyProperties(userDTO, one, true);
            save(one);   // 把新信息存到数据库
        }
        else throw new ServiceException(Constants.CODE_600, "用户已存在");
        return one;
    }

    private User getUserInfo(UserDTO userDTO, boolean login)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (login)
        {
            queryWrapper.eq("username", userDTO.getUsername());
            queryWrapper.eq("password", userDTO.getPassword());
        }else
        {
            queryWrapper.eq("username", userDTO.getUsername());
        }
        User one;
        try
        {
            one = getOne(queryWrapper);  // 从数据库查询用户信息，没查到则one为null
        }
        catch (Exception e)  // 数据库没有存储这个信息，捕获异常
        {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }

    /**
     * 获取当前角色的菜单列表
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String roleFlag)
    {
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        // 当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        // 查出数据库中的所有父级菜单
        List<Menu> parentMenus = menuService.findMenus("");  // findMenus方法封装了设置子菜单的过程，并返回父菜单
        // new一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
        // 再筛选当前用户角色能看到的菜单
        for (Menu parentmenu : parentMenus) {
            if (menuIds.contains(parentmenu.getId())){
                roleMenus.add(parentmenu);
            }
            // 再获取该父菜单的子菜单
            List<Menu> children = parentmenu.getChildren();
            // removeIf 移除子菜单children中那些不在menuIds中的menu, roleMenus也会跟着移除
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }
}
