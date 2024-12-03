> Core Model / Components
> https://docs.spring.io/spring-authorization-server/reference/core-model-components.html

===================================================================
# RegisteredClient
* -> is a representation of **`a client` that is `registered with the authorization server`**
* -> _the corresponding client registration model in **Spring Security's OAuth2 Client support** is **`ClientRegistration`**_

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

## Client information
* -> during **client registration**, 
* -> the client is assigned **`a unique client identifier`**, (optionally) **`a client secret`** (depending on client type), and **`metadata associated with its unique client identifier`**
* _the **client’s metadata** can range from human-facing display strings (such as **`client name`**) to items specific to a protocol flow (such as the list of **`valid redirect URIs`**)_

## Process
* -> the primary purpose of a client is to **request access to protected resources**
* -> the **`client first requests an access token`** by **authenticating with the authorization server** and **presenting the authorization grant**
* -> the **authorization server** **`authenticates the client and authorization grant`**, and, if they are valid, **`issues an access token`**
* -> the **`client can now request the protected resource`** from the resource server by presenting the access token

## Configuration

* -> configure a **`RegisteredClient`**

```java - that is allowed to perform the "authorization_code" grant flow to request an access token

RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
	.clientId("client-a")
	.clientSecret("{noop}secret") 
    // {noop} represents the PasswordEncoder id for Spring Security’s NoOpPasswordEncoder

	.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
	.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
	.redirectUri("http://127.0.0.1:8080/authorized")
	.scope("scope-a")
	.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
	.build();
```

* -> the corresponding configuration in **`Spring Security’s OAuth2 Client`** support is:
```yml
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

===================================================================
# RegisteredClientRepository
* -> is a **`REQUIRED`** component
* -> is **`the central component`** where **new clients can be registered** and **existing clients can be queried**

## Usage
* -> it is used by other components when following **a specific protocol flow**:
* _such as **`client authentication`**, **`authorization grant processing`**, **`token introspection`**, **`dynamic client registration`**, and others_

## built-in implementation
* -> the provided **implementations of RegisteredClientRepository** are **`InMemoryRegisteredClientRepository`** and **`JdbcRegisteredClientRepository`**

* -> the **InMemoryRegisteredClientRepository** implementation **`stores RegisteredClient instances in-memory`** and is recommended **`ONLY to be used during development and testing`**
* -> **JdbcRegisteredClientRepository** is a JDBC implementation that **`persists RegisteredClient instances by using JdbcOperations`**

## Register RegisteredClientRepository @Bean

```java
@Bean
public RegisteredClientRepository registeredClientRepository() {
	List<RegisteredClient> registrations = // ...
	return new InMemoryRegisteredClientRepository(registrations);
}
```

* -> alternatively, we can configure the RegisteredClientRepository through the **`OAuth2AuthorizationServerConfigurer`**:
* _the OAuth2AuthorizationServerConfigurer is useful when **applying multiple configuration options simultaneously**_
```java
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

===================================================================
# OAuth2Authorization
* -> is a representation of **an OAuth2 authorization**, which **`holds state related to the authorization granted to a client`**
* -> by the **resource owner** or **itself** in the case of the client_credentials authorization grant type

* -> the corresponding authorization model in **Spring Security's OAuth2 Client support** is **`OAuth2AuthorizedClient`**

```java - "OAuth2Authorization" and its attributes are defined as follows:
public class OAuth2Authorization implements Serializable {
	private String id;
	private String registeredClientId;
	private String principalName;
	private AuthorizationGrantType authorizationGrantType;
	private Set<String> authorizedScopes;
	private Map<Class<? extends OAuth2Token>, Token<?>> tokens;
	private Map<String, Object> attributes;

	// ...
}
```

## Attached info
* -> **after the successful completion of an authorization grant flow**, 
* -> an **`OAuth2Authorization`** is created 
* -> and associates an **`OAuth2AccessToken`**, an (optional) **`OAuth2RefreshToken`**, and **`additional state`** specific to the executed authorization grant type

## OAuth2Authorization.Token
* -> each **OAuth2Token** is held in an **`OAuth2Authorization.Token`**, which provides accessors for **isExpired()**, **isInvalidated()**, and **isActive()**
* -> **OAuth2Authorization.Token** also provides **`getClaims()`**, which returns **`the claims (if any) associated with the 'OAuth2Token'`**

## OAuth2Token
* _the instances of **`OAuth2Token` interface**  associated with an "OAuth2Authorization" vary, **depending on the authorization grant type**_

* -> for the **OAuth2 authorization_code grant**, an **`OAuth2AuthorizationCode`**, an **`OAuth2AccessToken`**, and an (optional) **`OAuth2RefreshToken`** are associated

* -> for the **OpenID Connect 1.0 authorization_code grant**, an **`OAuth2AuthorizationCode`**, an **`OidcIdToken`**, an **`OAuth2AccessToken`**, and an (optional) **`OAuth2RefreshToken`** are associated

* -> for the **OAuth2 client_credentials grant**, only an **`OAuth2AccessToken`** is associated

## 'OAuth2Authorization' and associated 'OAuth2Token' lifespan
* -> **OAuth2Authorization** and its **associated 'OAuth2Token' instances** have **`a set lifespan`**

* -> **`a newly issued 'OAuth2Token'`** is **active** and becomes **inactive** when it either **`expires or is invalidated (revoked)`**
* -> the **OAuth2Authorization is (implicitly) inactive** when **`all associated 'OAuth2Token' instances are inactive`**


===================================================================
# OAuth2AuthorizationService
* -> is **`the central component`** where **new authorizations are stored** and **existing authorizations are queried**
* -> the OAuth2AuthorizationService is an **`OPTIONAL`** component and **`defaults to InMemoryOAuth2AuthorizationService`**

## Usage
* -> it is used by other components when following **a specific protocol flow**
* _for example, **`client authentication`**, **`authorization grant processing`**, **`token introspection`**, **`token revocation`**, **`dynamic client registration`**, and others_

## Implementation
* -> the provided **implementations of OAuth2AuthorizationService** are **`InMemoryOAuth2AuthorizationService`** and **`JdbcOAuth2AuthorizationService`**

* -> the **InMemoryOAuth2AuthorizationService implementation** **`stores OAuth2Authorization instances in-memory`** and **`is recommended ONLY to be used during development and testing`** 
* -> **JdbcOAuth2AuthorizationService** is a JDBC implementation that **`persists 'OAuth2Authorization' instances by using 'JdbcOperations'`**

## Register 'OAuth2AuthorizationService' @Bean

```java
@Bean
public OAuth2AuthorizationService authorizationService() {
	return new InMemoryOAuth2AuthorizationService();
}
```

* -> alternatively, we can configure the "OAuth2AuthorizationService" through the **`OAuth2AuthorizationServerConfigurer`**:
* _the "OAuth2AuthorizationServerConfigurer" is useful when applying multiple configuration options simultaneously_
```java
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

===================================================================
# OAuth2AuthorizationConsent

===================================================================
# OAuth2AuthorizationConsentService


===================================================================
# OAuth2TokenContext

===================================================================
# OAuth2TokenGenerator

===================================================================
# OAuth2TokenCustomizer

===================================================================
# SessionRegistry