package com.example.isolatedcontext.service.internal.context;

import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.AntPathMatcher;

import java.util.Objects;
import java.util.Set;

import static java.util.Collections.emptySet;

class OptionalBeanTypeFilter extends TypeExcludeFilter implements EnvironmentAware {

    private static final String INCLUDE_PROPERTY = "spring.configure.include";
    private static final String EXCLUDE_PROPERTY = "spring.configure.exclude";

    private final AntPathMatcher matcher = new AntPathMatcher(".");

    private Set<String> include;
    private Set<String> exclude;

    @Override
    public void setEnvironment(Environment environment) {
        this.include = Binder.get(environment).bind(INCLUDE_PROPERTY, String[].class).map(Set::of).orElse(emptySet());
        this.exclude = Binder.get(environment).bind(EXCLUDE_PROPERTY, String[].class).map(Set::of).orElse(emptySet());
    }

    @Override
    public boolean match(MetadataReader reader, MetadataReaderFactory readerFactory) {
        String className = reader.getClassMetadata().getClassName();

        if (include.stream().anyMatch(pattern -> matcher.matchStart(pattern, className))) {
            return false;
        }
        return exclude.stream().anyMatch(pattern -> matcher.matchStart(pattern, className));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exclude, include);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        OptionalBeanTypeFilter other = (OptionalBeanTypeFilter) obj;
        return Objects.equals(exclude, other.exclude) && Objects.equals(include, other.include);
    }

}
