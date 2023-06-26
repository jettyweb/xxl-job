package com.xxl.job.admin.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.List;

/**
 * @Description:数据库初始化操作
 */
public class DataBaseInit {

    private static Logger log = LoggerFactory.getLogger(DataBaseInit.class);

    private Connection connection;

    public DataBaseInit(Connection connection) {
        this.connection = connection;
    }

    /**
     * 数据库初始化运行
     *
     * @throws Exception
     */
    public void run() throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("数据库初始化：开始");
        init();
        log.info("数据库初始化：结束，耗时：{}秒", (System.currentTimeMillis() - startTime) / 1000.0);
    }

    /**
     * 数据库初始化
     *
     * @throws Exception
     */
    private void init() throws Exception {
        DbType dbType = JdbcUtils.getDbType(connection.getMetaData().getURL());
        String dir = "data/init/" + dbType.getDb() + "/";
        String[] sqlFiles = {"init_table.sql", "init_data.sql"};
        for (String sqlFile : sqlFiles) {
            List<String> sqls = SqlScriptUtil.parseSqlFile(new ClassPathResource(dir + sqlFile).getInputStream());
            if (!CollectionUtils.isEmpty(sqls)) {
                SqlScriptUtil.executeBatchSql(sqls, connection);
            }
        }
    }
}
