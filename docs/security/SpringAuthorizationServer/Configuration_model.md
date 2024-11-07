=============================================================================
# Default configuration - OAuth2AuthorizationServerConfiguration
* -> it uses **OAuth2AuthorizationServerConfigurer** to provide **`minimal default configuration`** and registers a **`SecurityFilterChain @Bean`** composed of all the infrastructure components 
* -> for **an OAuth2 authorization server**

=============================================================================
# OAuth2
* -> **`OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(HttpSecurity)`** is a convenience (static) utility method that applies **`the default OAuth2 security configuration to HttpSecurity`**

## default protocol endpoints of "OAuth2 authorization server SecurityFilterChain @Bean"
* -> **`OAuth2 Authorization endpoint`**
* -> **`OAuth2 Device Authorization Endpoint`**
* -> **`OAuth2 Device Verification Endpoint`**
* -> **`OAuth2 Token endpoint`**
* -> **`OAuth2 Token Introspection endpoint`**
* -> **`OAuth2 Token Revocation endpoint`**
* -> **`OAuth2 Authorization Server Metadata endpoint`**
* -> **`JWK Set endpoint`** (_only if a JWKSource<SecurityContext> @Bean is registered_)

## Example: use 'OAuth2AuthorizationServerConfiguration' to apply the "minimal default configuration"
* -> nhưng ta cần lưu ý, **`authorization_code grant`** requires **`a user authentication mechanism`** must be configured in addition to the **default OAuth2 security configuration**

```java
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
}
```

=============================================================================
# OpenID Connect
* -> **`OpenID Connect 1.0`** is **`disabled` in the default configuration** (_vậy nên muốn dùng thì ta phải enable nó lên_)

* -> in addition to the **default protocol endpoints**, the **OAuth2 authorization server `SecurityFilterChain @Bean`** is configured with these **`OpenID Connect 1.0 protocol endpoints`**:
* -> **OpenID Connect 1.0 Provider Configuration endpoint**, **OpenID Connect 1.0 Logout endpoint**, **OpenID Connect 1.0 UserInfo endpoint**

```java - enable "OpenID Connect 1.0" by initializing the "OidcConfigurer"
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

	http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
		.oidc(Customizer.withDefaults());	// Initialize `OidcConfigurer`

	return http.build();
}
```

## JwtDecoder 
* -> **`OAuth2AuthorizationServerConfiguration.jwtDecoder(JWKSource<SecurityContext>)`** is a convenience (static) utility method that can be used to register a **`JwtDecoder @Bean`**, 
* -> which is **`REQUIRED`** for the **OpenID Connect 1.0 UserInfo endpoint** and the **OpenID Connect 1.0 Client Registration endpoint**

```java - to register a "JwtDecoder" @Bean:
@Bean
public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
	return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
}
```

=============================================================================
>  Although **OAuth2AuthorizationServerConfiguration** is to provide convenient methods to apply the **`minimal default configuration`** for **an OAuth2 authorization server**
> however, in most cases, customizing the configuration will be required

# Customizing the configuration - OAuth2AuthorizationServerConfigurer
* -> **OAuth2AuthorizationServerConfigurer** provides the ability to **`fully customize the security configuration`** for **an OAuth2 authorization server**

* -> it lets us **`specify the core components to use`** (_for example: **RegisteredClientRepository**, **OAuth2AuthorizationService**, **OAuth2TokenGenerator**, and others_)
* -> furthermore, it lets us **`customize the request processing logic for the protocol endpoints`** (_for example: **authorization endpoint**, **device authorization endpoint**, **device verification endpoint**, **token endpoint**, **token introspection endpoint**, and others_)

* _`OAuth2AuthorizationServerConfigurer` provides the following **configuration options**:_
```java
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
		new OAuth2AuthorizationServerConfigurer();
	http.apply(authorizationServerConfigurer);

	authorizationServerConfigurer
		.registeredClientRepository(registeredClientRepository)
		// the 'RegisteredClientRepository' (REQUIRED) for managing new and existing clients

		.authorizationService(authorizationService)
		// the 'OAuth2AuthorizationService' for managing new and existing authorizations

		.authorizationConsentService(authorizationConsentService)
		// the 'OAuth2AuthorizationConsentService' for managing new and existing authorization consents

		.authorizationServerSettings(authorizationServerSettings)
		// the 'AuthorizationServerSettings' (REQUIRED) for customizing configuration settings for the OAuth2 authorization server

		.tokenGenerator(tokenGenerator)
		// the 'OAuth2TokenGenerator' for generating tokens supported by the OAuth2 authorization server

		.clientAuthentication(clientAuthentication -> { })
		// the configurer for OAuth2 Client Authentication

		.authorizationEndpoint(authorizationEndpoint -> { })
		// the configurer for the OAuth2 Authorization endpoint

		.deviceAuthorizationEndpoint(deviceAuthorizationEndpoint -> { })
		// the configurer for the OAuth2 Device Authorization endpoint

		.deviceVerificationEndpoint(deviceVerificationEndpoint -> { })
		// the configurer for the OAuth2 Device Verification endpoint

		.tokenEndpoint(tokenEndpoint -> { })
		// the configurer for the OAuth2 Token endpoint

		.tokenIntrospectionEndpoint(tokenIntrospectionEndpoint -> { })
		// the configurer for the OAuth2 Token Introspection endpoint

		.tokenRevocationEndpoint(tokenRevocationEndpoint -> { })
		// the configurer for the OAuth2 Token Revocation endpoint

		.authorizationServerMetadataEndpoint(authorizationServerMetadataEndpoint -> { })
		// the configurer for the OAuth2 Authorization Server Metadata endpoint

		.oidc(oidc -> oidc
			.providerConfigurationEndpoint(providerConfigurationEndpoint -> { })
			// the configurer for the OpenID Connect 1.0 Provider Configuration endpoint

			.logoutEndpoint(logoutEndpoint -> { })
			// the configurer for the OpenID Connect 1.0 Logout endpoint.

			.userInfoEndpoint(userInfoEndpoint -> { })
			// the configurer for the OpenID Connect 1.0 UserInfo endpoint

			.clientRegistrationEndpoint(clientRegistrationEndpoint -> { })
			// the configurer for the OpenID Connect 1.0 Client Registration endpoint
		);

	return http.build();
}
```

=============================================================================
# Configuring Authorization Server Settings - AuthorizationServerSettings
* -> is a **`REQUIRED`** component that contains **`the configuration settings for the OAuth2 authorization server`**
* -> it specifies **`the URI for the protocol endpoints`** as well as **`the issuer identifier`**

* -> the **`default URI`** for the **protocol endpoints** are as follows:
```java
public final class AuthorizationServerSettings extends AbstractSettings {
	// ...
	public static Builder builder() {
		return new Builder()
			.authorizationEndpoint("/oauth2/authorize")
			.deviceAuthorizationEndpoint("/oauth2/device_authorization")
			.deviceVerificationEndpoint("/oauth2/device_verification")
			.tokenEndpoint("/oauth2/token")
			.tokenIntrospectionEndpoint("/oauth2/introspect")
			.tokenRevocationEndpoint("/oauth2/revoke")
			.jwkSetEndpoint("/oauth2/jwks")
			.oidcLogoutEndpoint("/connect/logout")
			.oidcUserInfoEndpoint("/userinfo")
			.oidcClientRegistrationEndpoint("/connect/register");
	}
	// ...
}
```

## Tips
* -> the **`@Import(OAuth2AuthorizationServerConfiguration.class)`** will **automatically registers an AuthorizationServerSettings @Bean**, if not already provided

## Customize the configuration settings
* -> to **`customize the configuration settings`** and **`register an AuthorizationServerSettings @Bean`**:

```java
@Bean
public AuthorizationServerSettings authorizationServerSettings() {
	return AuthorizationServerSettings.builder()
		.issuer("https://example.com")
		.authorizationEndpoint("/oauth2/v1/authorize")
		.deviceAuthorizationEndpoint("/oauth2/v1/device_authorization")
		.deviceVerificationEndpoint("/oauth2/v1/device_verification")
		.tokenEndpoint("/oauth2/v1/token")
		.tokenIntrospectionEndpoint("/oauth2/v1/introspect")
		.tokenRevocationEndpoint("/oauth2/v1/revoke")
		.jwkSetEndpoint("/oauth2/v1/jwks")
		.oidcLogoutEndpoint("/connect/v1/logout")
		.oidcUserInfoEndpoint("/connect/v1/userinfo")
		.oidcClientRegistrationEndpoint("/connect/v1/register")
		.build();
}
```

## AuthorizationServerContext
* -> is **a context object** that holds **`information of the Authorization Server runtime environment`**
* -> provides access to the **`AuthorizationServerSettings`** and **`the "current" issuer identifier`**

* -> if the **`issuer identifier`** is not configured in **AuthorizationServerSettings.builder().issuer(String)**, it is resolved from the current request
* -> the **AuthorizationServerContext** is accessible through the **`AuthorizationServerContextHolder`**, which associates it with **the current request thread by using a `ThreadLocal`**

=============================================================================
# Configuring Client Authentication
* -> **OAuth2ClientAuthenticationConfigurer** provides the ability to **`customize OAuth2 client authentication`**
* -> it defines extension points that let us **customize the `pre-processing`, `main processing`, and `post-processing logic` for client authentication requests**

```java - 'OAuth2ClientAuthenticationConfigurer' provides the following configuration options:
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
		new OAuth2AuthorizationServerConfigurer();
	http.apply(authorizationServerConfigurer);

	authorizationServerConfigurer
		.clientAuthentication(clientAuthentication ->
			clientAuthentication
				.authenticationConverter(authenticationConverter)
				// an 'AuthenticationConverter' (pre-processor) used when attempting to extract client credentials from 'HttpServletRequest' to an instance of 'OAuth2ClientAuthenticationToken'

				.authenticationConverters(authenticationConvertersConsumer)
				// sets the 'Consumer' providing access to the 'List' of default and (optionally) added 'AuthenticationConverter''s allowing the ability to add, remove, or customize a specific 'AuthenticationConverter'
			
				.authenticationProvider(authenticationProvider)
				// an 'AuthenticationProvider' (main processor) used for authenticating the 'OAuth2ClientAuthenticationToken'

				.authenticationProviders(authenticationProvidersConsumer)
				// sets the 'Consumer' providing access to the 'List' of default and (optionally) added 'AuthenticationProvider''s allowing the ability to add, remove, or customize a specific 'AuthenticationProvider'

				.authenticationSuccessHandler(authenticationSuccessHandler)
				// 'AuthenticationSuccessHandler' (post-processor) used for handling a successful client authentication and associating the 'OAuth2ClientAuthenticationToken' to the 'SecurityContext'

				.errorResponseHandler(errorResponseHandler)
				// the 'AuthenticationFailureHandler' (post-processor) used for handling a failed client authentication and returning the 'OAuth2Error' response
		);

	return http.build();
}
```

## Client authentication
* -> **`OAuth2ClientAuthenticationConfigurer`** configures the **`OAuth2ClientAuthenticationFilter`** and registers it with **the OAuth2 authorization server SecurityFilterChain @Bean** 
* -> **OAuth2ClientAuthenticationFilter** is **the `Filter` that processes `client authentication requests`**

* -> **`by default`**, **client authentication** is required for **`the OAuth2 Token endpoint`**, **`the OAuth2 Token Introspection endpoint`**, and **`the OAuth2 Token Revocation endpoint`**
* -> the **`supported client authentication methods`** are **client_secret_basic**, **client_secret_post**, **private_key_jwt**, **client_secret_jwt**, **tls_client_auth**, **self_signed_tls_client_auth**, and **none** (public clients)

## 'OAuth2ClientAuthenticationFilter' default configuration:

### AuthenticationConverter 
* -> a **`DelegatingAuthenticationConverter`**
* -> composed of **JwtClientAssertionAuthenticationConverter**, **X509ClientCertificateAuthenticationConverter**, **ClientSecretBasicAuthenticationConverter**, **ClientSecretPostAuthenticationConverter**, and **PublicClientAuthenticationConverter**

### AuthenticationManager 
* -> an **`AuthenticationManager`**
* -> composed of **JwtClientAssertionAuthenticationProvider**, **X509ClientCertificateAuthenticationProvider**, **ClientSecretAuthenticationProvider**, and **PublicClientAuthenticationProvider**

### AuthenticationSuccessHandler 
* -> an internal implementation that associates the "authenticated" **`OAuth2ClientAuthenticationToken`** (current **Authentication**) to the **SecurityContext**

### AuthenticationFailureHandler
* -> an internal implementation that uses the **`OAuth2Error`** associated with the **`OAuth2AuthenticationException`** to return the OAuth2 error response

## Customizing Jwt Client Assertion Validation
* -> **`JwtClientAssertionDecoderFactory.DEFAULT_JWT_VALIDATOR_FACTORY`** is the **default factory** that provides an **OAuth2TokenValidator<Jwt>** for the specified **RegisteredClient**
* -> and is used for **validating the `iss`, `sub`, `aud`, `exp` and `nbf` claims of the Jwt client assertion**

* -> **`JwtClientAssertionDecoderFactory`** provides the ability to **override the `default Jwt client assertion validation`** 
* -> by supplying **a custom factory** of type **Function<RegisteredClient, OAuth2TokenValidator<Jwt>>** to **setJwtValidatorFactory()**

* -> **`JwtClientAssertionDecoderFactory`** is the default **`JwtDecoderFactory`** used by **`JwtClientAssertionAuthenticationProvider`**
* -> that provides a **`JwtDecoder`** for the **specified 'RegisteredClient'** and is used for **authenticating a Jwt Bearer Token** during OAuth2 client authentication

### Use Case
* -> a common use case for customizing **JwtClientAssertionDecoderFactory** is to **`validate additional claims in the Jwt client assertion`**

```java - Example: 
// -> configure JwtClientAssertionAuthenticationProvider with a customized JwtClientAssertionDecoderFactory 
// -> that validates an additional claim in the Jwt client assertion:
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
		new OAuth2AuthorizationServerConfigurer();
	http.apply(authorizationServerConfigurer);

	authorizationServerConfigurer
		.clientAuthentication(clientAuthentication ->
			clientAuthentication
				.authenticationProviders(configureJwtClientAssertionValidator())
		);

	return http.build();
}

private Consumer<List<AuthenticationProvider>> configureJwtClientAssertionValidator() {
	return (authenticationProviders) ->
		authenticationProviders.forEach((authenticationProvider) -> {
			if (authenticationProvider instanceof JwtClientAssertionAuthenticationProvider) {
				// Customize JwtClientAssertionDecoderFactory
				JwtClientAssertionDecoderFactory jwtDecoderFactory = new JwtClientAssertionDecoderFactory();
				Function<RegisteredClient, OAuth2TokenValidator<Jwt>> jwtValidatorFactory = (registeredClient) ->
					new DelegatingOAuth2TokenValidator<>(
						// Use default validators
						JwtClientAssertionDecoderFactory.DEFAULT_JWT_VALIDATOR_FACTORY.apply(registeredClient),
						// Add custom validator
						new JwtClaimValidator<>("claim", "value"::equals));
				jwtDecoderFactory.setJwtValidatorFactory(jwtValidatorFactory);

				((JwtClientAssertionAuthenticationProvider) authenticationProvider)
					.setJwtDecoderFactory(jwtDecoderFactory);
			}
		});
}
```

## Customizing Mutual-TLS Client Authentication
* -> **`X509ClientCertificateAuthenticationProvider`** is used for **`authenticating the client 'X509Certificate' chain received`** 
* -> when **ClientAuthenticationMethod.TLS_CLIENT_AUTH** or **ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH method** is used during **`OAuth2 client authentication`**
* -> it is also composed with a **`Certificate Verifier`**, which is used to **verify the contents of the client 'X509Certificate'** after the TLS handshake has successfully completed

### PKI Mutual-TLS Method
* -> for the **PKI Mutual-TLS (ClientAuthenticationMethod.TLS_CLIENT_AUTH) method**, 
* -> the **default implementation of the certificate verifier** **`verifies the subject distinguished name of the client 'X509Certificate'`**
* -> against the setting **`RegisteredClient.getClientSettings.getX509CertificateSubjectDN()`**

* -> if we need to **verify another attribute of the client X509Certificate**
* _for example, `a Subject Alternative Name (SAN) entry`_
* _the following example shows how to configure `X509ClientCertificateAuthenticationProvider` with a custom implementation of a certificate verifier:_
```java
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
			new OAuth2AuthorizationServerConfigurer();
	http.apply(authorizationServerConfigurer);

	authorizationServerConfigurer
			.clientAuthentication(clientAuthentication ->
					clientAuthentication
							.authenticationProviders(configureX509ClientCertificateVerifier())
			);

	return http.build();
}

private Consumer<List<AuthenticationProvider>> configureX509ClientCertificateVerifier() {
	return (authenticationProviders) ->
			authenticationProviders.forEach((authenticationProvider) -> {
				if (authenticationProvider instanceof X509ClientCertificateAuthenticationProvider) {
					Consumer<OAuth2ClientAuthenticationContext> certificateVerifier = (clientAuthenticationContext) -> {
						OAuth2ClientAuthenticationToken clientAuthentication = clientAuthenticationContext.getAuthentication();
						RegisteredClient registeredClient = clientAuthenticationContext.getRegisteredClient();
						X509Certificate[] clientCertificateChain = (X509Certificate[]) clientAuthentication.getCredentials();
						X509Certificate clientCertificate = clientCertificateChain[0];

						// TODO Verify Subject Alternative Name (SAN) entry

					};

					((X509ClientCertificateAuthenticationProvider) authenticationProvider)
							.setCertificateVerifier(certificateVerifier);
				}
			});
}
```

### Self-Signed Certificate Mutual-TLS Method
* -> for the **Self-Signed Certificate Mutual-TLS (`ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH`) method`**, 
* -> the **default implementation** of the **`certificate verifier`** will **`retrieve the client's JSON Web Key Set`** using the setting **RegisteredClient.getClientSettings.getJwkSetUrl()**
* -> and **`expect to find a match against the client 'X509Certificate' received`** during the **TLS handshake**

* -> the **RegisteredClient.getClientSettings.getJwkSetUrl()** setting is used to **`retrieve the client’s certificates via a JSON Web Key (JWK) Set`**
* -> **`a certificate`** is represented with the **x5c parameter of an individual JWK within the set**

### Client Certificate-Bound Access Tokens
* -> When Mutual-TLS client authentication is used at the token endpoint, the authorization server is able to bind the issued access token to the client’s X509Certificate. The binding is accomplished by computing the SHA-256 thumbprint of the client’s X509Certificate and associating the thumbprint with the access token. For example, a JWT access token would include a x5t#S256 claim, containing the X509Certificate thumbprint, within the top-level cnf (confirmation method) claim.

Binding the access token to the client’s X509Certificate provides the ability to implement a proof-of-possession mechanism during protected resource access. For example, the protected resource would obtain the client’s X509Certificate used during Mutual-TLS authentication and then verify that the certificate thumbprint matches the x5t#S256 claim associated with the access token.

* _The following example shows how to enable certificate-bound access tokens for a client:_
```java
RegisteredClient mtlsClient = RegisteredClient.withId(UUID.randomUUID().toString())
		.clientId("mtls-client")
		.clientAuthenticationMethod(ClientAuthenticationMethod.TLS_CLIENT_AUTH)
		.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
		.scope("scope-a")
		.clientSettings(
				ClientSettings.builder()
						.x509CertificateSubjectDN("CN=mtls-client,OU=Spring Samples,O=Spring,C=US")
						.build()
		)
		.tokenSettings(
				TokenSettings.builder()
						.x509CertificateBoundAccessTokens(true)
						.build()
		)
		.build();
```