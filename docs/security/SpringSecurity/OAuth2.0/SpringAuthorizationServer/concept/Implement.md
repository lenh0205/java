> using KeyCloak: https://github.com/serlesen/authorization-server

# Note
* -> @EnableWebSecurity activates the Spring Security infrastructure
* -> chỉ dùng cho 1 configuration class that serves as the primary entry point for Spring Security customization
* -> if we have multiple security configurations spread across different classes, only one class needs to be annotated with @EnableWebSecurity; other classes can simply be annotated with @Configuration or use the @Bean method 

# enable 'Authorization Code' grant type with PKCE

```yml - application.yml
# for confidential clients (Ex: server-side applications)
spring:
  security:
    user:
      name: user
      password: password
    oauth2:
      authorizationserver:
        client:
          oidc-client:
            registration:
              client-id: "oidc-client"
              client-secret: "{noop}secret"
              client-authentication-methods:
                - "client_secret_basic"
                # used for confidential clients  
                # client sends the client ID and secret encoded in Base64 in the Authorization header to Authorization Server
              authorization-grant-types:
                - "authorization_code"
                - "refresh_token"
              redirect-uris:
                - "http://127.0.0.1:8080/login/oauth2/code/oidc-client"
              post-logout-redirect-uris:
                - "http://127.0.0.1:8080/"
              scopes:
                - "openid"
                - "profile"
            require-authorization-consent: true

# for public clients (Ex: SPA)
spring:
  security:
    oauth2:
      authorizationserver:
        client:
          public-client:
            registration:
              client-id: "public-client"
              client-authentication-methods:
                - "none"
              authorization-grant-types:
                - "authorization_code"
              redirect-uris:
                - "http://127.0.0.1:4200"
              scopes:
                - "openid"
                - "profile"
            require-authorization-consent: true
            require-proof-key: true # enable PKCE required
```

## Authenticate using Social Login

```bash - build.gradle
implementation "org.springframework.boot:spring-boot-starter-oauth2-client"
```

```yml - application.yml
okta:
  base-url: ${OKTA_BASE_URL}

spring:
  security:
    oauth2:
      client:
        registration:
          my-client:
            provider: okta
            client-id: ${OKTA_CLIENT_ID}
            client-secret: ${OKTA_CLIENT_SECRET}
            scope:
              - openid
              - profile
              - email
        provider:
          okta:
            authorization-uri: ${okta.base-url}/oauth2/v1/authorize
            token-uri: ${okta.base-url}/oauth2/v1/token
            user-info-uri: ${okta.base-url}/oauth2/v1/userinfo
            jwk-set-uri: ${okta.base-url}/oauth2/v1/keys
            user-name-attribute: sub
```

```java - Configure OAuth 2.0 Login
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {
		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
				OAuth2AuthorizationServerConfigurer.authorizationServer();

		http
			.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
			.with(authorizationServerConfigurer, (authorizationServer) ->
				authorizationServer
					.oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
			)
			// Redirect to the OAuth 2.0 Login endpoint when not authenticated
			// from the authorization endpoint
			.exceptionHandling((exceptions) -> exceptions
				.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/my-client"),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
			throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.anyRequest().authenticated()
			)
			// OAuth2 Login handles the redirect to the OAuth 2.0 Login endpoint
			// from the authorization server filter chain
			.oauth2Login(Customizer.withDefaults());

		return http.build();
	}

}
```

=============================================================================
# Reference
https://www.linkedin.com/pulse/spring-cloud-gateway-authorization-server-react-web-app-young-gyu-kim-zevoc
https://stackoverflow.com/questions/77850783/how-a-react-application-connect-to-an-oauth-2-spring-authorization-server-resou
https://www.baeldung.com/spring-authentication-single-page-application-pkce
https://www.devglan.com/spring-security/spring-boot-oauth2-angular#google_vignette
https://stackoverflow.com/questions/tagged/spring-authorization-server
https://www.reddit.com/r/SpringBoot/comments/1dx7snk/spring_security_vs_spring_oauth2_resource_server/
https://stackoverflow.com/questions/tagged/spring-authorization-server?tab=Active
https://www.baeldung.com/rest-api-spring-oauth2-angular
https://stackoverflow.com/questions/71479250/spring-security-oauth2-authorization-server-angular-auth-oidc-client
https://www.baeldung.com/rest-api-spring-oauth2-angular-legacy
https://medium.com/@koffimawuli.adjoda_20701/spring-boot-3-oauth2-with-angular-271be28e9ffe
https://www.youtube.com/playlist?list=PL4bT56Uw3S4zqmhhzJdsA_8aNhttF3mWa
https://github.com/lspil/full_stack_oauth2
https://stackoverflow.com/questions/74046391/spring-authorization-server-and-user-registration
https://stackoverflow.com/questions/78710786/spring-oauth2-authorization-server-login-registration-and-forget-passowrd-flo
https://www.baeldung.com/spring-dynamic-client-registration

# Github
https://github.com/rwinch/spring-enterprise-authorization-server/tree/main/authorization-server
https://github.com/dynamind/spring-boot-security-oauth2-minimal
https://github.com/Baeldung/spring-security-oauth