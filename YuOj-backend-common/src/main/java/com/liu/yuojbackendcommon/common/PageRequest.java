package com.liu.yuojbackendcommon.common;

import lombok.Data;
import com.liu.yuojbackendcommon.constant.CommonConstant;

/**
 * @Author 刘渠好
 * @Date 2024-05-16 22:40
 * 分页请求
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private long current =1;

    /**
     * 页面大小
     */
    private long pageSize=6; //默认6个一页

    /**
     * 排序字段
     */
    private String sortField="createTime";  //按照时间升序

    /**
     * 排序顺序
     */
    private String sortOrder= CommonConstant.SORT_ORDER_ASC;  //默认是升序

}
