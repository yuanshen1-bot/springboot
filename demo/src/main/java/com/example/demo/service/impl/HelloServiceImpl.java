package com.example.demo.service.impl;

import com.example.demo.entity.Hello;
import com.example.demo.mapper.HelloMapper;
import com.example.demo.service.IHelloService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jjm
 * @since 2023-05-22
 */
@Service
public class HelloServiceImpl extends ServiceImpl<HelloMapper, Hello> implements IHelloService {

}
