=============================================================================
# Default configuration - * -> OAuth2AuthorizationServerConfiguration
* -> it uses **OAuth2AuthorizationServerConfigurer** to provide **`minimal default configuration`** and registers a **`SecurityFilterChain @Bean`** composed of all the infrastructure components 
* -> for **an OAuth2 authorization server**

## for convenient setup
* -> **OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(HttpSecurity)** is a convenience (static) utility method that applies **`the default OAuth2 security configuration to HttpSecurity`**

## default protocol endpoints of "OAuth2 authorization server SecurityFilterChain @Bean"
* -> **`OAuth2 Authorization endpoint`**
* -> **`OAuth2 Device Authorization Endpoint`**
* -> **`OAuth2 Device Verification Endpoint`**
* -> **`OAuth2 Token endpoint`**
* -> **`OAuth2 Token Introspection endpoint`**
* -> **`OAuth2 Token Revocation endpoint`**
* -> **`OAuth2 Authorization Server Metadata endpoint`**
* -> **`JWK Set endpoint`** (_only if a JWKSource<SecurityContext> @Bean is registered_)

# Example: use 'OAuth2AuthorizationServerConfiguration' to apply the "minimal default configuration"
* -> nhưng ta cần lưu ý là nếu s/d **`authorization_code grant`** requires **`a user authentication mechanism`** must be configured in addition to the default OAuth2 security configuration
* -> và nếu ta cần sử dụng **`OpenID Connect`** thì ta cần enable nó lên vì mặc định nó là disabled

```java - :
@Configuration
@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig {

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		List<RegisteredClient> registrations = ...
		return new InMemoryRegisteredClientRepository(registrations);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		RSAKey rsaKey = ...
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults());	// Initialize `OidcConfigurer`

        return http.build();
    }
}
```

