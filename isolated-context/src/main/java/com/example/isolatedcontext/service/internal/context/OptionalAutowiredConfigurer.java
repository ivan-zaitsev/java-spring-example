package com.example.isolatedcontext.service.internal.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.util.AntPathMatcher;

import java.util.Set;

import static java.util.Collections.emptySet;

class OptionalAutowiredConfigurer implements BeanFactoryPostProcessor {

    private static final String OPTIONAL_PROPERTY = "spring.configure.optional";

    private final AntPathMatcher matcher = new AntPathMatcher(".");

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof DefaultListableBeanFactory defaultListableBeanFactory)) {
            throw new IllegalStateException(
                "OptionalAutowiredConfigurer needs to operate on a DefaultListableBeanFactory");
        }

        Set<String> optional = bindOptional(beanFactory);
        setAutowireCandidateResolver(defaultListableBeanFactory, optional);
    }

    private Set<String> bindOptional(ConfigurableListableBeanFactory beanFactory) {
        Environment environment = beanFactory.getBean(Environment.class);
        return Binder.get(environment).bind(OPTIONAL_PROPERTY, String[].class).map(Set::of).orElse(emptySet());
    }

    private void setAutowireCandidateResolver(DefaultListableBeanFactory beanFactory, Set<String> optional) {
        beanFactory.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver() {

            @Override
            public boolean isRequired(DependencyDescriptor descriptor) {
                MethodParameter parameter = descriptor.getMethodParameter();
                if (parameter != null && match(optional, parameter.getParameterType().getName())) {
                    return false;
                }
                return super.isRequired(descriptor);
            }

        });
    }

    private boolean match(Set<String> optional, String className) {
        return optional.stream().anyMatch(pattern -> matcher.matchStart(pattern, className));
    }

}
