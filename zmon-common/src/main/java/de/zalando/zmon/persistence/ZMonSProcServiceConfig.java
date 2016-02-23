package de.zalando.zmon.persistence;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import de.zalando.sprocwrapper.proxy.SProcProxyBuilder;

@Configuration
public class ZMonSProcServiceConfig {

    @Autowired
    private DataSourceProvider dataSourceProvider;

    @Bean(autowire = Autowire.BY_TYPE)
    public CheckDefinitionSProcService getCheckDefinitionSProcService() {
        return SProcProxyBuilder.build(dataSourceProvider, CheckDefinitionSProcService.class);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public AlertDefinitionSProcService getAlertDefinitionSProcService() {
        return SProcProxyBuilder.build(dataSourceProvider, AlertDefinitionSProcService.class);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public DashboardSProcService getDashboardSProcService() {
        return SProcProxyBuilder.build(dataSourceProvider, DashboardSProcService.class);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public ZMonSProcService getZMonSProcService() {
        return SProcProxyBuilder.build(dataSourceProvider, ZMonSProcService.class);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public EntitySProcService getEntitySprocService() {
        return SProcProxyBuilder.build(dataSourceProvider, EntitySProcService.class);
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public GrafanaDashboardSprocService getGrafanaSprocService() {
        return SProcProxyBuilder.build(dataSourceProvider, GrafanaDashboardSprocService.class);
    }
}
