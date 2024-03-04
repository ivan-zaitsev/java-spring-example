package com.example.isolatedcontext.service.internal.context;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

public class OptionalContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        registerOptionalBeanTypeFilter(applicationContext.getBeanFactory(), applicationContext.getEnvironment());
        registerOptionalAutowiredConfigurer(applicationContext.getBeanFactory());
    }

    private void registerOptionalBeanTypeFilter(ConfigurableBeanFactory beanFactory, Environment environment) {
        OptionalBeanTypeFilter optionalBeanTypeFilter = new OptionalBeanTypeFilter();
        optionalBeanTypeFilter.setEnvironment(environment);
        beanFactory.registerSingleton(OptionalBeanTypeFilter.class.getName(), optionalBeanTypeFilter);
    }

    private void registerOptionalAutowiredConfigurer(ConfigurableBeanFactory beanFactory) {
        OptionalAutowiredConfigurer optionalAutowiredConfigurer = new OptionalAutowiredConfigurer();
        beanFactory.registerSingleton(OptionalAutowiredConfigurer.class.getName(), optionalAutowiredConfigurer);
    }

}
