package org.dromara.common.redis.service.impl;

import org.dromara.common.core.constant.GenNumConstants;
import org.dromara.common.core.service.GenNumService;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 生成编号
 * @author Sue
 */
@Service
public class GenCodeNumServiceImpl implements GenNumService {

    /** redis操作String */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String GENERATE = "generate";

//    private static final int LENGTH = 5;

    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yy");

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyMM");

    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");


    @Override
    public String genNumberByYear(GenNumConstants genConstant) {
        return getNumber(genConstant, YEAR_FORMAT.format(LocalDate.now()));
    }

    @Override
    public String genNumberByMonth(GenNumConstants genConstant) {
        return getNumber(genConstant, MONTH_FORMAT.format(LocalDate.now()));
    }


    @Override
    public String genNumberByDay(GenNumConstants genConstant) {
        return getNumber(genConstant, DAY_FORMAT.format(LocalDate.now()));
    }

    private String getNumber(GenNumConstants genConstant, String month) {
        // 例redis存储：generate:test:TS:23121800001
        String key = GENERATE + ":" + genConstant.getSysCode() + ":" + genConstant.getPreCode() + ":" + month;
        Long number = stringRedisTemplate.opsForValue().increment(key);
        return genConstant.getPreCode() + month + StringUtils.leftPad(number != null ? number.toString() : "1", genConstant.getLength(), '0');
    }
}
