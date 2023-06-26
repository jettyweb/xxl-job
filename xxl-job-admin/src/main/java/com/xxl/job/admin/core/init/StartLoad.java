package com.xxl.job.admin.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @Description:系统启动环境加载
 */
@Component
public class StartLoad {

    private static Logger log = LoggerFactory.getLogger(StartLoad.class);

    @Autowired
    private DataSource primaryDataSource;

    /**
     * 系统启动环境加载运行
     */
    public void run() {
        try {
            if (!SqlScriptUtil.exists("xxl_job_lock", primaryDataSource.getConnection())) {// 判断库表是否存在
                new SystemInit().run(primaryDataSource.getConnection());
            } else {
                log.info("xxl_job already init , this is upgrade sql flow");
            }
        } catch (Exception e) {
            log.error("启动出错", e);
        }
    }
}
