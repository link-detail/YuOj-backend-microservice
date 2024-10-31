package com.liu.yuojbackenduserservice.controller.inner;

import com.liu.yuojbackendmodel.entity.User;
import com.liu.yuojbackendserviceclient.service.UserFeignClient;
import com.liu.yuojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author 刘渠好
 * @since 2024-10-31 16:30
 * 该服务仅是内部调用
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    @Override
    @GetMapping("/get/id")
    public User getById(long id) {
        User byId = userService.getById (id);
        System.out.println (byId);
        return byId;
    }

    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(Collection<Long> idList) {
        return userService.listByIds(idList);
    }
}
