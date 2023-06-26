package com.xxl.job.admin.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:sql脚本处理辅助类
 */
public class SqlScriptUtil {

    private static Logger log = LoggerFactory.getLogger(SqlScriptUtil.class);

    private static final String SQL_ERROR = "SQL执行异常信息";

    /**
     * 解析sql脚本文件
     *
     * @param inputStream sql脚本文输入流
     */
    public static List<String> parseSqlFile(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder sqlSb = new StringBuilder();
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            if (!str.startsWith("/*!") && !str.startsWith("--") && !str.contains("/*") && str.trim().length() > 0) {
                sqlSb.append("\n").append(str);
            }
        }
        inputStream.close();
        bufferedReader.close();
        String[] sqls = sqlSb.toString().split(";");
        return Arrays.stream(sqls).filter(sql -> !StringUtils.isEmpty(sql)).collect(Collectors.toList());
    }

    /**
     * 批量执行sql
     *
     * @param sqls sql语句集合
     * @param conn 数据库连接对象
     */
    public static void executeBatchSql(List<String> sqls, Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        try (Statement statement = conn.createStatement()) {
            int i = 0, len = sqls.size();
            for (; i < len; i++) {
                if (!StringUtils.isEmpty(sqls.get(i))) {
                    statement.addBatch(sqls.get(i));
                    if ((i + 1) % 50 == 0 && i > 0) {
                        statement.executeBatch();
                        statement.clearBatch();
                    }
                }
            }
            if (len % 50 != 0) {
                statement.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            log.error(SQL_ERROR, e);
            conn.rollback();
            throw e;
        } catch (Exception e) {
            log.error(SQL_ERROR, e);
            conn.rollback();
        }
    }

    public static boolean exists(String table, Connection conn) {
        ResultSet rs = null;
        try (Statement statement = conn.createStatement()) {
            DbType dbType = JdbcUtils.getDbType(conn.getMetaData().getURL());
            String sql = null;
            if (DbType.DM.equals(dbType)) {
                sql = "SELECT TABLE_NAME  FROM all_tables WHERE TABLE_NAME =  '" + table + "'";
            }
            if (DbType.MYSQL.equals(dbType)) {
                sql = "show tables like '" + table + "'";
            }
            if(DbType.POSTGRE_SQL.equals(dbType)){
                sql = "select relname from pg_statio_user_tables where relname='" + table + "'";
            }
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return table.equalsIgnoreCase(rs.getString(1));
            }
        } catch (Exception e) {
            log.error(SQL_ERROR, e);
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.error("SQL执行RS关闭异常", e);
                }
            }
        }
        return false;
    }
}
