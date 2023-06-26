package com.xxl.job.admin.core.init;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 *
 *
 * @author: hxq94
 * @date: 2023-06-20 17:29:20
 * @since V1.0.0
 */
@Component("xxlInitConfig")
public class XxlInitConfig{

    @Autowired
    private StartLoad startLoad;

    @PostConstruct
    public void init(){
        startLoad.run();
    }

    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties p = new Properties();
        p.setProperty("DM DBMS", "dm");
        p.setProperty("MySQL", "mysql");
        databaseIdProvider.setProperties(p);
        return databaseIdProvider;
    }

}
