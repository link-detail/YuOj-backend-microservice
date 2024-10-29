package com.liu.yuojbackendserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.yuojbackendmodel.dto.user.UserQueryRequest;
import com.liu.yuojbackendmodel.entity.User;
import com.liu.yuojbackendmodel.vo.user.LoginUserVO;
import com.liu.yuojbackendmodel.vo.user.UserVO;

import javax.servlet.http.HttpSession;
import java.util.List;

/**

 针对表【user(用户表)】的数据库操作Service
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    long userResister(String userAccount,String userPassword,String checkPassword);

    /**
     *用户登陆
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpSession session);

    /**
     * 用户注销
     */
    boolean userLogout(HttpSession session);

    /**
     * 获取当前登陆用户
     */
    User getLoginUser(HttpSession session);

    /**
     * 获取脱敏之后的数据
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取用户包装类 UserVO
     */
    UserVO getUserVO(User user);

    /**
     * 根据条件查询列表用户 wrapper
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    /**
     * 将用户类列表转为用户封装类列表
     */
    List<UserVO> getUserVOList(List<User> list);

    /**
     * 判断用户是否是管理员
     */
    boolean isAdmin(User user);

    /**
     *判断当前用户是否是管理员　
     */
    boolean isAdmin(HttpSession session);

}
