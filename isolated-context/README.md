# Spring isolated context

To be able to generate OpenAPI based on annotations the application should be started (Automatically using `org.springdoc.openapi-gradle-plugin` Gradle plugin).
When application starts it requires all downstream dependencies (such as database) to be up.

To avoid running dependent services, an isolated context can be used.
Basically we can run only Spring controller layer without needing to add services and repositories to context.
Spring allows to add context initializers and implement custom logic that should be loaded.
Then that custom initializers can be used during application startup.

`OptionalContextInitializer` is an initializer, it adds beans to Spring context in early stage when application starts, before any other bean is added to the context.
This way we could add any bean that Spring will load before all other beans to understand how to proceed next.
One of this beans is TypeExcludeFilter. All beans which extend this class are used by Spring to understand what classes should be loaded to the context.

`OptionalBeanTypeFilter` extends `TypeExcludeFilter` and added to the context in `OptionalContextInitializer`, it contains logic which decides which bean should be loaded.
For this it reads `application.yaml` or any other profile specific property file, then it uses `configure.include`, `configure.exclude` properties.
Packages can be specified for these properties with wildcards to exclude sub-packages.

When services are excluded from context by default the application will fail to start because controllers depend on them.
`OptionalAutowiredConfigurer` is used to make specific classes as optional to autowire. For this it uses `configure.optional` property.
