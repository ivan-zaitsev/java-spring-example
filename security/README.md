# Spring security

For security Spring `SecurityFilterChain`s are used. Multiple chains are used with different request matchers to authenticate requests.

First chain with order 1 uses `MainAuthenticationRequestMatcher`, it checks if the request contains token, then the chain is chosen if token present.
If the request doesn’t contain any token, the chain will be skipped and the next ones would be evaluated.
Then it uses requests matcher rules from chosen chain to authorize request.

Second chain with order 2 uses `AnyRequestMatcher`, it basically matches all request which doesn’t match first chain.
This chain is used to add request matchers which should be allowed for unauthenticated users.

OAuth2 token is used for authentication. For this `SupplierJwtDecoder` is used to configure JWT claims validation such as expiration, token issuer and audience.

When `@SpringBootTest` is used it runs Spring tests against real Spring Boot application which is started on separate thread from test thread.
This way it is not so straightforward to test security. Spring Secutiry uses ThreadLocal to store if user is authorized.
If we set the authorized user on test thread it would not simply work because tests are run on separate thread.
For this `AuthenticationConfigurer` is used, it adds special filter which will set the Authentication class to the `ThreadLocal` by using `SecurityContextHolder.getContext().setAuthentication`.
