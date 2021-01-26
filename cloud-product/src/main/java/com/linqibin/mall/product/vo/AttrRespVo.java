package com.linqibin.mall.product.vo;

import lombok.Data;

@Data
public class AttrRespVo extends AttrVo {

    /**
     * 所属分类名称
     */
    private String catalogName;

    /**
     * 所属分组名称
     */
    private String groupName;

    /**
     * 所属分类的完整路径  e.g. [2, 22, 225]
     * @author hugh&you
     * @since 2021/1/24 22:05
     */
    private Long[] catalogPath;
}
