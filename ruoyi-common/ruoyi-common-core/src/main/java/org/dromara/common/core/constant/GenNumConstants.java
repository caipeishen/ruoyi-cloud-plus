package org.dromara.common.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: caipeishen
 * @Date: 2023-12-18 13:00
 * @Description:
 **/
@Getter
@AllArgsConstructor
public enum GenNumConstants {

    /**
     * 测试生成编号
     */
    TEST("test", "TS", 5);

    // 不在返回结果中
    private final String sysCode;

    // 前缀
    private final String preCode;

    // 除去日期长度
    private final int length;
}
