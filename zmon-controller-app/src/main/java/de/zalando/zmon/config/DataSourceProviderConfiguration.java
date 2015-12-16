package de.zalando.zmon.config;

import com.jolbox.bonecp.BoneCPDataSource;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import de.zalando.sprocwrapper.dsprovider.SingleDataSourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Created by hjacobs on 16.12.15.
 */
@Configuration
public class DataSourceProviderConfiguration {

    @Autowired
    private Environment environment;

    private DataSource dataSource() {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass(environment.getRequiredProperty("spring.datasource.driverClassName"));
        dataSource.setInitSQL(environment.getRequiredProperty("spring.datasource.initSQL"));
        dataSource.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
        dataSource.setUsername(environment.getRequiredProperty("spring.datasource.username"));
        dataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));
        return dataSource;
    }

    @Bean
    public DataSourceProvider dataSourceProvider() {
        return new SingleDataSourceProvider(dataSource());
    }
}
