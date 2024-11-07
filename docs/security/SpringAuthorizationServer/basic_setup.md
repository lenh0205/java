
# Setup
* -> tạo 1 **spring-boot based application** sử dụng **spring security** (_xem phần `~\security\SpringSecurity`_)
* -> add **spring Boot's starter for Spring Authorization Server** as a dependency:

```bash - gradle
implementation "org.springframework.boot:spring-boot-starter-oauth2-authorization-server"
```

## Getting Started - minimum required components as @Bean
* -> to get started, we need **the minimum required components defined as a `@Bean`**
* -> when using the **spring-boot-starter-oauth2-authorization-server** dependency, define the following properties and **`Spring Boot will provide the necessary @Bean definitions`** for us:

```yml - application.yml
server:
  port: 9000 # specify the port that auth server will run 

logging:
  level:
    org.springframework.security: trace

spring:
  # security configuration
  security:
    user:
      name: user
      password: password
    oauth2:
      authorizationserver:

        # configure the repository of client services:
        client:
          oidc-client:
            registration:
              # identify which client is trying to access the resource:
              client-id: "oidc-client"

              # a secret known to the client and server that provides trust between the 2
              client-secret: "{noop}secret"

              client-authentication-methods:
                - "client_secret_basic" # basic authentication (username-password)

              authorization-grant-types:
                # allow the client to generate both an authorization code and a refresh token
                - "authorization_code"
                - "refresh_token"

              # the client will use it in a redirect-based flow
              redirect-uris:
                - "http://127.0.0.1:8080/login/oauth2/code/oidc-client"
              post-logout-redirect-uris:
                - "http://127.0.0.1:8080/"

              # defines authorizations that the client may have
              scopes:
                - "openid"
                - "profile"
            require-authorization-consent: true
```

## Customize the default configuration
* -> _most users will want to `customize the default configuration` - providing all of the necessary beans themself_
* -> by define the **minimum required components** as **a `@Bean` in a `Spring @Configuration`**
* (_đọc `~\security\SpringAuthorizationServer\Configuration_model.md` để hiểu thêm_)

```java - SecurityConfig.java
@Configuration
@EnableWebSecurity // enable the Spring web security module
public class SecurityConfig {

	@Bean 
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {
		// apply the default OAuth security:
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0
		http
			// Redirect to the login page when not authenticated from the
			// authorization endpoint
			.exceptionHandling((exceptions) -> exceptions
				.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint("/login"),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			)
			// Accept access tokens for User Info and/or Client Registration
			.oauth2ResourceServer((resourceServer) -> resourceServer
				.jwt(Customizer.withDefaults()));

		return http.build();
	}

	// configure second Spring Security filter chain for authentication:
	@Bean 
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
			throws Exception {
		http
			.authorizeHttpRequests((authorize) -> 
				authorize.anyRequest().authenticated()
				// require authentication for all requests
			)
			// providing a form-based authentication
			// Form login handles the redirect to the login page from the
			// authorization server filter chain
			.formLogin(Customizer.withDefaults());

		return http.build();
	}

	@Bean 
	public UserDetailsService userDetailsService() {
		UserDetails userDetails = User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(userDetails);
	}

	@Bean 
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("oidc-client")
				.clientSecret("{noop}secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
				.postLogoutRedirectUri("http://127.0.0.1:8080/")
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();

		return new InMemoryRegisteredClientRepository(oidcClient);
	}

	@Bean 
	public JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	private static KeyPair generateRsaKey() { 
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	@Bean 
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean 
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

}
```