> đối với 1 sản phẩm thực sự thì Authorization Server sẽ cần kết nối database với 3 tables tối thiểu là: JdbcRegisteredClientRepository, JdbcOAuth2AuthorizationService, JdbcOAuth2AuthorizationConsentService

> nhưng thực tế có thể ta sẽ cần table cho UserDetailsService cho việc Authenticate thông qua JdbcUserDetailsManager
> Spring Security's default JDBC-based UserDetailsService sẽ required **users** and **authorities** table
> https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html

===================================================================
> thằng này sẽ dùng để khởi tạo **RegisteredClientRepository**

# RegisteredClient
* -> is a representation of **`a client` that is `registered with the authorization server`**
* -> _the corresponding client registration model in **Spring Security's OAuth2 Client support** is **`ClientRegistration`**_

## "client registration" process
* -> during client registration, the **client** is assigned a unique **`client identifier`**, (optionally) a **`client secret`**, and **`metadata`** associated with its unique client identifier

## Configure 'RegisteredClient'

```java - Example in "Authorization Server" 
// configure a RegisteredClient that is allowed to perform the "authorization_code grant flow" to request an access token:
RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
	.clientId("client-a")
	.clientSecret("{noop}secret")
	// 	{noop} represents the PasswordEncoder id for Spring Security’s NoOpPasswordEncoder.
	.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
	.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
	.redirectUri("http://127.0.0.1:8080/authorized")
	.scope("scope-a")
	.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
	.build();
```

```yml - corresponding configuration in "Spring Security's OAuth2 Client" support
spring:
  security:
    oauth2:
      client:
        registration:
          client-a:
            provider: spring
            client-id: client-a
            client-secret: secret
            authorization-grant-type: authorization_code
            redirect-uri: "http://127.0.0.1:8080/authorized"
            scope: scope-a
        provider:
          spring:
            issuer-uri: http://localhost:9000
```

## Metadata
* -> a **RegisteredClient** has **`metadata (attributes) associated with its unique Client Identifier`** and is defined as follows:

```java
public class RegisteredClient implements Serializable {
    // id: The ID that uniquely identifies the RegisteredClient
	private String id;  

    // The client identifier
	private String clientId;

    // The time at which the client identifier was issued    
	private Instant clientIdIssuedAt;   

    //  The client’s secret. The value should be encoded using Spring Security’s PasswordEncoder
	private String clientSecret;   

    // The time at which the client secret expires 
	private Instant clientSecretExpiresAt;  

    // A descriptive name used for the client. 
    // The name may be used in certain scenarios, 
    // such as when displaying the client name in the consent page
	private String clientName;  

    // the authentication method(s) that the client may use
    // the supported values are "client_secret_basic", "client_secret_post", "private_key_jwt", "client_secret_jwt", and "none" (public clients)
	private Set<ClientAuthenticationMethod> clientAuthenticationMethods;  

    // the "authorization grant type(s)" that the client can use
    // the supported values are authorization_code, client_credentials, refresh_token, urn:ietf:params:oauth:grant-type:device_code, and urn:ietf:params:oauth:grant-type:token-exchange.  
	private Set<AuthorizationGrantType> authorizationGrantTypes; 

    // the registered redirect URI(s) that the client may use in redirect-based flows 
    // (for example, authorization_code grant)   
	private Set<String> redirectUris;   

    // the post logout redirect URI(s) that the client may use for logout
	private Set<String> postLogoutRedirectUris; 

    // the scope(s) that the client is allowed to request
	private Set<String> scopes; 

    // the custom settings for the client – for example, require PKCE, require authorization consent, and others
	private ClientSettings clientSettings;  

    // the custom settings for the OAuth2 tokens issued to the client 
    // (for example, access/refresh token time-to-live, reuse refresh tokens, and others)
	private TokenSettings tokenSettings;    

	// ...
}
```

===================================================================
> cần cấu hình thành Bean cho SecurityFilterChains

# RegisteredClientRepository
* -> is a **`REQUIRED`** component
* -> is **`the central component`** where **new clients can be registered** and **existing clients can be queried**

## Usage
* -> it is **used by other components when following a specific protocol flow**
* _such as **`client authentication`**, **`authorization grant processing`**, **`token introspection`**, **`dynamic client registration`**, and others_

## built-in implementation
* -> are **`InMemoryRegisteredClientRepository`** and **`JdbcRegisteredClientRepository`**

* -> the **`InMemoryRegisteredClientRepository`** implementation stores RegisteredClient instances **in-memory** and is recommended ONLY to be used during **development and testing**
* -> **`JdbcRegisteredClientRepository`** is a JDBC implementation that persists RegisteredClient instances by using **JdbcOperations**

## Register RegisteredClientRepository @Bean

* -> configure the **RegisteredClientRepository** through the **`OAuth2AuthorizationServerConfigurer`**:
```java
// the OAuth2AuthorizationServerConfigurer is useful when applying multiple configuration options simultaneously
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
		new OAuth2AuthorizationServerConfigurer();
	http.apply(authorizationServerConfigurer);

	authorizationServerConfigurer
		.registeredClientRepository(registeredClientRepository);

	// ...

	return http.build();
}
```

* -> hoặc cấu hình thành 1 Bean riêng cũng được
```java
@Bean
public RegisteredClientRepository registeredClientRepository() {
	List<RegisteredClient> registrations = // ...
	return new InMemoryRegisteredClientRepository(registrations);
}
```

===================================================================
> thằng này là kết quả sau khi authen và được resource owner authorize
> nó sẽ chứa access_token, refresh_token, Id_token đại điện cho client Authorization

# OAuth2Authorization
* -> is a representation of **`an OAuth2 authorization`** - which **holds state related to the authorization granted to a client** by the **`resource owner`** 
* (_or **`itself`** in the case of the **client_credentials** authorization grant type_)
* -> the corresponding authorization model in **Spring Security's OAuth2 Client support** is **`OAuth2AuthorizedClient`**

## Process
* -> **after the successful completion of an `authorization grant flow`**, 
* -> an **`OAuth2Authorization`** is created 
* -> and associates an **`OAuth2AccessToken`**, an (optional) **`OAuth2RefreshToken`**, and **`additional state` specific to the executed authorization grant type**

## Model
```java - "OAuth2Authorization" and its attributes are defined as follows:
public class OAuth2Authorization implements Serializable {
	// The ID that uniquely identifies the OAuth2Authorization
	private String id;

	// The ID that uniquely identifies the RegisteredClient
	private String registeredClientId;

	// The principal name of the resource owner (or client)
	private String principalName;

	//  The AuthorizationGrantType used
	private AuthorizationGrantType authorizationGrantType;

	// The Set of scope(s) authorized for the client
	private Set<String> authorizedScopes;

	// The OAuth2Token instances (and associated metadata) specific to the executed authorization grant type
	private Map<Class<? extends OAuth2Token>, Token<?>> tokens;

	// The additional attributes specific to the executed authorization grant type – for example, the authenticated Principal, OAuth2AuthorizationRequest, and others
	private Map<String, Object> attributes;

	// ...
}
```

## OAuth2Token
* -> each **OAuth2Token** is held in an **`OAuth2Authorization.Token`**, which provides accessors for **isExpired()**, **isInvalidated()**, and **isActive()**
* -> the **instances of `OAuth2Token` interface**  associated with an OAuth2Authorization vary, **depending on the `authorization grant type`**

* -> for the **OAuth2 authorization_code grant**, an **`OAuth2AuthorizationCode`**, an **`OAuth2AccessToken`**, and an (optional) **`OAuth2RefreshToken`** are associated.
* -> for the **OpenID Connect 1.0 authorization_code grant**, an **`OAuth2AuthorizationCode`**, an **`OidcIdToken`**, an **`OAuth2AccessToken`**, and an (optional) **`OAuth2RefreshToken`** are associated
* -> for the **OAuth2 client_credentials grant**, only an **`OAuth2AccessToken`** is associated

## Lifespan
* -> **OAuth2Authorization** and its **associated 'OAuth2Token' instances** have **`a set lifespan`**
* -> **a newly issued `OAuth2Token`** is **active** and becomes **inactive** when it either **`expires or is invalidated (revoked)`**
* -> the **`OAuth2Authorization`** is (implicitly) **inactive** when **`all associated 'OAuth2Token' instances are inactive`**

## Claim
* -> **OAuth2Authorization.Token** also provides **`getClaims()`**, which returns **`the claims (if any) associated with the 'OAuth2Token'`**

===================================================================
> có thể cấu hình thành Bean cho SecurityFilterChains

# OAuth2AuthorizationService
* -> is **the central component** where **`new authorizations are stored`** and **`existing authorizations are queried`**
* -> the OAuth2AuthorizationService is an **`OPTIONAL`** component and **`defaults to InMemoryOAuth2AuthorizationService`**

## Usage
* -> it is used by other components when following **a specific protocol flow**
* _for example, **`client authentication`**, **`authorization grant processing`**, **`token introspection`**, **`token revocation`**, **`dynamic client registration`**, and others_

## built-in Implementation
* -> the provided **implementations of OAuth2AuthorizationService** are **`InMemoryOAuth2AuthorizationService`** and **`JdbcOAuth2AuthorizationService`**

* -> the **`InMemoryOAuth2AuthorizationService`** implementation stores OAuth2Authorization instances **in-memory** and is recommended ONLY to be used during **development and testing** 
* -> **`JdbcOAuth2AuthorizationService`** is a JDBC implementation that persists 'OAuth2Authorization' instances by using **JdbcOperations**

## Register 'OAuth2AuthorizationService' @Bean

* -> configure through the **`OAuth2AuthorizationServerConfigurer`**:
```java
// the "OAuth2AuthorizationServerConfigurer" is useful when applying multiple configuration options simultaneously
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
		new OAuth2AuthorizationServerConfigurer();
	http.apply(authorizationServerConfigurer);

	authorizationServerConfigurer
		.authorizationService(authorizationService);

	// ...

	return http.build();
}
```

* -> hoặc ta muốn cấu hình riêng cũng được
```java
@Bean
public OAuth2AuthorizationService authorizationService() {
	return new InMemoryOAuth2AuthorizationService();
}
```

===================================================================
# OAuth2TokenContext
* -> is a **context object** that holds **`information associated with an OAuth2Token`** 
* -> is used by an **`OAuth2TokenGenerator`** and **`OAuth2TokenCustomizer`**

* -> OAuth2TokenContext provides the following **`accessors`**:
```java
public interface OAuth2TokenContext extends Context {
	// The RegisteredClient associated with the authorization grant
	default RegisteredClient getRegisteredClient() ...

	//  The Authentication instance of the resource owner (or client)
	default <T extends Authentication> T getPrincipal() ...

	// The AuthorizationServerContext object that holds information of the Authorization Server runtime environment
	default AuthorizationServerContext getAuthorizationServerContext() ...

	@Nullable
	//  The OAuth2Authorization associated with the authorization grant
	default OAuth2Authorization getAuthorization() ...

	// The scope(s) authorized for the client
	default Set<String> getAuthorizedScopes() ...

	//  The OAuth2TokenType to generate. The supported values are code, access_token, refresh_token, and id_token
	default OAuth2TokenType getTokenType() ...

	// The AuthorizationGrantType associated with the authorization grant
	default AuthorizationGrantType getAuthorizationGrantType() ...

	// The Authentication instance used by the AuthenticationProvider that processes the authorization grant
	default <T extends Authentication> T getAuthorizationGrant() ...

	// ...
}
```

===================================================================
> config như 1 Bean

# OAuth2TokenGenerator
* -> is an **`OPTIONAL`** component responsible for **`generating an OAuth2Token`** from the information contained in the provided **`OAuth2TokenContext`**

* -> the OAuth2TokenGenerator is primarily used by **components that implement `authorization grant processing`** – for example: **authorization_code**, **client_credentials**, and **refresh_token**
* -> the OAuth2TokenGenerator provides great flexibility, as it can support any **`custom token format`** for **access_token** and **refresh_token**

## generated 'OAuth2Token' instance
* -> primarily depends on the type of **`OAuth2TokenType`** specified in the **OAuth2TokenContext**

* _for example, when the **value** for **OAuth2TokenType** is:_
* -> **`code`**, then **`OAuth2AuthorizationCode`** is generated
* -> **`access_token`**, then **`OAuth2AccessToken`** is generated
* -> **`refresh_token`**, then **`OAuth2RefreshToken`** is generated
* -> **`id_token`**, then **`OidcIdToken`** is generated

## 'OAuth2AccessToken' format - Jwt or Opaque
* -> the format of the generated **OAuth2AccessToken** varies, depending on the **`TokenSettings.getAccessTokenFormat()`** configured for the **RegisteredClient**
* -> if the format is **`OAuth2TokenFormat.SELF_CONTAINED (the default)`**, then a **`Jwt`** is generated
* -> if the format is **`OAuth2TokenFormat.REFERENCE`**, then an **`opaque`** token is generated 

## built-in Implementation
* -> the provided implementations are **`OAuth2AccessTokenGenerator`**, **`OAuth2RefreshTokenGenerator`**, and **`JwtGenerator`**

* -> the **`OAuth2AccessTokenGenerator`** generates an **opaque** (OAuth2TokenFormat.REFERENCE) access token
* -> the **`JwtGenerator`** generates a **Jwt** (OAuth2TokenFormat.SELF_CONTAINED)

## Default
* -> and defaults to a **`DelegatingOAuth2TokenGenerator`** composed of an **`OAuth2AccessTokenGenerator`** and **`OAuth2RefreshTokenGenerator`**
* -> If **a `JwtEncoder @Bean` or `JWKSource<SecurityContext>` @Bean is registered**, then a **`JwtGenerator`** is additionally composed in the **DelegatingOAuth2TokenGenerator**

## Register 'OAuth2TokenGenerator' @Bean

* -> configure through the **`OAuth2AuthorizationServerConfigurer`**
```java
// the OAuth2AuthorizationServerConfigurer is useful when applying multiple configuration options simultaneously
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
			OAuth2AuthorizationServerConfigurer.authorizationServer();

	http
		.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
		.with(authorizationServerConfigurer, (authorizationServer) ->
			authorizationServer
				.tokenGenerator(tokenGenerator)
		)
	    ...

	return http.build();
}
```

* -> hoặc cấu hình riêng ra
```java
@Bean
public OAuth2TokenGenerator<?> tokenGenerator() {
	JwtEncoder jwtEncoder = ...
	JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
	OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
	OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
	return new DelegatingOAuth2TokenGenerator(
			jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
}
```

===================================================================
# OAuth2TokenCustomizer
* -> it is used by an **`OAuth2TokenGenerator`** to let it **`customize the attributes of the OAuth2Token`** (_which are accessible in the provided **OAuth2TokenContext**_) before it is generated

## OAuth2TokenClaimsContext
* -> **Auth2TokenClaimsContext** implements **`OAuth2TokenContext`**

* -> an **`OAuth2TokenCustomizer<OAuth2TokenClaimsContext>`** provides the ability to **customize the claims of an "opaque" OAuth2AccessToken**
* -> **`OAuth2TokenClaimsContext.getClaims()`** provides access to the **OAuth2TokenClaimsSet.Builder** - allowing the ability to **`add, replace, and remove claims`**

### Implement 'OAuth2TokenCustomizer<OAuth2TokenClaimsContext>' and config with 'OAuth2AccessTokenGenerator' 

* -> **NOTE**: If the **`OAuth2TokenGenerator`** is not provided as a @Bean or is not configured through the OAuth2AuthorizationServerConfigurer, 
* -> an **OAuth2TokenCustomizer<OAuth2TokenClaimsContext> @Bean** will **`automatically be configured with an 'OAuth2AccessTokenGenerator'`**


```java
@Bean
public OAuth2TokenGenerator<?> tokenGenerator() {
	JwtEncoder jwtEncoder = ...
	JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
	OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
	accessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer());
	OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
	return new DelegatingOAuth2TokenGenerator(
			jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
}

@Bean
public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer() {
	return context -> {
		OAuth2TokenClaimsSet.Builder claims = context.getClaims();
		// Customize claims

	};
}
```

## JwtEncodingContext
* -> **JwtEncodingContext** implements **`OAuth2TokenContext`**

* -> an **`OAuth2TokenCustomizer<JwtEncodingContext>`** declared with a generic type of  provides the ability to **customize the headers and claims of a Jwt**
* -> **`JwtEncodingContext.getJwsHeader()`** provides access to the **JwsHeader.Builder** - allowing the ability to **`add, replace, and remove headers`**
* -> **`JwtEncodingContext.getClaims()`** provides access to the **JwtClaimsSet.Builder** - allowing the ability to **`add, replace, and remove claims`**

### implement an 'OAuth2TokenCustomizer<JwtEncodingContext>' and configure it with a 'JwtGenerator'

* -> **NOTE**: if the **`OAuth2TokenGenerator`** is not provided as a @Bean or is not configured through the OAuth2AuthorizationServerConfigurer 
* -> **an OAuth2TokenCustomizer<JwtEncodingContext> @Bean** will automatically be configured with a **`JwtGenerator`**

```java
@Bean
public OAuth2TokenGenerator<?> tokenGenerator() {
	JwtEncoder jwtEncoder = ...
	JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
	jwtGenerator.setJwtCustomizer(jwtCustomizer());
	OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
	OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
	return new DelegatingOAuth2TokenGenerator(
			jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
}

@Bean
public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
	return context -> {
		JwsHeader.Builder headers = context.getJwsHeader();
		JwtClaimsSet.Builder claims = context.getClaims();
		if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
			// Customize headers/claims for access_token

		} else if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
			// Customize headers/claims for id_token

		}
	};
}
```

===================================================================
> chứa nội dung consent do resource owner cấp cho client

# OAuth2AuthorizationConsent
* -> is a representation of an **`authorization "consent" (decision)`** from an **OAuth2 authorization request flow**
* _for example, the **`authorization_code grant`**, which holds the **authorities** granted to a **client** by the **resource owner**_

## Process
* -> when authorizing access to a client, the **resource owner may grant only a subset of the authorities requested by the client**
* -> the typical use case is the **authorization_code grant flow**, in which the **`client requests scopes`** and the **`resource owner grants (or denies) access`** to the requested scopes
* -> after the completion of an OAuth2 authorization request flow, an **`OAuth2AuthorizationConsent is created`** (or updated) and **`associates the granted authorities`** with the client and resource owner.

## Model

```java - "OAuth2AuthorizationConsent" and its attributes are defined as follows:
public final class OAuth2AuthorizationConsent implements Serializable {
	// The ID that uniquely identifies the RegisteredClient
	private final String registeredClientId;

	// The principal name of the resource owner
	private final String principalName;

	// The authorities granted to the client by the resource owner. An authority can represent a scope, a claim, a permission, a role, and others
	private final Set<GrantedAuthority> authorities;

	// ...
}
```

===================================================================
> đăng ký là 1 Bean trong SecurityFilterChain

# OAuth2AuthorizationConsentService
* -> is the **central component** where **`new authorization consents are stored`** and **`existing authorization consents are queried`**
* ->  is an **`OPTIONAL`** component 
* -> primarily used by **components that implement an `OAuth2 authorization request flow`** - for example, the **authorization_code grant**

## built-in Implementation
* -> the provided implementations are **`InMemoryOAuth2AuthorizationConsentService`** and **`JdbcOAuth2AuthorizationConsentService`**
* -> defaults to **`InMemoryOAuth2AuthorizationConsentService`**

* -> **`InMemoryOAuth2AuthorizationConsentService`** implementation stores OAuth2AuthorizationConsent instances **in-memory** and is recommended ONLY for **development and testing** 
* -> **`JdbcOAuth2AuthorizationConsentService`** is a JDBC implementation that persists OAuth2AuthorizationConsent instances by using **JdbcOperations**

## Register an 'OAuth2AuthorizationConsentService' @Bean

* -> configure through the **`OAuth2AuthorizationServerConfigurer`**
```java
// the OAuth2AuthorizationServerConfigurer is useful when applying multiple configuration options simultaneously
@Bean
public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
			OAuth2AuthorizationServerConfigurer.authorizationServer();

	http
		.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
		.with(authorizationServerConfigurer, (authorizationServer) ->
			authorizationServer
				.authorizationConsentService(authorizationConsentService)
		)
	    // ...

	return http.build();
}
```

* -> hoặc cũng có thể cấu hình riêng ra
```java
@Bean
public OAuth2AuthorizationConsentService authorizationConsentService() {
	return new InMemoryOAuth2AuthorizationConsentService();
}
```

===================================================================
> nếu không cấu hình Bean thì nó vẫn sẽ được tự động config	

# SessionRegistry
* -> if **OpenID Connect 1.0 is enabled**, a "SessionRegistry instance" is used to **`track authenticated sessions`**
* -> the **SessionRegistry** is used by the **default implementation of `SessionAuthenticationStrategy`** associated to the **OAuth2 Authorization Endpoint** for **`registering new authenticated sessions`**

## SessionRegistryImpl 
* -> if a **SessionRegistry @Bean is not registered**, the default implementation **`SessionRegistryImpl`** will be used

* -> if a **SessionRegistry @Bean is registered** and is **an instance of SessionRegistryImpl**, 
* -> a **`HttpSessionEventPublisher @Bean`** SHOULD also be registered as it’s responsible for notifying SessionRegistryImpl of session lifecycle events, 
* _for example, **SessionDestroyedEvent**, to provide the ability to remove the SessionInformation instance_

## Logout
* -> When a logout is requested by an End-User, the OpenID Connect 1.0 Logout Endpoint uses the SessionRegistry to lookup the SessionInformation associated to the authenticated End-User to perform the logout

## Concurrent Session Control
* -> If Spring Security’s Concurrent Session Control feature is being used, it is RECOMMENDED to register a SessionRegistry @Bean to ensure it’s shared between Spring Security’s Concurrent Session Control and Spring Authorization Server’s Logout feature.

## Register 
* -> register a **`SessionRegistry @Bean`** and **`HttpSessionEventPublisher @Bean`** (required by SessionRegistryImpl):
```java
@Bean
public SessionRegistry sessionRegistry() {
	return new SessionRegistryImpl();
}

@Bean
public HttpSessionEventPublisher httpSessionEventPublisher() {
	return new HttpSessionEventPublisher();
}
```
