package org.dromara.common.core.service;

import org.dromara.common.core.constant.GenNumConstants;

/**
 * @Author: caipeishen
 * @Date: 2023-12-15 17:44
 * @Description: 生成编号
 **/
public interface GenNumService {

    /**
     * TEST("test", "TS", 5)
     * TS2300001
     * @return 编号
     */
    String genNumberByYear(GenNumConstants genConstant);

    /**
     * TEST("test", "TS", 5)
     * TS231200001
     * @return 编号
     */
    String genNumberByMonth(GenNumConstants genConstant);


    /**
     * TEST("test", "TS", 5)
     * TS23121800001
     * @return 编号
     */
    String genNumberByDay(GenNumConstants genConstant);
}
