package com.liu.yuojbackendmodel.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author 刘渠好
 * @Date 2024-05-17 23:03
 * 用户自我更新请求类
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
