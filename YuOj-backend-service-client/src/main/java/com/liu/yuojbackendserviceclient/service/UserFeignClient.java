package com.liu.yuojbackendserviceclient.service;

import com.liu.yuojbackendcommon.common.ErrorCode;
import com.liu.yuojbackendcommon.exception.BusinessException;
import com.liu.yuojbackendcommon.utils.CopyUtil;
import com.liu.yuojbackendmodel.entity.User;
import com.liu.yuojbackendmodel.enums.UserRoleEnum;
import com.liu.yuojbackendmodel.vo.user.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;

import static com.liu.yuojbackendcommon.constant.UserConstant.USER_LOGIN_STATE;

/**

 针对表【user(用户表)】的数据库操作Service
*/
@FeignClient(name = "YuOj-backend-user-service",path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 根据id获取用户
     * @param id
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("id") long id);

    /**
     *
     * 根据id获取用户集合
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);
    /**
     * 获取当前登陆用户
     */

    default User getLoginUser(HttpSession session){
        //从session中获取用户信息
        User loginUser = (User) session.getAttribute (USER_LOGIN_STATE);

        // 先判断是否登录
        if (loginUser == null|| loginUser.getId () == null){
            throw new BusinessException (ErrorCode.NOT_LOGIN_ERROR);
        }
        return loginUser;
    }

    /**
     * 获取用户包装类 UserVO
     */
   default UserVO getUserVO(User user){
       if (user==null){
           return null;
       }
       UserVO vo = CopyUtil.copy (user, UserVO.class);
       vo.setUserRole (user.getUserRole ().getValue ());
       return vo;
   }


    /**
     *判断当前用户是否是管理员　
     */
    default boolean isAdmin(User user){
        return user!=null && UserRoleEnum.ADMIN.equals (user.getUserRole ());
     }



}
