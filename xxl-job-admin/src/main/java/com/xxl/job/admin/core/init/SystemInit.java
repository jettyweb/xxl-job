package com.xxl.job.admin.core.init;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * @Description:系统初始化
 */
public class SystemInit {

    private static Logger log = LoggerFactory.getLogger(SystemInit.class);

    /**
     * 系统初始化运行
     *
     * @throws Exception
     */
    public void run(Connection connection) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("系统初始化：开始");
        // 初始化数据库
        new DataBaseInit(connection).run();

        log.info("系统初始化：结束，耗时：{}秒", (System.currentTimeMillis() - startTime) / 1000.0);
    }


}
