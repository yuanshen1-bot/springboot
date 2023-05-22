package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

//这是一个接口类


//@Mapper 是用于让Springboot管理这个用于查询数据库的类，不加这个无法访问这个类

public interface UserMapper extends BaseMapper<User>
{

//    @Select("SELECT * FROM sys_user") //将SQL命令赋予给下面这个方法，查询这张表所有内容
//    List<User> findAll();
//
    @Insert("INSERT into sys_user(username, password, nickname, email, phone, address) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{email}, #{phone}, #{address})")
    //插入值要用#和花括号，因为值是动态的
    int insert(User user);
//
//    int update(User user);
//
//    @Delete("delete from sys_user where id = #{id}")
//    Integer deleteById(@Param("id") Integer id);
//
//    @Select("select * from sys_user where username like concat('%', #{username}, '%') and email like concat('%', #{email}, '%') limit #{pageNum}, #{pageSize}")
//    List<User> selectPage(Integer pageNum, Integer pageSize, String username, String email);
//
//    @Select("select count(*) from sys_user where username like concat('%', #{username}, '%')")
//    Integer selectTotal(String username);
}
