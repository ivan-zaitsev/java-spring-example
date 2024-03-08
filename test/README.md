# Spring tests

# Unit testing:

Project path: `src/test/java`

## Layers:

### Services:

Business logic should be tested. Only public methods are tested, all downstream private methods are tested by public call.
Mocks and Stubs are used to substitute downstream dependencies (such as other services, or repositories).

#### Details:

Unit tests are used to test service layer. No need to run Spring context.

# Integration testing:

Project path: `src/test-integration/java`

## Layers:

### Controllers:

Each controller should be tested for negative (not authorized, bad request) and positive cases. This type of testing uses all downstream layers (Service, Repository) without any stubs or mocks (Mockito). Rest clients can be mocked on HTTP level by using MockServer or Wiremock.

### Services:

Services can have Spring specific functionality such as `@Transactional` or `@Retry` which cannot be fully tested with unit tests. In this case such logic should be tested with integration tests.

### Repositories:

Each JPA query should be tested for correctness by using test slices (isolated context - `@DataJpaTest`), it should run only repositories without loading controllers and services to the context.
These type of tests cover narrow cases which are not fully tested through Controller tests.

Each external rest client should be tested by using MockServer or Wiremock.

#### Details:

To be able to test application as close as possible to production, `@SpringBootTest` annotation is used to run whole context and test application starting from controllers and ending with repositories.

By default Spring will run separate context for each class where `@SpringBootTest` annotation is used.
To reduce testing time parent test bases are used. This way for example we create test base per layer ControllerTestBase and extend all test classes from the test base class.

To test correctness of database queries, testcontainers are used. To reuse testcontainers code context initializers are used.
`@ContextConfiguration` annotation is used to run specific context initializer for specific test class.
